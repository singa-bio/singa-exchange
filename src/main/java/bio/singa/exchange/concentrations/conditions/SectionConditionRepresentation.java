package bio.singa.exchange.concentrations.conditions;

import bio.singa.exchange.sections.SubsectionRepresentation;
import bio.singa.simulation.model.concentrations.SectionCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import bio.singa.exchange.sections.SubsectionCache;

/**
 * @author cl
 */
public class SectionConditionRepresentation extends ConditionRepresentation {

    @JsonProperty
    private String subsection;

    public static SectionConditionRepresentation of(SectionCondition condition) {
        SectionConditionRepresentation representation = new SectionConditionRepresentation();
        representation.setSubsection(SubsectionRepresentation.of(condition.getSubsection()).getIdentifier());
        return representation;
    }

    public SectionCondition toModel() {
        return SectionCondition.forSection(SubsectionCache.get(getSubsection()));
    }

    public String getSubsection() {
        return subsection;
    }

    public void setSubsection(String subsection) {
        this.subsection = subsection;
    }
}
