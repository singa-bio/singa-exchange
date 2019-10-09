package bio.singa.exchange.agents;

import bio.singa.exchange.Converter;
import bio.singa.mathematics.geometry.edges.VectorPath;
import bio.singa.simulation.model.agents.linelike.LineLikeAgent;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class FilamentRepresentation {

    @JsonProperty
    private String type;

    @JsonProperty
    private PathRepresentation path;

    public FilamentRepresentation() {

    }

    public static FilamentRepresentation of(LineLikeAgent agent) {
        FilamentRepresentation representation = new FilamentRepresentation();
        representation.setPath(PathRepresentation.of(agent.getPath()));
        representation.setType(agent.getType());
        return representation;
    }

    public LineLikeAgent toModel() {
        LineLikeAgent agent = new LineLikeAgent(getType());
        agent.setPath(new VectorPath(getPath().toModel()));
        agent.associateInGraph(Converter.current.getGraph());
        agent.setMinusEndBehaviour(LineLikeAgent.GrowthBehaviour.STAGNANT);
        agent.setPlusEndBehaviour(LineLikeAgent.GrowthBehaviour.STAGNANT);
        return agent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PathRepresentation getPath() {
        return path;
    }

    public void setPath(PathRepresentation path) {
        this.path = path;
    }

}
