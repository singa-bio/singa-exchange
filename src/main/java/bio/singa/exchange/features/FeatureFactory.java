package bio.singa.exchange.features;

import bio.singa.exchange.IllegalConversionException;
import bio.singa.exchange.concentrations.InitialConcentrationRepresentation;
import bio.singa.exchange.evidence.EvidenceCache;
import bio.singa.exchange.sections.RegionCache;
import bio.singa.exchange.sections.SubsectionCache;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureRegistry;
import bio.singa.features.model.StringFeature;
import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.EntityRegistry;
import bio.singa.simulation.features.*;
import bio.singa.simulation.features.model.EntityFeature;
import bio.singa.simulation.features.model.MultiEntityFeature;
import bio.singa.simulation.features.model.MultiStringFeature;
import bio.singa.simulation.features.model.RegionFeature;
import bio.singa.simulation.model.concentrations.InitialConcentration;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import org.reflections.Reflections;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author cl
 */
public class FeatureFactory {

    private static Set<Class<? extends Feature>> featureTypes;

    public static void cacheSubtypes() {
        if (featureTypes == null) {
            Reflections reflections = new Reflections("bio.singa");
            featureTypes = reflections.getSubTypesOf(Feature.class);
        }
    }

    /**
     * Tries to instantiate the actual feature class from the general feature representation.
     *
     * @param representation The representation to instantiate.
     * @return The resulting feature.
     */
    public static <Type> Feature<?> create(FeatureRepresentation<Type> representation) {
        cacheSubtypes();
        // check cache
        Feature feature = FeatureRegistry.get(representation.getIdentifier());
        if (feature != null) {
            if (representation instanceof QuantitativeFeatureRepresentation) {
                // handle quantity based features
                QuantitativeFeatureRepresentation quantitative = (QuantitativeFeatureRepresentation) representation;
                Quantity content = Quantities.getQuantity(quantitative.getQuantity(), quantitative.getUnit());
                for (Type alternativeValue : representation.getAlternativeValues()) {
                    ComparableQuantity additionalValue = Quantities.getQuantity((Double) alternativeValue, content.getUnit());
                    if (!feature.getAlternativeContents().contains(additionalValue)) {
                        feature.addAlternativeContent(additionalValue);
                    }
                }
            } else {
                for (Type alternativeValue : representation.getAlternativeValues()) {
                    if (!feature.getAlternativeContents().contains(alternativeValue)) {
                        feature.addAlternativeContent(alternativeValue);
                    }
                }
            }
            return feature;
        }
        String className = representation.getName();
        // get the class
        Class<? extends Feature> featureClass = null;
        for (Class<? extends Feature> featureType : featureTypes) {
            if (featureType.getSimpleName().equals(className)) {
                featureClass = featureType;
            }
        }
        // if class cannot be found something went wrong
        Objects.requireNonNull(featureClass);
        if (representation instanceof QuantitativeFeatureRepresentation) {
            // handle quantity based features
            QuantitativeFeatureRepresentation quantitative = (QuantitativeFeatureRepresentation) representation;
            Quantity content = Quantities.getQuantity(quantitative.getQuantity(), quantitative.getUnit());
            feature = instantiate(featureClass, Quantity.class, content);
            for (Type alternativeValue : representation.getAlternativeValues()) {
                feature.addAlternativeContent(Quantities.getQuantity((Double) alternativeValue, content.getUnit()));
            }
        } else if (MultiEntityFeature.class.isAssignableFrom(featureClass)) {
            // handle multi-entity based features
            MultiEntityFeatureRepresentation multiEntity = (MultiEntityFeatureRepresentation) representation;
            List<ChemicalEntity> entities = new ArrayList<>();
            for (String entity : multiEntity.getEntities()) {
                entities.add(EntityRegistry.get(entity));
            }
            feature = instantiate(featureClass, List.class, entities);
        } else if (MultiStringFeature.class.isAssignableFrom(featureClass)) {
            // handle multi-entity based features
            MultiStringFeatureRepresentation multiEntity = (MultiStringFeatureRepresentation) representation;
            List<String> strings = new ArrayList<>(multiEntity.getStrings());
            feature = instantiate(featureClass, List.class, strings);
        } else if (InitialConcentrations.class.isAssignableFrom(featureClass)) {
            // handle multi-entity based features
            MultiConcentrationFeatureRepresentation multiConcentration = (MultiConcentrationFeatureRepresentation) representation;
            List<InitialConcentration> concentrations = new ArrayList<>();
            for (InitialConcentrationRepresentation concentrationRepresentation : multiConcentration.getConcentrations()) {
                concentrations.add(concentrationRepresentation.toModel());
            }
            feature = instantiate(featureClass, List.class, concentrations);
        } else if (representation instanceof QualitativeFeatureRepresentation) {
            if (StringFeature.class.isAssignableFrom(featureClass)) {
                // handle string based feature
                String content = ((QualitativeFeatureRepresentation) representation).fetchContent();
                feature = instantiate(featureClass, String.class, content);
            } else if (EntityFeature.class.isAssignableFrom(featureClass)) {
                // handle entity based features
                ChemicalEntity content = EntityRegistry.get(((QualitativeFeatureRepresentation) representation).fetchContent());
                feature = instantiate(featureClass, ChemicalEntity.class, content);
            } else if (RegionFeature.class.isAssignableFrom(featureClass)) {
                // handle region based features
                CellRegion content = RegionCache.get(((QualitativeFeatureRepresentation) representation).fetchContent());
                feature = instantiate(featureClass, CellRegion.class, content);
            } else if (AffectedSection.class.isAssignableFrom(featureClass)) {
                // handle region based features
                CellSubsection content = SubsectionCache.get(((QualitativeFeatureRepresentation) representation).fetchContent());
                feature = instantiate(featureClass, CellSubsection.class, content);
            }
        }


        if (feature != null) {
            feature.setIdentifier(representation.getIdentifier());
            for (String evidenceIdentifier : representation.getEvidence()) {
                feature.addEvidence(EvidenceCache.get(evidenceIdentifier));
            }
            return feature;
        }
        throw new IllegalConversionException("Unable to convert feature " + representation.getName());
    }

    private static Feature instantiate(Class<? extends Feature> featureClass, Class<?> constructorClass, Object construtorParameter) {
        Constructor<?> constructor;
        try {
            constructor = featureClass.getConstructor(constructorClass);
        } catch (NoSuchMethodException e) {
            throw new IllegalConversionException("Unable to get constructor for feature " + featureClass.getName(), e);
        }

        try {
            return (Feature) constructor.newInstance(construtorParameter);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalConversionException("Unable to create instance of feature " + featureClass.getName(), e);
        }
    }

}
