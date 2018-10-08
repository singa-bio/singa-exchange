package singa.bio.exchange.model.sbml;

import bio.singa.features.parameters.Environment;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import java.util.ArrayList;

import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class SBMLParserTest {

    private static final Logger logger = LoggerFactory.getLogger(SBMLParserTest.class);

    @Test
    @Ignore
    public static Simulation shouldInitializeFromSBML() {

        // setup time step size
        logger.debug("Adjusting time step size ... ");
        Environment.setTimeStep(Quantities.getQuantity(1.0, SECOND));

        // setup simulation
        Simulation simulation = new Simulation();
        // BIOMD0000000023
        // BIOMD0000000064
        // BIOMD0000000184 for ca oscillations

        logger.info("Setting up simulation for model BIOMD0000000184 ...");
        SBMLParser model = BioModelsParserService.parseModelById("BIOMD0000000184");

        logger.debug("Setting up example graph ...");
        // setup graph with a single node
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        CellRegion region = new CellRegion("Default");
        model.getCompartments().keySet().forEach(subsection -> region.addSubSection(INNER, subsection));
        graph.getNodes().forEach(node -> node.setCellRegion(region));

        // initialize species in graph with desired concentration
        logger.debug("Initializing starting concentrations of species and node states in graph ...");
        AutomatonNode bioNode = graph.getNodes().iterator().next();
        model.getStartingConcentrations().forEach((entity, value) -> {
            logger.debug("Initialized concentration of {} to {}.", entity.getIdentifier(), value);
            bioNode.getConcentrationContainer().set(INNER, entity, value);
        });

        // add graph
        simulation.setGraph(graph);
        // add reaction to the reactions used in the simulations
        model.getReactions().forEach(reaction -> reaction.setSimulation(simulation));
        simulation.getModules().addAll(model.getReactions());
        // add, sort and apply assignment rules
        simulation.setAssignmentRules(new ArrayList<>(model.getAssignmentRules()));
        simulation.applyAssignmentRules();

        return simulation;
    }

}