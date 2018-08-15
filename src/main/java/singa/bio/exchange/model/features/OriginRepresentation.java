package singa.bio.exchange.model.features;

import bio.singa.features.model.FeatureOrigin;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class OriginRepresentation {

    @JsonProperty
    private String shortName;

    public OriginRepresentation() {

    }

    public static OriginRepresentation of(FeatureOrigin orgin) {
        OriginRepresentation representation = new OriginRepresentation();
        representation.setShortName(orgin.getName());
        return representation;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
