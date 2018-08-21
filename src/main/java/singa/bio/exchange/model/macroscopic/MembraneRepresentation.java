package singa.bio.exchange.model.macroscopic;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.simulation.model.agents.membranes.Membrane;
import bio.singa.simulation.model.agents.membranes.MembraneSegment;
import bio.singa.simulation.model.agents.membranes.MembraneTracer;
import bio.singa.simulation.model.sections.CellRegion;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.Converter;
import singa.bio.exchange.model.sections.RegionCache;
import singa.bio.exchange.model.sections.RegionRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class MembraneRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty
    private String region;

    @JsonProperty
    private List<SegmentRepresentation> segments;

    public MembraneRepresentation() {
        segments = new ArrayList<>();
    }

    public static MembraneRepresentation of(Membrane membrane) {
        MembraneRepresentation representation = new MembraneRepresentation();
        representation.setIdentifier(membrane.getIdentifier());
        representation.setRegion(RegionRepresentation.of(membrane.getRepresentativeRegion()).getIdentifier());
        for (MembraneSegment segment : membrane.getSegments()) {
            representation.addSegment(SegmentRepresentation.of(segment.getSegment()));
        }
        return representation;
    }

    public Membrane toModel() {
        CellRegion membraneRegion = RegionCache.get(getRegion());
        CellRegion innerRegion = RegionCache.getInner(membraneRegion.getInnerSubsection().getIdentifier());
        List<LineSegment> lineSegments = segments.stream().map(SegmentRepresentation::toModel).collect(Collectors.toList());
        Polygon polygon = new VertexPolygon(lineSegments);
        return MembraneTracer.membraneToRegion(membraneRegion, innerRegion, polygon, Converter.current.getGraph());
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<SegmentRepresentation> getSegments() {
        return segments;
    }

    public void setSegments(List<SegmentRepresentation> segments) {
        this.segments = segments;
    }
    public void addSegment(SegmentRepresentation segment) {
        segments.add(segment);
    }

}
