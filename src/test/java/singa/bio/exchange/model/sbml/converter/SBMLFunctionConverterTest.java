package singa.bio.exchange.model.sbml.converter;

import bio.singa.core.utility.Resources;
import bio.singa.simulation.model.parameters.ParameterStorage;
import bio.singa.simulation.model.simulation.Simulation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.Converter;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.modules.ModuleDataset;
import singa.bio.exchange.model.sbml.SBMLParser;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

/**
 * @author cl
 */
public class SBMLFunctionConverterTest {

    private static final Logger logger = LoggerFactory.getLogger(SBMLFunctionConverterTest.class);

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

        SBMLParser.current = new Simulation();
        SBMLUnitConverter.convert(document.getModel().getListOfUnitDefinitions());
        SBMLCompartmentConverter.convert(document.getModel().getListOfCompartments());
        SBMLSpeciesConverter.convert(document.getModel().getListOfSpecies());
        SBMLParameterConverter.convert(document.getModel().getListOfParameters());
        SBMLFunctionConverter.convert(document.getModel().getListOfFunctionDefinitions());
        SBMLReactionConverter.convert(document.getModel().getListOfReactions());
        try {
            ModuleDataset moduleDataset = Converter.getModuleDatasetFrom(SBMLParser.current);
            String json = moduleDataset.toJson();
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

}