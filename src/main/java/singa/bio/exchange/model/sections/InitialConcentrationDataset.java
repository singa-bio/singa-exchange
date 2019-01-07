package singa.bio.exchange.model.sections;

import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import bio.singa.simulation.model.sections.concentration.InitialConcentration;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class InitialConcentrationDataset {

    @JsonProperty
    private List<SectionConcentrationRepresentation> concentrations;

    public InitialConcentrationDataset() {
        concentrations = new ArrayList<>();
    }

    public ConcentrationInitializer toModel() {
        Set<InitialConcentration> initialConcentrations = getConcentrations().stream()
                .map(SectionConcentrationRepresentation::toModel)
                .collect(Collectors.toSet());
        return new ConcentrationInitializer(initialConcentrations);
    }

    public List<SectionConcentrationRepresentation> getConcentrations() {
        return concentrations;
    }

    public void setConcentrations(List<SectionConcentrationRepresentation> concentrations) {
        this.concentrations = concentrations;
    }

    public void addConcentration(SectionConcentrationRepresentation concentration) {
        concentrations.add(concentration);
    }

}
