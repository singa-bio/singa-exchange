package singa.bio.exchange.model.concentrations.conditions;

import bio.singa.simulation.model.concentrations.NodeTypeCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.IllegalConversionException;

/**
 * @author cl
 */
public class NodeTypeConditionRepresentation extends ConditionRepresentation {

    @JsonProperty("node-type")
    private String nodeType;

    public static NodeTypeConditionRepresentation of(NodeTypeCondition condition) {
        NodeTypeConditionRepresentation representation = new NodeTypeConditionRepresentation();
        representation.setNodeType(condition.getType());
        return representation;
    }

    public NodeTypeCondition toModel() {
        if (getNodeType().equals("node")) {
            return NodeTypeCondition.isNode();
        } else if (getNodeType().equals("vesicle")) {
            return NodeTypeCondition.isNode();
        } else {
            throw new IllegalConversionException("Trying to create node type condition representation from unknown type " + getNodeType() + ".");
        }
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
