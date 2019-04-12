package singa.bio.exchange.model.conversion;

import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneTracer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.modules.concentration.imlementations.transport.Diffusion;
import bio.singa.simulation.model.modules.concentration.imlementations.transport.MembraneDiffusion;
import bio.singa.simulation.model.modules.concentration.imlementations.transport.SingleFileChannelMembraneTransport;
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
import javax.measure.quantity.Length;
import java.io.IOException;
import java.util.List;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static singa.bio.exchange.model.conversion.Constants.BOUNDING_BOX;
import static singa.bio.exchange.model.conversion.Constants.YANG1997;
import static tec.units.indriya.unit.MetricPrefix.MICRO;
import static tec.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
public class ConcentrationBasedTest {

    @Test
    @DisplayName("conversion - concentration based modules, linear membrane creation")
    void test() {

        EntitySupplier entities = new EntitySupplier();
        RegionSupplier regions = new RegionSupplier();

        // setup simulation
        Simulation simulation = new Simulation();

        // set system parameters
        double simulationExtend = 800;
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(25, MICRO(METRE));
        Environment.setSimulationExtend(simulationExtend);
        Environment.setSystemExtend(systemExtend);
        // setup graph
        AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(25, 1, BOUNDING_BOX));
        // set graph
        simulation.setGraph(graph);

        // assign lumen nodes
        graph.getNodes().stream()
                .filter(node -> node.getIdentifier().getColumn() < 5)
                .forEach(node -> node.setCellRegion(regions.lumenRegion));
        // assign luminal membrane
        graph.getNodes().stream()
                .filter(node -> node.getIdentifier().getColumn() == 5)
                .forEach(node -> node.setCellRegion(regions.apicalMembraneRegion));
        // assign cytoplasma
        graph.getNodes().stream()
                .filter(node -> node.getIdentifier().getColumn() > 5 && node.getIdentifier().getColumn() < 19)
                .forEach(node -> node.setCellRegion(regions.cytoplasmRegion));
        // assign basolateral membrane
        graph.getNodes().stream()
                .filter(node -> node.getIdentifier().getColumn() == 19)
                .forEach(node -> node.setCellRegion(regions.basolateralMembraneRegion));
        // assign interstitium nodes
        graph.getNodes().stream()
                .filter(node -> node.getIdentifier().getColumn() > 19)
                .forEach(node -> node.setCellRegion(regions.interstitiumRegion));

        // initialize membranes
        MembraneLayer membraneLayer = new MembraneLayer();
        List<Membrane> membranes = MembraneTracer.regionsToMembrane(graph);
        membraneLayer.addMembranes(membranes);
        simulation.setMembraneLayer(membraneLayer);

        ConcentrationInitializer initializer = new ConcentrationInitializer();
        ComparableQuantity<Area> area = Quantities.getQuantity(1, MICRO(METRE).pow(2)).asType(Area.class);
        initializer.addInitialConcentration(new SectionConcentration(regions.lumen, entities.water, Quantities.getQuantity(53.61, MOLE_PER_LITRE)));
        initializer.addInitialConcentration(new SectionConcentration(regions.lumen, entities.solute, Quantities.getQuantity(0.3, MOLE_PER_LITRE)));
        initializer.addInitialConcentration(new MembraneConcentration(regions.apicalMembraneRegion, entities.aqp2, area, 3700, YANG1997));
        initializer.addInitialConcentration(new SectionConcentration(regions.cytoplasm, entities.water, Quantities.getQuantity(47.22, MOLE_PER_LITRE)));
        initializer.addInitialConcentration(new SectionConcentration(regions.cytoplasm, entities.solute, Quantities.getQuantity(0.4, MOLE_PER_LITRE)));
        initializer.addInitialConcentration(new MembraneConcentration(regions.basolateralMembraneRegion, entities.aqp3, area, 5900, YANG1997));
        initializer.addInitialConcentration(new MembraneConcentration(regions.basolateralMembraneRegion, entities.aqp4, area, 6100, YANG1997));
        initializer.addInitialConcentration(new SectionConcentration(regions.interstitium, entities.water, Quantities.getQuantity(53.11, MOLE_PER_LITRE)));
        initializer.addInitialConcentration(new SectionConcentration(regions.interstitium, entities.solute, Quantities.getQuantity(0.5, MOLE_PER_LITRE)));
        simulation.setConcentrationInitializer(initializer);
        // sources
        graph.getNode(0, 0).getConcentrationManager().setConcentrationFixed(true);
        // sinks
        graph.getNode(24, 0).getConcentrationManager().setConcentrationFixed(true);

        // free diffusion
        Diffusion.inSimulation(simulation)
                .onlyFor(entities.water)
                .build();
        // membrane diffusion
        MembraneDiffusion.inSimulation(simulation)
                .cargo(entities.water)
                .build();
        // single file channel membrane transport
        SingleFileChannelMembraneTransport.inSimulation(simulation)
                .transporter(entities.aqp2)
                .cargo(entities.water)
                .forSolute(entities.solute)
                .build();

        SingleFileChannelMembraneTransport.inSimulation(simulation)
                .transporter(entities.aqp3)
                .cargo(entities.water)
                .forSolute(entities.solute)
                .build();
        SingleFileChannelMembraneTransport.inSimulation(simulation)
                .transporter(entities.aqp4)
                .cargo(entities.water)
                .forSolute(entities.solute)
                .build();

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
