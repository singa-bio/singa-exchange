package singa.bio.exchange.model.variation;

import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.entities.EntityCache;
import singa.bio.exchange.model.sections.SubsectionCache;

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

    public static Observation create(String alias, String entity, String compatrment, String updatable) {
        Observation observation = new Observation();
        observation.setEntity(entity);
        observation.setSubsection(compatrment);
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

}
