package singa.bio.exchange.model.features;

import bio.singa.chemistry.MultiEntityFeature;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import singa.bio.exchange.model.origins.OriginRepresentation;

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
        @JsonSubTypes.Type(value = MultiEntityFeatureRepresentation.class, name = "qualitative-multi"),
})
public abstract class FeatureRepresentation {

    @JsonProperty
    private String name;

    @JsonProperty
    private String origin;

    public FeatureRepresentation() {

    }

    public static FeatureRepresentation of(Feature feature) {
        if (feature.getFeatureContent() instanceof Quantity) {
            QuantitativeFeatureRepresentation representation = new QuantitativeFeatureRepresentation();
            representation.setName(feature.getClass().getSimpleName());
            representation.setOrigin(OriginRepresentation.of(feature.getFeatureOrigin()).getShortDescriptor());
            Quantity quantity = (Quantity) feature.getFeatureContent();
            representation.setQuantity(quantity.getValue().doubleValue());
            representation.setUnit(quantity.getUnit());
            return representation;
        } else if (feature instanceof MultiEntityFeature) {
            MultiEntityFeatureRepresentation representation = new MultiEntityFeatureRepresentation();
            representation.setName(feature.getClass().getSimpleName());
            representation.setOrigin(OriginRepresentation.of(feature.getFeatureOrigin()).getShortDescriptor());
            for (ChemicalEntity chemicalEntity : ((MultiEntityFeature) feature).getFeatureContent()) {
                representation.addEntity(chemicalEntity.getIdentifier().toString());
            }
            return representation;
        } else {
            QualitativeFeatureRepresentation representation = new QualitativeFeatureRepresentation();
            representation.setName(feature.getClass().getSimpleName());
            representation.setOrigin(OriginRepresentation.of(feature.getFeatureOrigin()).getShortDescriptor());
            // entity feature
            if (feature.getFeatureContent() instanceof ChemicalEntity) {
                representation.setContent(((ChemicalEntity) feature.getFeatureContent()).getIdentifier().toString());
            } else {
                representation.setContent(feature.getFeatureContent().toString());
            }
            return representation;
        }
    }

    public Feature toModel() {
        return FeatureFactory.create(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

}
