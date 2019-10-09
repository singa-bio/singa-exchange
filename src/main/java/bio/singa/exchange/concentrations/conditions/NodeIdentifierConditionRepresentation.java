package bio.singa.exchange.concentrations.conditions;

import bio.singa.simulation.model.concentrations.NodeIdentifierCondition;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class NodeIdentifierConditionRepresentation extends ConditionRepresentation {

    @JsonProperty
    private List<String> identifiers;

    public NodeIdentifierConditionRepresentation() {
        this.identifiers = new ArrayList<>();
    }

    public static NodeIdentifierConditionRepresentation of(NodeIdentifierCondition condition) {
        NodeIdentifierConditionRepresentation representation = new NodeIdentifierConditionRepresentation();
        representation.identifiers = condition.getIdentifiers();
        return representation;
    }

    public NodeIdentifierCondition toModel() {
        return NodeIdentifierCondition.forIdentifiers(getIdentifiers());
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }
}
