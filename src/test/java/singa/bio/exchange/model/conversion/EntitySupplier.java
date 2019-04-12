package singa.bio.exchange.model.conversion;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.chemistry.features.permeability.OsmoticPermeability;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.structure.features.molarmass.MolarMass;
import tec.units.indriya.quantity.Quantities;

import static bio.singa.chemistry.annotations.AnnotationType.NOTE;
import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static bio.singa.chemistry.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
import static singa.bio.exchange.model.conversion.Constants.*;

/**
 * @author cl
 */
public class EntitySupplier {

    public final SmallMolecule water;
    public final SmallMolecule solute;

    // vasopressin and receptor
    public final Protein v2r;
    public final ChemicalEntity avp;
    public final ComplexEntity v2rAvp;

    // g proteins
    public final Protein gA;
    public final Protein gB;
    public final Protein gG;
    public final ChemicalEntity gdp;
    public final ChemicalEntity gtp;

    public final ComplexEntity gBG;
    public final ComplexEntity gABG;
    public final ComplexEntity gABGd;
    public final ComplexEntity gAd;
    public final ComplexEntity gAt;

    // protein kinase a
    public final Protein akap;
    public final Protein pkaC;
    public final Protein pkaR;
    public final SmallMolecule camp;

    public final ComplexEntity akapPka00;
    public final ComplexEntity akapPka10;
    public final ComplexEntity akapPka11;
    public final ComplexEntity akapPka20;
    public final ComplexEntity akapPka12;
    public final ComplexEntity akapPkaX0;
    public final ComplexEntity akapPkaX1;
    public final ComplexEntity akapPkaX2;
    public final ComplexEntity akapPkaXX;

    public final SmallMolecule atp;
    public final SmallMolecule adp;
    public final SmallMolecule amp;

    // adenylate cyclase
    public final Protein ac6;
    public final ComplexEntity ac6gA;

    // snares for membrane fusion
    public final Protein vamp2;
    public final Protein stx3;
    public final Protein snap23;
    public final ComplexEntity snareComplex;

    // aquaporin 2
    public final Protein aqp2;
    public final SmallMolecule phosphate;
    public final ComplexEntity aqp2p;
    public final ComplexEntity aqp2pp;

    public final Protein trp;
    public final ComplexEntity aqp2ptrp;

    public final Protein aqp3;
    public final Protein aqp4;

    public final Protein pp1;
    public final ComplexEntity aqp2ppPP1;
    public final ComplexEntity aqp2pPP1;

    public final Protein rab11;
    public final Protein fib2;
    public final Protein myoVb;
    public final ComplexEntity myoComplex;

    public final Protein fib3;
    public final Protein dynli;
    public final Protein dynhc;
    public final ComplexEntity dyneinComplex;

    public final ComplexEntity clathrinTriskelion;

    public final Protein pp1R;
    public final ComplexEntity pp1Rp;
    public final ComplexEntity pp1RpPP1;

    public final Protein pde4;
    public final ComplexEntity pde4p;
    public final ComplexEntity pde4Camp;
    public final ComplexEntity pde4pCamp;

    public final Protein pp2b;
    public final ComplexEntity pp2bAqp2p;

    public final Protein pkaRi;
    public final ComplexEntity v2rgad;
    public final ComplexEntity gABGt;
    public final ComplexEntity v2rgat;
    public final ComplexEntity v2rgabgt;
    public final ComplexEntity v2ravpgad;
    public final ComplexEntity v2ravpgat;
    public final ComplexEntity v2ravpgabgd;
    public final ComplexEntity v2ravpgabgt;
    public final ComplexEntity v2rgabgd;


    public EntitySupplier() {

        water = SmallMolecule.create("H2O")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:15377"))
                .assignFeature(new Diffusivity(Quantities.getQuantity(2.6e-6, SQUARE_CENTIMETRE_PER_SECOND), TOFTS2000))
                .assignFeature(new MembranePermeability(Quantities.getQuantity(3.5e-3 * 0.5, CENTIMETRE_PER_SECOND), HAINES1994))
                .build();

        solute = SmallMolecule.create("SOL")
                .assignFeature(new MolarMass(52.0))
                .build();

        aqp3 = Protein.create("AQP3")
                .additionalIdentifier(new UniProtIdentifier("Q92482"))
                .assignFeature(new OsmoticPermeability(2.2e-14, YANG1997))
                .build();

        aqp4 = Protein.create("AQP4")
                .additionalIdentifier(new UniProtIdentifier("P55087"))
                .assignFeature(new OsmoticPermeability(7.0e-14, HASHIDO2007))
                .build();

        // vasopressin v2 receptor
        v2r = Protein.create("V2R")
                .additionalIdentifier(new UniProtIdentifier("P30518"))
                .build();

        // vasopressin
        avp = SmallMolecule.create("AVP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:34543"))
                .build();

        // g-protein subunits
        gA = Protein.create("GA")
                .additionalIdentifier(new UniProtIdentifier("P63092"))
                .build();

        gB = Protein.create("GB")
                .additionalIdentifier(new UniProtIdentifier("P62873"))
                .build();

        gG = Protein.create("GG")
                .additionalIdentifier(new UniProtIdentifier("P63211"))
                .build();

        // g-protein substrates
        gdp = SmallMolecule.create("GDP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();

        gtp = SmallMolecule.create("GTP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();

        // beta gamma complex
        gBG = ComplexEntity.from(gB, gG);

        // alpha beta gamma complex
        gABG = ComplexEntity.from(gA, gBG);

        // gdp alpha complex
        gAd = ComplexEntity.from(gA, gdp);

        // gtp alpha complex
        gAt = ComplexEntity.from(gA, gtp);

        // gdp alpha beta gamma complex
        gABGd = ComplexEntity.from(gAd, gBG);

        // gtp alpha beta gamma complex
        gABGt = ComplexEntity.from(gAt, gBG);

        // v2r avp complex
        v2rAvp = ComplexEntity.from(v2r, avp);

        // v2r gdp alpha complex
        v2rgad = ComplexEntity.from(v2r, gAd);

        // v2r gat alpha complex
        v2rgat = ComplexEntity.from(v2r, gAt);

        // v2r gabgd complex
        v2rgabgt = ComplexEntity.from(v2r, gABGd);

        // v2r avp gad complex
        v2ravpgad = ComplexEntity.from(v2rAvp, gAd);

        // v2r avp gat complex
        v2ravpgat = ComplexEntity.from(v2rAvp, gAt);

        // v2r avp gabgt complex
        v2ravpgabgt = ComplexEntity.from(v2rAvp, gABGt);

        // v2r avp gabgt complex
        v2ravpgabgd = ComplexEntity.from(v2rAvp, gABGd);

        // v2r gabgd complex
        v2rgabgd = ComplexEntity.from(v2r, gABGd);

        akap = Protein.create("AKAP")
                .additionalIdentifier(new UniProtIdentifier("Q9P0M2"))
                .build();

        pkaC = Protein.create("PKAC")
                .additionalIdentifier(new UniProtIdentifier("P22694"))
                .build();

        pkaR = Protein.create("PKAR")
                .additionalIdentifier(new UniProtIdentifier("P31323"))
                .build();

        pkaRi = Protein.create("PKARi")
                .additionalIdentifier(new UniProtIdentifier("P31323"))
                .build();

        camp = SmallMolecule.create("CAMP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17489"))
                .build();

        ComplexEntity pka = ComplexEntity.from(pkaC, pkaR);
        ComplexEntity pkacamp = ComplexEntity.from(pkaC, pkaR, camp);
        ComplexEntity pka2camp = ComplexEntity.from(pkaC, pkaR, camp, camp);
        ComplexEntity pkai2camp = ComplexEntity.from(pkaC, pkaRi, camp, camp);

        // AKAP:2PKAC:2PKAR (0/0)
        akapPka00 = ComplexEntity.from(pka, pka);

        // AKAP:2PKAC:PKAR:CAMP:PKAR (1/0)
        akapPka10 = ComplexEntity.from(pkacamp, pka);

        // AKAP:2PKAC:PKAR:CAMP:PKAR:CAMP (1/1)
        akapPka11 = ComplexEntity.from(pkacamp, pkacamp);

        // AKAP:2PKAC:PKAR:2CAMP:PKAR (2/0)
        akapPka20 = ComplexEntity.from(pka2camp, pka);

        // AKAP:2PKAC:PKAR:CAMP:PKAR:2CAMP (1/2)
        akapPka12 = ComplexEntity.from(pkacamp, pka2camp);

        // AKAP:2PKAC:PKAR (x/0)
        akapPkaX0 = ComplexEntity.from(pkai2camp, pka);

        // AKAP:2PKAC:PKAR:CAMP (x/1)
        akapPkaX1 = ComplexEntity.from(pkai2camp, pkacamp);

        // AKAP:2PKAC:PKAR:2CAMP (x/2)
        akapPkaX2 = ComplexEntity.from(pkai2camp, pka2camp);

        // AKAP:2PKAC (x/x)
        akapPkaXX = ComplexEntity.from(pkai2camp, pkai2camp);

        // adenylate cyclase
        ac6 = Protein.create("AC6")
                .additionalIdentifier(new UniProtIdentifier("O43306"))
                .build();

        ac6gA = ComplexEntity.from(ac6, gAt);

        vamp2 = Protein.create("VAMP2")
                .assignFeature(new UniProtIdentifier("Q15836"))
                .annotation(new Annotation<>(NOTE, "SNARE type", "R-SNARE"))
                .build();

        stx3 = Protein.create("STX3")
                .assignFeature(new UniProtIdentifier("Q13277"))
                .annotation(new Annotation<>(NOTE, "SNARE type", "Qa-SNARE"))
                .build();

        snap23 = Protein.create("SNAP23")
                .assignFeature(new UniProtIdentifier("O00161"))
                .annotation(new Annotation<>(NOTE, "SNARE type", "Qbc-SNARE"))
                .build();

        snareComplex = ComplexEntity.from(stx3, snap23);

        // aquaporin 2
        aqp2 = Protein.create("AQP2")
                .additionalIdentifier(new UniProtIdentifier("P41181"))
                .assignFeature(new OsmoticPermeability(5.31e-14, BINESH2015))
                .build();

        // inorcanic phosphate
        phosphate = SmallMolecule.create("P")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:24838"))
                .build();

        // aquaporin 2 265-phosphorylated
        aqp2p = ComplexEntity.from(aqp2, phosphate);

        // aquaporin 2 265 and 269 phosphorylated
        aqp2pp = ComplexEntity.from(aqp2p, phosphate);

        trp = Protein.create("TRP")
                .additionalIdentifier(new UniProtIdentifier("P09493"))
                .build();

        aqp2ptrp = ComplexEntity.from(aqp2p, trp);

        // Ras-related protein Rab-11A
        rab11 = Protein.create("RAB11")
                .additionalIdentifier(new UniProtIdentifier("P62491"))
                .build();

        // Rab11 family-interacting protein 2
        fib2 = Protein.create("FIP2")
                .additionalIdentifier(new UniProtIdentifier("Q7L804"))
                .build();

        // Unconventional myosin-Vb
        myoVb = Protein.create("MYOVB")
                .additionalIdentifier(new UniProtIdentifier("Q9ULV0"))
                .build();

        // Rab11 family-interacting protein 3
        fib3 = Protein.create("FIP3")
                .additionalIdentifier(new UniProtIdentifier("O75154"))
                .build();

        // Cytoplasmic dynein 1 light intermediate chain 1
        dynli = Protein.create("DYNLI")
                .additionalIdentifier(new UniProtIdentifier("Q9Y6G9"))
                .build();

        // Cytoplasmic dynein 1 heavy chain 1
        dynhc = Protein.create("DYNHC")
                .additionalIdentifier(new UniProtIdentifier("Q14204"))
                .build();

        // attached dynein complex
        ComplexEntity dyn = ComplexEntity.from(dynli, dynhc);
        dyneinComplex = ComplexEntity.from(rab11, fib3, dyn);

        myoComplex = ComplexEntity.from(rab11, fib2, myoVb);

        // serine/threonine-protein phosphatase
        pp1 = Protein.create("PP1")
                .additionalIdentifier(new UniProtIdentifier("P62136"))
                .build();

        aqp2ppPP1 = ComplexEntity.from(aqp2pp, pp1);

        aqp2pPP1 = ComplexEntity.from(aqp2p, pp1);

        // setup species for clathrin decay
        Protein clathrinHeavyChain = Protein.create("Clathrin heavy chain")
                .assignFeature(new UniProtIdentifier("Q00610"))
                .build();

        Protein clathrinLightChain = Protein.create("Clathrin light chain")
                .assignFeature(new UniProtIdentifier("P09496"))
                .build();

        ComplexEntity clathrin = ComplexEntity.from(clathrinHeavyChain, clathrinLightChain);
        clathrinTriskelion = ComplexEntity.from(clathrin, clathrin, clathrin);

        // atp
        atp = SmallMolecule.create("ATP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:15422"))
                .build();

        // adp
        adp = SmallMolecule.create("ADP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:16761"))
                .build();

        // TODO should be generated automatically
//        // intermediate complex 1
//        pkaCatp = ComplexEntity.from("PKAC:ATP")
//                .addAssociatedPart(pkaC)
//                .addAssociatedPart(atp)
//                .build();
//
//        // intermediate complex 2
//        pkaCatpAqp2 = ComplexEntity.from("PKAC:AQP2:ATP")
//                .addAssociatedPart(pkaC)
//                .addAssociatedPart(atp)
//                .addAssociatedPart(aqp2)
//                .build();
//
//        // intermediate complex 3
//        pkaCadpAqp2p = ComplexEntity.from("PKAC:AQP2P:ADP")
//                .addAssociatedPart(pkaC)
//                .addAssociatedPart(adp)
//                .addAssociatedPart(aqp2p)
//                .build();

//        pkaCatpPp1R = ComplexEntity.from("PKAC:PP1R:ATP")
//                .addAssociatedPart(pkaC)
//                .addAssociatedPart(atp)
//                .addAssociatedPart(pp1R)
//                .build();
//
//        pkaCadpPp1Rp = ComplexEntity.from("PKAC:PP1R:ADP")
//                .addAssociatedPart(pkaC)
//                .addAssociatedPart(adp)
//                .addAssociatedPart(pp1Rp)
//                .build();

//        pkaCatpPde4 = ComplexEntity.from("PKAC:PDE4:ATP")
//                .addAssociatedPart(pkaCatp)
//                .addAssociatedPart(pde4)
//                .build();
//
//        pkaCadpPde4p = ComplexEntity.from("PKAC:PDE4P:ADP")
//                .addAssociatedPart(pkaC)
//                .addAssociatedPart(adp)
//                .addAssociatedPart(aqp2p)
//                .build();


        pp1R = Protein.create("PP1R")
                .additionalIdentifier(new UniProtIdentifier("Q13522"))
                .build();

        pp1Rp = ComplexEntity.from(pp1R,phosphate);

        pp1RpPP1 = ComplexEntity.from(pp1Rp,pp1);


        pde4 = Protein.create("PDE4")
                .additionalIdentifier(new UniProtIdentifier("Q08499"))
                .build();

        pde4Camp = ComplexEntity.from(pde4,camp);

        pde4p = ComplexEntity.from(pde4,phosphate);

        pde4pCamp = ComplexEntity.from(pde4p,camp);


        amp = SmallMolecule.create("AMP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:16027"))
                .build();

        pp2b = Protein.create("PP2B")
                .additionalIdentifier(new UniProtIdentifier("Q08209"))
                .build();

        pp2bAqp2p = ComplexEntity.from(aqp2p, pp2b);

    }

}
