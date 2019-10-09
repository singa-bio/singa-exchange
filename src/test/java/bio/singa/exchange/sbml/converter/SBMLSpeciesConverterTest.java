package bio.singa.exchange.sbml.converter;

import bio.singa.core.utility.Resources;
import bio.singa.exchange.entities.EntityDataset;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class SBMLSpeciesConverterTest {

    private static final Logger logger = LoggerFactory.getLogger(SBMLSpeciesConverterTest.class);

    @Test
    public void convertSpeciesDataset() {

        InputStream inputStream = Resources.getResourceAsStream("BIOMD0000000023.xml");

        SBMLReader reader = new SBMLReader();
        logger.info("Parsing SBML...");
        SBMLDocument document;
        try {
            document = reader.readSBMLFromStream(inputStream);
        } catch (XMLStreamException e) {
            throw new IllegalConversionException("Unable to read SBML file.");
        }

        SBMLSpeciesConverter.convert(document.getModel().getListOfSpecies());
        try {
            String s = EntityDataset.fromCache().toJson();
            System.out.println(s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }
}