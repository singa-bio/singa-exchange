package bio.singa.exchange.evidence;

import bio.singa.exchange.EnumTransformation;
import bio.singa.features.model.Evidence;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class EvidenceRepresentation {

    @JsonProperty
    private String type;

    @JsonProperty
    private String identifier;

    @JsonProperty
    private String description;

    public EvidenceRepresentation() {

    }

    public static EvidenceRepresentation of(Evidence evidence) {
        if (evidence == null) {
            return EvidenceRepresentation.of(Evidence.NO_EVIDENCE);
        }
        EvidenceRepresentation representation = new EvidenceRepresentation();
        representation.setType(EnumTransformation.fromSourceType(evidence.getType()));
        representation.setIdentifier(evidence.getIdentifier());
        representation.setDescription(evidence.getDescription());
        EvidenceCache.add(evidence);
        return representation;
    }

    public Evidence toModel() {
        return new Evidence(EnumTransformation.toSourceType(getType()), getIdentifier(), getDescription());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
