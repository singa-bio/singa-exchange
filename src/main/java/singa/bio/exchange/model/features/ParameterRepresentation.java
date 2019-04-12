package singa.bio.exchange.model.features;

import bio.singa.simulation.model.parameters.Parameter;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.evidence.EvidenceCache;
import singa.bio.exchange.model.evidence.EvidenceRepresentation;
import singa.bio.exchange.model.variation.Variable;
import tec.units.indriya.quantity.Quantities;

import javax.measure.Unit;

/**
 * @author cl
 */
public class ParameterRepresentation extends Variable<Double> {

    @JsonProperty
    private String identifier;

    @JsonProperty
    private double quantity;

    @JsonProperty
    private Unit unit;

    @JsonProperty
    private String origin;

    public ParameterRepresentation() {
    }

    public static ParameterRepresentation of(Parameter parameter) {
        ParameterRepresentation representation= new ParameterRepresentation();
        representation.setIdentifier(parameter.getIdentifier());
        representation.setQuantity(parameter.getQuantity().getValue().doubleValue());
        representation.setUnit(parameter.getQuantity().getUnit());
        representation.setOrigin(EvidenceRepresentation.of(parameter.getOrigin()).getIdentifier());
        return representation;
    }

    public Parameter toModel() {
        return new Parameter(getIdentifier(), Quantities.getQuantity(getQuantity(), getUnit()), EvidenceCache.get(origin));
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

}
