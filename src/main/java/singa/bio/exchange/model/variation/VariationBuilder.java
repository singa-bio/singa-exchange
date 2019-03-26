package singa.bio.exchange.model.variation;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.features.variation.ConcentrationVariation;
import bio.singa.simulation.features.variation.ModuleFeatureVariation;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;
import singa.bio.exchange.model.entities.EntityCache;
import singa.bio.exchange.model.sections.RegionCache;
import singa.bio.exchange.model.sections.SubsectionCache;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class VariationBuilder {

    public static EntityVariationStep entityVariation() {
        return new EntityVariationBuilder();
    }

    public static ModuleFeatureVariationStep featureVariation(Simulation simulation) {
        return new ModuleFeatureVariationBuilder(simulation);
    }

    public interface EntityVariationStep {
        SubsectionStep entity(String chemicalEntity);
    }

    public interface SubsectionStep {
        RegionStep subsection(String subsection);
    }

    public interface RegionStep {
        ConcentrationStep region(String region);

        ConcentrationStep everywhere();
    }

    public interface ConcentrationStep {
        ConcentrationUnitStep concentrations(double... concentrations);
    }

    public interface ConcentrationUnitStep {
        EntityVariationBuildStep unit(Unit<MolarConcentration> unit);
    }

    public interface EntityVariationBuildStep {
        ConcentrationVariation build();
    }


    public interface ModuleFeatureVariationStep {
        FeatureClassStep module(String moduleIdentifier);
    }

    public interface FeatureClassStep {
        <T extends Feature> QuantityStep featureClass(Class<T> featureClass);
    }

    public interface QuantityStep {
        QuantityUnitStep quantityValues(double... values);
    }

    public interface QuantityUnitStep {
        <T extends Quantity<T>> ModuleFeatureVariationBuildStep unit(Unit<?> unit, Class<T> unitType);
    }

    public interface ModuleFeatureVariationBuildStep<T> {
        ModuleFeatureVariation<T> build();
    }

    public static class EntityVariationBuilder implements EntityVariationStep, SubsectionStep, RegionStep, ConcentrationStep, ConcentrationUnitStep, EntityVariationBuildStep {

        private String entityIdentifier;
        private String subsectionIdentifier;
        private String regionIdentifier;
        private double[] concentrationValues;
        private Unit<MolarConcentration> concentrationUnit;

        @Override
        public SubsectionStep entity(String entityIdentifier) {
            this.entityIdentifier = entityIdentifier;
            return this;
        }

        @Override
        public RegionStep subsection(String subsectionIdentifier) {
            this.subsectionIdentifier = subsectionIdentifier;
            return this;
        }

        @Override
        public ConcentrationStep region(String regionIdentifier) {
            this.regionIdentifier = regionIdentifier;
            return this;
        }

        @Override
        public ConcentrationStep everywhere() {
            this.regionIdentifier = null;
            return this;
        }

        @Override
        public ConcentrationUnitStep concentrations(double... concentrations) {
            concentrationValues = concentrations;
            return this;
        }

        @Override
        public EntityVariationBuildStep unit(Unit<MolarConcentration> concentrationUnit) {
            this.concentrationUnit = concentrationUnit;
            return this;
        }

        @Override
        public ConcentrationVariation build() {
            // get subsection
            CellSubsection subsection = SubsectionCache.get(subsectionIdentifier);
            if (subsection == null) {
                throw new IllegalArgumentException("No subsection with the identifier \"" + subsectionIdentifier + "\" could not be found in the subsection cache.");
            }
            // get chemical entity
            ChemicalEntity chemicalEntity = EntityCache.get(entityIdentifier);
            if (chemicalEntity == null) {
                throw new IllegalArgumentException("No entity with the identifier \"" + entityIdentifier + "\" could not be found in the entity cache.");
            }
            // create variation with or without region
            ConcentrationVariation variation;
            if (regionIdentifier != null) {
                CellRegion region = RegionCache.get(regionIdentifier);
                if (region == null) {
                    throw new IllegalArgumentException("No region with the identifier \"" + regionIdentifier + "\" could not be found in the region cache.");
                }
                variation = new ConcentrationVariation(region, subsection, chemicalEntity);
            } else {
                variation = new ConcentrationVariation(subsection, chemicalEntity);
            }

            for (double concentrationValue : concentrationValues) {
                variation.addVariation(new MolarConcentration(concentrationValue, concentrationUnit));
            }
            return variation;
        }
    }

    public static class ModuleFeatureVariationBuilder implements ModuleFeatureVariationStep, FeatureClassStep, QuantityStep, QuantityUnitStep, ModuleFeatureVariationBuildStep {

        private Simulation simulation;

        private String moduleIdentifier;
        private Class<? extends Feature> featureClass;
        private double[] values;
        private Unit<? extends Quantity> unit;
        private Class<? extends Quantity> unitType;


        public ModuleFeatureVariationBuilder(Simulation simulation) {
            this.simulation = simulation;
        }

        @Override
        public FeatureClassStep module(String moduleIdentifier) {
            this.moduleIdentifier = moduleIdentifier;
            return this;
        }

        @Override
        public <T extends Feature> QuantityStep featureClass(Class<T> featureClass) {
            this.featureClass = featureClass;
            return this;
        }

        @Override
        public QuantityUnitStep quantityValues(double... values) {
            this.values = values;
            return this;
        }

        @Override
        public <T extends Quantity<T>> ModuleFeatureVariationBuildStep unit(Unit<?> unit, Class<T> unitType) {
            this.unit = unit;
            this.unitType = unitType;
            return this;
        }

        @Override
        public ModuleFeatureVariation build() {
            UpdateModule module = getModule(moduleIdentifier);
            if (module == null) {
                throw new IllegalArgumentException("No module with the identifier \"" + moduleIdentifier + "\" could not be found in the parsed simulation.");
            }
            ModuleFeatureVariation moduleFeatureVariation = new ModuleFeatureVariation(module, featureClass);
            for (double value : values) {
                moduleFeatureVariation.addVariation(createQuantity(value));
            }
            return moduleFeatureVariation;
        }

        private UpdateModule getModule(String identifier) {
            for (UpdateModule module : simulation.getModules()) {
                if (module.getIdentifier().equals(identifier)) {
                    return module;
                }
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        private Quantity<?> createQuantity(double value) {
            return Quantities.getQuantity(value, unit).asType(unitType);
        }

    }

}
