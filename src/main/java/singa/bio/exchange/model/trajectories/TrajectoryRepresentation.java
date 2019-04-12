package singa.bio.exchange.model.trajectories;

import bio.singa.simulation.model.simulation.Updatable;
import bio.singa.simulation.trajectories.nested.TrajactoryDataPoint;
import bio.singa.simulation.trajectories.nested.TrajectoryData;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public Map<String, TrajectoryDatapointRepresentation> getData() {
        return data;
    }

    public void setData(Map<String, TrajectoryDatapointRepresentation> data) {
        this.data = data;
    }

}
