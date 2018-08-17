package singa.bio.exchange.model.modules;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.imlementations.ComplexBuildingReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.Reaction;
import bio.singa.simulation.model.modules.concentration.reactants.StoichiometricReactant;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import singa.bio.exchange.model.entities.ReactantRepresentation;
import singa.bio.exchange.model.features.FeatureRepresentation;

/**
 * @author cl
 */
public class ModuleFactory {

    public static ModuleRepresentation createRepresentation(UpdateModule module) {
        if (module instanceof ConcentrationBasedModule) {
            return createConcentrationBasedRepresentation(module);
        } else if (module instanceof DisplacementBasedModule) {
            return createDisplacementBasedRepresentation(module);
        } else {
            return createQuantitativeBasedRepresentation(module);
        }
    }

    private static ModuleRepresentation createConcentrationBasedRepresentation(UpdateModule module) {
        if (module instanceof Reaction) {
            // reactions
            ReactionModuleRepresentation representation = new ReactionModuleRepresentation();
            representation.setName(module.getClass().getSimpleName());
            representation.setIdentifier(module.getIdentifier());
            for (Feature<?> feature : module.getFeatures()) {
                representation.addFeature(FeatureRepresentation.of(feature));
            }
            Reaction reaction = (Reaction) module;
            representation.setReaction(reaction.getReactionString());
            for (StoichiometricReactant substrate : reaction.getSubstrates()) {
                representation.addSubstrate(ReactantRepresentation.of(substrate));
            }
            for (StoichiometricReactant product : reaction.getProducts()) {
                representation.addProduct(ReactantRepresentation.of(product));
            }
            return representation;
        } else if (module instanceof ComplexBuildingReaction) {
            // complex building reactions
            ReactionModuleRepresentation representation = new ReactionModuleRepresentation();
            representation.setName(module.getClass().getSimpleName());
            representation.setIdentifier(module.getIdentifier());
            for (Feature<?> feature : module.getFeatures()) {
                representation.addFeature(FeatureRepresentation.of(feature));
            }
            ComplexBuildingReaction reaction = (ComplexBuildingReaction) module;
            representation.setReaction(reaction.getReactionString());
            
            ReactantRepresentation bindee = new ReactantRepresentation();
            bindee.setIdentifier(reaction.getBindee().getIdentifier().toString());
            bindee.setStoichiometricNumber(1.0);
            bindee.setPreferredTopology(reaction.getBindeeTopology());

            ReactantRepresentation binder = new ReactantRepresentation();
            binder.setIdentifier(reaction.getBinder().getIdentifier().toString());
            binder.setStoichiometricNumber(1.0);
            binder.setPreferredTopology(reaction.getBinderTopology());

            ReactantRepresentation complex = new ReactantRepresentation();
            complex.setIdentifier(reaction.getComplex().getIdentifier().toString());
            complex.setStoichiometricNumber(1.0);
            complex.setPreferredTopology(reaction.getBinderTopology());

            representation.addSubstrate(bindee);
            representation.addSubstrate(binder);
            representation.addProduct(complex);

            return representation;
        }
        // other concentration based modules
        ConcentrationModuleRepresentation representation = new ConcentrationModuleRepresentation();
        representation.setName(module.getClass().getSimpleName());
        representation.setIdentifier(module.getIdentifier());
        for (Feature<?> feature : module.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        for (ChemicalEntity referencedEntity : module.getReferencedEntities()) {
            representation.addAffectedEntity(referencedEntity.getIdentifier().getIdentifier());
        }
        return representation;
    }

    private static ModuleRepresentation createDisplacementBasedRepresentation(UpdateModule module) {
        // displacement based modules
        DisplacementModuleRepresentation representation = new DisplacementModuleRepresentation();
        representation.setName(module.getClass().getSimpleName());
        representation.setIdentifier(module.getIdentifier());
        for (Feature<?> feature : module.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        return representation;
    }

    private static ModuleRepresentation createQuantitativeBasedRepresentation(UpdateModule module) {
        // qualitative modules
        QualitativeModuleRepresentation representation = new QualitativeModuleRepresentation();
        representation.setName(module.getClass().getSimpleName());
        representation.setIdentifier(module.getIdentifier());
        for (Feature<?> feature : module.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        return representation;
    }

}
