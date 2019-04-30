package singa.bio.exchange.model.trajectories;

import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.trajectories.nested.TrajactoryDataPoint;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author cl
 */
public class TrajectoryDatapointRepresentation {

    @JsonProperty
    private Map<String, SubsectionDatapointRepresentation> subsections;

    private TrajectoryDatapointRepresentation() {
        subsections = new TreeMap<>();
    }

    public static TrajectoryDatapointRepresentation of(TrajactoryDataPoint dataPoint) {
        TrajectoryDatapointRepresentation representation = new TrajectoryDatapointRepresentation();
        for (Map.Entry<CellSubsection, TrajactoryDataPoint.SubsectionDatapoint> entry : dataPoint.getSubsectionData().entrySet()) {
            representation.subsections.put(entry.getKey().getIdentifier(), SubsectionDatapointRepresentation.of(entry.getValue()));
        }
        return representation;
    }

    public Map<String, SubsectionDatapointRepresentation> getSubsections() {
        return subsections;
    }

    public void setSubsections(Map<String, SubsectionDatapointRepresentation> subsections) {
        this.subsections = subsections;
    }

}
