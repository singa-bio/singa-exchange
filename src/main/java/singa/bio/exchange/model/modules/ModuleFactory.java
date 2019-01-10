package singa.bio.exchange.model.modules;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.ScalableQuantitativeFeature;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.imlementations.ComplexBuildingReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.DynamicReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.Reaction;
import bio.singa.simulation.model.modules.concentration.reactants.KineticLaw;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.reactants.ReactantRole;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import bio.singa.simulation.model.parameters.Parameter;
import bio.singa.simulation.model.simulation.Simulation;
import org.reflections.Reflections;
import singa.bio.exchange.model.Converter;
import singa.bio.exchange.model.EnumTransformation;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.entities.ReactantRepresentation;
import singa.bio.exchange.model.features.FeatureRepresentation;
import singa.bio.exchange.model.features.ParameterRepresentation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author cl
 */
public class ModuleFactory {

    private static Set<Class<? extends UpdateModule>> moduleTypes;

    public static void cacheSubtypes() {
        if (moduleTypes == null) {
            Reflections reflections = new Reflections("bio.singa");
            moduleTypes = reflections.getSubTypesOf(UpdateModule.class);
        }
    }

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
            for (Reactant substrate : reaction.getSubstrates()) {
                if (substrate.getRole() == null) {
                    substrate.setRole(ReactantRole.SUBSTRATE);
                }
                representation.addReactant(ReactantRepresentation.of(substrate));
            }
            for (Reactant product : reaction.getProducts()) {
                if (product.getRole() == null) {
                    product.setRole(ReactantRole.PRODUCT);
                }
                representation.addReactant(ReactantRepresentation.of(product));
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
            bindee.setRole(EnumTransformation.fromRole(ReactantRole.SUBSTRATE));
            bindee.setStoichiometricNumber(1.0);
            bindee.setPreferredTopology(reaction.getBindeeTopology());

            ReactantRepresentation binder = new ReactantRepresentation();
            binder.setIdentifier(reaction.getBinder().getIdentifier().toString());
            binder.setRole(EnumTransformation.fromRole(ReactantRole.SUBSTRATE));
            binder.setStoichiometricNumber(1.0);
            binder.setPreferredTopology(reaction.getBinderTopology());

            ReactantRepresentation complex = new ReactantRepresentation();
            complex.setIdentifier(reaction.getComplex().getIdentifier().toString());
            complex.setRole(EnumTransformation.fromRole(ReactantRole.PRODUCT));
            complex.setStoichiometricNumber(1.0);
            complex.setPreferredTopology(reaction.getBinderTopology());

            representation.addReactant(bindee);
            representation.addReactant(binder);
            representation.addReactant(complex);

            return representation;
        } else if (module instanceof DynamicReaction) {
            // reactions
            KineticLawModuleRepresentation representation = new KineticLawModuleRepresentation();
            representation.setName(module.getClass().getSimpleName());
            representation.setIdentifier(module.getIdentifier());
            DynamicReaction reaction = (DynamicReaction) module;
            representation.setLaw(reaction.getKineticLaw().getExpressionString());
            for (Map.Entry<String, Reactant> reactantEntry : reaction.getKineticLaw().getConcentrationMap().entrySet()) {
                representation.addReactant(reactantEntry.getKey(), ReactantRepresentation.of(reactantEntry.getValue()));
            }
            for (Map.Entry<String, Parameter> constantEntry : reaction.getKineticLaw().getParameterMap().entrySet()) {
                representation.addConstant(constantEntry.getKey(), ParameterRepresentation.of(constantEntry.getValue()));
            }
            for (Map.Entry<String, ScalableQuantitativeFeature> featureEntry : reaction.getKineticLaw().getFeatureMap().entrySet()) {
                representation.addKineticFeature(featureEntry.getKey(), FeatureRepresentation.of(featureEntry.getValue()));
            }
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
            representation.addAffectedEntity(referencedEntity.getIdentifier().getContent());
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

    public static UpdateModule createModule(ModuleRepresentation representation) {
        cacheSubtypes();
        String className = representation.getName();
        // get the class
        Class<? extends UpdateModule> moduleClass = null;
        for (Class<? extends UpdateModule> moduleType : moduleTypes) {
            if (moduleType.getSimpleName().equals(className)) {
                moduleClass = moduleType;
            }
        }
        // if class cannot be found something went wrong
        Objects.requireNonNull(moduleClass);
        if (ConcentrationBasedModule.class.isAssignableFrom(moduleClass)) {
            ModuleBuilder builder;
            try {
                builder = (ModuleBuilder) moduleClass.getDeclaredMethod("getBuilder", Simulation.class).invoke(null, Converter.current);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalConversionException("Unable to get builder for concentration based module " + className, e);
            }
            // ConcentrationBasedModule module = builder.createModule(Converter.current);
            ConcentrationBasedModule module = builder.getModule();
            // classical reactions
            if (Reaction.class.isAssignableFrom(moduleClass)) {
                Reaction reaction = (Reaction) module;
                ReactionModuleRepresentation reactionRepresentation = (ReactionModuleRepresentation) representation;
                // add substrates and products
                for (ReactantRepresentation reactant : reactionRepresentation.getReactants()) {
                    reaction.addStochiometricReactant(reactant.toModel());
                }
            }
            // complex reactions
            if (ComplexBuildingReaction.class.isAssignableFrom(moduleClass)) {
                ComplexBuildingReaction reaction = (ComplexBuildingReaction) module;
                ReactionModuleRepresentation reactionRepresentation = (ReactionModuleRepresentation) representation;
                // get complex first
                ReactantRepresentation compex = null;
                for (ReactantRepresentation reactantRepresentation : reactionRepresentation.getReactants()) {
                    Reactant reactant = reactantRepresentation.toModel();
                    if (EnumTransformation.toRole(reactantRepresentation.getRole()) == ReactantRole.PRODUCT) {
                        reaction.setComplex((ComplexedChemicalEntity) reactant.getEntity());
                        compex = reactantRepresentation;
                    }
                }
                if (compex == null) {
                    throw new IllegalConversionException("Complex Building Reactions require at least one reactant with role product.");
                }
                // substrates
                for (ReactantRepresentation reactantRepresentation : reactionRepresentation.getReactants()) {
                    Reactant reactant = reactantRepresentation.toModel();
                    if (EnumTransformation.toRole(reactantRepresentation.getRole()) == ReactantRole.SUBSTRATE) {
                        if (reactant.getPreferredTopology().equals(EnumTransformation.toTopology(compex.getPreferredTopology()))
                                && reaction.getBinderTopology() == null) {
                            reaction.setBinder(reactant.getEntity());
                            reaction.setBinderTopology(reactant.getPreferredTopology());
                        } else {
                            reaction.setBindee(reactant.getEntity());
                            reaction.setBindeeTopology(reactant.getPreferredTopology());
                        }
                    }
                }

            }
            // dynamic reactions
            if (DynamicReaction.class.isAssignableFrom(moduleClass)) {
                DynamicReaction reaction = (DynamicReaction) module;
                KineticLawModuleRepresentation reactionRepresentation = (KineticLawModuleRepresentation) representation;
                KineticLaw kineticLaw = new KineticLaw(reactionRepresentation.getLaw());
                for (Map.Entry<String, ReactantRepresentation> representationEntry : reactionRepresentation.getReactants().entrySet()) {
                    kineticLaw.referenceReactant(representationEntry.getKey(), representationEntry.getValue().toModel());
                }
                for (Map.Entry<String, ParameterRepresentation> representationEntry : reactionRepresentation.getConstants().entrySet()) {
                    kineticLaw.referenceParameter(representationEntry.getValue().toModel());
                }
                for (Map.Entry<String, FeatureRepresentation> representationEntry : reactionRepresentation.getKineticFeatures().entrySet()) {
                    kineticLaw.referenceFeature(representationEntry.getKey(), (ScalableQuantitativeFeature) representationEntry.getValue().toModel());
                }
            }
            // identifier
            module.setIdentifier(representation.getIdentifier());
            // features
            for (FeatureRepresentation feature : representation.getFeatures()) {
                module.setFeature(feature.toModel());
            }
            return builder.build();
        } else if (DisplacementBasedModule.class.isAssignableFrom(moduleClass)) {
            // displacement based
            Constructor<?> constructor;
            try {
                constructor = moduleClass.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalConversionException("Unable to get constructor for displacement module.", e);
            }
            DisplacementBasedModule module;
            try {
                module = (DisplacementBasedModule) constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalConversionException("Unable to create instance of displacement module.", e);
            }
            // identifier
            module.setIdentifier(representation.getIdentifier());
            module.setSimulation(Converter.current);
            // features
            for (FeatureRepresentation feature : representation.getFeatures()) {
                module.setFeature(feature.toModel());
            }
            Converter.current.addModule(module);
            return module;
        } else if (QualitativeModule.class.isAssignableFrom(moduleClass)) {
            // displacement based
            Constructor<?> constructor;
            try {
                constructor = moduleClass.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalConversionException("Unable to get constructor for displacement module.", e);
            }
            QualitativeModule module;
            try {
                module = (QualitativeModule) constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalConversionException("Unable to create instance of displacement module.", e);
            }
            // identifier
            module.setIdentifier(representation.getIdentifier());
            module.setSimulation(Converter.current);
            // features
            for (FeatureRepresentation feature : representation.getFeatures()) {
                module.setFeature(feature.toModel());
            }
            Converter.current.addModule(module);
            return module;
        }
        throw new IllegalConversionException("Unable to convert module.");
    }

}
