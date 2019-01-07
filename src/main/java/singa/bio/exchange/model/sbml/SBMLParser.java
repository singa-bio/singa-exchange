package singa.bio.exchange.model.sbml;

import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.rules.AssignmentRule;
import bio.singa.simulation.model.simulation.Simulation;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.sbml.converter.*;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class SBMLParser {

    private static final Logger logger = LoggerFactory.getLogger(SBMLParser.class);

    public static final Evidence DEFAULT_SBML_ORIGIN = new Evidence(Evidence.SourceType.DATABASE,
            "SBML", "Parsed from SBML file.");

    private SBMLDocument document;
    public static Simulation current;

    // assignment rules
    private final List<AssignmentRule> assignmentRules;

    public SBMLParser(InputStream inputStream) {
        assignmentRules = new ArrayList<>();
        initializeDocument(inputStream);
    }

    private void initializeDocument(InputStream inputStream) {
        SBMLReader reader = new SBMLReader();
        current = new Simulation();
        logger.info("Parsing SBML...");
        try {
            document = reader.readSBMLFromStream(inputStream);
        } catch (XMLStreamException e) {
            logger.error("Could not read SBML File.");
            e.printStackTrace();
        }
    }

    public List<AssignmentRule> getAssignmentRules() {
        return assignmentRules;
    }

    public void parse() {
        Model model = document.getModel();
        // parseUnits();
        SBMLUnitConverter.convert(model.getListOfUnitDefinitions());
        // parseGlobalParameters();
        SBMLParameterConverter.convert(model.getListOfParameters());
        // parseCompartments();
        SBMLCompartmentConverter.convert(model.getListOfCompartments());
        // parseFunctions();
        SBMLFunctionConverter.convert(model.getListOfFunctionDefinitions());
        // parseSpecies(); parseStartingConcentrations();
        SBMLSpeciesConverter.convert(model.getListOfSpecies());
        // parseReactions();
        SBMLReactionConverter.convert(model.getListOfReactions());
        //parseAssignmentRules();
    }


    private void parseAssignmentRules() {
        logger.info("Parsing Assignment Rules ...");
//        SBMLAssignmentRuleConverter converter = new SBMLAssignmentRuleConverter(units, entities, functions, globalParameters);
//        document.getModel().getListOfRules().forEach(rule -> {
//            if (rule.isAssignment()) {
//                assignmentRules.add(converter.convertAssignmentRule((org.sbml.jsbml.AssignmentRule) rule));
//            }
//        });
    }

}
