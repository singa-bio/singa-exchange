package singa.bio.exchange.model.modules;

import bio.singa.features.model.ScalableQuantitativeFeature;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.DynamicKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.parameters.Parameter;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.entities.StaticReactantRepresentation;
import singa.bio.exchange.model.features.FeatureRepresentation;
import singa.bio.exchange.model.features.ParameterRepresentation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class DynamicKineticLawRepresentation extends KineticLawRepresentation {

    @JsonProperty
    private Map<String, ParameterRepresentation> constants;

    @JsonProperty("kinetic-features")
    private Map<String, FeatureRepresentation> kineticFeatures;

    @JsonProperty
    private Map<String, StaticReactantRepresentation> reactants;

    public DynamicKineticLawRepresentation() {
        constants = new HashMap<>();
        kineticFeatures = new HashMap<>();
        reactants = new HashMap<>();
    }

    public static DynamicKineticLawRepresentation of(DynamicKineticLaw kineitcLaw) {
        DynamicKineticLawRepresentation representation = new DynamicKineticLawRepresentation();
        representation.setLaw(kineitcLaw.getExpressionString());
        for (Map.Entry<String, Reactant> reactantEntry : kineitcLaw.getConcentrationMap().entrySet()) {
            representation.addReactant(reactantEntry.getKey(), StaticReactantRepresentation.of(reactantEntry.getValue()));
        }
        for (Map.Entry<String, Parameter> constantEntry : kineitcLaw.getParameterMap().entrySet()) {
            representation.addConstant(constantEntry.getKey(), ParameterRepresentation.of(constantEntry.getValue()));
        }
        for (Map.Entry<String, ScalableQuantitativeFeature> featureEntry : kineitcLaw.getFeatureMap().entrySet()) {
            representation.addKineticFeature(featureEntry.getKey(), FeatureRepresentation.of(featureEntry.getValue()));
        }
        return representation;
    }

    public DynamicKineticLaw toModel(Reaction reaction) {
        DynamicKineticLaw kineticLaw = new DynamicKineticLaw(reaction, getLaw());
        for (Map.Entry<String, StaticReactantRepresentation> representationEntry : getReactants().entrySet()) {
            kineticLaw.referenceReactant(representationEntry.getKey(), representationEntry.getValue().toModel());
        }
        for (Map.Entry<String, ParameterRepresentation> representationEntry : getConstants().entrySet()) {
            kineticLaw.referenceParameter(representationEntry.getValue().toModel());
        }
        for (Map.Entry<String, FeatureRepresentation> representationEntry : getKineticFeatures().entrySet()) {
            kineticLaw.referenceFeature(representationEntry.getKey(), (ScalableQuantitativeFeature) representationEntry.getValue().toModel());
        }
        return kineticLaw;
    }

    public Map<String, ParameterRepresentation> getConstants() {
        return constants;
    }

    public void setConstants(Map<String, ParameterRepresentation> constants) {
        this.constants = constants;
    }

    public void addConstant(String identifier, ParameterRepresentation constantRepresentation) {
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

    public Map<String, StaticReactantRepresentation> getReactants() {
        return reactants;
    }

    public void setReactants(Map<String, StaticReactantRepresentation> reactants) {
        this.reactants = reactants;
    }

    public void addReactant(String identifier, StaticReactantRepresentation reactant) {
        reactants.put(identifier, reactant);
    }

}
