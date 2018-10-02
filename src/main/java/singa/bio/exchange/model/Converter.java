package singa.bio.exchange.model;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.chemistry.entities.*;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.chemistry.features.permeability.OsmoticPermeability;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.parameters.Environment;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.membranes.Membrane;
import bio.singa.simulation.model.agents.membranes.MembraneLayer;
import bio.singa.simulation.model.agents.membranes.MembraneTracer;
import bio.singa.simulation.model.agents.organelles.OrganelleTypes;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.imlementations.*;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.reactants.ReactantRole;
import bio.singa.simulation.model.modules.displacement.implementations.EndocytosisActinBoost;
import bio.singa.simulation.model.modules.displacement.implementations.VesicleDiffusion;
import bio.singa.simulation.model.modules.displacement.implementations.VesicleTransport;
import bio.singa.simulation.model.modules.qualitative.implementations.ClathrinMediatedEndocytosis;
import bio.singa.simulation.model.modules.qualitative.implementations.VesicleAttachment;
import bio.singa.simulation.model.modules.qualitative.implementations.VesicleFusion;
import bio.singa.simulation.model.sections.*;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.parser.organelles.OrganelleTemplate;
import bio.singa.structure.features.molarmass.MolarMass;
import com.fasterxml.jackson.databind.ObjectMapper;
import singa.bio.exchange.model.entities.EntityDataset;
import singa.bio.exchange.model.entities.EntityRepresentation;
import singa.bio.exchange.model.graphs.GraphRepresentation;
import singa.bio.exchange.model.macroscopic.MembraneDataset;
import singa.bio.exchange.model.macroscopic.MembraneRepresentation;
import singa.bio.exchange.model.modules.ModuleDataset;
import singa.bio.exchange.model.modules.ModuleRepresentation;
import singa.bio.exchange.model.origins.OriginDataset;
import singa.bio.exchange.model.sections.InitialConcentrationDataset;
import singa.bio.exchange.model.sections.InitialConcentrationRepresentation;
import singa.bio.exchange.model.sections.RegionDataset;
import singa.bio.exchange.model.sections.SubsectionDataset;
import singa.bio.exchange.model.units.UnitJacksonModule;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static bio.singa.chemistry.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
import static bio.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.features.units.UnitProvider.NANO_MOLE_PER_LITRE;
import static bio.singa.simulation.features.DefaultFeatureSources.BINESH2015;
import static bio.singa.simulation.model.sections.CellTopology.*;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.MINUTE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class Converter {

    public static Simulation current;

    static final FeatureOrigin BUSH2016 = new FeatureOrigin(FeatureOrigin.OriginType.LITERATURE, "Bush 2016", "Bush, Alan, et al. \"Yeast GPCR signaling reflects the fraction of occupied receptors, not the number.\" Molecular systems biology 12.12 (2016): 898.");
    static final FeatureOrigin PUTNAM1971 = new FeatureOrigin(FeatureOrigin.OriginType.LITERATURE, "Putnam 1971", "Putnam, David F. \"Composition and concentrative properties of human urine.\" (1971).");
    static final FeatureOrigin TOFTS2000 = new FeatureOrigin(FeatureOrigin.OriginType.LITERATURE, "Tofts 2000", "Tofts, P. S., et al. \"Test liquids for quantitative MRI measurements of self‐diffusion coefficient in vivo.\" Magnetic resonance in medicine 43.3 (2000): 368-374.");
    static final FeatureOrigin HAINES1994 = new FeatureOrigin(FeatureOrigin.OriginType.LITERATURE, "Haines 1994", "Haines, Thomas H. \"Water transport across biological membranes.\" FEBS letters 346.1 (1994): 115-122.");
    static final FeatureOrigin CHEN2005 = new FeatureOrigin(FeatureOrigin.OriginType.LITERATURE, "Chen-Goodspeed 2005", "Chen-Goodspeed, Misty, Abolanle N. Lukan, and Carmen W. Dessauer. \"Modeling of Gαs and Gαi regulation of human type V and VI adenylyl cyclase.\" Journal of Biological Chemistry 280.3 (2005): 1808-1816.");

    public static EntityDataset getEntityDatasetFrom(Simulation simulation) {
        EntityDataset dataset = new EntityDataset();
        List<ChemicalEntity> sortedEntities = ChemicalEntities.sortByComplexDependencies(new ArrayList<>(simulation.getAllChemicalEntities()));
        for (ChemicalEntity chemicalEntity : sortedEntities) {
            dataset.addEntity(EntityRepresentation.of(chemicalEntity));
        }
        return dataset;
    }

    public static ModuleDataset getModuleDatasetFrom(Simulation simulation) {
        ModuleDataset dataset = new ModuleDataset();
        for (UpdateModule module : simulation.getModules()) {
            dataset.addModule(ModuleRepresentation.of(module));
        }
        return dataset;
    }

    public static GraphRepresentation getGraphFrom(Simulation simulation) {
        return GraphRepresentation.of(simulation.getGraph());
    }

    public static MembraneDataset getMembranesFrom(Simulation simulation) {
        MembraneDataset dataset = new MembraneDataset();
        if (simulation.getMembraneLayer() != null) {
            for (Membrane membrane : simulation.getMembraneLayer().getMembranes()) {
                dataset.addMembrane(MembraneRepresentation.of(membrane));
            }
        }
        return dataset;
    }

    public static InitialConcentrationDataset getConcentrationsFrom(Simulation simulation) {
        InitialConcentrationDataset dataset = new InitialConcentrationDataset();
        for (InitialConcentration initialConcentration : simulation.getConcentrationInitializer().getInitialConcentrations()) {
            dataset.addConcentration(InitialConcentrationRepresentation.of(initialConcentration));
        }
        return dataset;
    }

    public static SimulationRepresentation getSimulationFrom(Simulation simulation) {
        ModuleDataset moduleDataset = getModuleDatasetFrom(simulation);
        EntityDataset entityDataset = getEntityDatasetFrom(simulation);
        GraphRepresentation graph = getGraphFrom(simulation);
        MembraneDataset membranes = getMembranesFrom(simulation);
        InitialConcentrationDataset concentrations = getConcentrationsFrom(simulation);

        SimulationRepresentation representation = new SimulationRepresentation();
        representation.setMetadata(Metadata.forSinga());
        representation.setEntities(entityDataset);
        representation.setModules(moduleDataset);
        representation.setGraph(graph);
        representation.setMembranes(membranes);
        representation.setOrigins(OriginDataset.fromCache());
        representation.setSubsections(SubsectionDataset.fromCache());
        representation.setRegions(RegionDataset.fromCache());
        representation.setEnvironment(EnvironmentRepresentation.fromSingleton());
        representation.setConcentrations(concentrations);
        return representation;
    }

    public static Simulation getSimulationFrom(String json) throws IOException {
        current = new Simulation();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new UnitJacksonModule());
        return SimulationRepresentation.to(mapper.readValue(json, SimulationRepresentation.class));
    }

    public static void main(String[] args) {

        Simulation simulation = new Simulation();

        double simulationExtend = 800;
        int nodesHorizontal = 22;
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(22, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, nodesHorizontal);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(nodesHorizontal, nodesHorizontal);
        simulation.setGraph(graph);

        // setup spatial representations
        simulation.initializeSpatialRepresentations();
        MembraneLayer membraneLayer = new MembraneLayer();
        simulation.setMembraneLayer(membraneLayer);

        // initialize extracellular space as default
        for (AutomatonNode automatonNode : graph.getNodes()) {
            automatonNode.setCellRegion(CellRegions.EXTRACELLULAR_REGION);
        }

        // initialize cell membrane and nucleus
        OrganelleTemplate cell = OrganelleTypes.CELL.create();
        // blue is basolateral
        int green = java.awt.Color.GREEN.getRGB();
        cell.initializeGroup(green, "basolateral plasma membrane", "GO:0016323");
        // red is the default region
        int red = java.awt.Color.RED.getRGB();
        cell.initializeGroup(red, cell.getMembraneRegion());
        // green is the
        int blue = java.awt.Color.BLUE.getRGB();
        cell.initializeGroup(blue, "apical plasma membrane", "GO:0016324");
        Membrane cellMembrane = MembraneTracer.membraneToRegion(cell, graph);
        membraneLayer.addMembrane(cellMembrane);

        OrganelleTemplate nucleus = OrganelleTypes.NUCLEUS.create();
        Membrane nuclearMembrane = MembraneTracer.membraneToRegion(nucleus, graph);
        membraneLayer.addMembrane(nuclearMembrane);

        // reactome https://reactome.org/PathwayBrowser/#/R-HSA-432040&SEL=R-HSA-432197&FLG=O14610)
        // biomodels yeast carrousel https://www.ebi.ac.uk/biomodels-main/BIOMD0000000637

        // parameters
        RateConstant kOnR_G = RateConstant.create(4.6111e-3)
                .forward().secondOrder()
                .concentrationUnit(NANO_MOLE_PER_LITRE).timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        RateConstant kOffR_G = RateConstant.create(0.1)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        RateConstant kEf_Gd = RateConstant.create(6.2e-4)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        RateConstant kEf_RG = RateConstant.create(6.2e-4)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        RateConstant kHf_Gt = RateConstant.create(2.0e-3)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        RateConstant kAf_Gd = RateConstant.create(0.2158)
                .forward().secondOrder()
                .concentrationUnit(NANO_MOLE_PER_LITRE).timeUnit(SECOND)
                .origin(BUSH2016).build();


        RateConstant kAr_Gd = RateConstant.create(1.3e-3)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        RateConstant kEf_LRGd = RateConstant.create(1.5)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        RateConstant kEf_LRG = RateConstant.create(1.5)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        RateConstant kHf_LRGt = RateConstant.create(0.11)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        // backwards rate constant is effectively zero
        RateConstant kEf_zero = RateConstant.create(0.0)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        // transformed getEntityDatasetFrom static receptor parameter in original model
        RateConstant kOn_LR = RateConstant.create(1.7857e-4)
                .forward().secondOrder()
                .concentrationUnit(NANO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        RateConstant kOff_LR = RateConstant.create(0.001)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .origin(BUSH2016)
                .build();

        // entities
        // vasopressin v2 receptor
        Receptor vasopressinReceptor = new Receptor.Builder("V2R")
                .additionalIdentifier(new UniProtIdentifier("P30518"))
                .build();

        // vasopressinReceptor.setFeature(MolarMass.class);

        // vasopressin
        ChemicalEntity vasopressin = new SmallMolecule.Builder("AVP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:34543"))
                .build();

        // g-protein subunits
        Protein gProteinAlpha = new Protein.Builder("G(A)")
                .additionalIdentifier(new UniProtIdentifier("P63092"))
                .build();

        Protein gProteinBeta = new Protein.Builder("G(B)")
                .additionalIdentifier(new UniProtIdentifier("P62873"))
                .build();

        Protein gProteinGamma = new Protein.Builder("G(G)")
                .additionalIdentifier(new UniProtIdentifier("P63211"))
                .build();

        // g-protein substrates
        ChemicalEntity gdp = new SmallMolecule.Builder("GDP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();

        ChemicalEntity gtp = new SmallMolecule.Builder("GTP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();

        // complexed entities
        // g-protein complexes
        // free - beta gamma complex
        ComplexedChemicalEntity gProteinBetaGamma = new ComplexedChemicalEntity.Builder("G(BG)")
                .addAssociatedPart(gProteinBeta)
                .addAssociatedPart(gProteinGamma)
                .setMembraneAnchored(true)
                .build();

        // free - alpha beta gamma complex
        ComplexedChemicalEntity gProteinAlphaBetaGamma = new ComplexedChemicalEntity.Builder("G(ABG)")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gProteinBetaGamma)
                .setMembraneAnchored(true)
                .build();

        // gdp bound - alpha beta gamma complex
        ComplexedChemicalEntity gdpGProteinAlphaBetaGamma = new ComplexedChemicalEntity.Builder("G(ABG):GDP")
                .addAssociatedPart(gProteinAlphaBetaGamma)
                .addAssociatedPart(gdp)
                .setMembraneAnchored(true)
                .build();

        // gdp bound - alpha complex
        ComplexedChemicalEntity gdpGProteinAlpha = new ComplexedChemicalEntity.Builder("G(A):GDP")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gdp)
                .setMembraneAnchored(true)
                .build();

        // gtp bound - alpha complex
        ComplexedChemicalEntity gtpGProteinAlpha = new ComplexedChemicalEntity.Builder("G(A):GTP")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gtp)
                .setMembraneAnchored(true)
                .build();

        // receptor - ligand
        ComplexedChemicalEntity receptorLigandComplex = new ComplexedChemicalEntity.Builder("V2R:AVP")
                .addAssociatedPart(vasopressinReceptor)
                .addAssociatedPart(vasopressin)
                .build();

        // modules
        // binding R.Gd
        ComplexBuildingReaction binding01 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 01")
                .of(gdpGProteinAlpha, kOnR_G)
                .in(INNER)
                .by(vasopressinReceptor, kOffR_G)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGDPReceptor = binding01.getComplex();

        // binding R.Gt
        ComplexBuildingReaction binding02 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 02")
                .of(gtpGProteinAlpha, kOnR_G)
                .in(INNER)
                .by(vasopressinReceptor, kOffR_G)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGTPReceptor = binding02.getComplex();

        // binding R.G
        ComplexBuildingReaction binding03 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 03")
                .of(gdpGProteinAlphaBetaGamma, kOnR_G)
                .in(INNER)
                .by(vasopressinReceptor, kOffR_G)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaBetaGammaGDPReceptor = binding03.getComplex();

        // binding L.R
        ComplexBuildingReaction binding04 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 04")
                .of(vasopressin, kOn_LR)
                .in(OUTER)
                .by(vasopressinReceptor, kOff_LR)
                .to(MEMBRANE)
                .formingComplex(receptorLigandComplex)
                .build();

        // binding LR.Gd
        ComplexBuildingReaction binding05 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 05")
                .of(gdpGProteinAlpha, kOnR_G)
                .in(INNER)
                .by(receptorLigandComplex, kOffR_G)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGDPReceptorLigandComplex = binding05.getComplex();

        // binding LR.Gt
        ComplexBuildingReaction binding06 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 06")
                .of(gtpGProteinAlpha, kOnR_G)
                .in(INNER)
                .by(receptorLigandComplex, kOffR_G)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGTPReceptorLigandComplex = binding06.getComplex();

        // binding LR.G
        ComplexBuildingReaction binding07 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 07")
                .of(gdpGProteinAlphaBetaGamma, kOnR_G)
                .in(INNER)
                .by(receptorLigandComplex, kOffR_G)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaBetaGammaGDPReceptorLigandComplex = binding07.getComplex();

        // exchange G
        NthOrderReaction reaction08 = NthOrderReaction.inSimulation(simulation)
                .identifier("reaction 08")
                .addSubstrate(gdpGProteinAlphaBetaGamma)
                .addProduct(gtpGProteinAlpha)
                .addProduct(gProteinBetaGamma)
                .rateConstant(kEf_Gd)
                .build();

        // exchange Gd, hydrolysis Gt
        ReversibleReaction reaction09 = ReversibleReaction.inSimulation(simulation)
                .identifier("reaction 09")
                .addSubstrate(gtpGProteinAlpha)
                .addProduct(gdpGProteinAlpha)
                .forwardsRateConstant(kEf_Gd)
                .backwardsRateConstant(kHf_Gt)
                .build();

        // association Gd
        ComplexBuildingReaction binding10 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 10")
                .of(gdpGProteinAlpha, kAf_Gd)
                .in(INNER)
                .by(gProteinBetaGamma, kAr_Gd)
                .to(INNER)
                .formingComplex(gdpGProteinAlphaBetaGamma)
                .build();

        // hydrolysis LR.Gt, exchange LR.Gd
        ReversibleReaction reaction11 = ReversibleReaction.inSimulation(simulation)
                .identifier("reaction 11")
                .addSubstrate(alphaGDPReceptorLigandComplex)
                .addProduct(alphaGTPReceptorLigandComplex)
                .forwardsRateConstant(kEf_LRGd)
                .backwardsRateConstant(kHf_LRGt)
                .build();

        // exchange LRG
        ComplexBuildingReaction binding12 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 12")
                .of(gProteinBetaGamma, kEf_zero)
                .in(INNER)
                .by(alphaGDPReceptorLigandComplex, kEf_LRG)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptorLigandComplex)
                .build();

        // association LRGd
        ComplexBuildingReaction binding13 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 13")
                .of(gProteinBetaGamma, kAf_Gd)
                .in(INNER)
                .by(alphaGDPReceptorLigandComplex, kAr_Gd)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptorLigandComplex)
                .build();

        // hydrolysis R.Gt, exchange R.Gd
        ReversibleReaction reaction14 = ReversibleReaction.inSimulation(simulation)
                .identifier("reaction 14")
                .addSubstrate(alphaGDPReceptor)
                .addProduct(alphaGTPReceptor)
                .forwardsRateConstant(kEf_Gd)
                .backwardsRateConstant(kHf_LRGt)
                .build();

        // exchange RG
        ComplexBuildingReaction binding15 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 15")
                .of(gProteinBetaGamma, kEf_zero)
                .in(INNER)
                .by(alphaGTPReceptor, kEf_RG)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptor)
                .build();

        // association RGd
        ComplexBuildingReaction binding16 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 16")
                .of(gProteinBetaGamma, kAf_Gd)
                .in(INNER)
                .by(alphaGDPReceptor, kAr_Gd)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptor)
                .build();

        // binding L.RGd
        ComplexBuildingReaction binding17 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 17")
                .of(vasopressin, kOn_LR)
                .in(OUTER)
                .by(alphaGDPReceptor, kOff_LR)
                .to(MEMBRANE)
                .formingComplex(alphaGDPReceptorLigandComplex)
                .build();

        // binding L.RG
        ComplexBuildingReaction binding18 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 18")
                .of(vasopressin, kOn_LR)
                .in(OUTER)
                .by(alphaBetaGammaGDPReceptor, kOff_LR)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptorLigandComplex)
                .build();

        // binding L.RGt
        ComplexBuildingReaction binding19 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 19")
                .of(vasopressin, kOn_LR)
                .in(OUTER)
                .by(alphaGTPReceptor, kOff_LR)
                .to(MEMBRANE)
                .formingComplex(alphaGTPReceptorLigandComplex)
                .build();

        // setup species for clathrin decay
        ChemicalEntity clathrinHeavyChain = new Protein.Builder("Clathrin heavy chain")
                .assignFeature(new UniProtIdentifier("Q00610"))
                .build();

        ChemicalEntity clathrinLightChain = new Protein.Builder("Clathrin light chain")
                .assignFeature(new UniProtIdentifier("P09496"))
                .build();

        ComplexedChemicalEntity clathrinTriskelion = ComplexedChemicalEntity.create("Clathrin Triskelion")
                .addAssociatedPart(clathrinHeavyChain, 3)
                .addAssociatedPart(clathrinLightChain, 3)
                .build();

        RateConstant scaling = RateConstant.create(1.0)
                .forward().firstOrder()
                .timeUnit(MINUTE)
                .origin(new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION, "Unit Scaling", "Scales the velocity to the corresponding unit"))
                .build();

        // adenylate cyclase
        Protein ac6 = new Protein.Builder("AC6")
                .additionalIdentifier(new UniProtIdentifier("O43306"))
                .build();

        SmallMolecule atp = SmallMolecule.create("ATP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:15422"))
                .build();

        SmallMolecule camp = SmallMolecule.create("cAMP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17489"))
                .build();

        MichaelisConstant km = new MichaelisConstant(Quantities.getQuantity(20, MICRO_MOLE_PER_LITRE), CHEN2005);

        FeatureOrigin regression = new FeatureOrigin(FeatureOrigin.OriginType.PREDICTION, "Regression", "Regression using Matlab.");
        DynamicReaction reaction = DynamicReaction.inSimulation(simulation)
                .identifier("Adenylate Cyclase Reaction")
                // derived from v = kCat*E*S/(kM+s), where kCat has is a pseudo first order rate derived from regression
                // the scaling parameter determines the unit of the equation
                .kineticLaw("(1/((p01/cATPuM)^p02+(p03/cGAuM)^p04))*us*cAC6*(cATP/(kM+cATP))")
                // matlab regression
                .referenceParameter("p01", 6.144e5, regression)
                .referenceParameter("p02", 0.3063, regression)
                .referenceParameter("p03", 1.196, regression)
                .referenceParameter("p04", 1.153, regression)
                .referenceParameter("us", scaling)
                .referenceParameter("kM", km)
                .referenceParameter("cATPuM", new Reactant(atp, ReactantRole.CATALYTIC, MICRO_MOLE_PER_LITRE))
                .referenceParameter("cGAuM", new Reactant(gtpGProteinAlpha, ReactantRole.CATALYTIC, MICRO_MOLE_PER_LITRE))
                .referenceParameter("cAC6", new Reactant(ac6, ReactantRole.CATALYTIC, CellTopology.MEMBRANE))
                .referenceParameter("cATP", new Reactant(atp, ReactantRole.SUBSTRATE))
                .referenceParameter(new Reactant(camp, ReactantRole.PRODUCT))
                .build();

        // setup snares for fusion
        Protein vamp2 = new Protein.Builder("VAMP2")
                .assignFeature(new UniProtIdentifier("Q15836"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "R-SNARE"))
                .build();

        Protein vamp3 = new Protein.Builder("VAMP3")
                .assignFeature(new UniProtIdentifier("P63027"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "R-SNARE"))
                .build();

        Protein syntaxin3 = new Protein.Builder("Syntaxin 3")
                .assignFeature(new UniProtIdentifier("Q13277"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qa-SNARE"))
                .build();

        Protein syntaxin4 = new Protein.Builder("Syntaxin 4")
                .assignFeature(new UniProtIdentifier("Q12846"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qa-SNARE"))
                .build();

        Protein snap23 = new Protein.Builder("SNAP23")
                .assignFeature(new UniProtIdentifier("O00161"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qbc-SNARE"))
                .build();

        ComplexedChemicalEntity snareComplex1 = ComplexedChemicalEntity.create(syntaxin3.getIdentifier().getIdentifier() + ":" + snap23.getIdentifier().getIdentifier())
                .addAssociatedPart(syntaxin3)
                .addAssociatedPart(snap23)
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qabc-SNARE"))
                .build();

        ComplexedChemicalEntity snareComplex2 = ComplexedChemicalEntity.create(syntaxin4.getIdentifier().getIdentifier() + ":" + snap23.getIdentifier().getIdentifier())
                .addAssociatedPart(syntaxin4)
                .addAssociatedPart(snap23)
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qabc-SNARE"))
                .build();


        SmallMolecule water = SmallMolecule.create("water")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:15377"))
                .assignFeature(new Diffusivity(Quantities.getQuantity(2.6e-6, SQUARE_CENTIMETRE_PER_SECOND), TOFTS2000))
                .assignFeature(new MembranePermeability(Quantities.getQuantity(3.5e-3 * 0.5, CENTIMETRE_PER_SECOND), HAINES1994))
                .build();

        // solutes
        SmallMolecule solute = SmallMolecule.create("solutes")
                .name("solutes")
                .assignFeature(new MolarMass(52.0, PUTNAM1971)) // average solute mass
                .build();
        solute.setFeature(Diffusivity.class);

        // aqp2
        Protein aquaporin2 = new Protein.Builder("aqp2")
                .additionalIdentifier(new UniProtIdentifier("P41181"))
                .assignFeature(new OsmoticPermeability(5.31e-14, BINESH2015))
                .build();

        SingleFileChannelMembraneTransport.inSimulation(simulation)
                .transporter(aquaporin2)
                .cargo(water)
                .forSolute(solute)
                .build();

        // setup endocytosis budding
        ClathrinMediatedEndocytosis budding = new ClathrinMediatedEndocytosis();
        budding.setSimulation(simulation);
        budding.addMembraneCargo(Quantities.getQuantity(31415.93, new ProductUnit<Area>(NANO(METRE).pow(2))), 60.0, clathrinTriskelion);
        budding.addMembraneCargo(Quantities.getQuantity(10000, new ProductUnit<Area>(NANO(METRE).pow(2))), 10, vamp3);
        budding.setFeature(BuddingRate.DEFAULT_BUDDING_RATE);
        budding.setFeature(VesicleRadius.DEFAULT_VESICLE_RADIUS);
        budding.setFeature(MaturationTime.DEFAULT_MATURATION_TIME);
        simulation.getModules().add(budding);

        // setup vesicle diffusion
        VesicleDiffusion diffusion = new VesicleDiffusion();
        diffusion.useLiteratureDiffusivity();
        diffusion.setSimulation(simulation);
        simulation.getModules().add(diffusion);

        // setup actin boost
        EndocytosisActinBoost boost = new EndocytosisActinBoost();
        boost.setFeature(new DecayingEntity(clathrinTriskelion, MANUALLY_ANNOTATED));
        boost.setFeature(ActinBoostVelocity.DEFAULT_ACTIN_VELOCITY);
        boost.setSimulation(simulation);
        simulation.getModules().add(boost);

        // setup attachment
        VesicleAttachment attachment = new VesicleAttachment();
        attachment.setFeature(AttachmentDistance.DEFAULT_DYNEIN_ATTACHMENT_DISTANCE);
        attachment.setSimulation(simulation);
        simulation.getModules().add(attachment);

        // setup transport
        VesicleTransport transport = new VesicleTransport();
        transport.setFeature(MotorMovementVelocity.DEFAULT_MOTOR_VELOCITY);
        transport.setSimulation(simulation);
        simulation.getModules().add(transport);

        // setup tethering and fusion
        VesicleFusion fusion = new VesicleFusion();
        HashSet<ChemicalEntity> qSnareEntities = new HashSet<>();
        qSnareEntities.add(snareComplex1);
        qSnareEntities.add(snareComplex2);
        MatchingQSnares qSnares = new MatchingQSnares(qSnareEntities, MANUALLY_ANNOTATED);
        fusion.setFeature(qSnares);

        HashSet<ChemicalEntity> rSnareEntities = new HashSet<>();
        rSnareEntities.add(vamp2);
        rSnareEntities.add(vamp3);
        MatchingRSnares rSnares = new MatchingRSnares(rSnareEntities, MANUALLY_ANNOTATED);
        fusion.setFeature(rSnares);

        fusion.setFeature(rSnares);
        fusion.initializeComplexes();
        fusion.setFeature(new FusionPairs(Quantities.getQuantity(3, ONE), MANUALLY_ANNOTATED));
        fusion.setFeature(TetheringTime.DEFAULT_TETHERING_TIME);
        fusion.setFeature(AttachmentDistance.DEFAULT_DYNEIN_ATTACHMENT_DISTANCE);
        fusion.setSimulation(simulation);
        simulation.getModules().add(fusion);

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CellSubsections.EXTRACELLULAR_REGION, solute, Quantities.getQuantity(0.3, MOLE_PER_LITRE));
        ci.addInitialConcentration(CellSubsections.EXTRACELLULAR_REGION, water, Quantities.getQuantity(53.61, MOLE_PER_LITRE));
        ci.initialize(simulation);
        simulation.setConcentrationInitializer(ci);

        SimulationRepresentation representation = getSimulationFrom(simulation);

        try {
            String json = representation.toJson();
            System.out.println(json);
            Simulation reparsedSimulation = Converter.getSimulationFrom(json);
            System.out.println(reparsedSimulation);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
