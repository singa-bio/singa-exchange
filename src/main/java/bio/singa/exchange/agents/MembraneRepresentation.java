package bio.singa.exchange.agents;

import bio.singa.exchange.sections.RegionCache;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.sections.CellRegion;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
@JsonPropertyOrder({"identifier", "inner-region", "inner-point", "membrane-region", "regions"})
public class MembraneRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty("inner-region")
    private String innerRegion;

    @JsonProperty("membrane-region")
    private String membraneRegion;

    @JsonProperty
    private List<MembraneSegmentRepresentation> segments;


    public MembraneRepresentation() {
        segments = new ArrayList<>();
    }

    public static MembraneRepresentation of(Membrane membrane) {
        // initialize
        MembraneRepresentation representation = new MembraneRepresentation();
        representation.setIdentifier(membrane.getIdentifier());
        RegionCache.add(membrane.getInnerRegion());
        representation.setInnerRegion(membrane.getInnerRegion().getIdentifier());
        RegionCache.add(membrane.getMembraneRegion());
        // convert membrane segments
        membrane.getSegments().stream()
                .map(MembraneSegmentRepresentation::of)
                .forEach(representation::addMembraneSegment);
        return representation;
    }

    public Membrane toModel() {
        // initialize
        Membrane membrane = new Membrane(getIdentifier());
        membrane.setInnerRegion(RegionCache.get(getInnerRegion()));
        CellRegion membraneRegion = RegionCache.get(getMembraneRegion());
        membrane.setMembraneRegion(membraneRegion);
        // reconstruct membrane segments
        getSegments().stream()
                .map(MembraneSegmentRepresentation::toModel)
                .forEach(membrane::addSegment);
        // reconstruct mapping
        List<Vector2D> orderedVectors = Vectors.getVectorsInOrder(membrane.getSegments());
        Map<CellRegion, List<Vector2D>> regionMap = new HashMap<>();
        regionMap.put(membraneRegion, orderedVectors);
        membrane.setRegionMap(regionMap);
        return membrane;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getInnerRegion() {
        return innerRegion;
    }

    public void setInnerRegion(String innerRegion) {
        this.innerRegion = innerRegion;
    }

    public String getMembraneRegion() {
        return membraneRegion;
    }

    public void setMembraneRegion(String membraneRegion) {
        this.membraneRegion = membraneRegion;
    }

    public List<MembraneSegmentRepresentation> getSegments() {
        return segments;
    }

    public void addMembraneSegment(MembraneSegmentRepresentation representation) {
        segments.add(representation);
    }

    public void setSegments(List<MembraneSegmentRepresentation> segments) {
        this.segments = segments;
    }
}
