package singa.bio.exchange.model.features;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class QualitativeFeatureRepresentation extends FeatureRepresentation {

    @JsonProperty
    private String content;

    public QualitativeFeatureRepresentation() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
