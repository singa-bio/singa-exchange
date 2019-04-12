package singa.bio.exchange.model.conversion;

import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.model.Evidence;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneTracer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import bio.singa.simulation.model.sections.concentration.MembraneConcentration;
import bio.singa.simulation.model.sections.concentration.SectionConcentration;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import singa.bio.exchange.model.Converter;
import singa.bio.exchange.model.SimulationRepresentation;
import tec.units.indriya.ComparableQuantity;
import tec.units.indriya.quantity.Quantities;

import javax.measure.quantity.Area;
import java.io.IOException;
import java.util.List;

import static bio.singa.features.model.Evidence.SourceType.PREDICTION;
import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.*;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static tec.units.indriya.unit.MetricPrefix.MICRO;
import static tec.units.indriya.unit.Units.*;

/**
 * @author cl
 */
public class DynamicKineticsTest {

    @Test
    @DisplayName("conversion - reaction, dynamic kinetic law, membrane concentrations")
    void test() {
        EntitySupplier entities = new EntitySupplier();
        RegionSupplier regions = new RegionSupplier();

        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.001, SECOND));

        RateConstant scaling = RateConstant.create(1.0)
                .forward().firstOrder()
                .timeUnit(MINUTE)
                .evidence(new Evidence(PREDICTION, "Unit Scaling", "Scales the velocity to the corresponding unit"))
                .build();

        MichaelisConstant km = new MichaelisConstant(Quantities.getQuantity(20, MICRO_MOLE_PER_LITRE), Constants.CHEN2005);

        ReactionBuilder.staticReactants(simulation)
                // derived from v = kCat*E*S/(kM+s), where kCat has is a pseudo first order rate derived from regression
                // the scaling parameter determines the unit of the equation
                .kineticLaw("(1/((p01/cATPuM)^p02+(p03/cGAuM)^p04))*us*cAC6*(cATP/(kM+cATP))")
                // matlab regression
                .referenceParameter("p01", 6.144e5)
                .referenceParameter("p02", 0.3063)
                .referenceParameter("p03", 1.196)
                .referenceParameter("p04", 1.153)
                .referenceParameter("us", scaling)
                .referenceParameter("kM", UnitRegistry.scale(km.getContent()).getValue().doubleValue())
                .referenceParameter("cATPuM", new Reactant(entities.atp, CATALYTIC, MICRO_MOLE_PER_LITRE))
                .referenceParameter("cGAuM", new Reactant(entities.gAt, CATALYTIC, MEMBRANE, MICRO_MOLE_PER_LITRE))
                .referenceParameter("cAC6", new Reactant(entities.ac6, CATALYTIC, MEMBRANE))
                .referenceParameter("cATP", new Reactant(entities.atp, SUBSTRATE))
                .referenceParameter(new Reactant(entities.camp, PRODUCT))
                .identifier("camp synthesis: ac6 activation and reaction")
                .build();

        // create graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(regions.basolateralMembraneRegion);

        MembraneLayer membraneLayer = new MembraneLayer();
        List<Membrane> membranes = MembraneTracer.regionsToMembrane(graph);
        membraneLayer.addMembranes(membranes);
        simulation.setMembraneLayer(membraneLayer);

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ComparableQuantity<Area> area = Quantities.getQuantity(1, MICRO(METRE).pow(2)).asType(Area.class);
        // chen publication
        ci.addInitialConcentration(new SectionConcentration(regions.basolateralMembrane, entities.gAt, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE)));
        // TODO exploration to achieve ~ 25 percent depletion of original atp after 10 min
        ci.addInitialConcentration(new MembraneConcentration(regions.basolateralMembraneRegion, entities.ac6, area, 1000, Evidence.NO_EVIDENCE));
        // chen publication
        ci.addInitialConcentration(new SectionConcentration(regions.cytoplasm, entities.atp, Quantities.getQuantity(0.25, MICRO_MOLE_PER_LITRE)));
        simulation.setConcentrationInitializer(ci);

        SimulationRepresentation representation = Converter.getRepresentationFrom(simulation);
        String json = "";
        try {
            json = representation.toJson();
            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Simulation reparsedSimulation = Converter.getSimulationFrom(json);
            reparsedSimulation.nextEpoch();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
