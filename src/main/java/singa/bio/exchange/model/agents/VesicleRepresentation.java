package singa.bio.exchange.model.agents;

import bio.singa.simulation.model.agents.pointlike.Vesicle;
import com.fasterxml.jackson.annotation.JsonProperty;
import tec.uom.se.quantity.Quantities;

import javax.measure.Unit;
import javax.measure.quantity.Length;

/**
 * @author cl
 */
public class VesicleRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty
    private VectorRepresentation position;

    @JsonProperty("radius-value")
    private double radiusValue;

    @JsonProperty("radius-Unit")
    private Unit<Length> radiusUnit;

    public VesicleRepresentation() {

    }

    public static VesicleRepresentation of(Vesicle agent) {
        VesicleRepresentation representation = new VesicleRepresentation();
        representation.setIdentifier(agent.getStringIdentifier());
        representation.setPosition(VectorRepresentation.of(agent.getCurrentPosition()));
        representation.setRadiusValue(agent.getRadius().getValue().doubleValue());
        representation.setRadiusUnit(agent.getRadius().getUnit());
        return representation;
    }

    public Vesicle toModel() {
        return new Vesicle(getIdentifier(), getPosition().toModel(), Quantities.getQuantity(getRadiusValue(), getRadiusUnit()));
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public VectorRepresentation getPosition() {
        return position;
    }

    public void setPosition(VectorRepresentation position) {
        this.position = position;
    }

    public double getRadiusValue() {
        return radiusValue;
    }

    public void setRadiusValue(double radiusValue) {
        this.radiusValue = radiusValue;
    }

    public Unit<Length> getRadiusUnit() {
        return radiusUnit;
    }

    public void setRadiusUnit(Unit<Length> radiusUnit) {
        this.radiusUnit = radiusUnit;
    }

}
