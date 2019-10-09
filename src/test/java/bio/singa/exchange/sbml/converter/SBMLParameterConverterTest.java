package bio.singa.exchange.sbml.converter;

import bio.singa.core.utility.Resources;
import bio.singa.exchange.features.ParameterDataset;
import bio.singa.simulation.model.parameters.ParameterStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bio.singa.exchange.IllegalConversionException;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

/**
 * @author cl
 */
public class SBMLParameterConverterTest {


    private static final Logger logger = LoggerFactory.getLogger(SBMLParameterConverterTest.class);

    @BeforeEach
    @AfterEach
    public void cleanUp() {
        ParameterStorage.clear();
    }

    @Test
    public void convertCompartmentDataset() {
        InputStream inputStream = Resources.getResourceAsStream("BIOMD0000000184.xml");

        SBMLReader reader = new SBMLReader();
        logger.info("Parsing SBML...");
        SBMLDocument document;
        try {
            document = reader.readSBMLFromStream(inputStream);
        } catch (XMLStreamException e) {
            throw new IllegalConversionException("Unable to read SBML file.");
        }

        // requires units
        SBMLUnitConverter.convert(document.getModel().getListOfUnitDefinitions());
        SBMLParameterConverter.convert(document.getModel().getListOfParameters());
        try {
            String parameters = ParameterDataset.fromCache().toJson();
            System.out.println(parameters);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

}