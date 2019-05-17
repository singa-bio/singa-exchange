package singa.bio.exchange.model.trajectories;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.trajectories.nested.TrajectoryDataPoint;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.agents.VectorRepresentation;
import singa.bio.exchange.model.entities.EntityCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class SubsectionDataPointRepresentation {

    @JsonProperty
    private Map<String, Double> concentrations;

    @JsonProperty
    private List<VectorRepresentation> positions;

    public SubsectionDataPointRepresentation() {
        concentrations = new HashMap<>();
        positions = new ArrayList<>();
    }

    public static SubsectionDataPointRepresentation of(TrajectoryDataPoint.SubsectionDataPoint subsectionData) {
        SubsectionDataPointRepresentation representation = new SubsectionDataPointRepresentation();
        for (Map.Entry<ChemicalEntity, Double> concentrationEntry : subsectionData.getConcentrations().entrySet()) {
            representation.concentrations.put(concentrationEntry.getKey().getIdentifier().toString(), concentrationEntry.getValue());
        }
        for (Vector2D position : subsectionData.getPositions()) {
            representation.positions.add(VectorRepresentation.of(position));
        }
        return representation;
    }

    public TrajectoryDataPoint.SubsectionDataPoint toModel() {
        TrajectoryDataPoint.SubsectionDataPoint SubsectionDataPoint = new TrajectoryDataPoint.SubsectionDataPoint();
        for (Map.Entry<String, Double> entry : concentrations.entrySet()) {
            ChemicalEntity entity = EntityCache.get(entry.getKey());
            if (entity == null) {
                entity = SmallMolecule.create(entry.getKey()).build();
            }
            SubsectionDataPoint.addConcentration(entity, entry.getValue());
        }
        for (VectorRepresentation position : positions) {
            SubsectionDataPoint.addPosition(position.toModel());
        }
        return SubsectionDataPoint;
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
