package singa.bio.exchange.model.modules;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexModification;
import bio.singa.features.model.Feature;
import bio.singa.simulation.export.format.FormatReactionEquation;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.*;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.*;
import bio.singa.simulation.model.sections.CellTopology;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import singa.bio.exchange.model.Converter;
import singa.bio.exchange.model.EnumTransformation;
import singa.bio.exchange.model.entities.*;
import singa.bio.exchange.model.features.FeatureRepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author cl
 */
@JsonPropertyOrder({"type", "name", "identifier", "reaction", "entity-type", "kinetic-law", "reactants", "features"})
public class ReactionRepresentation extends ModuleRepresentation {

    @JsonProperty
    private List<ReactantRepresentation> reactants;

    @JsonProperty("kinetic-law")
    private KineticLawRepresentation kineticLaw;

    @JsonProperty("entity-type")
    private String entityType;

    @JsonProperty
    private String reaction;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> targets;

    public ReactionRepresentation() {
        reactants = new ArrayList<>();
        targets = new HashMap<>();
    }

    public static ReactionRepresentation of(Reaction reaction) {
        ReactionRepresentation representation = new ReactionRepresentation();
        representation.setIdentifier(reaction.getIdentifier());
        representation.setName(reaction.getClass().getSimpleName());
        representation.setReaction(FormatReactionEquation.formatASCII(reaction));
        representation.setKineticLaw(fromKinteicLaw(reaction.getKineticLaw()));
        representation.processReactantBehavior(reaction);
        for (Feature<?> feature : reaction.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        return representation;
    }

    public UpdateModule toModel() {
        // initialize reaction
        ReactionBuilder.GeneralReactionBuilder builder = new ReactionBuilder.GeneralReactionBuilder(Converter.current);
        builder.identifier(getIdentifier());
        Reaction reaction = builder.getModule();
        // set kinetic law
        reaction.setKineticLaw(toKineticLaw(getKineticLaw(), reaction));
        // set reactants and their behavior
        reaction.setReactantBehavior(createReactantBehavior());
        // set features
        for (FeatureRepresentation feature : getFeatures()) {
            reaction.setFeature(feature.toModel());
        }
        // execute build (calls post construct and adds to referenced simulation)
        builder.build();
        return reaction;
    }

    public static KineticLawRepresentation fromKinteicLaw(KineticLaw kineticLaw) {
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

    private void processReactantBehavior(Reaction reaction) {
        for (Reactant substrate : reaction.getReactantBehavior().getSubstrates()) {
            reactants.add(StaticReactantRepresentation.of(substrate));
        }
        for (Reactant product : reaction.getReactantBehavior().getProducts()) {
            reactants.add(StaticReactantRepresentation.of(product));
        }
        for (Reactant catalyst : reaction.getReactantBehavior().getCatalysts()) {
            reactants.add(StaticReactantRepresentation.of(catalyst));
        }
        if (reaction.getReactantBehavior() instanceof DynamicReactantBehavior) {
            DynamicReactantBehavior reactantBehavior = (DynamicReactantBehavior) reaction.getReactantBehavior();
            for (DynamicChemicalEntity dynamicSubstrate : reactantBehavior.getDynamicSubstrates()) {
                reactants.add(DynamicSubstrateRepresentation.of(dynamicSubstrate));
            }
            for (Map.Entry<String, List<ComplexModification>> entry : reactantBehavior.getDynamicProducts().entrySet()) {
                reactants.add(DynamicProductRepresentation.of(entry.getKey(), entry.getValue()));
            }
            if (reactantBehavior.isDynamicComplex()) {
                reactants.add(DynamicProductRepresentation.dynamicComplex());
            }
            setEntityType("dynamic");
            for (Map.Entry<ChemicalEntity, CellTopology> entry : reactantBehavior.getTargetTopologies().entrySet()) {
                targets.put(EntityRepresentation.of(entry.getKey()).getPrimaryIdentifier(), EnumTransformation.fromTopology(entry.getValue()));
            }
        } else if (reaction.getReactantBehavior() instanceof StaticReactantBehavior) {
            setEntityType("static");
        } else {
            throw new IllegalArgumentException("The reactant type " + reaction.getReactantBehavior().getClass() + " has no implemented representation.");
        }
    }

    private ReactantBehavior createReactantBehavior() {
        if (getEntityType().equals("dynamic")) {
            DynamicReactantBehavior dynamicReactantBehavior = new DynamicReactantBehavior();
            for (ReactantRepresentation representation : reactants) {
                if (representation instanceof StaticReactantRepresentation) {
                    dynamicReactantBehavior.addReactant(((StaticReactantRepresentation) representation).toModel());
                } else if (representation instanceof DynamicSubstrateRepresentation) {
                    dynamicReactantBehavior.addDynamicSubstrate(((DynamicSubstrateRepresentation) representation).toModel());
                } else if (representation instanceof DynamicProductRepresentation) {
                    DynamicProductRepresentation product = ((DynamicProductRepresentation) representation);
                    if (product.getRelevantEntity().equals("[complex]")) {
                        dynamicReactantBehavior.setDynamicComplex(true);
                    } else {
                        List<ComplexModification> modifications = product.getModifications().stream()
                                .map(ModificationRepresentation::toModel)
                                .collect(Collectors.toList());
                        dynamicReactantBehavior.addDynamicProduct(product.getRelevantEntity(), modifications);
                    }
                }
            }
            if (targets.size() > 0) {
                for (Map.Entry<String, String> entry : targets.entrySet()) {
                    dynamicReactantBehavior.addTargetTopology(EntityCache.get(entry.getKey()), EnumTransformation.toTopology(entry.getValue()));
                }
            }
            return dynamicReactantBehavior;
        } else if (getEntityType().equals("static")) {
            StaticReactantBehavior staticReactantBehavior = new StaticReactantBehavior();
            for (ReactantRepresentation representation : reactants) {
                if (representation instanceof StaticReactantRepresentation) {
                    staticReactantBehavior.addReactant(((StaticReactantRepresentation) representation).toModel());
                }
            }
            return staticReactantBehavior;
        } else {
            throw new IllegalArgumentException("The reactant type " + getEntityType().getClass() + " has no implemented representation.");
        }
    }


    public List<ReactantRepresentation> getReactants() {
        return reactants;
    }

    public void setReactants(List<ReactantRepresentation> reactants) {
        this.reactants = reactants;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public KineticLawRepresentation getKineticLaw() {
        return kineticLaw;
    }

    public void setKineticLaw(KineticLawRepresentation kineticLaw) {
        this.kineticLaw = kineticLaw;
    }

}
