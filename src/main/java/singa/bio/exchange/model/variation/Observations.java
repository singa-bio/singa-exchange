package singa.bio.exchange.model.variation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import singa.bio.exchange.model.Jsonizable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class Observations implements Jsonizable {

    @JsonProperty
    private List<Observation> observations;

    public Observations() {
        observations = new ArrayList<>();
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }

    public void addObservation(String alias, String entity, String subsection, String updatable) {
        Observation observation = Observation.create(alias, entity, subsection, updatable);
        observation.validate();
        observations.add(observation);
    }

    public static Observations getObservationsFrom(String json) throws IOException {
        return new ObjectMapper().readValue(json, Observations.class);
    }

}
