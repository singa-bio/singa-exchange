package bio.singa.exchange.agents;

import bio.singa.exchange.sections.RegionCache;
import bio.singa.exchange.sections.RegionRepresentation;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Unit;
import javax.measure.quantity.Length;

/**
 * @author cl
 */
public class VesicleRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty
    private String region;

    @JsonProperty
    private String state = VesicleStateRegistry.UNATTACHED;

    @JsonProperty
    private VectorRepresentation position;

    @JsonProperty("radius-value")
    private double radiusValue;

    @JsonProperty("radius-Unit")
    private Unit<Length> radiusUnit;

    public VesicleRepresentation() {

    }

    public static VesicleRepresentation of(Vesicle vesicle) {
        VesicleRepresentation representation = new VesicleRepresentation();
        representation.setIdentifier(vesicle.getStringIdentifier());
        representation.setRegion(RegionRepresentation.of(vesicle.getRegion()).getIdentifier());
        representation.setState(vesicle.getState());
        representation.setPosition(VectorRepresentation.of(vesicle.getPosition()));
        representation.setRadiusValue(vesicle.getRadius().getValue().doubleValue());
        representation.setRadiusUnit(vesicle.getRadius().getUnit());
        return representation;
    }

    public Vesicle toModel() {
        Vesicle vesicle;
        if (region == null) {
            vesicle = new Vesicle(getPosition().toModel(), Quantities.getQuantity(getRadiusValue(), getRadiusUnit()));
            vesicle.setState(getState());
            vesicle.setIdentifier(getIdentifier());
        } else {
            vesicle = new Vesicle(RegionCache.get(getRegion()), getPosition().toModel(), Quantities.getQuantity(getRadiusValue(), getRadiusUnit()));
            vesicle.setState(getState());
            vesicle.setIdentifier(getIdentifier());
        }
        VesicleCache.add(vesicle);
        return vesicle;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
