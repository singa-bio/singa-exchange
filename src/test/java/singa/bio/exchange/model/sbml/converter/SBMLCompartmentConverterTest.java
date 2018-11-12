package singa.bio.exchange.model.sbml.converter;

import bio.singa.core.utility.Resources;
import bio.singa.simulation.model.parameters.ParameterStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.sections.RegionDataset;
import singa.bio.exchange.model.sections.SubsectionDataset;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

/**
 * @author cl
 */
public class SBMLCompartmentConverterTest {


    private static final Logger logger = LoggerFactory.getLogger(SBMLSpeciesConverterTest.class);

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

        SBMLCompartmentConverter.convert(document.getModel().getListOfCompartments());
        try {
            String regions = RegionDataset.fromCache().toJson();
            System.out.println(regions);
            System.out.println();
            String subsections = SubsectionDataset.fromCache().toJson();
            System.out.println(subsections);
            System.out.println();
            ParameterStorage.getAll().forEach((k, v) -> System.out.println(k + ": " + v.getQuantity()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

}