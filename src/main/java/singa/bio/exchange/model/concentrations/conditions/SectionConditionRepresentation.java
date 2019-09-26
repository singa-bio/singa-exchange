package singa.bio.exchange.model.concentrations.conditions;

import bio.singa.simulation.model.concentrations.SectionCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.sections.SubsectionCache;
import singa.bio.exchange.model.sections.SubsectionRepresentation;

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
