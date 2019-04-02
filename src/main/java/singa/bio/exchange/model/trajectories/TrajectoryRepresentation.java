package singa.bio.exchange.model.trajectories;

import bio.singa.simulation.model.simulation.Updatable;
import bio.singa.simulation.trajectories.nested.ConcentrationData;
import bio.singa.simulation.trajectories.nested.TrajectoryData;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */

public class TrajectoryRepresentation {

    @JsonProperty("concentration-data")
    private Map<String, TrajectoryConcentrationRepresentation> concentrationData;

    private TrajectoryRepresentation() {
        concentrationData = new HashMap<>();
    }

    public static TrajectoryRepresentation of(TrajectoryData trajectoryData) {
        TrajectoryRepresentation representation = new TrajectoryRepresentation();
        for (Map.Entry<Updatable, ConcentrationData> entry : trajectoryData.getConcentrationData().entrySet()) {
            representation.concentrationData.put(entry.getKey().getStringIdentifier(), TrajectoryConcentrationRepresentation.of(entry.getValue()));
        }
        return representation;
    }

    public Map<String, TrajectoryConcentrationRepresentation> getConcentrationData() {
        return concentrationData;
    }

    public void setConcentrationData(Map<String, TrajectoryConcentrationRepresentation> concentrationData) {
        this.concentrationData = concentrationData;
    }

}
