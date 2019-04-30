package singa.bio.exchange.model.trajectories;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.trajectories.nested.TrajactoryDataPoint;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.agents.VectorRepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class SubsectionDatapointRepresentation {

    @JsonProperty
    private Map<String, Double> concentrations;

    @JsonProperty
    private List<VectorRepresentation> positions;

    public SubsectionDatapointRepresentation() {
        concentrations = new HashMap<>();
        positions = new ArrayList<>();
    }

    public static SubsectionDatapointRepresentation of(TrajactoryDataPoint.SubsectionDatapoint subsectionData) {
        SubsectionDatapointRepresentation representation = new SubsectionDatapointRepresentation();
        for (Map.Entry<ChemicalEntity, Double> concentrationEntry : subsectionData.getConcentration().entrySet()) {
            representation.concentrations.put(concentrationEntry.getKey().getIdentifier().toString(), concentrationEntry.getValue());
        }
        for (Vector2D position : subsectionData.getPositions()) {
            representation.positions.add(VectorRepresentation.of(position));
        }
        return representation;
    }

    public Map<String, Double> getConcentrations() {
        return concentrations;
    }

    public void setConcentrations(Map<String, Double> concentrations) {
        this.concentrations = concentrations;
    }

    public List<VectorRepresentation> getPositions() {
        return positions;
    }

    public void setPositions(List<VectorRepresentation> positions) {
        this.positions = positions;
    }
}
