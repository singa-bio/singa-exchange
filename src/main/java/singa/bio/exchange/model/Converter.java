package singa.bio.exchange.model;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.chemistry.entities.*;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.chemistry.features.permeability.OsmoticPermeability;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.imlementations.ComplexBuildingReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.NthOrderReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.ReversibleReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.SingleFileChannelMembraneTransport;
import bio.singa.simulation.model.modules.displacement.implementations.EndocytosisActinBoost;
import bio.singa.simulation.model.modules.displacement.implementations.VesicleDiffusion;
import bio.singa.simulation.model.modules.displacement.implementations.VesicleTransport;
import bio.singa.simulation.model.modules.qualitative.implementations.ClathrinMediatedEndocytosis;
import bio.singa.simulation.model.modules.qualitative.implementations.VesicleAttachment;
import bio.singa.simulation.model.modules.qualitative.implementations.VesicleFusion;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.structure.features.molarmass.MolarMass;
import com.fasterxml.jackson.databind.ObjectMapper;
import singa.bio.exchange.model.entities.EntityDataset;
import singa.bio.exchange.model.entities.EntityRepresentation;
import singa.bio.exchange.model.modules.ModuleDataset;
import singa.bio.exchange.model.modules.ModuleRepresentation;
import singa.bio.exchange.model.units.UnitJacksonModule;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.quantity.Area;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static bio.singa.chemistry.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
import static bio.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static bio.singa.features.units.UnitProvider.NANO_MOLE_PER_LITRE;
import static bio.singa.simulation.features.DefaultFeatureSources.BINESH2015;
import static bio.singa.simulation.model.sections.CellTopology.*;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class Converter {

    public static Simulation current;

    static final FeatureOrigin BUSH2016 = new FeatureOrigin(FeatureOrigin.OriginType.LITERATURE, "Bush 2016", "Bush, Alan, et al. \"Yeast GPCR signaling reflects the fraction of occupied receptors, not the number.\" Molecular systems biology 12.12 (2016): 898.");
    static final FeatureOrigin PUTNAM1971 = new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION, "Putnam 1971", "Putnam, David F. \"Composition and concentrative properties of human urine.\" (1971).");
    static final FeatureOrigin TOFTS2000 = new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION, "Tofts 2000", "Tofts, P. S., et al. \"Test liquids for quantitative MRI measurements of self‐diffusion coefficient in vivo.\" Magnetic resonance in medicine 43.3 (2000): 368-374.");
    static final FeatureOrigin HAINES1994 = new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION, "Haines 1994", "Haines, Thomas H. \"Water transport across biological membranes.\" FEBS letters 346.1 (1994): 115-122.");

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

    public static SimulationRepresentation getSimulationFrom(Simulation simulation) {
        ModuleDataset moduleDataset = getModuleDatasetFrom(simulation);
        EntityDataset entityDataset = getEntityDatasetFrom(simulation);
        SimulationRepresentation representation = new SimulationRepresentation();
        representation.setEntities(entityDataset);
        representation.setModules(moduleDataset);
        return representation;
    }

    public static List<ChemicalEntity> getEntityDatasetFrom(String entitySetJasonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new UnitJacksonModule());
        return EntityDataset.to(mapper.readValue(entitySetJasonString, EntityDataset.class));
    }

    public static Simulation getSimulationFrom(String json) throws IOException {
        current = new Simulation();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new UnitJacksonModule());
        return SimulationRepresentation.to(mapper.readValue(json, SimulationRepresentation.class));
    }

    public static Simulation getSimulationfromDatasets(EntityDataset entityDataset, ModuleDataset moduleDataset) {
        current = new Simulation();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new UnitJacksonModule());
        List<ChemicalEntity> entities = EntityDataset.to(entityDataset);
        List<UpdateModule> modules = ModuleDataset.to(moduleDataset);
        return current;
    }

    public static void main(String[] args) {

        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.001, SECOND));

        CellSubsection cytoplasm = new CellSubsection("Cytoplasm");
        CellSubsection interstitium = new CellSubsection("Interstitium");
        CellSubsection basolateralMembrane = new CellSubsection("Basolateral membrane");

        CellRegion interstitiumMembrane = new CellRegion("Interstitium membrane");
        interstitiumMembrane.addSubSection(CellTopology.INNER, cytoplasm);
        interstitiumMembrane.addSubSection(CellTopology.MEMBRANE, basolateralMembrane);
        interstitiumMembrane.addSubSection(CellTopology.OUTER, interstitium);

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

        vasopressinReceptor.setFeature(MolarMass.class);

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

        SimulationRepresentation representation = getSimulationFrom(simulation);

        try {
            String json = representation.toJson();
            System.out.println(json);
            Simulation reparsedSimulation = Converter.getSimulationFrom(json);
            System.out.println(simulation);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
