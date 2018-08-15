package singa.bio.exchange.model.features;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.measure.Unit;

/**
 * @author cl
 */
public class QuantitativeFeatureRepresentation extends FeatureRepresentation {

    @JsonProperty
    private double quantity;

    @JsonProperty
    private Unit unit;

    public QuantitativeFeatureRepresentation() {

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

}
