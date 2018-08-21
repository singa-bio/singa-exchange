package singa.bio.exchange.model.entities;

import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.reactants.ReactantRole;
import bio.singa.simulation.model.modules.concentration.reactants.StoichiometricReactant;
import bio.singa.simulation.model.sections.CellTopology;
import com.fasterxml.jackson.annotation.JsonProperty;

import static bio.singa.simulation.model.sections.CellTopology.*;

/**
 * @author cl
 */
public class ReactantRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty("stoichiometric-number")
    private double stoichiometricNumber;

    @JsonProperty("preferred-topology")
    private String preferredTopology;

    public ReactantRepresentation() {

    }

    public static ReactantRepresentation of(Reactant reactant) {
        ReactantRepresentation representation = new ReactantRepresentation();
        representation.setIdentifier(reactant.getEntity().getIdentifier().toString());
        representation.setPreferredTopology(fromType(reactant.getPrefferedTopology()));
        if (reactant instanceof StoichiometricReactant) {
            representation.setStoichiometricNumber(((StoichiometricReactant) reactant).getStoichiometricNumber());
        } else {
            representation.setStoichiometricNumber(1.0);
        }
        return representation;
    }

    public StoichiometricReactant toModel(boolean isSubstrate) {
        ReactantRole role;
        if (isSubstrate) {
            role = ReactantRole.DECREASING;
        } else {
            role = ReactantRole.INCREASING;
        }
        StoichiometricReactant reactant = new StoichiometricReactant(EntityCache.get(getIdentifier()), role, getStoichiometricNumber());
        reactant.setPrefferedTopology(reactant.getPrefferedTopology());
        return reactant;
    }

    public static String fromType(CellTopology type) {
        switch (type) {
            case OUTER:
                return "outer";
            case MEMBRANE:
                return "membrane";
            default:
                return "inner";
        }
    }

    public static CellTopology toType(String type) {
        switch (type) {
            case "outer":
                return OUTER;
            case "membrane":
                return MEMBRANE;
            default:
                return INNER;
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public double getStoichiometricNumber() {
        return stoichiometricNumber;
    }

    public void setStoichiometricNumber(double stoichiometricNumber) {
        this.stoichiometricNumber = stoichiometricNumber;
    }

    public String getPreferredTopology() {
        return preferredTopology;
    }

    public void setPreferredTopology(String preferredTopology) {
        this.preferredTopology = preferredTopology;
    }

    public void setPreferredTopology(CellTopology preferredTopology) {
        this.preferredTopology = fromType(preferredTopology);
    }

}
