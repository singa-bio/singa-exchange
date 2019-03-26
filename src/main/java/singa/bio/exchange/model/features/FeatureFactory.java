package singa.bio.exchange.model.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.StringFeature;
import bio.singa.simulation.features.EntityFeature;
import bio.singa.simulation.features.MultiEntityFeature;
import bio.singa.simulation.features.MultiStringFeature;
import bio.singa.simulation.features.RegionFeature;
import bio.singa.simulation.model.sections.CellRegion;
import org.reflections.Reflections;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.entities.EntityCache;
import singa.bio.exchange.model.evidence.EvidenceCache;
import singa.bio.exchange.model.sections.RegionCache;
import tec.uom.se.quantity.Quantities;

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
    public static <Type> Feature create(FeatureRepresentation<Type> representation) {
        cacheSubtypes();
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
        Feature feature = null;
        if (representation instanceof QuantitativeFeatureRepresentation) {
            // handle quantity based features
            QuantitativeFeatureRepresentation quantitative = (QuantitativeFeatureRepresentation) representation;
            Quantity content = Quantities.getQuantity(quantitative.getQuantity(), quantitative.getUnit());
            feature = instantiate(featureClass, Quantity.class, content);
        } else if (representation instanceof QualitativeFeatureRepresentation) {
            if (StringFeature.class.isAssignableFrom(featureClass)) {
                // handle string based feature
                String content = ((QualitativeFeatureRepresentation) representation).getContent();
                feature = instantiate(featureClass, String.class, content);
            } else if (EntityFeature.class.isAssignableFrom(featureClass)) {
                // handle entity based features
                ChemicalEntity content = EntityCache.get(((QualitativeFeatureRepresentation) representation).getContent());
                feature = instantiate(featureClass, ChemicalEntity.class, content);
            } else if (RegionFeature.class.isAssignableFrom(featureClass)) {
                // handle region based features
                CellRegion content = RegionCache.get(((QualitativeFeatureRepresentation) representation).getContent());
                feature = instantiate(featureClass, CellRegion.class, content);
            }
        } else if (MultiEntityFeature.class.isAssignableFrom(featureClass)) {
            // handle multi-entity based features
            MultiEntityFeatureRepresentation multiEntity = (MultiEntityFeatureRepresentation) representation;
            List<ChemicalEntity> entities = new ArrayList<>();
            for (String entity : multiEntity.getEntities()) {
                entities.add(EntityCache.get(entity));
            }
            feature = instantiate(featureClass, List.class, entities);
        } else if (MultiStringFeature.class.isAssignableFrom(featureClass)) {
            // handle multi-entity based features
            MultiStringFeatureRepresentation multiEntity = (MultiStringFeatureRepresentation) representation;
            List<String> strings = new ArrayList<>(multiEntity.getStrings());
            feature = instantiate(featureClass, List.class, strings);
        }

        if (feature != null) {
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
