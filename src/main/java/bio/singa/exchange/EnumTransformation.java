package bio.singa.exchange;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole;
import bio.singa.simulation.model.sections.CellTopology;

import static bio.singa.features.model.Evidence.SourceType;
import static bio.singa.features.model.Evidence.SourceType.*;
import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.*;
import static bio.singa.simulation.model.sections.CellTopology.*;

/**
 * @author cl
 */
public class EnumTransformation {

    public static String fromTopology(CellTopology type) {
        switch (type) {
            case OUTER:
                return "outer";
            case MEMBRANE:
                return "membrane";
            default:
                return "inner";
        }
    }

    public static CellTopology toTopology(String type) {
        switch (type) {
            case "outer":
                return OUTER;
            case "membrane":
                return MEMBRANE;
            default:
                return INNER;
        }
    }

    public static String fromRole(ReactantRole type) {
        switch (type) {
            case PRODUCT:
                return "product";
            case SUBSTRATE:
                return "substrate";
            default:
                return "catalyst";
        }
    }

    public static ReactantRole toRole(String type) {
        switch (type) {
            case "product":
                return PRODUCT;
            case "substrate":
                return SUBSTRATE;
            default:
                return CATALYTIC;
        }
    }

    public static String fromSourceType(SourceType type) {
        switch (type) {
            case ESTIMATION:
                return "estimation";
            case PREDICTION:
                return "prediction";
            case DATABASE:
                return "database";
            case LITERATURE:
                return "literature";
            default:
                return "guess";
        }
    }

    public static SourceType toSourceType(String type) {
        switch (type) {
            case "estimation":
                return ESTIMATION;
            case "prediction":
                return PREDICTION;
            case "database":
                return DATABASE;
            case "literature":
                return LITERATURE;
            default:
                return GUESS;
        }
    }

}
