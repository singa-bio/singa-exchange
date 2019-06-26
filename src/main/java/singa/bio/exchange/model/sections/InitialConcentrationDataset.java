package singa.bio.exchange.model.sections;

import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class InitialConcentrationDataset {

    @JsonProperty
    private List<InitialConcentrationRepresentation> concentrations;

    public InitialConcentrationDataset() {
        concentrations = new ArrayList<>();
    }

    public ConcentrationInitializer toModel() {
        ConcentrationInitializer concentrationInitializer = new ConcentrationInitializer();
        getConcentrations().stream()
                .map(InitialConcentrationRepresentation::toModel)
                .forEach(concentrationInitializer::addInitialConcentration);
        return concentrationInitializer;
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
