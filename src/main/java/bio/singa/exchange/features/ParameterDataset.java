package bio.singa.exchange.features;

import bio.singa.exchange.Jsonizable;
import bio.singa.simulation.model.parameters.ParameterStorage;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class ParameterDataset implements Jsonizable {

    @JsonProperty
    private List<ParameterRepresentation> parameters;

    public ParameterDataset() {
        parameters = new ArrayList<>();
    }

    public static ParameterDataset fromCache() {
        ParameterDataset dataset = new ParameterDataset();
        ParameterStorage.getAll().values().stream()
                .map(ParameterRepresentation::of)
                .forEach(dataset::addParameter);
        return dataset;
    }

    public List<ParameterRepresentation> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterRepresentation> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(ParameterRepresentation representation) {
        parameters.add(representation);
    }

}
