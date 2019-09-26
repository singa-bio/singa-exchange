package singa.bio.exchange.model.concentrations.conditions;

import bio.singa.simulation.model.concentrations.TopologyCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.EnumTransformation;

/**
 * @author cl
 */
public class TopologyConditionRepresentation extends ConditionRepresentation {

    @JsonProperty
    private String topology;

    public static TopologyConditionRepresentation of(TopologyCondition condition) {
        TopologyConditionRepresentation representation = new TopologyConditionRepresentation();
        representation.setTopology(EnumTransformation.fromTopology(condition.getTopology()));
        return representation;
    }

    public TopologyCondition toModel() {
        return TopologyCondition.isTopology(EnumTransformation.toTopology(getTopology()));
    }

    public String getTopology() {
        return topology;
    }

    public void setTopology(String topology) {
        this.topology = topology;
    }

}
