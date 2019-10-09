package bio.singa.exchange.sbml;

import bio.singa.exchange.Converter;
import bio.singa.exchange.sections.RegionCache;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.simulation.Simulation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author cl
 */
public class SBMLParserTest {

    private static final Logger logger = LoggerFactory.getLogger(SBMLParserTest.class);

    @Test
    public void shouldInitializeFromSBML() {

        // BIOMD0000000023
        // BIOMD0000000064
        // BIOMD0000000184 for ca oscillations

        String modelIdentifier = "BIOMD0000000184";
        logger.info("Setting up simulation for model {}.", modelIdentifier);
        Simulation simulation = BioModelsParserService.parseModelById(modelIdentifier);
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);
        Collection<CellRegion> regions = RegionCache.getAll();
        if (regions.size() == 1) {
            graph.getNode(0,0).setCellRegion(regions.iterator().next());
        }
        try {
            String json = Converter.getRepresentationFrom(simulation).toJson();
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // add, sort and apply assignment rules
        // simulation.setAssignmentRules(new ArrayList<>(model.getAssignmentRules()));
        simulation.applyAssignmentRules();

    }

}