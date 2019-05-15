package singa.bio.exchange.model.trajectories;

import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.trajectories.nested.TrajactoryDataPoint;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.sections.SubsectionCache;

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

    public TrajactoryDataPoint toModel() {
        TrajactoryDataPoint datapoint = new TrajactoryDataPoint();
        for (Map.Entry<String, SubsectionDatapointRepresentation> entry : getSubsections().entrySet()) {
            datapoint.put(SubsectionCache.get(entry.getKey()), entry.getValue().toModel());
        }
        return datapoint;
    }

    public Map<String, SubsectionDatapointRepresentation> getSubsections() {
        return subsections;
    }

    public void setSubsections(Map<String, SubsectionDatapointRepresentation> subsections) {
        this.subsections = subsections;
    }

}
