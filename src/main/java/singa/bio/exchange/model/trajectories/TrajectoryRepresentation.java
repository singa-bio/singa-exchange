package singa.bio.exchange.model.trajectories;

import bio.singa.simulation.model.simulation.Updatable;
import bio.singa.simulation.trajectories.nested.TrajactoryDataPoint;
import bio.singa.simulation.trajectories.nested.TrajectoryData;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.variation.UpdatableCacheManager;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author cl
 */

public class TrajectoryRepresentation {

    @JsonProperty("data")
    private Map<String, TrajectoryDatapointRepresentation> data;

    private TrajectoryRepresentation() {
        data = new TreeMap<>();
    }

    public static TrajectoryRepresentation of(TrajectoryData trajectoryData) {
        TrajectoryRepresentation representation = new TrajectoryRepresentation();
        for (Map.Entry<Updatable, TrajactoryDataPoint> entry : trajectoryData.getConcentrationData().entrySet()) {
            representation.data.put(entry.getKey().getStringIdentifier(), TrajectoryDatapointRepresentation.of(entry.getValue()));
        }
        return representation;
    }

    public TrajectoryData toModel() {
        TrajectoryData trajectory = new TrajectoryData();
        for (Map.Entry<String, TrajectoryDatapointRepresentation> entry : data.entrySet()) {
            trajectory.put(UpdatableCacheManager.get(entry.getKey()), entry.getValue().toModel());
        }
        return trajectory;
    }

    public Map<String, TrajectoryDatapointRepresentation> getData() {
        return data;
    }

    public void setData(Map<String, TrajectoryDatapointRepresentation> data) {
        this.data = data;
    }

}
