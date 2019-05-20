package singa.bio.exchange.model.conversion;


import bio.singa.features.formatter.GeneralQuantityFormatter;
import bio.singa.features.formatter.QuantityFormatter;
import bio.singa.features.model.Evidence;
import bio.singa.mathematics.geometry.faces.Rectangle;
import javafx.embed.swing.JFXPanel;

import javax.measure.quantity.Time;
import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import static bio.singa.features.model.Evidence.SourceType.LITERATURE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
public final class Constants {

    public static final Path HOME_PATH = Paths.get(System.getProperty("user.home"));
    public static final Path GIT_PATH = HOME_PATH.resolve("git").resolve("model-data");
    public static final Path RAW_DATA_PATH = GIT_PATH.resolve("raw_data");

    public static final Rectangle BOUNDING_BOX = new Rectangle(800, 100);
    public static final QuantityFormatter<Time> TIME_FORMATTER = new GeneralQuantityFormatter<>(SECOND, true);

    // publications
    // bitex key as name, full source "MLA" formatted

    // diabetes insipidus reduced
    public static final Evidence YANG1997 = new Evidence(LITERATURE, "Yang 1997", "Yang, Baoxue, and A. S. Verkman. \"Water and glycerol permeabilities of aquaporins 1–5 and MIP determined quantitatively by expression of epitope-tagged constructs inXenopus oocytes.\" Journal of Biological Chemistry 272.26 (1997): 16140-16146.");
    // water features
    public static final Evidence KELL1977 = new Evidence(LITERATURE, "Kell 1977", "G .S. Kell, Effect of isotopic composition, temperature, pressure, and dissolved gases on the density of liquid water, Journal of Physical Chemistry Reference Data, 6 (1977), pp. 1109-1131.");
    public static final Evidence PUTNAM1971 = new Evidence(LITERATURE, "Putnam 1971", "Putnam, David F. \"Composition and concentrative properties of human urine.\" (1971).");
    public static final Evidence TOFTS2000 = new Evidence(LITERATURE, "Tofts 2000", "Tofts, P. S., et al. \"Test liquids for quantitative MRI measurements of self‐diffusion coefficient in vivo.\" Magnetic resonance in medicine 43.3 (2000): 368-374.");
    public static final Evidence HAINES1994 = new Evidence(LITERATURE, "Haines 1994", "Haines, Thomas H. \"Water transport across biological membranes.\" FEBS letters 346.1 (1994): 115-122.");
    public static final Evidence BINESH2015 = new Evidence(LITERATURE, "Binesh 2015", "Binesh, A. R., and R. Kamali. \"Molecular dynamics insights into human aquaporin 2 water channel.\" Biophysical chemistry 207 (2015): 107-113.");
    public static final Evidence HASHIDO2007 = new Evidence(LITERATURE, "Hashido 2007", "Hashido, Masanori, Akinori Kidera, and Mitsunori Ikeguchi. \"Water transport in aquaporins: osmotic permeability matrix analysis of molecular dynamics simulations.\" Biophysical journal 93.2 (2007): 373-385.");
    public static final Evidence CHEN2005 = new Evidence(LITERATURE, "Chen-Goodspeed 2005", "Chen-Goodspeed, Misty, Abolanle N. Lukan, and Carmen W. Dessauer. \"Modeling of Gαs and Gαi regulation of human type V and VI adenylyl cyclase.\" Journal of Biological Chemistry 280.3 (2005): 1808-1816.");
    public static final Evidence SHAFFER1999A = new Evidence(LITERATURE, "Shaffer 1999a", "Shaffer, Jennifer, and Joseph A. Adams. \"Detection of conformational changes along the kinetic pathway of protein kinase A using a catalytic trapping technique.\" Biochemistry 38.37 (1999): 12072-12079.");
    public static final Evidence SHAFFER1999B = new Evidence(LITERATURE, "Shaffer 1999b", "Shaffer, Jennifer, and Joseph A. Adams. \"An ATP-linked structural change in protein kinase A precedes phosphoryl transfer under physiological magnesium concentrations.\" Biochemistry 38.17 (1999): 5572-5581.");

    // poc monovalent receptor binding
    public static final Evidence HUGHES1982 = new Evidence(LITERATURE, "Hughes 1982", "Hughes, Richard J., et al. \"Characterization of coexisting alpha 1-and beta 2-adrenergic receptors on a cloned muscle cell line, BC3H-1.\" Molecular pharmacology 22.2 (1982): 258-266.");

    // simulation vasopressin receptor binding
    public static final Evidence BUSH2016 = new Evidence(LITERATURE, "Bush 2016", "Bush, Alan, et al. \"Yeast GPCR signaling reflects the fraction of occupied receptors, not the number.\" Molecular systems biology 12.12 (2016): 898.");

    // endo exo
    public static final Evidence NEDVETSKY2007 = new Evidence(LITERATURE, "Nedvetsky 2007", "Nedvetsky, Pavel I., et al. \"A role of myosin Vb and Rab11‐FIP2 in the aquaporin‐2 shuttle.\" Traffic 8.2 (2007): 110-123.");
    public static final Evidence TRYBUS2008 = new Evidence(LITERATURE, "Trybus 2008", "Trybus, Kathleen M. \"Myosin V from head to tail.\" Cellular and Molecular Life Sciences 65.9 (2008): 1378-1389.");
    public static final Evidence PIEROBON2009 = new Evidence(LITERATURE, "Pierobon 2009", "Pierobon, Paolo, et al. \"Velocity, processivity, and individual steps of single myosin V molecules in live cells.\" Biophysical journal 96.10 (2009): 4268-4275.");
    public static final Evidence DONOVAN2015 = new Evidence(LITERATURE, "Donovan 2015", "Donovan, Kirk W., and Anthony Bretscher. \"Tracking individual secretory vesicles during exocytosis reveals an ordered and regulated process.\" J Cell Biol (2015): jcb-201501118.");
    public static final Evidence KARATEKIN2010 = new Evidence(LITERATURE, "Karatekin 2010", "Karatekin, Erdem, et al. \"A fast, single-vesicle fusion assay mimics physiological SNARE requirements.\" Proceedings of the National Academy of Sciences (2010): 200914723.");
    public static final Evidence MISTRY2009 = new Evidence(LITERATURE, "Mistry 2009", "Mistry, Abinash C., et al. \"Syntaxin specificity of aquaporins in the inner medullary collecting duct.\" American Journal of Physiology-Renal Physiology 297.2 (2009): F292-F300.");
    public static final Evidence LOERKE2009 = new Evidence(LITERATURE,"Loerke 2009", "Loerke, Dinah, et al. \"Cargo and dynamin regulate clathrin-coated pit maturation.\" PLoS biology 7.3 (2009): e1000057.");
    public static final Evidence MARPLES1998 = new Evidence(LITERATURE, "Marples 1998", "Marples, David, et al. \"Dynein and dynactin colocalize with AQP2 water channels in intracellular vesicles from kidney collecting duct.\" American Journal of Physiology-Renal Physiology 274.2 (1998): F384-F394.");
    public static final Evidence TOBA2006 = new Evidence(LITERATURE, "Toba 2006", "Toba, Shiori, et al. \"Overlapping hand-over-hand mechanism of single molecular motility of cytoplasmic dynein.\" Proceedings of the National Academy of Sciences 103.15 (2006): 5741-5745.");
    public static final Evidence BURGESS2003 = new Evidence(LITERATURE, "Burgess 2003", "Burgess, Stan A., et al. \"Dynein structure and power stroke.\" Nature 421.6924 (2003): 715.");

    public static final Evidence SETTE1996 = new Evidence(LITERATURE, "Sette 1996", "Sette, Claudio, and Marco Conti. \"Phosphorylation and Activation of a cAMP-specific Phosphodiesterase by the cAMP-dependent Protein Kinase.\" Journal of Biological Chemistry 271.28 (1996): 16526-16534.");

    /**
     * Initializes JavaFX environment.
     */
    public static void doJavaFXMagic() {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel();
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
