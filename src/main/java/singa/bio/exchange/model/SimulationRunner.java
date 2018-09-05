package singa.bio.exchange.model;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.QuantityFormatter;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.simulation.events.EpochUpdateWriter;
import bio.singa.simulation.features.variation.EntityFeatureVariationEntry;
import bio.singa.simulation.features.variation.ModuleFeatureVariationEntry;
import bio.singa.simulation.features.variation.VariationSet;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.sections.InitialConcentration;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.SimulationManager;
import javafx.embed.swing.JFXPanel;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class SimulationRunner {

    public static final QuantityFormatter<MolarConcentration> CONCENTRATION_FORMATTER = new QuantityFormatter<>(new DecimalFormat("0.0000E00"), NANO(MOLE_PER_LITRE), false);
    private static QuantityFormatter<Time> TIME_FORMATTER = new QuantityFormatter<>(new DecimalFormat("0.0000E00"), MILLI(SECOND), false);

    public static void run(Path simulationPath, List<RectangularCoordinate> observedNodes) throws IOException, InterruptedException {

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

    public static Map<String, Double> runVariations(Path baseSimulation, VariationSet variations, List<RectangularCoordinate> observedNodes, Function<Simulation, Double> valueExtraction) throws IOException, InterruptedException {
        List<Set<?>> parameterVariations = variations.generateAllCombinations();
        String baseStrings = String.join("", Files.readAllLines(baseSimulation));
        Map<String, Double> resultingValues = new HashMap<>();
        int i = 1;
        for (Set<?> parameterSet : parameterVariations) {
            Simulation simulation = Converter.getSimulationFrom(baseStrings);
            applyParameters(simulation, parameterSet);
            Path folder = Paths.get("set_" + i);
            // write variations
            String collect = parameterSet.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(System.lineSeparator()));
            Path simulationFolder = baseSimulation.getParent().resolve(folder);
            if (!Files.exists(simulationFolder)) {
                Files.createDirectories(simulationFolder);
            }
            Files.write(simulationFolder.resolve("variations.log"), collect.getBytes());
            runSimulation(baseSimulation.getParent(), folder, simulation, observedNodes);
            resultingValues.put(parameterSet.stream()
                    .map(SimulationRunner::getValueString)
                    .collect(Collectors.joining(",")), valueExtraction.apply(simulation));
            i++;
        }
        return resultingValues;
    }

    private static void applyParameters(Simulation simulation, Set<?> parameterVariations) {
        for (Object parameterVariation : parameterVariations) {
            if (parameterVariation instanceof InitialConcentration) {
                // varying concentration
                simulation.getConcentrationInitializer().addInitialConcentration((InitialConcentration) parameterVariation);
            } else if (parameterVariation instanceof EntityFeatureVariationEntry) {
                // varying feature of a entity
                Collection<ChemicalEntity> chemicalEntities = simulation.getChemicalEntities();
                EntityFeatureVariationEntry entityVariation = (EntityFeatureVariationEntry) parameterVariation;
                for (ChemicalEntity chemicalEntity : chemicalEntities) {
                    if (chemicalEntity.equals(entityVariation.getEntity())) {
                        chemicalEntity.setFeature(entityVariation.getFeature());
                        break;
                    }
                }
            } else if (parameterVariation instanceof ModuleFeatureVariationEntry) {
                // varying feature of a module
                List<UpdateModule> modules = simulation.getModules();
                ModuleFeatureVariationEntry moduleVariation = (ModuleFeatureVariationEntry) parameterVariation;
                for (UpdateModule module : modules) {
                    if (module.equals(moduleVariation.getModule())) {
                        module.setFeature(moduleVariation.getFeature());
                    }
                }
            } else {
                // nonsense variation
                throw new IllegalStateException("The parameter variation " + parameterVariation + " is invalid.");
            }
        }
    }

    private static String getValueString(Object parameter) {
        if (parameter instanceof InitialConcentration) {
            // varying concentration
            return String.valueOf(((InitialConcentration) parameter).getConcentration().getValue().doubleValue());
        } else if (parameter instanceof EntityFeatureVariationEntry) {
            // varying feature of a entity
            Object featureContent = ((EntityFeatureVariationEntry) parameter).getFeature().getFeatureContent();
            if (featureContent instanceof Quantity) {
                return String.valueOf(((Quantity) featureContent).getValue().doubleValue());
            }
            return String.valueOf(featureContent);
        } else if (parameter instanceof ModuleFeatureVariationEntry) {
            // varying feature of a module
            Object featureContent = ((ModuleFeatureVariationEntry) parameter).getFeature().getFeatureContent();
            if (featureContent instanceof Quantity) {
                return String.valueOf(((Quantity) featureContent).getValue().doubleValue());
            }
            return String.valueOf(featureContent);
        } else {
            // nonsense variation
            throw new IllegalArgumentException("The parameter " + parameter + " is not a parameter.");
        }
    }


    private static void runSimulation(Path simulationPath, Path simulationFolder, Simulation simulation, List<RectangularCoordinate> observedNodes) throws IOException, InterruptedException {
        // create writer
        EpochUpdateWriter epochUpdateWriter = EpochUpdateWriter.create()
                .workspace(simulationPath)
                .folder(simulationFolder, false)
                .simulation(simulation)
                .allEntities()
                .allModules()
                .concentrationFormat(CONCENTRATION_FORMATTER)
                .timeFormat(TIME_FORMATTER)
                .build();

        // create manager
        SimulationManager simulationManager = new SimulationManager(simulation);

        // reference latch for termination
        CountDownLatch terminationLatch = new CountDownLatch(1);
        simulationManager.setTerminationLatch(terminationLatch);

        // set termination condition
        simulationManager.setSimulationTerminationToTime(Quantities.getQuantity(10, SECOND));
        simulationManager.setUpdateEmissionToTimePassed(Quantities.getQuantity(10, MILLI(SECOND)));

        // reference nodes to write
        for (RectangularCoordinate coordinate : observedNodes) {
            AutomatonNode node = simulation.getGraph().getNode(coordinate);
            epochUpdateWriter.addNodeToObserve(node);
            simulation.observeNode(node);
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
    }

    private static void initializeJFXEnvironment() {
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
