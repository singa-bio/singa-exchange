package singa.bio.exchange.model.macroscopic;

import bio.singa.simulation.model.agents.membranes.Membrane;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.Jasonizable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MembraneDataset implements Jasonizable {

    @JsonProperty
    private List<MembraneRepresentation> membranes;

    public MembraneDataset() {
        membranes = new ArrayList<>();
    }

    public List<Membrane> toModel() {
        List<Membrane> modelMembranes = new ArrayList<>();
        for (MembraneRepresentation membrane : membranes) {
            membrane.toModel();
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
