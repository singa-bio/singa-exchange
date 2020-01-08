package bio.singa.exchange;

import bio.singa.core.utility.Resources;
import bio.singa.simulation.model.simulation.Simulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author cl
 */
class SimulationRunnerTest {

    public static void main(String[] args) {
        String fileLocation = Resources.getResourceAsFileLocation("simulation_setup.json");
        try {
            Simulation simulation = Converter.getSimulationFrom(String.join("", Files.readAllLines(Paths.get(fileLocation))));
            System.out.println(simulation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}