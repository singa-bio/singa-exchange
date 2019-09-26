package singa.bio.exchange.model.concentrations.conditions;

import bio.singa.mathematics.geometry.faces.ComplexPolygon;
import bio.singa.simulation.model.concentrations.AreaCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.agents.PathRepresentation;

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
