package singa.bio.exchange.model.origins;

import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.model.FeatureOrigin.OriginType;
import com.fasterxml.jackson.annotation.JsonProperty;

import static bio.singa.features.model.FeatureOrigin.OriginType.*;

/**
 * @author cl
 */
public class OriginRepresentation {

    @JsonProperty
    private String type;

    @JsonProperty("short-descriptor")
    private String shortDescriptor;

    @JsonProperty("detailed-descriptor")
    private String detailedDescriptor;

    public OriginRepresentation() {

    }

    public static OriginRepresentation of(FeatureOrigin origin) {
        OriginRepresentation representation = new OriginRepresentation();
        representation.setType(fromType(origin.getOriginType()));
        representation.setShortDescriptor(origin.getName());
        representation.setDetailedDescriptor(origin.getPublication());
        OriginCache.add(origin);
        return representation;
    }

    public FeatureOrigin toModel() {
        return new FeatureOrigin(toType(getType()), getShortDescriptor(), getDetailedDescriptor());
    }

    private static String fromType(OriginType type) {
        switch (type) {
            case PREDICTION:
                return "prediction";
            case DATABASE:
                return "database";
            case LITERATURE:
                return "literature";
            default:
                return "manual";
        }
    }

    private static OriginType toType(String type) {
        switch (type) {
            case "prediction":
                return PREDICTION;
            case "database":
                return DATABASE;
            case "literature":
                return LITERATURE;
            default:
                return OriginType.MANUAL_ANNOTATION;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShortDescriptor() {
        return shortDescriptor;
    }

    public void setShortDescriptor(String shortDescriptor) {
        this.shortDescriptor = shortDescriptor;
    }

    public String getDetailedDescriptor() {
        return detailedDescriptor;
    }

    public void setDetailedDescriptor(String detailedDescriptor) {
        this.detailedDescriptor = detailedDescriptor;
    }

}
