package singa.bio.exchange.model.graphs;

import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonNode;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class NodeRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty
    private double x;

    @JsonProperty
    private double y;

    public NodeRepresentation() {

    }

    public static NodeRepresentation of(Node<?, Vector2D, ?> node) {
        NodeRepresentation representation = new NodeRepresentation();
        representation.setIdentifier(node.getIdentifier().toString());
        representation.setX(node.getPosition().getX());
        representation.setY(node.getPosition().getY());
        return representation;
    }

    public AutomatonNode toModel() {
        AutomatonNode node = new AutomatonNode(RectangularCoordinate.fromString(getIdentifier()));
        node.setPosition(new Vector2D(getX(), getY()));
        NodeCache.add(node);
        return node;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

}
