package singa.bio.exchange.model.conversion;

import bio.singa.features.identifiers.GoTerm;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;

/**
 * @author cl
 */
public class RegionSupplier {

    public final CellSubsection lumen;
    public final CellSubsection cytoplasm;
    public final CellSubsection interstitium;
    public final CellSubsection apicalMembrane;
    public final CellSubsection basolateralMembrane;
    public final CellRegion lumenRegion;
    public final CellRegion apicalMembraneRegion;
    public final CellRegion cytoplasmRegion;
    public final CellRegion basolateralMembraneRegion;
    public final CellRegion interstitiumRegion;

    public RegionSupplier() {
        // lumen | luminal Membrane | cytoplasma | basolateral membrane | interstitium
        // setup compartments and membranes
        lumen = new CellSubsection("lumen", new GoTerm("GO:0005576"));
        cytoplasm = new CellSubsection("cytoplasm", new GoTerm("GO:0005737"));
        interstitium = new CellSubsection("interstitium", new GoTerm("GO:0005576"));
        apicalMembrane = new CellSubsection("apical plasma membrane", new GoTerm("GO:0016324"));
        basolateralMembrane = new CellSubsection("basolateral plasma membrane", new GoTerm("GO:0016323"));

        // compose regions
        lumenRegion = new CellRegion("Lumen", new GoTerm("GO:0005576"));
        lumenRegion.addSubsection(CellTopology.INNER, lumen);

        apicalMembraneRegion = new CellRegion("apical plasma membrane", new GoTerm("GO:0016324"));
        apicalMembraneRegion.addSubsection(CellTopology.OUTER, lumen);
        apicalMembraneRegion.addSubsection(CellTopology.MEMBRANE, apicalMembrane);
        apicalMembraneRegion.addSubsection(CellTopology.INNER, cytoplasm);

        cytoplasmRegion = new CellRegion("cytoplasm", new GoTerm("GO:0005576"));
        cytoplasmRegion.addSubsection(CellTopology.INNER, cytoplasm);

        basolateralMembraneRegion = new CellRegion("basolateral plasma membrane", new GoTerm("GO:0016323"));
        basolateralMembraneRegion.addSubsection(CellTopology.INNER, cytoplasm);
        basolateralMembraneRegion.addSubsection(CellTopology.MEMBRANE, basolateralMembrane);
        basolateralMembraneRegion.addSubsection(CellTopology.OUTER, interstitium);

        interstitiumRegion = new CellRegion("Interstitium", new GoTerm("GO:0005576"));
        interstitiumRegion.addSubsection(CellTopology.INNER, interstitium);
    }
}
