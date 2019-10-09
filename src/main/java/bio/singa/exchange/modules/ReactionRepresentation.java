package bio.singa.exchange.modules;

import bio.singa.exchange.Converter;
import bio.singa.exchange.entities.ReactantRepresentation;
import bio.singa.exchange.features.FeatureRepresentation;
import bio.singa.features.model.Feature;
import bio.singa.simulation.export.format.FormatReactionEquation;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.*;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantBehavior;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.RuleBasedReactantBehavior;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.StaticReactantBehavior;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import bio.singa.exchange.entities.ReactantSetRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
@JsonPropertyOrder({"type", "name", "identifier", "reaction", "entity-type", "kinetic-law", "reactants", "features"})
public class ReactionRepresentation extends ModuleRepresentation {

    @JsonProperty
    private List<ReactantSetRepresentation> reactants;

    @JsonProperty("kinetic-law")
    private KineticLawRepresentation kineticLaw;

    @JsonProperty
    private String reaction;

    public ReactionRepresentation() {
        reactants = new ArrayList<>();
    }

    public static ReactionRepresentation of(Reaction reaction) {
        ReactionRepresentation representation = new ReactionRepresentation();
        representation.setIdentifier(reaction.getIdentifier());
        representation.setName(reaction.getClass().getSimpleName());
        representation.setReaction(FormatReactionEquation.formatASCII(reaction));
        representation.setKineticLaw(fromKineticLaw(reaction.getKineticLaw()));
        representation.representationFromReactantBehavior(reaction);
        for (Feature<?> feature : reaction.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        return representation;
    }

    public static KineticLawRepresentation fromKineticLaw(KineticLaw kineticLaw) {
        KineticLawRepresentation kineticLawRepresentation;
        if (kineticLaw instanceof ReversibleKineticLaw) {
            kineticLawRepresentation = new PredefinedKineticLawRepresentation();
            kineticLawRepresentation.setLaw("reversible");
        } else if (kineticLaw instanceof IrreversibleKineticLaw) {
            kineticLawRepresentation = new PredefinedKineticLawRepresentation();
            kineticLawRepresentation.setLaw("irreversible");
        } else if (kineticLaw instanceof MichaelisMentenKineticLaw) {
            kineticLawRepresentation = new PredefinedKineticLawRepresentation();
            kineticLawRepresentation.setLaw("michaelis-menten");
        } else if (kineticLaw instanceof DynamicKineticLaw) {
            kineticLawRepresentation = DynamicKineticLawRepresentation.of(((DynamicKineticLaw) kineticLaw));
        } else {
            throw new IllegalArgumentException("The kinetic law " + kineticLaw.getClass() + " has no implemented representation.");
        }
        return kineticLawRepresentation;
    }

    public static KineticLaw toKineticLaw(KineticLawRepresentation kineticLaw, Reaction reaction) {
        if ("reversible".equals(kineticLaw.getLaw())) {
            return new ReversibleKineticLaw(reaction);
        } else if ("irreversible".equals(kineticLaw.getLaw())) {
            return new IrreversibleKineticLaw(reaction);
        } else if ("michaelis-menten".equals(kineticLaw.getLaw())) {
            return new MichaelisMentenKineticLaw(reaction);
        } else if (kineticLaw instanceof DynamicKineticLawRepresentation) {
            return ((DynamicKineticLawRepresentation) kineticLaw).toModel(reaction);
        }
        throw new IllegalArgumentException("The kinetic law " + kineticLaw + " has no implemented representation.");
    }

    public UpdateModule toModel() {
        // initialize reaction
        ReactionBuilder.GeneralReactionBuilder builder = new ReactionBuilder.GeneralReactionBuilder(Converter.current);
        builder.identifier(getIdentifier());
        Reaction reaction = builder.getModule();
        // set kinetic law
        reaction.setKineticLaw(toKineticLaw(getKineticLaw(), reaction));
        // set reactants and their behavior
        reaction.setReactantBehavior(reactantBehaviorFromRepresentation());
        // set features
        for (FeatureRepresentation feature : getFeatures()) {
            reaction.setFeature(feature.toModel());
        }
        // execute build (calls post construct and adds to referenced simulation)
        builder.build();
        return reaction;
    }

    private void representationFromReactantBehavior(Reaction reaction) {
        reaction.getReactantBehavior().getReactantSets().stream()
                .map(ReactantSetRepresentation::of)
                .forEach(reactantSetRepresentation -> reactants.add(reactantSetRepresentation));
    }

    private ReactantBehavior reactantBehaviorFromRepresentation() {
        if (reactants.size() == 1) {
            StaticReactantBehavior staticReactantBehavior = new StaticReactantBehavior();
            reactants.get(0).getSubstrates().stream()
                    .map(ReactantRepresentation::toModel)
                    .forEach(staticReactantBehavior::addSubstrate);
            reactants.get(0).getProducts().stream()
                    .map(ReactantRepresentation::toModel)
                    .forEach(staticReactantBehavior::addProduct);
            reactants.get(0).getCatalysts().stream()
                    .map(ReactantRepresentation::toModel)
                    .forEach(staticReactantBehavior::addCatalyst);
            return staticReactantBehavior;
        } else {
            RuleBasedReactantBehavior ruleBasedReactantBehavior = new RuleBasedReactantBehavior();
            reactants.stream()
                    .map(ReactantSetRepresentation::toModel)
                    .forEach(ruleBasedReactantBehavior::addReactantSet);
            return ruleBasedReactantBehavior;
        }
    }

    public List<ReactantSetRepresentation> getReactants() {
        return reactants;
    }

    public void setReactants(List<ReactantSetRepresentation> reactants) {
        this.reactants = reactants;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public KineticLawRepresentation getKineticLaw() {
        return kineticLaw;
    }

    public void setKineticLaw(KineticLawRepresentation kineticLaw) {
        this.kineticLaw = kineticLaw;
    }

}
