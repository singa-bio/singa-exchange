package singa.bio.exchange.model.features;

import bio.singa.simulation.model.parameters.Parameter;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.origins.OriginCache;
import singa.bio.exchange.model.origins.OriginRepresentation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Unit;

/**
 * @author cl
 */
public class ParameterRepresentation {

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
        representation.setOrigin(OriginRepresentation.of(parameter.getOrigin()).getShortDescriptor());
        return representation;
    }

    public Parameter toModel() {
        return new Parameter(getIdentifier(), Quantities.getQuantity(getQuantity(), getUnit()), OriginCache.get(origin));
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
