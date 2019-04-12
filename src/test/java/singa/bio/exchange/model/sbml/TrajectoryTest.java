package singa.bio.exchange.model.sbml;

import bio.singa.core.utility.Resources;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.SimulationManager;
import bio.singa.simulation.trajectories.nested.NestedUpdateRecorder;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import singa.bio.exchange.model.Converter;
import singa.bio.exchange.model.trajectories.TrajectoryDataset;
import tec.units.indriya.quantity.Quantities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import static bio.singa.features.units.UnitProvider.NANO_MOLE_PER_LITRE;
import static org.junit.jupiter.api.Assertions.fail;
import static tec.units.indriya.unit.MetricPrefix.MILLI;
import static tec.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
public class TrajectoryTest {

    @Test
    void shouldWriteTrajectory() {

        // create simulation
        Simulation simulation = null;
        try {
            String jsonContent = String.join("",
                    Files.readAllLines(Paths.get(Resources.getResourceAsFileLocation("receptor_setup.json"))));
            simulation = Converter.getSimulationFrom(jsonContent);
        } catch (IOException e) {
            fail(e);
        }
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.01, SECOND));

        // create manager
        SimulationManager simulationManager = new SimulationManager(simulation);
        // reference trajectory observer
        NestedUpdateRecorder observer = new NestedUpdateRecorder(simulation, MILLI(SECOND), NANO_MOLE_PER_LITRE);
        simulationManager.addGraphUpdateListener(observer);

        // reference latch for termination
        CountDownLatch terminationLatch = new CountDownLatch(1);
        simulationManager.setTerminationLatch(terminationLatch);

        // set termination condition
        simulationManager.setSimulationTerminationToTime(Quantities.getQuantity(10, SECOND));
        simulationManager.setUpdateEmissionToTimePassed(Quantities.getQuantity(10, MILLI(SECOND)));

        // start
        Thread thread = new Thread(simulationManager);
        thread.setDaemon(true);
        thread.start();

        // wait for each simulation to finish since there con be problems with the environment singleton
        try {
            terminationLatch.await();
        } catch (InterruptedException e) {
            fail(e);
        }

        try {
            System.out.println(TrajectoryDataset.of(observer.getTrajectories()).toJson());
        } catch (JsonProcessingException e) {
            fail(e);
        }

    }
}
