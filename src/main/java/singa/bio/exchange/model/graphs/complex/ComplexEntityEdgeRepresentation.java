package singa.bio.exchange.model.graphs.complex;

import bio.singa.chemistry.entities.complex.GraphComplexEdge;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class ComplexEntityEdgeRepresentation {

    @JsonProperty
    private int identifier;

    @JsonProperty
    private int source;

    @JsonProperty
    private int target;

    @JsonProperty
    private String connection;

    public ComplexEntityEdgeRepresentation() {
    }

    public static ComplexEntityEdgeRepresentation of(GraphComplexEdge edge) {
        ComplexEntityEdgeRepresentation representation = new ComplexEntityEdgeRepresentation();
        representation.setIdentifier(edge.getIdentifier());
        representation.setSource(edge.getSource().getIdentifier());
        representation.setTarget(edge.getTarget().getIdentifier());
        representation.setConnection(edge.getConnectedSite().toString());
        return representation;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }
}
