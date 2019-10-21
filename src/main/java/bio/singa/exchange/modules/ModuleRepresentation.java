package bio.singa.exchange.modules;

import bio.singa.exchange.features.FeatureCarrier;
import bio.singa.simulation.model.modules.UpdateModule;
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
        @JsonSubTypes.Type(value = ConcentrationModuleRepresentation.class, name = "concentration-based"),
        @JsonSubTypes.Type(value = DisplacementModuleRepresentation.class, name = "displacement-based"),
        @JsonSubTypes.Type(value = ReactionRepresentation.class, name = "reaction-based"),
        @JsonSubTypes.Type(value = QualitativeModuleRepresentation.class, name = "qualitative")
})
public abstract class ModuleRepresentation extends FeatureCarrier {

    @JsonProperty
    private String name;

    @JsonProperty
    private String identifier;

    public ModuleRepresentation() {
        super();
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



}
