package singa.bio.exchange.model.variation;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.features.model.Feature;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.features.variation.ConcentrationVariation;
import bio.singa.simulation.features.variation.EntityFeatureVariation;
import bio.singa.simulation.features.variation.ModuleFeatureVariation;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;
import singa.bio.exchange.model.sections.RegionCache;
import singa.bio.exchange.model.sections.SubsectionCache;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * @author cl
 */
public class VariationBuilder {

    public static EntityConcentrationVariationStep entityConcentrationVariation() {
        return new EntityConcentrationVariationBuilder();
    }

    public interface EntityConcentrationVariationStep {
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

    public static class EntityConcentrationVariationBuilder implements EntityConcentrationVariationStep, SubsectionStep, RegionStep, ConcentrationStep, ConcentrationUnitStep, EntityVariationBuildStep {

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
            ChemicalEntity chemicalEntity = EntityRegistry.get(entityIdentifier);
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


    public static ModuleFeatureVariationStep moduleFeatureVariation(Simulation simulation) {
        return new ModuleFeatureVariationBuilder(simulation);
    }

    public interface ModuleFeatureVariationStep {
        ModuleFeatureClassStep module(String moduleIdentifier);
    }

    public interface ModuleFeatureClassStep {
        <T extends Feature> ModuleQuantityStep featureClass(Class<T> featureClass);
    }

    public interface ModuleQuantityStep {
        ModuleQuantityUnitStep quantityValues(double... values);
    }

    public interface ModuleQuantityUnitStep {
        <T extends Quantity<T>> ModuleFeatureVariationBuildStep unit(Unit<?> unit, Class<T> unitType);
    }

    public interface ModuleFeatureVariationBuildStep<T> {
        ModuleFeatureVariation<T> build();
    }

    public static class ModuleFeatureVariationBuilder implements ModuleFeatureVariationStep, ModuleFeatureClassStep, ModuleQuantityStep, ModuleQuantityUnitStep, ModuleFeatureVariationBuildStep {

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
        public ModuleFeatureClassStep module(String moduleIdentifier) {
            this.moduleIdentifier = moduleIdentifier;
            return this;
        }

        @Override
        public <T extends Feature> ModuleQuantityStep featureClass(Class<T> featureClass) {
            this.featureClass = featureClass;
            return this;
        }

        @Override
        public ModuleQuantityUnitStep quantityValues(double... values) {
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


    public static EntityFeatureVariationStep entityFeatureVariation() {
        return new EntityFeatureVariationBuilder();
    }

    public interface EntityFeatureVariationStep {
        EntityFeatureClassStep entity(String moduleIdentifier);
    }

    public interface EntityFeatureClassStep {
        <T extends Feature> EntityQuantityStep featureClass(Class<T> featureClass);
    }

    public interface EntityQuantityStep {
        EntityQuantityUnitStep quantityValues(double... values);
    }

    public interface EntityQuantityUnitStep {
        <T extends Quantity<T>> EntityFeatureVariationBuildStep unit(Unit<?> unit, Class<T> unitType);
    }

    public interface EntityFeatureVariationBuildStep<T> {
        EntityFeatureVariation<T> build();
    }


    public static class EntityFeatureVariationBuilder implements EntityFeatureVariationStep, EntityFeatureClassStep, EntityQuantityStep, EntityQuantityUnitStep, EntityFeatureVariationBuildStep {

        private String entityIdentifier;
        private Class<? extends Feature> featureClass;
        private double[] values;
        private Unit<? extends Quantity> unit;
        private Class<? extends Quantity> unitType;

        public EntityFeatureVariationBuilder() {
        }

        @Override
        public EntityFeatureClassStep entity(String entityIdentifier) {
            this.entityIdentifier = entityIdentifier;
            return this;
        }

        @Override
        public <T extends Feature> EntityQuantityStep featureClass(Class<T> featureClass) {
            this.featureClass = featureClass;
            return this;
        }

        @Override
        public EntityQuantityUnitStep quantityValues(double... values) {
            this.values = values;
            return this;
        }

        @Override
        public <T extends Quantity<T>> EntityFeatureVariationBuildStep unit(Unit<?> unit, Class<T> unitType) {
            this.unit = unit;
            this.unitType = unitType;
            return this;
        }

        @Override
        public EntityFeatureVariation build() {
            ChemicalEntity entity = EntityRegistry.get(entityIdentifier);
            if (entity == null) {
                throw new IllegalArgumentException("No entity with the identifier \"" + entityIdentifier + "\" could not be found in the parsed simulation.");
            }
            EntityFeatureVariation entityFeatureVariation = new EntityFeatureVariation(entity, featureClass);
            for (double value : values) {
                entityFeatureVariation.addVariation(createQuantity(value));
            }
            return entityFeatureVariation;
        }

        @SuppressWarnings("unchecked")
        private Quantity<?> createQuantity(double value) {
            return Quantities.getQuantity(value, unit).asType(unitType);
        }
    }

}
