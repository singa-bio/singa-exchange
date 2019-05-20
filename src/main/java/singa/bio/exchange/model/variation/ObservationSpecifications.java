package singa.bio.exchange.model.variation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import singa.bio.exchange.model.Jsonizable;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class ObservationSpecifications implements Jsonizable {

    @JsonProperty
    private List<ObservationSpecification> observations;

    public ObservationSpecifications() {
        observations = new ArrayList<>();
    }

    public List<ObservationSpecification> getObservations() {
        return observations;
    }

    public void setObservations(List<ObservationSpecification> observations) {
        this.observations = observations;
    }

    public void addObservation(String alias, List<Quantity<Time>> times, String entity, String subsection, String updatable) {
        ObservationSpecification observation = ObservationSpecification.create(alias, times, entity, subsection, updatable);
        observation.validate();
        observations.add(observation);
    }

    public static ObservationSpecifications getObservationsFrom(String json) throws IOException {
        return new ObjectMapper().readValue(json, ObservationSpecifications.class);
    }

}
