package singa.bio.exchange.model.trajectories;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.trajectories.nested.Trajectories;
import bio.singa.simulation.trajectories.nested.TrajectoryData;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import singa.bio.exchange.model.units.UnitJacksonModule;

import javax.measure.Unit;
import javax.measure.quantity.Time;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author cl
 */
public class TrajectoryDataset {

    @JsonProperty("time-unit")
    private Unit<Time> timeUnit;

    @JsonProperty("concentration-unit")
    private Unit<MolarConcentration> concentrationUnit;

    @JsonProperty("simulation-width")
    private double simulationWidth;

    @JsonProperty("simulation-height")
    private double simulationHeight;

    @JsonProperty("trajectory-data")
    private Map<Double, TrajectoryRepresentation> trajectoryData;

    public TrajectoryDataset() {
        trajectoryData = new TreeMap<>();
    }

    public static TrajectoryDataset of(Trajectories trajectories) {
        TrajectoryDataset dataset = new TrajectoryDataset();
        dataset.setTimeUnit(trajectories.getTimeUnit());
        dataset.setConcentrationUnit(trajectories.getConcentrationUnit());
        dataset.setSimulationWidth(trajectories.getSimulationWidth());
        dataset.setSimulationHeight(trajectories.getSimulationHeight());
        for (Map.Entry<Double, TrajectoryData> entry : trajectories.getTrajectoryData().entrySet()) {
            dataset.trajectoryData.put(entry.getKey(), TrajectoryRepresentation.of(entry.getValue()));
        }
        return dataset;
    }

    public Unit<Time> getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(Unit<Time> timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Unit<MolarConcentration> getConcentrationUnit() {
        return concentrationUnit;
    }

    public void setConcentrationUnit(Unit<MolarConcentration> concentrationUnit) {
        this.concentrationUnit = concentrationUnit;
    }

    public Map<Double, TrajectoryRepresentation> getTrajectoryData() {
        return trajectoryData;
    }

    public void setTrajectoryData(Map<Double, TrajectoryRepresentation> trajectoryData) {
        this.trajectoryData = trajectoryData;
    }

    public double getSimulationWidth() {
        return simulationWidth;
    }

    public void setSimulationWidth(double simulationWidth) {
        this.simulationWidth = simulationWidth;
    }

    public double getSimulationHeight() {
        return simulationHeight;
    }

    public void setSimulationHeight(double simulationHeight) {
        this.simulationHeight = simulationHeight;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new UnitJacksonModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        return mapper.writeValueAsString(this);
    }

}
