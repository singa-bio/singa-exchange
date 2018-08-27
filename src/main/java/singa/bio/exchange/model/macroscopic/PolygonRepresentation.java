package singa.bio.exchange.model.macroscopic;

import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class PolygonRepresentation {

    @JsonProperty
    private List<VectorRepresentation> vertices;

    public PolygonRepresentation() {
        vertices = new ArrayList<>();
    }

    public static PolygonRepresentation of(Polygon polygon) {
        PolygonRepresentation representation = new PolygonRepresentation();
        for (Vector2D vertex : polygon.getVertices()) {
            representation.addVector(VectorRepresentation.of(vertex));
        }
        return representation;
    }

    public Polygon toModel() {
        List<Vector2D> vertices = getVertices().stream()
                .map(VectorRepresentation::toModel)
                .collect(Collectors.toList());
        return new VertexPolygon(vertices);

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
