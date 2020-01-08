package bio.singa.exchange.agents;

import bio.singa.exchange.Converter;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.graphs.AutomatonNode;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class MembraneSegmentRepresentation {

    @JsonProperty
    private VectorRepresentation start;

    @JsonProperty
    private VectorRepresentation end;

    @JsonProperty
    private String node;

    public static MembraneSegmentRepresentation of(MembraneSegment membraneSegment) {
        MembraneSegmentRepresentation representation = new MembraneSegmentRepresentation();
        representation.setStart(VectorRepresentation.of(membraneSegment.getStartingPoint()));
        representation.setEnd(VectorRepresentation.of(membraneSegment.getEndingPoint()));
        representation.setNode(membraneSegment.getNode().getStringIdentifier());
        return representation;
    }

    public MembraneSegment toModel() {
        RectangularCoordinate coordinate = RectangularCoordinate.fromString(getNode().replace("n", ""));
        AutomatonNode node = Converter.current.getGraph().getNode(coordinate);
        LineSegment lineSegemnt = new SimpleLineSegment(getStart().toModel(), getEnd().toModel());
        return new MembraneSegment(node, lineSegemnt);
    }

    public VectorRepresentation getStart() {
        return start;
    }

    public void setStart(VectorRepresentation start) {
        this.start = start;
    }

    public VectorRepresentation getEnd() {
        return end;
    }

    public void setEnd(VectorRepresentation end) {
        this.end = end;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }
}
