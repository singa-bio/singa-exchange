package bio.singa.exchange.sbml.converter;

import bio.singa.core.utility.Resources;
import bio.singa.features.units.UnitRegistry;
import org.junit.jupiter.api.Test;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bio.singa.exchange.IllegalConversionException;
import bio.singa.exchange.units.UnitCache;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

/**
 * @author cl
 */
public class SBMLUnitConverterTest {

    private static final Logger logger = LoggerFactory.getLogger(SBMLUnitConverterTest.class);


    @Test
    public void convertUnitDefinitions() {
        InputStream inputStream = Resources.getResourceAsStream("BIOMD0000000184.xml");

        SBMLReader reader = new SBMLReader();
        logger.info("Parsing SBML...");
        SBMLDocument document;
        try {
            document = reader.readSBMLFromStream(inputStream);
        } catch (XMLStreamException e) {
            throw new IllegalConversionException("Unable to read SBML file.");
        }

        SBMLUnitConverter.convert(document.getModel().getListOfUnitDefinitions());
        UnitCache.getAll().forEach((k, v) -> System.out.println(k + ": " + v));
        UnitRegistry.getDefaultUnits().forEach((k, v) -> System.out.println(k + ": " + v));
    }

}