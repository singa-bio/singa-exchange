package bio.singa.exchange.modules;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import bio.singa.simulation.model.simulation.Simulation;
import org.reflections.Reflections;
import bio.singa.exchange.Converter;
import bio.singa.exchange.IllegalConversionException;
import bio.singa.exchange.features.FeatureRepresentation;

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
            return ReactionRepresentation.of(((Reaction) module));
        }
        // other concentration based modules
        ConcentrationModuleRepresentation representation = new ConcentrationModuleRepresentation();
        performBaseSetup(module, representation);
        for (ChemicalEntity referencedEntity : module.getReferencedChemicalEntities()) {
            representation.addAffectedEntity(referencedEntity.getIdentifier());
        }
        return representation;
    }

    private static ModuleRepresentation createDisplacementBasedRepresentation(UpdateModule module) {
        // displacement based modules
        DisplacementModuleRepresentation representation = new DisplacementModuleRepresentation();
        performBaseSetup(module, representation);
        return representation;
    }

    private static ModuleRepresentation createQuantitativeBasedRepresentation(UpdateModule module) {
        // qualitative modules
        QualitativeModuleRepresentation representation = new QualitativeModuleRepresentation();
        performBaseSetup(module, representation);
        return representation;
    }

    private static void performBaseSetup(UpdateModule module, ModuleRepresentation representation) {
        representation.setName(module.getClass().getSimpleName());
        representation.setIdentifier(module.getIdentifier());
        for (Feature<?> feature : module.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
    }

    public static UpdateModule createModule(ModuleRepresentation representation) {
        if (representation instanceof ReactionRepresentation) {
            return representation.toModel();
        }
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
            module.setIdentifier(representation.getIdentifier());
            performBaseModelSetup(representation, module);
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
            module.setIdentifier(representation.getIdentifier());
            performBaseModelSetup(representation, module);
            return module;
        }
        throw new IllegalConversionException("Unable to convert module.");
    }

    private static void performBaseModelSetup(ModuleRepresentation representation, UpdateModule module) {
        module.setSimulation(Converter.current);
        // features
        for (FeatureRepresentation feature : representation.getFeatures()) {
            module.setFeature(feature.toModel());
        }
        Converter.current.addModule(module);
    }

}
