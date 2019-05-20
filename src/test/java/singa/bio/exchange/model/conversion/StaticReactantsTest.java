package singa.bio.exchange.model.conversion;

import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneTracer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import bio.singa.simulation.model.sections.concentration.SectionConcentration;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import singa.bio.exchange.model.Converter;
import singa.bio.exchange.model.SimulationRepresentation;
import tech.units.indriya.quantity.Quantities;

import java.io.IOException;
import java.util.List;

import static bio.singa.features.units.UnitProvider.NANO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static bio.singa.simulation.model.sections.CellTopology.OUTER;
import static singa.bio.exchange.model.conversion.Constants.BUSH2016;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class StaticReactantsTest {

    @Test
    @DisplayName("conversion - reactions, static reactions, section concentrations, complex entities")
    void test() {
        EntitySupplier entities = new EntitySupplier();
        RegionSupplier regions = new RegionSupplier();

        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.0001, SECOND));

        // reactome https://reactome.org/PathwayBrowser/#/R-HSA-432040&SEL=R-HSA-432197&FLG=O14610)
        // biomodels yeast carrousel https://www.ebi.ac.uk/biomodels-main/BIOMD0000000637

        // parameters
        RateConstant kOnR_G = RateConstant.create(4.6111e-3)
                .forward().secondOrder()
                .concentrationUnit(NANO_MOLE_PER_LITRE).timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        RateConstant kOffR_G = RateConstant.create(0.1)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        RateConstant kEf_Gd = RateConstant.create(6.2e-4)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        RateConstant kEf_RG = RateConstant.create(6.2e-4)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        RateConstant kHf_Gt = RateConstant.create(2.0e-3)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        RateConstant kAf_Gd = RateConstant.create(0.2158)
                .forward().secondOrder()
                .concentrationUnit(NANO_MOLE_PER_LITRE).timeUnit(SECOND)
                .evidence(BUSH2016).build();

        RateConstant kAr_Gd = RateConstant.create(1.3e-3)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        RateConstant kEf_LRGd = RateConstant.create(1.5)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        RateConstant kEf_LRG = RateConstant.create(1.5)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        RateConstant kHf_LRGt = RateConstant.create(0.11)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        // backwards rate constant is effectively zero
        RateConstant kEf_zero = RateConstant.create(0.0)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        // transformed from static receptor parameter in original model
        RateConstant kOn_LR = RateConstant.create(1.7857e-4)
                .forward().secondOrder()
                .concentrationUnit(NANO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        RateConstant kOff_LR = RateConstant.create(0.001)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .evidence(BUSH2016)
                .build();

        // modules
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.avp, OUTER)
                .addSubstrate(entities.v2r, MEMBRANE)
                .addProduct(entities.v2rAvp, MEMBRANE)
                .complexBuilding()
                .associationRate(kOn_LR)
                .dissociationRate(kOff_LR)
                .identifier("receptor activation: V2R AVP binding")
                .build();


        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gAd, MEMBRANE)
                .addSubstrate(entities.v2r, MEMBRANE)
                .addProduct(entities.v2rgad, MEMBRANE)
                .complexBuilding()
                .associationRate(kOnR_G)
                .dissociationRate(kOffR_G)
                .identifier("receptor activation: V2R G(A)GDP binding")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gAt, MEMBRANE)
                .addSubstrate(entities.v2r, MEMBRANE)
                .addProduct(entities.v2rgat, MEMBRANE)
                .complexBuilding()
                .associationRate(kOnR_G)
                .dissociationRate(kOffR_G)
                .identifier("receptor activation: V2R G(A)GTP binding")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gABGd, MEMBRANE)
                .addSubstrate(entities.v2r, MEMBRANE)
                .addProduct(entities.v2rgabgd, MEMBRANE)
                .complexBuilding()
                .associationRate(kOnR_G)
                .dissociationRate(kOffR_G)
                .identifier("receptor activation: V2R G(ABG)GDP binding")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gAd, MEMBRANE)
                .addSubstrate(entities.v2rAvp, MEMBRANE)
                .addProduct(entities.v2ravpgad, MEMBRANE)
                .complexBuilding()
                .associationRate(kOnR_G)
                .dissociationRate(kOffR_G)
                .identifier("receptor activation: V2R:AVP G(A)GDP binding")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gAt, MEMBRANE)
                .addSubstrate(entities.v2rAvp, MEMBRANE)
                .addProduct(entities.v2ravpgat, MEMBRANE)
                .complexBuilding()
                .associationRate(kOnR_G)
                .dissociationRate(kOffR_G)
                .identifier("receptor activation: V2R:AVP G(A)GTP binding")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gABGd, MEMBRANE)
                .addSubstrate(entities.v2rAvp, MEMBRANE)
                .addProduct(entities.v2ravpgabgd, MEMBRANE)
                .complexBuilding()
                .associationRate(kOnR_G)
                .dissociationRate(kOffR_G)
                .identifier("receptor activation: V2R:AVP G(ABG)GDP binding")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gABGd, MEMBRANE)
                .addProduct(entities.gAt, MEMBRANE)
                .addProduct(entities.gBG, MEMBRANE)
                .irreversible()
                .rate(kEf_Gd)
                .identifier("receptor activation: G(ABG)GDP split and GDP exchange")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gAt, MEMBRANE)
                .addProduct(entities.gAd, MEMBRANE)
                .reversible()
                .forwardReactionRate(kEf_Gd)
                .backwardReactionRate(kHf_Gt)
                .identifier("receptor activation: G(A)GDP hydrolysis")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gAd, MEMBRANE)
                .addSubstrate(entities.gBG, MEMBRANE)
                .addProduct(entities.gABGd, MEMBRANE)
                .complexBuilding()
                .associationRate(kAf_Gd)
                .dissociationRate(kAr_Gd)
                .identifier("receptor activation: G(A)GDP G(BG) binding")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.v2ravpgad, MEMBRANE)
                .addProduct(entities.v2ravpgat, MEMBRANE)
                .reversible()
                .forwardReactionRate(kEf_LRGd)
                .backwardReactionRate(kHf_LRGt)
                .identifier("receptor activation: V2R:AVP:G(A)GDP hydrolysis")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gBG, MEMBRANE)
                .addSubstrate(entities.v2ravpgat, MEMBRANE)
                .addProduct(entities.v2ravpgabgd, MEMBRANE)
                .irreversible()
                .rate(kEf_LRG)
                .identifier("receptor activation: V2R:AVP:G(ABG)GDP GDP exchange")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gBG, MEMBRANE)
                .addSubstrate(entities.v2ravpgad, MEMBRANE)
                .addProduct(entities.v2ravpgabgd, MEMBRANE)
                .complexBuilding()
                .associationRate(kAf_Gd)
                .dissociationRate(kAr_Gd)
                .identifier("receptor activation: V2R:AVP:G(A)GDP G(BG) binding")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.v2rgad, MEMBRANE)
                .addProduct(entities.v2rgat, MEMBRANE)
                .reversible()
                .forwardReactionRate(kEf_Gd)// FIXME kEf_LRGd ?
                .backwardReactionRate(kHf_LRGt)// FIXME kHf_Gt ?
                .identifier("receptor activation: V2R:G(A)GDP hydrolysis")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gBG, MEMBRANE)
                .addSubstrate(entities.v2rgat, MEMBRANE)
                .addProduct(entities.v2rgabgd, MEMBRANE)
                .complexBuilding()// FIXME could be irreversible
                .associationRate(kEf_zero)
                .dissociationRate(kEf_RG)
                .identifier("receptor activation: V2R:G(ABG)GDP GDP exchange")
                .build();


        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.gBG, MEMBRANE)
                .addSubstrate(entities.v2rgad, MEMBRANE)
                .addProduct(entities.v2rgabgd, MEMBRANE)
                .complexBuilding()
                .associationRate(kAf_Gd)
                .dissociationRate(kAr_Gd)
                .identifier("receptor activation: V2R:G(A)GDP G(BG) binding")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.avp, OUTER)
                .addSubstrate(entities.v2rgad, MEMBRANE)
                .addProduct(entities.v2ravpgad, MEMBRANE)
                .complexBuilding()
                .associationRate(kOn_LR)
                .dissociationRate(kOff_LR)
                .identifier("receptor activation: V2R:G(A)GDP ligand binding")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.avp, OUTER)
                .addSubstrate(entities.v2rgabgd, MEMBRANE)
                .addProduct(entities.v2ravpgabgd, MEMBRANE)
                .complexBuilding()
                .associationRate(kOn_LR)
                .dissociationRate(kOff_LR)
                .identifier("receptor activation: V2R:G(ABG)GDP ligand binding")
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(entities.avp, OUTER)
                .addSubstrate(entities.v2rgat, MEMBRANE)
                .addProduct(entities.v2ravpgat, MEMBRANE)
                .complexBuilding()
                .associationRate(kOn_LR)
                .dissociationRate(kOff_LR)
                .identifier("receptor activation: V2R:G(A)GTP ligand binding")
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
        ci.addInitialConcentration(new SectionConcentration(regions.interstitium, entities.avp, Quantities.getQuantity(1000, NANO_MOLE_PER_LITRE)));
        ci.addInitialConcentration(new SectionConcentration(regions.basolateralMembrane, entities.v2r, Quantities.getQuantity(843, NANO_MOLE_PER_LITRE)));
        ci.addInitialConcentration(new SectionConcentration(regions.basolateralMembrane, entities.gABGd, Quantities.getQuantity(520, NANO_MOLE_PER_LITRE)));
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