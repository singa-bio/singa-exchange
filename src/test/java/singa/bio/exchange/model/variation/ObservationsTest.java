package singa.bio.exchange.model.variation;

import bio.singa.core.utility.Resources;
import org.junit.jupiter.api.Test;
import singa.bio.exchange.model.Converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author cl
 */
class ObservationsTest {

    @Test
    void testCreationValidation() throws IOException {

        String fileLocation = Resources.getResourceAsFileLocation("pka_regulation.json");
        Converter.getSimulationFrom(String.join("", Files.readAllLines(Paths.get(fileLocation))));

        Observations observations = new Observations();
        observations.addObservation("pka","(PKAR:APS)", "vesicle membrane", "v1");
        observations.addObservation("camp", "CAMP", "cytoplasm", "(0,0)");

        System.out.println(observations.toJson());


    }
}