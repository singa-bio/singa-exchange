package singa.bio.exchange.model.modules;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.imlementations.ComplexBuildingReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.Reaction;
import bio.singa.simulation.model.modules.concentration.reactants.StoichiometricReactant;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import bio.singa.simulation.model.simulation.Simulation;
import org.reflections.Reflections;
import singa.bio.exchange.model.Converter;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.entities.ReactantRepresentation;
import singa.bio.exchange.model.features.FeatureRepresentation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
                for (ReactantRepresentation substrate : reactionRepresentation.getSubstrates()) {
                    reaction.addStochiometricReactant(ReactantRepresentation.to(substrate, true));
                }
                for (ReactantRepresentation product : reactionRepresentation.getProducts()) {
                    reaction.addStochiometricReactant(ReactantRepresentation.to(product, false));
                }
            }
            // complex reactions
            if (ComplexBuildingReaction.class.isAssignableFrom(moduleClass)) {
                ComplexBuildingReaction reaction = (ComplexBuildingReaction) module;
                ReactionModuleRepresentation reactionRepresentation = (ReactionModuleRepresentation) representation;
                // only one complex product
                ReactantRepresentation product = reactionRepresentation.getProducts().iterator().next();
                StoichiometricReactant complex = ReactantRepresentation.to(product, false);
                reaction.setComplex((ComplexedChemicalEntity) complex.getEntity());
                // substrates
                for (ReactantRepresentation substrate : reactionRepresentation.getSubstrates()) {
                    StoichiometricReactant stoichiometricReactant = ReactantRepresentation.to(substrate, true);
                    if (stoichiometricReactant.getPrefferedTopology().equals(complex.getPrefferedTopology())
                            && reaction.getBinderTopology() == null) {
                        reaction.setBinder(stoichiometricReactant.getEntity());
                        reaction.setBinderTopology(complex.getPrefferedTopology());
                    } else {
                        reaction.setBindee(stoichiometricReactant.getEntity());
                        reaction.setBindeeTopology(stoichiometricReactant.getPrefferedTopology());
                    }
                }
            }
            // identifier
            module.setIdentifier(representation.getIdentifier());
            // features
            for (FeatureRepresentation feature : representation.getFeatures()) {
                module.setFeature(FeatureRepresentation.to(feature));
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
                module.setFeature(FeatureRepresentation.to(feature));
            }
            Converter.current.getModules().add(module);
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
                module.setFeature(FeatureRepresentation.to(feature));
            }
            Converter.current.getModules().add(module);
            return module;
        }
        throw new IllegalConversionException("Unable to convert module.");
    }

}
