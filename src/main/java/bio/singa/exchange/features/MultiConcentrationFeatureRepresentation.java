package bio.singa.exchange.features;

import bio.singa.exchange.concentrations.InitialConcentrationRepresentation;
import bio.singa.features.model.Feature;
import bio.singa.simulation.features.InitialConcentrations;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MultiConcentrationFeatureRepresentation extends FeatureRepresentation<List<InitialConcentrationRepresentation>> {

    @JsonProperty
    private List<InitialConcentrationRepresentation> concentrations;

    public MultiConcentrationFeatureRepresentation() {
        concentrations = new ArrayList<>();
    }

    public static MultiConcentrationFeatureRepresentation of(Feature<?> feature) {
        MultiConcentrationFeatureRepresentation representation = new MultiConcentrationFeatureRepresentation();
        representation.setName(feature.getClass().getSimpleName());
        ((InitialConcentrations) feature).getContent().stream()
                .map(InitialConcentrationRepresentation::of)
                .forEach(representation::addInitialConcentration);
        return representation;
    }

    public List<InitialConcentrationRepresentation> getConcentrations() {
        return concentrations;
    }

    public void addInitialConcentration(InitialConcentrationRepresentation representation) {
        concentrations.add(representation);
    }

    public void setConcentrations(List<InitialConcentrationRepresentation> concentrations) {
        this.concentrations = concentrations;
    }
}
