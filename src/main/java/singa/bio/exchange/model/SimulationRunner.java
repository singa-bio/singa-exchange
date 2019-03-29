package singa.bio.exchange.model;

import bio.singa.features.formatter.ConcentrationFormatter;
import bio.singa.features.formatter.GeneralQuantityFormatter;
import bio.singa.features.formatter.QuantityFormatter;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.simulation.events.EpochUpdateWriter;
import bio.singa.simulation.features.variation.VariationSet;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.SimulationManager;
import bio.singa.simulation.trajectories.TrajectoryObserver;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.embed.swing.JFXPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Time;
import javax.swing.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bio.singa.features.units.UnitProvider.NANO_MOLE_PER_LITRE;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.MINUTE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class SimulationRunner {

    private static final Logger logger = LoggerFactory.getLogger(SimulationRunner.class);

    private static QuantityFormatter<Time> TIME_FORMATTER = new GeneralQuantityFormatter<>(new DecimalFormat("0.0000E00"), SECOND, false);

    public static void runMultipleSimulations(Path simulationPath, List<RectangularCoordinate> observedNodes) throws IOException, InterruptedException {

        List<Path> simulationFiles = new ArrayList<>();

        // if directory
        if (Files.isDirectory(simulationPath)) {
            // get all json files as simulations
            Files.walk(simulationPath)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(simulationFiles::add);
        } else {
            // else add simulation file
            simulationFiles.add(simulationPath);
        }

        // for each potential simulation
        for (Path simulationFile : simulationFiles) {
            // get simulation
            Simulation simulation = Converter.getSimulationFrom(String.join("", Files.readAllLines(simulationFile)));
            Path folder = Paths.get(simulationFile.getFileName().toString().replace(".json", ""));
            runSimulation(simulationPath, folder, simulation, observedNodes);
        }
    }

    public static void runSimulationWithVariations(Path baseSimulation, VariationSet variations, List<RectangularCoordinate> observedNodes, Function<Simulation, Double> valueExtraction) throws IOException, InterruptedException {
        List<List<?>> parameterVariations = variations.generateAllCombinations();
        String baseSimulationJson = String.join("", Files.readAllLines(baseSimulation));
        // remember values from the value extraction function
        Map<String, Double> resultingValues = new HashMap<>();
        // for each variation set
        int currentSetIdentifier = 1;
        for (List<?> currentVariationSet : parameterVariations) {
            String identifierString = currentVariationSet.stream()
                    .map(VariationSet::getValueString)
                    .collect(Collectors.joining(","));
            System.out.println("Starting simulation " + currentSetIdentifier + " of " + parameterVariations.size() + " for set " + identifierString);
            // create simulation
            Simulation simulation = Converter.getSimulationFrom(baseSimulationJson);
            simulation.getScheduler().setRecalculationCutoff(0.03);
            // apply variation parameters
            VariationSet.applyParameters(simulation, currentVariationSet);
            // create folder for this set
            Path currentVariationSetPath = baseSimulation.getParent().resolve("set_" + currentSetIdentifier);
            VariationSet.createFolderForVariationSet(currentVariationSetPath);
            // write variations
            VariationSet.writeVariationLog(currentVariationSetPath, currentVariationSet);
            // write json
            // writeVariationJson(currentVariationSetPath, simulation);
            // run simulation
            runSimulation(baseSimulation.getParent(), currentVariationSetPath, simulation, observedNodes);
            // remember resulting values

            Double result = valueExtraction.apply(simulation);
            resultingValues.put(identifierString, result);
            System.out.println("Finished Simulation " + currentSetIdentifier + " of " + parameterVariations.size() + " resulted in " + identifierString + " -> " + result);
            currentSetIdentifier++;
        }
        VariationSet.writeVariationResults(baseSimulation, variations, resultingValues);
    }

    private static void writeVariationJson(Path currentVariationSetPath, Simulation simulation) {
        SimulationRepresentation variationRepresentation = Converter.getRepresentationFrom(simulation);
        String json;
        try {
            json = variationRepresentation.toJson();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to create json from the current simulation simulation.", e);
        }
        try {
            Files.write(currentVariationSetPath.resolve("variation_simulation.json"), json.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to write variation json to " + currentVariationSetPath + ".", e);
        }
    }

    public static void runSimulation(Path simulationPath, Path simulationFolder, Simulation simulation, List<RectangularCoordinate> observedNodes) throws IOException, InterruptedException {
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.01, SECOND));
        // create writer
        EpochUpdateWriter epochUpdateWriter = EpochUpdateWriter.create()
                .workspace(simulationPath)
                .folder(simulationFolder, false)
                .simulation(simulation)
                .allEntities()
                .allModules()
                .concentrationFormat(ConcentrationFormatter.forUnit(NANO_MOLE_PER_LITRE))
                .timeFormat(TIME_FORMATTER)
                .build();

        // create manager
        SimulationManager simulationManager = new SimulationManager(simulation);
        TrajectoryObserver trajectoryObserver = new TrajectoryObserver();
        simulationManager.addGraphUpdateListener(trajectoryObserver);

        // reference latch for termination
        CountDownLatch terminationLatch = new CountDownLatch(1);
        simulationManager.setTerminationLatch(terminationLatch);

        // set termination condition
        simulationManager.setSimulationTerminationToTime(Quantities.getQuantity(5, MINUTE));
        simulationManager.setUpdateEmissionToTimePassed(Quantities.getQuantity(100, MILLI(SECOND)));

        // reference nodes to write
        for (RectangularCoordinate coordinate : observedNodes) {
            AutomatonNode node = simulation.getGraph().getNode(coordinate);
            epochUpdateWriter.addUpdatableToObserve(node);
            simulation.observe(node);
        }

        // reference writer
        simulationManager.addNodeUpdateListener(epochUpdateWriter);

        // if you want to use fx tasks you need some magic
        initializeJFXEnvironment();

        // start
        Thread thread = new Thread(simulationManager);
        thread.setDaemon(true);
        thread.start();

        // wait for each simulation to finish since there con be problems with the environment singleton
        terminationLatch.await();
//        TrajectoryDataset trajectoryDataset = TrajectoryDataset.of(trajectoryObserver.getTrajectories());
//        System.out.println(trajectoryDataset.toJson());
    }

    public static void initializeJFXEnvironment() {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel();
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
