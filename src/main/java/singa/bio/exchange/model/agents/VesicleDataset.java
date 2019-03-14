package singa.bio.exchange.model.agents;

import bio.singa.simulation.model.agents.pointlike.Vesicle;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class VesicleDataset {

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<VesicleRepresentation> vesicles;

    public VesicleDataset() {
        vesicles = new ArrayList<>();
    }

    public List<Vesicle> toModel() {
        List<Vesicle> vesicleAgents = new ArrayList<>();
        for (VesicleRepresentation vesicle : vesicles) {
            vesicleAgents.add(vesicle.toModel());
        }
        return vesicleAgents;
    }

    public List<VesicleRepresentation> getVesicles() {
        return vesicles;
    }

    public void setVesicles(List<VesicleRepresentation> vesicles) {
        this.vesicles = vesicles;
    }

    public void addVesicle(VesicleRepresentation representation) {
        this.vesicles.add(representation);
    }

}
