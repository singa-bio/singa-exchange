package bio.singa.exchange.trajectories;

import bio.singa.simulation.model.simulation.Updatable;
import bio.singa.simulation.trajectories.nested.TrajectoryDataPoint;
import bio.singa.simulation.trajectories.nested.TrajectoryData;
import com.fasterxml.jackson.annotation.JsonProperty;
import bio.singa.exchange.variation.UpdatableCacheManager;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author cl
 */

public class TrajectoryRepresentation {

    @JsonProperty("data")
    private Map<String, TrajectoryDataPointRepresentation> data;

    private TrajectoryRepresentation() {
        data = new TreeMap<>();
    }

    public static TrajectoryRepresentation of(TrajectoryData trajectoryData) {
        TrajectoryRepresentation representation = new TrajectoryRepresentation();
        for (Map.Entry<Updatable, TrajectoryDataPoint> entry : trajectoryData.getConcentrationData().entrySet()) {
            representation.data.put(entry.getKey().getStringIdentifier(), TrajectoryDataPointRepresentation.of(entry.getValue()));
        }
        return representation;
    }

    public TrajectoryData toModel() {
        TrajectoryData trajectory = new TrajectoryData();
        for (Map.Entry<String, TrajectoryDataPointRepresentation> entry : data.entrySet()) {
            trajectory.put(UpdatableCacheManager.get(entry.getKey()), entry.getValue().toModel());
        }
        return trajectory;
    }

    public Map<String, TrajectoryDataPointRepresentation> getData() {
        return data;
    }

    public void setData(Map<String, TrajectoryDataPointRepresentation> data) {
        this.data = data;
    }

}
