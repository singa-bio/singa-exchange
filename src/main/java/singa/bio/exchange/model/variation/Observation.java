package singa.bio.exchange.model.variation;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.simulation.Updatable;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.entities.EntityCache;
import singa.bio.exchange.model.sections.SubsectionCache;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class Observation {

    @JsonProperty
    private String alias;

    @JsonProperty
    private String entity;

    @JsonProperty
    private String subsection;

    @JsonProperty
    private String updatable;

    public Observation() {
    }

    public static Observation create(String alias, String entity, String subsection, String updatable) {
        Observation observation = new Observation();
        observation.setAlias(alias);
        observation.setEntity(entity);
        observation.setSubsection(subsection);
        observation.setUpdatable(updatable);
        return observation;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getSubsection() {
        return subsection;
    }

    public void setSubsection(String subsection) {
        this.subsection = subsection;
    }

    public String getUpdatable() {
        return updatable;
    }

    public void setUpdatable(String updatable) {
        this.updatable = updatable;
    }

    public void validate() {
        if (!(EntityCache.contains(entity) && SubsectionCache.contains(subsection) && UpdatableCacheManager.isAvailable(updatable))) {
            throw new IllegalConversionException("The observation (entity: " + entity + " subsection: " + subsection + " updatable: " + updatable + ") would not be able to record any data.");
        }
    }

    public Quantity<MolarConcentration> observe() {
        Updatable updatable = UpdatableCacheManager.get(this.updatable);
        if (updatable != null) {
            return UnitRegistry.concentration(updatable.getConcentrationContainer().get(SubsectionCache.get(subsection), EntityCache.get(entity)));
        }
        return null;
    }

}
