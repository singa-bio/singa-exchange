package bio.singa.exchange.features;

import bio.singa.features.model.Feature;
import bio.singa.features.model.Featureable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public abstract class FeatureCarrier {

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FeatureRepresentation> features;

    public FeatureCarrier() {
        features = new ArrayList<>();
    }

    public List<FeatureRepresentation> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureRepresentation> features) {
        this.features = features;
    }

    public void addFeature(FeatureRepresentation feature) {
        features.add(feature);
    }

    public void appendFeaturesTo(Featureable featureable) {
        for (FeatureRepresentation featureRepresentation : getFeatures()) {
            Feature feature = featureRepresentation.toModel();
            featureable.setFeature(feature);
        }
    }


}
