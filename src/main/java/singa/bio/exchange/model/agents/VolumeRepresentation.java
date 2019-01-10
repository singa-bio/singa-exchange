package singa.bio.exchange.model.agents;

import bio.singa.mathematics.geometry.faces.ComplexPolygon;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.sections.RegionCache;
import singa.bio.exchange.model.sections.RegionRepresentation;

/**
 * @author cl
 */
public class VolumeRepresentation {

    @JsonProperty
    private PathRepresentation area;

    @JsonProperty
    private String region;

    public VolumeRepresentation() {

    }

    public static VolumeRepresentation of(VolumeLikeAgent agent) {
        VolumeRepresentation representation = new VolumeRepresentation();
        representation.setArea(PathRepresentation.of(agent.getArea()));
        representation.setRegion(RegionRepresentation.of(agent.getCellRegion()).getIdentifier());
        return representation;
    }

    public VolumeLikeAgent toModel() {
        return new VolumeLikeAgent(new ComplexPolygon(getArea().toModel()), RegionCache.get(getRegion()));
    }

    public PathRepresentation getArea() {
        return area;
    }

    public void setArea(PathRepresentation area) {
        this.area = area;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
