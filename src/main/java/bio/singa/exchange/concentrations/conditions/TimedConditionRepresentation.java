package bio.singa.exchange.concentrations.conditions;

import bio.singa.exchange.EnumTransformation;
import bio.singa.exchange.sections.SubsectionCache;
import bio.singa.exchange.sections.SubsectionRepresentation;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.concentrations.SectionCondition;
import bio.singa.simulation.model.concentrations.TimedCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Unit;
import javax.measure.quantity.Time;

public class TimedConditionRepresentation extends ConditionRepresentation {

    @JsonProperty("time-value")
    private double timeValue;

    @JsonProperty("time-unit")
    private Unit<Time> timeUnit;

    private String relation;

    public static TimedConditionRepresentation of(TimedCondition condition) {
        TimedConditionRepresentation representation = new TimedConditionRepresentation();
        representation.setTimeValue(condition.getTime().getValue().doubleValue());
        representation.setTimeUnit(condition.getTime().getUnit());
        representation.setRelation(EnumTransformation.fromRelation(condition.getRelation()));
        return representation;
    }

    public TimedCondition toModel() {
        return TimedCondition.of(EnumTransformation.toRelation(getRelation()), Quantities.getQuantity(getTimeValue(), getTimeUnit()));
    }

    public double getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(double timeValue) {
        this.timeValue = timeValue;
    }

    public Unit<Time> getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(Unit<Time> timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
