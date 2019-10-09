package bio.singa.exchange.concentrations.conditions;

import bio.singa.mathematics.geometry.faces.ComplexPolygon;
import bio.singa.simulation.model.concentrations.AreaCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import bio.singa.exchange.agents.PathRepresentation;

/**
 * @author cl
 */
public class AreaConditionRepresentation extends ConditionRepresentation {

    @JsonProperty
    private PathRepresentation area;

    public static AreaConditionRepresentation of(AreaCondition condition) {
        AreaConditionRepresentation representation = new AreaConditionRepresentation();
        representation.area = PathRepresentation.of(condition.getPolygon());
        return representation;
    }

    public AreaCondition toModel() {
        return AreaCondition.inPolygon(new ComplexPolygon(getArea().toModel()));
    }

    public PathRepresentation getArea() {
        return area;
    }

    public void setArea(PathRepresentation area) {
        this.area = area;
    }

}
