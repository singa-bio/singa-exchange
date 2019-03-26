package singa.bio.exchange.model.variation;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.features.variation.ConcentrationVariation;
import bio.singa.simulation.features.variation.ModuleFeatureVariation;
import bio.singa.simulation.features.variation.Variation;
import bio.singa.simulation.features.variation.VariationSet;
import singa.bio.exchange.model.SimulationRepresentation;
import singa.bio.exchange.model.entities.EntityCache;
import singa.bio.exchange.model.features.FeatureRepresentation;
import singa.bio.exchange.model.features.QuantitativeFeatureRepresentation;
import singa.bio.exchange.model.modules.ModuleRepresentation;
import singa.bio.exchange.model.sections.InitialConcentrationRepresentation;
import singa.bio.exchange.model.sections.RegionCache;
import singa.bio.exchange.model.sections.SectionConcentrationRepresentation;
import singa.bio.exchange.model.sections.SubsectionCache;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class VariationGenerator {

    // TODO apply variations using their representations and an initialized simulation

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

    private <Type> void attachAlternativeValue(Variation<Type> variation) {
        // section concentrations
        if (variation instanceof ConcentrationVariation) {
            ConcentrationVariation cv = (ConcentrationVariation) variation;
            for (InitialConcentrationRepresentation representation : simulation.getConcentrations().getConcentrations()) {
                if (representation instanceof SectionConcentrationRepresentation) {
                    SectionConcentrationRepresentation scr = (SectionConcentrationRepresentation) representation;
                    if (sectionRepresentationMatches(scr, cv)) {
                        for (MolarConcentration concentration : cv.getVariations()) {
                            scr.addAlternativeValue(concentration.to(scr.getConcentrationUnit()).getValue().doubleValue());
                        }
                        return;
                    }
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
    }

    private boolean sectionRepresentationMatches(SectionConcentrationRepresentation representation, ConcentrationVariation variation) {
        String representationEntity = representation.getEntity();
        String representationSubsection = representation.getSubsection();
        String representationRegion;
        if (representation.getRegion() == null) {
            representationRegion = "none";
        } else {
            representationRegion = representation.getRegion();
        }

        String variationEntity = variation.getEntity().getIdentifier().getContent();
        String variationSubsection = variation.getSubsection().getIdentifier();
        String variationRegion;
        if (variation.getCellRegion() == null) {
            variationRegion = "none";
        } else {
            variationRegion = variation.getCellRegion().getIdentifier();
        }

        return representationEntity.equals(variationEntity) && representationSubsection.equals(variationSubsection) && representationRegion.equals(variationRegion);
    }

    private boolean featureRepresentationMatches(ModuleRepresentation module, FeatureRepresentation feature, ModuleFeatureVariation featureVariation) {
        String representationModule = module.getIdentifier();
        String representationClass = feature.getName();

        String variationModule = featureVariation.getModule().getIdentifier();
        String variationClass = featureVariation.getFeatureClass().getSimpleName();

        return representationModule.equals(variationModule) && representationClass.equals(variationClass);
    }

    public static VariationSet generateVariationSet(SimulationRepresentation simulationRepresentation) {
        VariationGenerator generator = new VariationGenerator(simulationRepresentation);
        return generator.generateVariationSet();
    }

    private VariationSet generateVariationSet() {
        VariationSet variations = new VariationSet();
        // collect all modules
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
            if (concentration instanceof SectionConcentrationRepresentation) {
                if (!concentration.getAlternativeValues().isEmpty()) {
                    SectionConcentrationRepresentation scr = (SectionConcentrationRepresentation) concentration;
                    Variation variation = new ConcentrationVariation(RegionCache.get(scr.getRegion()), SubsectionCache.get(scr.getSubsection()), EntityCache.get(scr.getEntity()));
                    variations.addVariation(variation);
                    for (Double alternativeValue : scr.getAlternativeValues()) {
                        variation.addVariation(Quantities.getQuantity(alternativeValue, scr.getConcentrationUnit()));
                    }
                }
            }
        }
        return variations;
    }



}
