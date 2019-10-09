package bio.singa.exchange.graphs.automaton;

import bio.singa.mathematics.graphs.model.Edge;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class EdgeRepresentation {

    @JsonProperty
    private int identifier;

    @JsonProperty
    private String source;

    @JsonProperty
    private String target;

    public EdgeRepresentation() {

    }

    public static EdgeRepresentation of(Edge edge) {
        EdgeRepresentation representation = new EdgeRepresentation();
        representation.setIdentifier(edge.getIdentifier());
        representation.setSource(edge.getSource().getIdentifier().toString());
        representation.setTarget(edge.getTarget().getIdentifier().toString());
        return representation;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
