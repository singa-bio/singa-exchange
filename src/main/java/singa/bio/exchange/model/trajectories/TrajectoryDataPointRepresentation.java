package singa.bio.exchange.model.trajectories;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.trajectories.nested.TrajectoryDataPoint;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.agents.VectorRepresentation;
import singa.bio.exchange.model.entities.EntityCache;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author cl
 */
public class TrajectoryDataPointRepresentation {

    @JsonProperty
    private Map<String, Map<String, Double>> concentrations;

    @JsonProperty
    private VectorRepresentation position;

    private TrajectoryDataPointRepresentation() {
        concentrations = new TreeMap<>();
    }

    public static TrajectoryDataPointRepresentation of(TrajectoryDataPoint dataPoint) {
        TrajectoryDataPointRepresentation representation = new TrajectoryDataPointRepresentation();
        for (Map.Entry<CellSubsection, TrajectoryDataPoint.SubsectionDataPoint> entry : dataPoint.getSubsectionData().entrySet()) {
            CellSubsection subsection = entry.getKey();
            Map<String, Double> currentMap;
            if (!representation.getConcentrations().containsKey(subsection.getIdentifier())) {
                currentMap = new HashMap<>();
                representation.getConcentrations().put(subsection.getIdentifier(), currentMap);
            } else {
                currentMap = representation.getConcentrations().get(subsection.getIdentifier());
            }
            TrajectoryDataPoint.SubsectionDataPoint SubsectionDataPoint = entry.getValue();
            for (Map.Entry<ChemicalEntity, Double> concentrationEntry : SubsectionDataPoint.getConcentrations().entrySet()) {
                currentMap.put(concentrationEntry.getKey().getIdentifier().getContent(), concentrationEntry.getValue());
            }
        }
        return representation;
    }

    public TrajectoryDataPoint toModel() {
        TrajectoryDataPoint trajectoryDataPoint = new TrajectoryDataPoint();
        for (Map.Entry<String, Map<String, Double>> mapEntry : getConcentrations().entrySet()) {
            TrajectoryDataPoint.SubsectionDataPoint subsectionData = new TrajectoryDataPoint.SubsectionDataPoint();
            for (Map.Entry<String, Double> entry : mapEntry.getValue().entrySet()) {
                ChemicalEntity chemicalEntity = EntityCache.draft(entry.getKey());
                subsectionData.addConcentration(chemicalEntity, entry.getValue());
            }
        }
        return trajectoryDataPoint;
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
