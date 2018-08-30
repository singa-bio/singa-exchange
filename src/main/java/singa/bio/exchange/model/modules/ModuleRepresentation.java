package singa.bio.exchange.model.modules;

import bio.singa.simulation.model.modules.UpdateModule;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import singa.bio.exchange.model.features.FeatureRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConcentrationModuleRepresentation.class, name = "concentration-based"),
        @JsonSubTypes.Type(value = DisplacementModuleRepresentation.class, name = "displacement-based"),
        @JsonSubTypes.Type(value = ReactionModuleRepresentation.class, name = "reaction-based"),
        @JsonSubTypes.Type(value = QualitativeModuleRepresentation.class, name = "qualitative")
})
@JsonPropertyOrder({ "name", "identifier" })
public abstract class ModuleRepresentation {

    @JsonProperty
    private String name;

    @JsonProperty
    private String identifier;

    @JsonProperty
    private List<FeatureRepresentation> features;

    public ModuleRepresentation() {
        features = new ArrayList<>();

    }

    public static ModuleRepresentation of(UpdateModule module) {
        return ModuleFactory.createRepresentation(module);
    }

    public UpdateModule toModel() {
        return ModuleFactory.createModule(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

}
