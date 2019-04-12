package singa.bio.exchange.model.trajectories;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.trajectories.nested.TrajactoryDataPoint;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.agents.VectorRepresentation;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author cl
 */
public class TrajectoryDatapointRepresentation {

    @JsonProperty
    private Map<String, Map<String, Double>> concentrations;

    @JsonProperty
    private VectorRepresentation position;

    private TrajectoryDatapointRepresentation() {
        concentrations = new TreeMap<>();
    }

    public static TrajectoryDatapointRepresentation of(TrajactoryDataPoint dataPoint) {
        TrajectoryDatapointRepresentation representation = new TrajectoryDatapointRepresentation();
        for (Map.Entry<CellSubsection, Map<ChemicalEntity, Double>> subsectionEntry : dataPoint.getConcentrations().entrySet()) {
            String subsection = subsectionEntry.getKey().getIdentifier();
            Map<String, Double> concentrationMap = new HashMap<>();
            for (Map.Entry<ChemicalEntity, Double> concentrationEntry : subsectionEntry.getValue().entrySet()) {
                concentrationMap.put(concentrationEntry.getKey().getIdentifier().toString(), concentrationEntry.getValue());
            }
            representation.concentrations.put(subsection, concentrationMap);
        }
        representation.setPosition(VectorRepresentation.of(dataPoint.getPosition()));
        return representation;
    }

    public Map<String, Map<String, Double>> getConcentrations() {
        return concentrations;
    }

    public void setConcentrations(Map<String, Map<String, Double>> concentrations) {
        this.concentrations = concentrations;
    }

    public VectorRepresentation getPosition() {
        return position;
    }

    public void setPosition(VectorRepresentation position) {
        this.position = position;
    }
}
