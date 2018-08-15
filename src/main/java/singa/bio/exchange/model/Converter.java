package singa.bio.exchange.model;

import bio.singa.chemistry.entities.*;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.simulation.model.modules.concentration.imlementations.ComplexBuildingReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.NthOrderReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.ReversibleReaction;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.structure.features.molarmass.MolarMass;
import com.fasterxml.jackson.core.JsonProcessingException;
import singa.bio.exchange.model.entities.EntityDataset;
import singa.bio.exchange.model.entities.EntityRepresentation;
import tec.uom.se.AbstractUnit;
import tec.uom.se.quantity.Quantities;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import java.util.Comparator;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellTopology.*;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class Converter {

    private static final FeatureOrigin BUSH2016 = FeatureOrigin.MANUALLY_ANNOTATED;

    public static EntityDataset from(Simulation simulation) {
        EntityDataset dataset = new EntityDataset();
        for (ChemicalEntity chemicalEntity : simulation.getChemicalEntities()) {
            dataset.addEntity(EntityRepresentation.of(chemicalEntity));
        }
        return dataset;
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
                .concentrationUnit(NANO(MOLE_PER_LITRE)).timeUnit(SECOND)
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
                .concentrationUnit(NANO(MOLE_PER_LITRE)).timeUnit(SECOND)
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

        // transformed from static receptor parameter in original model
        RateConstant kOn_LR = RateConstant.create(1.7857e-4)
                .forward().secondOrder()
                .concentrationUnit(NANO(MOLE_PER_LITRE))
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

        System.out.println();
        System.out.println("--- Entities ---");
        System.out.println();
        simulation.getChemicalEntities().stream()
                .sorted(Comparator.comparing(entity -> entity.getIdentifier().getIdentifier()))
                .forEach(entity -> System.out.println(entity.getStringForProtocol() + System.lineSeparator()));

        Unit<Dimensionless> one = AbstractUnit.ONE;
        EntityDataset entityDataset = Converter.from(simulation);
        try {
            System.out.println(entityDataset.toJson());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }


}
