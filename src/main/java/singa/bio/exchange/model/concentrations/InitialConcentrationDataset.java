package singa.bio.exchange.model.concentrations;

import bio.singa.simulation.model.concentrations.InitialConcentration;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class InitialConcentrationDataset {

    @JsonProperty
    private List<InitialConcentrationRepresentation> concentrations;

    public InitialConcentrationDataset() {
        concentrations = new ArrayList<>();
    }

    public List<InitialConcentration> toModel() {
        return getConcentrations().stream()
                .map(InitialConcentrationRepresentation::toModel)
                .collect(Collectors.toList());
    }

    public List<InitialConcentrationRepresentation> getConcentrations() {
        return concentrations;
    }

    public void setConcentrations(List<InitialConcentrationRepresentation> concentrations) {
        this.concentrations = concentrations;
    }

    public void addConcentration(InitialConcentrationRepresentation concentration) {
        concentrations.add(concentration);
    }

}
