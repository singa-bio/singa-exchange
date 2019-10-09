package bio.singa.exchange.agents;

import bio.singa.simulation.model.agents.surfacelike.Membrane;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import bio.singa.exchange.Jsonizable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MembraneDataset implements Jsonizable {

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<MembraneRepresentation> membranes;

    public MembraneDataset() {
        membranes = new ArrayList<>();
    }

    public List<Membrane> toModel() {
        List<Membrane> modelMembranes = new ArrayList<>();
        for (MembraneRepresentation membrane : membranes) {
            modelMembranes.add(membrane.toModel());
        }
        return modelMembranes;
    }

    public List<MembraneRepresentation> getMembranes() {
        return membranes;
    }

    public void setMembranes(List<MembraneRepresentation> membranes) {
        this.membranes = membranes;
    }

    public void addMembrane(MembraneRepresentation representation) {
        this.membranes.add(representation);
    }

}
