package singa.bio.exchange.model.variation;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.simulation.Updatable;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.entities.EntityCache;
import singa.bio.exchange.model.sections.SubsectionCache;
import singa.bio.exchange.model.trajectories.TrajectoryDataset;
import tec.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.AbstractMap;
import java.util.Map;

/**
 * @author cl
 */
public class Observation {

    private static final Logger logger = LoggerFactory.getLogger(Observation.class);

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
            logger.warn("The observation (entity: " + entity + " subsection: " + subsection + " updatable: " + updatable + ") might not be able to record any data.");
        }
    }

    /**
     * Observes the the current value in the simulation.
     *
     * @return The concentration related to this observation.
     */
    public Quantity<MolarConcentration> observe() {
        Updatable updatable = UpdatableCacheManager.get(this.updatable);
        if (updatable != null) {
            return UnitRegistry.concentration(updatable.getConcentrationContainer().get(SubsectionCache.get(subsection), EntityCache.get(entity)));
        }
        return null;
    }

    /**
     * Observes this observation in a trajectory at the given time.
     *
     * @param trajectory
     * @return
     */
    public Map.Entry<Quantity<Time>, Quantity<MolarConcentration>> observe(TrajectoryDataset trajectory, Quantity<Time> extractionTime) {
        // get best matching time step
        double extractionValue = extractionTime.to(trajectory.getTimeUnit()).getValue().doubleValue();
        double minimalDeviation = Double.MAX_VALUE;
        double optimalTimestep = -1.0;
        for (double currentTimestep : trajectory.getTrajectoryData().keySet()) {
            double currentDeviation = Math.abs(currentTimestep - extractionValue);
            if (currentDeviation < minimalDeviation) {
                minimalDeviation = currentDeviation;
                optimalTimestep = currentTimestep;
            }
        }
        // retrieve the concentration
        Double concentration = trajectory.getTrajectoryData().get(optimalTimestep)
                .getData().get(getUpdatable())
                .getSubsections().get(getSubsection())
                .getConcentrations().get(getEntity());
        // return with annotated units
        return new AbstractMap.SimpleEntry<>(Quantities.getQuantity(optimalTimestep, trajectory.getTimeUnit()),
                Quantities.getQuantity(concentration, trajectory.getConcentrationUnit()));
    }

    @Override
    public String toString() {
        return alias + ": " +
                "E = " + entity + " " +
                "S = " + subsection + " " +
                "U = " + updatable;
    }
}
