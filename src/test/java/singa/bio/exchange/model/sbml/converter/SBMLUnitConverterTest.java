package singa.bio.exchange.model.sbml.converter;

import bio.singa.core.utility.Resources;
import org.junit.Test;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.IllegalConversionException;

import javax.measure.Unit;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author cl
 */
public class SBMLUnitConverterTest {

    private static final Logger logger = LoggerFactory.getLogger(SBMLUnitConverterTest.class);


    @Test
    public void convertSpeciesDataset() {
        InputStream inputStream = Resources.getResourceAsStream("BIOMD0000000184.xml");

        SBMLReader reader = new SBMLReader();
        logger.info("Parsing SBML...");
        SBMLDocument document;
        try {
            document = reader.readSBMLFromStream(inputStream);
        } catch (XMLStreamException e) {
            throw new IllegalConversionException("Unable to read SBML file.");
        }

        Map<String, Unit<?>> unitMap = SBMLUnitConverter.convert(document.getModel().getListOfUnitDefinitions());
        unitMap.forEach((k, v) -> System.out.println(k + ": " + v));
    }

}