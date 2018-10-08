package singa.bio.exchange.model.sbml.converter;

import bio.singa.core.utility.Resources;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.entities.EntityDataset;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

/**
 * @author cl
 */
public class SBMLSpeciesConverterTest {

    private static final Logger logger = LoggerFactory.getLogger(SBMLSpeciesConverterTest.class);

    @Test
    public void convertSpeciesDataset() {

        InputStream inputStream = Resources.getResourceAsStream("sabio_sbml.xml");

        SBMLReader reader = new SBMLReader();
        logger.info("Parsing SBML...");
        SBMLDocument document;
        try {
            document = reader.readSBMLFromStream(inputStream);
        } catch (XMLStreamException e) {
            throw new IllegalConversionException("Unable to read SBML file.");
        }

        EntityDataset entityDataset = SBMLSpeciesConverter.convert(document.getModel().getListOfSpecies());
        try {
            String s = entityDataset.toJson();
            System.out.println(s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}