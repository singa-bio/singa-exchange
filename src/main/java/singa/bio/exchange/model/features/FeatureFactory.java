package singa.bio.exchange.model.features;

import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.identifiers.model.IdentifierPatternRegistry;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureOrigin;
import org.reflections.Reflections;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
            if (featureType.getSimpleName().equals(className)){
                featureClass = featureType;
            }
        }
        // if class cannot be found something went wrong
        Objects.requireNonNull(featureClass);
        // it is an identifier
        if (Identifier.class.isAssignableFrom(featureClass)) {
            String identifierString = ((QualitativeFeatureRepresentation) representation).getContent();
            // get identifier by pattern or use simple string identifier
            return IdentifierPatternRegistry.instantiate(identifierString).orElse(new SimpleStringIdentifier(identifierString));
        }
        // it is a quantitative feature
        if (representation instanceof QuantitativeFeatureRepresentation) {
            // create quantity
            QuantitativeFeatureRepresentation quantitative = (QuantitativeFeatureRepresentation) representation;
            ComparableQuantity quantity = Quantities.getQuantity(quantitative.getQuantity(), quantitative.getUnit());
            Constructor<?> constructor = null;
            try {
                constructor = featureClass.getConstructor(Quantity.class, FeatureOrigin.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            Objects.requireNonNull(constructor);
            try {
                return (Feature) constructor.newInstance(quantity, OriginRepresentation.to(representation.getOrigin()));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("Unable to assign feature");
    }

}
