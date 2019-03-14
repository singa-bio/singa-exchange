package singa.bio.exchange.model.entities;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.sections.CellTopology;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import singa.bio.exchange.model.EnumTransformation;

import javax.measure.Unit;

/**
 * @author cl
 */
@JsonPropertyOrder({ "identifier", "type", "role", "preferred-topology", "stoichiometric-number"})
public class StaticReactantRepresentation extends ReactantRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty
    private String role;

    @JsonProperty("stoichiometric-number")
    private double stoichiometricNumber;

    @JsonProperty("preferred-topology")
    private String preferredTopology;

    @JsonProperty("preferred-unit")
    private Unit preferredUnit;

    public StaticReactantRepresentation() {

    }

    public static StaticReactantRepresentation of(Reactant reactant) {
        StaticReactantRepresentation representation = new StaticReactantRepresentation();
        representation.setIdentifier(reactant.getEntity().getIdentifier().toString());
        representation.setRole(EnumTransformation.fromRole(reactant.getRole()));
        representation.setStoichiometricNumber(reactant.getStoichiometricNumber());
        representation.setPreferredTopology(EnumTransformation.fromTopology(reactant.getPreferredTopology()));
        representation.setPreferredUnit(reactant.getPreferredConcentrationUnit());
        return representation;
    }

    public Reactant toModel() {
        Reactant reactant = new Reactant(EntityCache.get(getIdentifier()), EnumTransformation.toRole(getRole()), getStoichiometricNumber());
        reactant.setPreferredTopology(EnumTransformation.toTopology(getPreferredTopology()));
        reactant.setPreferredConcentrationUnit(getPreferredUnit());
        return reactant;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
        this.preferredTopology = EnumTransformation.fromTopology(preferredTopology);
    }

    public Unit getPreferredUnit() {
        return preferredUnit;
    }

    public void setPreferredUnit(Unit preferredUnit) {
        this.preferredUnit = preferredUnit;
    }
}
