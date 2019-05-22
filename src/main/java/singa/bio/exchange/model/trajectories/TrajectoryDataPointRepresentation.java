package singa.bio.exchange.model.trajectories;

import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.trajectories.nested.TrajectoryDataPoint;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.sections.SubsectionCache;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author cl
 */
public class TrajectoryDataPointRepresentation {

    @JsonProperty
    private Map<String, SubsectionDataPointRepresentation> subsections;

    private TrajectoryDataPointRepresentation() {
        subsections = new TreeMap<>();
    }

    public static TrajectoryDataPointRepresentation of(TrajectoryDataPoint dataPoint) {
        TrajectoryDataPointRepresentation representation = new TrajectoryDataPointRepresentation();
        for (Map.Entry<CellSubsection, TrajectoryDataPoint.SubsectionDataPoint> entry : dataPoint.getSubsectionData().entrySet()) {
            representation.subsections.put(entry.getKey().getIdentifier(), SubsectionDataPointRepresentation.of(entry.getValue()));
        }
        return representation;
    }

    public TrajectoryDataPoint toModel() {
        TrajectoryDataPoint datapoint = new TrajectoryDataPoint();
        for (Map.Entry<String, SubsectionDataPointRepresentation> entry : getSubsections().entrySet()) {
            datapoint.put(SubsectionCache.get(entry.getKey()), entry.getValue().toModel());
        }
        return datapoint;
    }

    public Map<String, SubsectionDataPointRepresentation> getSubsections() {
        return subsections;
    }

    public void setSubsections(Map<String, SubsectionDataPointRepresentation> subsections) {
        this.subsections = subsections;
    }

}
