package singa.bio.exchange.model.sbml;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.simulation.model.modules.concentration.imlementations.DynamicReaction;
import bio.singa.simulation.model.parameters.SimulationParameter;
import bio.singa.simulation.model.rules.AssignmentRule;
import bio.singa.simulation.model.sections.CellSubsection;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.sbml.converter.SBMLAssignmentRuleConverter;
import singa.bio.exchange.model.sbml.converter.SBMLParameterConverter;
import singa.bio.exchange.model.sbml.converter.SBMLReactionConverter;
import tec.uom.se.AbstractUnit;
import tec.uom.se.quantity.Quantities;

import javax.measure.Unit;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class SBMLParser {

    private static final Logger logger = LoggerFactory.getLogger(SBMLParser.class);
    public static final FeatureOrigin DEFAULT_SBML_ORIGIN = new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION,
            "SBML", "Parsed from SBML file.");
    // the subsections mapped to their sizes
    private final Map<CellSubsection, Double> compartments;
    // the chemical entities
    private final Map<String, ChemicalEntity> entities;
    // their starting concentrations
    private final Map<ChemicalEntity, Double> startingConcentrations;
    // a utility map to provide species by their database identifier
    private final Map<Identifier, ChemicalEntity> entitiesByDatabaseId;
    // the functions
    private final Map<String, FunctionReference> functions;
    // assignment rules
    private final List<AssignmentRule> assignmentRules;
    private SBMLDocument document;
    // the units
    private Map<String, Unit<?>> units;
    // the reactions
    private List<DynamicReaction> reactions;
    // the global parameters
    private Map<String, SimulationParameter<?>> globalParameters;

    public SBMLParser(InputStream inputStream) {
        entities = new HashMap<>();
        entitiesByDatabaseId = new HashMap<>();
        startingConcentrations = new HashMap<>();
        reactions = new ArrayList<>();
        globalParameters = new HashMap<>();
        functions = new HashMap<>();
        assignmentRules = new ArrayList<>();
        compartments = new HashMap<>();
        initializeDocument(inputStream);
    }

    private void initializeDocument(InputStream inputStream) {
        SBMLReader reader = new SBMLReader();
        logger.info("Parsing SBML...");
        try {
            document = reader.readSBMLFromStream(inputStream);
        } catch (XMLStreamException e) {
            logger.error("Could not read SBML File.");
            e.printStackTrace();
        }
    }

    public Map<String, ChemicalEntity> getChemicalEntities() {
        return entities;
    }

    public Map<CellSubsection, Double> getCompartments() {
        return compartments;
    }

    public List<DynamicReaction> getReactions() {
        return reactions;
    }

    public Map<ChemicalEntity, Double> getStartingConcentrations() {
        return startingConcentrations;
    }

    public Map<String, SimulationParameter<?>> getGlobalParameters() {
        return globalParameters;
    }

    public List<AssignmentRule> getAssignmentRules() {
        return assignmentRules;
    }

    public void parse() {
        // parseUnits();
        parseGlobalParameters();
        parseCompartments();
        parseFunctions();
        // parseSpecies();
        parseReactions();
        parseStartingConcentrations();
        parseAssignmentRules();
    }

    private void parseCompartments() {
        logger.info("Parsing Compartments ...");
        document.getModel().getListOfCompartments().forEach(compartment -> {
            CellSubsection singaCompartment = new CellSubsection(compartment.getId());
            compartments.put(singaCompartment, compartment.getSize());
            globalParameters.put(singaCompartment.getIdentifier(),
                    new SimulationParameter<>(singaCompartment.getIdentifier(),
                            Quantities.getQuantity(compartment.getSize(), AbstractUnit.ONE)));
        });
    }

    private void parseAssignmentRules() {
        logger.info("Parsing Assignment Rules ...");
        SBMLAssignmentRuleConverter converter = new SBMLAssignmentRuleConverter(units, entities, functions, globalParameters);
        document.getModel().getListOfRules().forEach(rule -> {
            if (rule.isAssignment()) {
                assignmentRules.add(converter.convertAssignmentRule((org.sbml.jsbml.AssignmentRule) rule));
            }
        });
    }

    private void parseFunctions() {
        logger.info("Parsing Functions ...");
        document.getModel().getListOfFunctionDefinitions().forEach(function ->
                functions.put(function.getId(), new FunctionReference(function.getId(), function.getMath().toString()))
        );
    }

    private void parseReactions() {
        logger.info("Parsing Reactions ...");
        SBMLReactionConverter converter = new SBMLReactionConverter(units, entities, functions, globalParameters);
        reactions = converter.convertReactions(document.getModel().getListOfReactions());
    }

    private void parseStartingConcentrations() {
        logger.info("Parsing initial concentrations ...");
        document.getModel().getListOfSpecies().forEach(species -> {
            ChemicalEntity entity = entities.get(species.getId());
            startingConcentrations.put(entity, species.getInitialConcentration());
        });
    }

    private void parseGlobalParameters() {
        logger.info("Parsing global parameters ...");
        SBMLParameterConverter converter = new SBMLParameterConverter(units);
        globalParameters = converter.convertSimulationParameters(document.getModel().getListOfParameters());
    }

}
