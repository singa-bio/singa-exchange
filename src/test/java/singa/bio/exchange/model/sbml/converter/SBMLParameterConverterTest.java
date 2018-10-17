package singa.bio.exchange.model.sbml.converter;

import bio.singa.core.utility.Resources;
import bio.singa.simulation.model.parameters.ParameterStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.features.ParameterDataset;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

/**
 * @author cl
 */
public class SBMLParameterConverterTest {


    private static final Logger logger = LoggerFactory.getLogger(SBMLParameterConverterTest.class);

    @Before
    @After
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