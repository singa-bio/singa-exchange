package singa.bio.exchange.model.modules;

import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.entities.ReactantRepresentation;
import singa.bio.exchange.model.features.ConstantRepresentation;
import singa.bio.exchange.model.features.FeatureRepresentation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class KineticLawModuleRepresentation extends ModuleRepresentation {

    @JsonProperty
    private String law;

    @JsonProperty
    private Map<String, ConstantRepresentation> constants;

    @JsonProperty("kinetic-features")
    private Map<String, FeatureRepresentation> kineticFeatures;

    @JsonProperty
    private Map<String, ReactantRepresentation> reactants;

    public KineticLawModuleRepresentation() {
        constants = new HashMap<>();
        kineticFeatures = new HashMap<>();
        reactants = new HashMap<>();
    }

    public String getLaw() {
        return law;
    }

    public void setLaw(String law) {
        this.law = law;
    }

    public Map<String, ConstantRepresentation> getConstants() {
        return constants;
    }

    public void setConstants(Map<String, ConstantRepresentation> constants) {
        this.constants = constants;
    }

    public void addConstant(String identifier, ConstantRepresentation constantRepresentation) {
        constants.put(identifier, constantRepresentation);
    }

    public Map<String, FeatureRepresentation> getKineticFeatures() {
        return kineticFeatures;
    }

    public void setKineticFeatures(Map<String, FeatureRepresentation> kineticFeatures) {
        this.kineticFeatures = kineticFeatures;
    }

    public void addKineticFeature(String identifier, FeatureRepresentation kineticFeature) {
        kineticFeatures.put(identifier, kineticFeature);
    }

    public Map<String, ReactantRepresentation> getReactants() {
        return reactants;
    }

    public void setReactants(Map<String, ReactantRepresentation> reactants) {
        this.reactants = reactants;
    }

    public void addReactant(String identifier, ReactantRepresentation reactant) {
        reactants.put(identifier, reactant);
    }

}
