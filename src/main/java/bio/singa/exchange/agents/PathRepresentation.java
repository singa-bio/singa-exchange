package bio.singa.exchange.agents;

import bio.singa.mathematics.geometry.edges.VectorPath;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class PathRepresentation {

    @JsonProperty
    private List<VectorRepresentation> vertices;

    public PathRepresentation() {
        vertices = new ArrayList<>();
    }

    public static PathRepresentation of(Polygon polygon) {
        PathRepresentation representation = new PathRepresentation();
        for (Vector2D vertex : polygon.getVertices()) {
            representation.addVector(VectorRepresentation.of(vertex));
        }
//        Vector2D first = polygon.getVertices().get(0);
//        Vector2D last = polygon.getVertices().get(polygon.getNumberOfVertices() - 1);
//        if (!first.equals(last)) {
//            representation.addVector(VectorRepresentation.of(first));
//        }
        return representation;
    }

    public static  PathRepresentation of (VectorPath path) {
        PathRepresentation representation = new PathRepresentation();
        for (Vector2D segment : path.getSegments()) {
            representation.addVector(VectorRepresentation.of(segment));
        }
        return representation;
    }

    public List<Vector2D> toModel() {
        return getVertices().stream()
                .map(VectorRepresentation::toModel)
                .collect(Collectors.toList());
    }

    public List<VectorRepresentation> getVertices() {
        return vertices;
    }

    public void setVertices(List<VectorRepresentation> vertices) {
        this.vertices = vertices;
    }

    public void addVector(VectorRepresentation vector) {
        vertices.add(vector);
    }

}
