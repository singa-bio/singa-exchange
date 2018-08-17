package singa.bio.exchange.model.features;

import bio.singa.features.model.Feature;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.measure.Quantity;

/**
 * @author cl
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = QuantitativeFeatureRepresentation.class, name = "quantitative"),
        @JsonSubTypes.Type(value = QualitativeFeatureRepresentation.class, name = "qualitative"),
})
public abstract class FeatureRepresentation {

    @JsonProperty
    private String name;

    @JsonProperty
    private OriginRepresentation origin;

    public FeatureRepresentation() {

    }

    public static FeatureRepresentation of(Feature feature) {
        if (feature.getFeatureContent() instanceof Quantity) {
            QuantitativeFeatureRepresentation representation = new QuantitativeFeatureRepresentation();
            representation.setName(feature.getClass().getSimpleName());
            representation.setOrigin(OriginRepresentation.of(feature.getFeatureOrigin()));
            Quantity quanitity = (Quantity) feature.getFeatureContent();
            representation.setQuantity(quanitity.getValue().doubleValue());
            representation.setUnit(quanitity.getUnit());
            return representation;
        } else {
            QualitativeFeatureRepresentation representation = new QualitativeFeatureRepresentation();
            representation.setName(feature.getClass().getSimpleName());
            representation.setOrigin(OriginRepresentation.of(feature.getFeatureOrigin()));
            representation.setContent(feature.getFeatureContent().toString());
            return representation;
        }
    }

    public static Feature to(FeatureRepresentation representation) {
        return FeatureFactory.create(representation);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OriginRepresentation getOrigin() {
        return origin;
    }

    public void setOrigin(OriginRepresentation origin) {
        this.origin = origin;
    }

}
