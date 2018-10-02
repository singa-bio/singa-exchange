package singa.bio.exchange.model.features;

import bio.singa.simulation.features.Constant;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.origins.OriginCache;
import singa.bio.exchange.model.origins.OriginRepresentation;

/**
 * @author cl
 */
public class ConstantRepresentation {

    @JsonProperty
    private double value;

    @JsonProperty
    private String origin;

    public ConstantRepresentation() {
    }

    public static ConstantRepresentation of(Constant constant) {
        ConstantRepresentation representation= new ConstantRepresentation();
        representation.setValue(constant.getValue());
        representation.setOrigin(OriginRepresentation.of(constant.getOrigin()).getShortDescriptor());
        return representation;
    }

    public Constant toModel() {
        return new Constant(getValue(), OriginCache.get(origin));
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

}
