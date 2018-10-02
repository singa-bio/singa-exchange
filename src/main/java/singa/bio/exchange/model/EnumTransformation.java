package singa.bio.exchange.model;

import bio.singa.simulation.model.modules.concentration.reactants.ReactantRole;
import bio.singa.simulation.model.sections.CellTopology;

import static bio.singa.simulation.model.modules.concentration.reactants.ReactantRole.CATALYTIC;
import static bio.singa.simulation.model.modules.concentration.reactants.ReactantRole.PRODUCT;
import static bio.singa.simulation.model.modules.concentration.reactants.ReactantRole.SUBSTRATE;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static bio.singa.simulation.model.sections.CellTopology.OUTER;

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

}
