package singa.bio.exchange.model.modules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author cl
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DynamicKineticLawRepresentation.class, name = "dynamic"),
        @JsonSubTypes.Type(value = PredefinedKineticLawRepresentation.class, name = "predefined")
})
public abstract class KineticLawRepresentation {

    @JsonProperty
    private String law;

    public KineticLawRepresentation() {
    }

    public String getLaw() {
        return law;
    }

    public void setLaw(String law) {
        this.law = law;
    }

}
