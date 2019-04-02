package singa.bio.exchange.model.trajectories;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.trajectories.nested.ConcentrationData;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class TrajectoryConcentrationRepresentation {

    @JsonProperty
    private Map<String, Map<String, Double>> concentrations;

    private TrajectoryConcentrationRepresentation() {
        concentrations = new HashMap<>();
    }

    public static TrajectoryConcentrationRepresentation of(ConcentrationData concentrationData) {
        TrajectoryConcentrationRepresentation representation = new TrajectoryConcentrationRepresentation();
        for (Map.Entry<CellSubsection, Map<ChemicalEntity, Double>> subsectionEntry : concentrationData.getConcentrations().entrySet()) {
            String subsection = subsectionEntry.getKey().getIdentifier();
            Map<String, Double> concentrationMap = new HashMap<>();
            for (Map.Entry<ChemicalEntity, Double> concentrationEntry : subsectionEntry.getValue().entrySet()) {
                concentrationMap.put(concentrationEntry.getKey().getIdentifier().toString(), concentrationEntry.getValue());
            }
            representation.concentrations.put(subsection, concentrationMap);
        }
        return representation;
    }

    public Map<String, Map<String, Double>> getConcentrations() {
        return concentrations;
    }

    public void setConcentrations(Map<String, Map<String, Double>> concentrations) {
        this.concentrations = concentrations;
    }

}
