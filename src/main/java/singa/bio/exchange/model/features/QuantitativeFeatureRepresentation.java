package singa.bio.exchange.model.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.Feature;
import bio.singa.features.units.UnitRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.measure.Quantity;
import javax.measure.Unit;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class QuantitativeFeatureRepresentation extends FeatureRepresentation<Double> {

    @JsonProperty
    private double quantity;

    @JsonProperty
    private Unit unit;

    public QuantitativeFeatureRepresentation() {

    }

    public static QuantitativeFeatureRepresentation of(Feature<?> feature) {
        QuantitativeFeatureRepresentation representation = new QuantitativeFeatureRepresentation();
        representation.setName(feature.getClass().getSimpleName());
        Quantity quantity = (Quantity) feature.getContent();
        if (quantity.getUnit().isCompatible(MOLE_PER_LITRE)) {
            quantity = UnitRegistry.humanReadable(quantity);
        }
        representation.setQuantity(quantity.getValue().doubleValue());
        representation.setUnit(quantity.getUnit());
        representation.addEvidence(feature.getAllEvidence());
        if (representation.getEvidence().isEmpty()) {
            representation.addEvidence(Evidence.NO_EVIDENCE);
        }
        return representation;
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
