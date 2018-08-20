package singa.bio.exchange.model.features;

import bio.singa.chemistry.MultiEntityFeature;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.EntityFeature;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.identifiers.model.IdentifierPatternRegistry;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureOrigin;
import org.reflections.Reflections;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.entities.EntityCache;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
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

    public static Feature create(FeatureRepresentation representation) {
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
        // is identifier
        if (Identifier.class.isAssignableFrom(featureClass)) {
            String identifierString = ((QualitativeFeatureRepresentation) representation).getContent();
            // get identifier by pattern or use simple string identifier
            return IdentifierPatternRegistry.instantiate(identifierString).orElse(new SimpleStringIdentifier(identifierString));
        }
        // is simple entity feature
        if (EntityFeature.class.isAssignableFrom(featureClass)) {
            ChemicalEntity chemicalEntity = EntityCache.get(((QualitativeFeatureRepresentation) representation).getContent());
            Constructor<?> constructor;
            try {
                constructor = featureClass.getConstructor(ChemicalEntity.class, FeatureOrigin.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalConversionException("Unable to get constructor for entity feature " + representation.getName(), e);
            }

            try {
                return (Feature) constructor.newInstance(chemicalEntity, OriginRepresentation.to(representation.getOrigin()));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalConversionException("Unable to create instance of entity feature " + representation.getName(), e);
            }
        }
        // is multi entity feature
        if (MultiEntityFeature.class.isAssignableFrom(featureClass)) {
            MultiEntityFeatureRepresentation multiEntity = (MultiEntityFeatureRepresentation) representation;
            Set<ChemicalEntity> entities = new HashSet<>();
            for (String entity : multiEntity.getEntities()) {
                entities.add(EntityCache.get(entity));
            }
            Constructor<?> constructor;
            try {
                constructor = featureClass.getConstructor(Set.class, FeatureOrigin.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalConversionException("Unable to get constructor for multi entity feature " + representation.getName(), e);
            }

            try {
                return (Feature) constructor.newInstance(entities, OriginRepresentation.to(representation.getOrigin()));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalConversionException("Unable to create instance of multi entity feature " + representation.getName(), e);
            }
        }

        // it is a quantitative feature
        if (representation instanceof QuantitativeFeatureRepresentation) {
            // create quantity
            QuantitativeFeatureRepresentation quantitative = (QuantitativeFeatureRepresentation) representation;
            Quantity quantity = Quantities.getQuantity(quantitative.getQuantity(), quantitative.getUnit());
            Constructor<?> constructor;
            try {
                constructor = featureClass.getConstructor(Quantity.class, FeatureOrigin.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalConversionException("Unable to get constructor for quantitative feature " + representation.getName(), e);
            }
            try {
                return (Feature) constructor.newInstance(quantity, OriginRepresentation.to(representation.getOrigin()));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalConversionException("Unable to create instance of quantitative feature " + representation.getName(), e);
            }
        }
        throw new IllegalConversionException("Unable to convert feature " + representation.getName());
    }

}
