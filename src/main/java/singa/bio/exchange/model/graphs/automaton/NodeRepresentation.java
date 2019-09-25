package singa.bio.exchange.model.graphs.automaton;

import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.sections.RegionCache;
import singa.bio.exchange.model.sections.RegionRepresentation;

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

    @JsonProperty
    private String region;

    public NodeRepresentation() {

    }

    public static NodeRepresentation of(AutomatonNode node) {
        NodeCache.add(node);
        NodeRepresentation representation = new NodeRepresentation();
        representation.setIdentifier(node.getIdentifier().toString());
        representation.setX(node.getPosition().getX());
        representation.setY(node.getPosition().getY());
        representation.setRegion(RegionRepresentation.of(node.getCellRegion()).getIdentifier());
        return representation;
    }

    public AutomatonNode toModel() {
        AutomatonNode node = new AutomatonNode(RectangularCoordinate.fromString(getIdentifier()));
        node.setPosition(new Vector2D(getX(), getY()));
        if (region != null) {
            node.setCellRegion(RegionCache.get(getRegion()));
        }
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
