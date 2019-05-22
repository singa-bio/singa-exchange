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
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class ObservationSpecification {

    private static final Logger logger = LoggerFactory.getLogger(ObservationSpecification.class);

    @JsonProperty
    private String alias;

    @JsonProperty
    private List<Quantity<Time>> times;

    @JsonProperty
    private String entity;

    @JsonProperty
    private String subsection;

    @JsonProperty
    private String updatable;

    public ObservationSpecification() {
    }

    public static ObservationSpecification create(String alias, List<Quantity<Time>> times, String entity, String subsection, String updatable) {
        ObservationSpecification observation = new ObservationSpecification();
        observation.setAlias(alias);
        observation.setTimes(times);
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

    public List<Quantity<Time>> getTimes() {
        return times;
    }

    public void setTimes(List<Quantity<Time>> times) {
        this.times = times;
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
    public Map<Quantity<Time>, Quantity<MolarConcentration>> observe(TrajectoryDataset trajectory) {
        // get best matching time step
        Map<Quantity<Time>, Quantity<MolarConcentration>> observations = new HashMap<>();
        for (Quantity<Time> time : getTimes()) {
            double extractionValue = time.to(trajectory.getTimeUnit()).getValue().doubleValue();
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
                    .getConcentrations()
                    .get(getEntity());
            observations.put(Quantities.getQuantity(optimalTimestep, trajectory.getTimeUnit()),
                    Quantities.getQuantity(concentration, trajectory.getConcentrationUnit()));
        }
        // return with annotated units
        return observations;
    }

    @Override
    public String toString() {
        return alias + ": " +
                "E = " + entity + " " +
                "S = " + subsection + " " +
                "U = " + updatable;
    }
}
