package singa.bio.exchange.model.concentrations.conditions;

import bio.singa.simulation.model.concentrations.*;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import singa.bio.exchange.model.IllegalConversionException;

/**
 * @author cl
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AreaConditionRepresentation.class, name = "area"),
        @JsonSubTypes.Type(value = NodeIdentifierConditionRepresentation.class, name = "node-identifier"),
        @JsonSubTypes.Type(value = NodeTypeConditionRepresentation.class, name = "node-type"),
        @JsonSubTypes.Type(value = RegionConditionRepresentation.class, name = "region"),
        @JsonSubTypes.Type(value = SectionConditionRepresentation.class, name = "subsection"),
        @JsonSubTypes.Type(value = TopologyConditionRepresentation.class, name = "topology"),
})
@JsonPropertyOrder({"primary-identifier", "type"})
public abstract class ConditionRepresentation {

    public static ConditionRepresentation of(ConcentrationCondition condition) {
        if (condition instanceof AreaCondition) {
            return AreaConditionRepresentation.of(((AreaCondition) condition));
        } else if (condition instanceof NodeIdentifierCondition) {
            return NodeIdentifierConditionRepresentation.of(((NodeIdentifierCondition) condition));
        } else if (condition instanceof NodeTypeCondition) {
            return NodeTypeConditionRepresentation.of(((NodeTypeCondition) condition));
        } else if (condition instanceof RegionCondition) {
            return RegionConditionRepresentation.of(((RegionCondition) condition));
        } else if (condition instanceof SectionCondition) {
            return SectionConditionRepresentation.of(((SectionCondition) condition));
        } else if (condition instanceof  TopologyCondition) {
            return TopologyConditionRepresentation.of(((TopologyCondition) condition));
        }
        throw new IllegalConversionException("Trying to create condition representation from unknown type.");
    }

    abstract public ConcentrationCondition toModel();

}
