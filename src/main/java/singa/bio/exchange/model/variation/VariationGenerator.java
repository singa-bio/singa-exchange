package singa.bio.exchange.model.variation;

import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.features.variation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.SimulationRepresentation;
import singa.bio.exchange.model.concentrations.InitialConcentrationRepresentation;
import singa.bio.exchange.model.entities.EntityRepresentation;
import singa.bio.exchange.model.features.FeatureRepresentation;
import singa.bio.exchange.model.features.QuantitativeFeatureRepresentation;
import singa.bio.exchange.model.modules.ModuleRepresentation;
import singa.bio.exchange.model.sections.SubsectionCache;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class VariationGenerator {

    private static final Logger logger = LoggerFactory.getLogger(VariationGenerator.class);

    private SimulationRepresentation simulation;

    public VariationGenerator(SimulationRepresentation simulation) {
        this.simulation = simulation;
    }

    public static void attachAlternativeValue(SimulationRepresentation simulationRepresentation, VariationSet variations) {
        VariationGenerator generator = new VariationGenerator(simulationRepresentation);
        for (Variation<?> variation : variations.getVariations()) {
            generator.attachAlternativeValue(variation);
        }
    }

    public static VariationSet generateVariationSet(SimulationRepresentation simulationRepresentation) {
        VariationGenerator generator = new VariationGenerator(simulationRepresentation);
        return generator.generateVariationSet();
    }

    private <Type> void attachAlternativeValue(Variation<Type> variation) {
        // section concentrations
        if (variation instanceof ConcentrationVariation) {
            ConcentrationVariation cv = (ConcentrationVariation) variation;
            for (InitialConcentrationRepresentation representation : simulation.getConcentrations().getConcentrations()) {
                if (sectionRepresentationMatches(representation, cv)) {
                    for (MolarConcentration concentration : cv.getVariations()) {
                        representation.addAlternativeValue(concentration.to(representation.getConcentrationUnit()).getValue().doubleValue());
                    }
                    return;
                }
            }
        }
        // module features
        if (variation instanceof ModuleFeatureVariation) {
            ModuleFeatureVariation<Type> fv = (ModuleFeatureVariation<Type>) variation;
            for (ModuleRepresentation module : simulation.getModules().getModules()) {
                for (FeatureRepresentation feature : module.getFeatures()) {
                    if (feature instanceof QuantitativeFeatureRepresentation) {
                        QuantitativeFeatureRepresentation qfr = (QuantitativeFeatureRepresentation) feature;
                        if (featureRepresentationMatches(module, feature, fv)) {
                            for (Type type : fv.getVariations()) {
                                // by contract if the class matches the quantity will match
                                feature.addAlternativeValue(((Quantity) type).to(qfr.getUnit()).getValue().doubleValue());
                            }
                            return;
                        }
                    }
                }
            }
        }
        // entity features
        if (variation instanceof EntityFeatureVariation) {
            EntityFeatureVariation<Type> fv = (EntityFeatureVariation<Type>) variation;
            for (EntityRepresentation entity : simulation.getEntities().getEntities()) {
                for (FeatureRepresentation feature : entity.getFeatures()) {
                    if (feature instanceof QuantitativeFeatureRepresentation) {
                        QuantitativeFeatureRepresentation qfr = (QuantitativeFeatureRepresentation) feature;
                        if (featureRepresentationMatches(entity, feature, fv)) {
                            for (Type type : fv.getVariations()) {
                                feature.addAlternativeValue(((Quantity) type).to(qfr.getUnit()).getValue().doubleValue());
                            }
                            return;
                        }
                    }
                }
            }
        }
        logger.warn("unable to attach variation {}", variation);
    }

    private boolean sectionRepresentationMatches(InitialConcentrationRepresentation representation, ConcentrationVariation variation) {
        String representationEntity = representation.getEntity();
        String representationSubsection = representation.getSubsection();

        String variationEntity = variation.getEntity().getIdentifier();
        String variationSubsection = variation.getSubsection().getIdentifier();
        // FIXME should compare more values

        return representationEntity.equals(variationEntity) && representationSubsection.equals(variationSubsection);
    }

    private boolean featureRepresentationMatches(ModuleRepresentation module, FeatureRepresentation feature, ModuleFeatureVariation featureVariation) {
        String representationModule = module.getIdentifier();
        String representationClass = feature.getName();

        String variationModule = featureVariation.getModule().getIdentifier();
        String variationClass = featureVariation.getFeatureClass().getSimpleName();

        return representationModule.equals(variationModule) && representationClass.equals(variationClass);
    }

    private boolean featureRepresentationMatches(EntityRepresentation entity, FeatureRepresentation feature, EntityFeatureVariation featureVariation) {
        String representationEntity = entity.getPrimaryIdentifier();
        String representationClass = feature.getName();

        String variationEntity = featureVariation.getEntity().getIdentifier();
        String variationClass = featureVariation.getFeatureClass().getSimpleName();

        return representationEntity.equals(variationEntity) && representationClass.equals(variationClass);
    }

    private VariationSet generateVariationSet() {
        VariationSet variations = new VariationSet();
        // collect all module feature variations
        for (ModuleRepresentation module : simulation.getModules().getModules()) {
            for (FeatureRepresentation feature : module.getFeatures()) {
                if (!feature.getAlternativeValues().isEmpty()) {
                    if (feature instanceof QuantitativeFeatureRepresentation) {
                        Variation variation = new ModuleFeatureVariation(module.toModel(), feature.toModel().getClass());
                        variations.addVariation(variation);
                        for (Object alternativeValue : feature.getAlternativeValues()) {
                            variation.addVariation(Quantities.getQuantity((Double) alternativeValue, ((QuantitativeFeatureRepresentation) feature).getUnit()));
                        }
                    }
                }
            }
        }
        // collect all concentrations
        for (InitialConcentrationRepresentation concentration : simulation.getConcentrations().getConcentrations()) {
            if (!concentration.getAlternativeValues().isEmpty()) {
                Variation variation = new ConcentrationVariation(null, SubsectionCache.get(concentration.getSubsection()), EntityRegistry.get(concentration.getEntity()));
                variations.addVariation(variation);
                for (Double alternativeValue : concentration.getAlternativeValues()) {
                    variation.addVariation(Quantities.getQuantity(alternativeValue, concentration.getConcentrationUnit()));
                }
            }
        }
        // collect all entity feature variations
        for (EntityRepresentation entity : simulation.getEntities().getEntities()) {
            for (FeatureRepresentation feature : entity.getFeatures()) {
                if (!feature.getAlternativeValues().isEmpty()) {
                    if (feature instanceof QuantitativeFeatureRepresentation) {
                        Variation variation = new EntityFeatureVariation(entity.toModel(), feature.toModel().getClass());
                        variations.addVariation(variation);
                        for (Object alternativeValue : feature.getAlternativeValues()) {
                            variation.addVariation(Quantities.getQuantity((Double) alternativeValue, ((QuantitativeFeatureRepresentation) feature).getUnit()));
                        }
                    }
                }
            }
        }
        return variations;
    }


}
