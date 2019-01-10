package singa.bio.exchange.model.agents;

import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class VolumeDataset {

    @JsonProperty
    private List<VolumeRepresentation> volumes;

    public VolumeDataset() {
        volumes = new ArrayList<>();
    }

    public List<VolumeLikeAgent> toModel() {
        List<VolumeLikeAgent> volumeAgents = new ArrayList<>();
        for (VolumeRepresentation volume : volumes) {
            volumeAgents.add(volume.toModel());
        }
        return volumeAgents;
    }

    public List<VolumeRepresentation> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<VolumeRepresentation> volumes) {
        this.volumes = volumes;
    }

    public void addVolume(VolumeRepresentation representation) {
        this.volumes.add(representation);
    }

}
