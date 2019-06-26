package singa.bio.exchange.model.sections;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.sections.concentration.FixedConcentration;
import bio.singa.simulation.model.sections.concentration.InitialConcentration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.entities.EntityCache;
import singa.bio.exchange.model.entities.EntityRepresentation;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Unit;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class FixedConcentrationRepresentation extends InitialConcentrationRepresentation<Double> {

    @JsonProperty
    private String subsection;

    @JsonProperty
    private List<String> identifiers;

    @JsonProperty("concentration-value")
    private double concentrationValue;

    @JsonProperty("concentration-unit")
    private Unit<MolarConcentration> concentrationUnit;

    @JsonProperty("time-value")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private double timeValue;

    @JsonProperty("time-unit")
    private Unit<Time> timeUnit;

    public FixedConcentrationRepresentation() {
        identifiers = new ArrayList<>();
    }

    public static FixedConcentrationRepresentation of(FixedConcentration initialConcentration) {
        FixedConcentrationRepresentation representation = new FixedConcentrationRepresentation();
        representation.setIdentifiers(initialConcentration.getIdentifiers());
        representation.setSubsection(SubsectionRepresentation.of(initialConcentration.getSubsection()).getIdentifier());
        representation.setEntity(EntityRepresentation.of(initialConcentration.getEntity()).getPrimaryIdentifier());
        representation.setConcentrationValue(initialConcentration.getConcentration().getValue().doubleValue());
        representation.setConcentrationUnit(initialConcentration.getConcentration().getUnit());
        if (initialConcentration.getTime() != null) {
            representation.setTimeValue(initialConcentration.getTime().getValue().doubleValue());
            representation.setTimeUnit(initialConcentration.getTime().getUnit());
        }
        representation.addEvidence(initialConcentration.getEvidence());
        return representation;
    }

    @Override
    public InitialConcentration toModel() {
        FixedConcentration fixedConcentration = new FixedConcentration(getIdentifiers(),
                SubsectionCache.get(getSubsection()),
                EntityCache.get(getEntity()),
                Quantities.getQuantity(getConcentrationValue(), getConcentrationUnit()));
        if (timeUnit != null) {
            fixedConcentration.setTime(Quantities.getQuantity(getTimeValue(), getTimeUnit()));
        }
        return fixedConcentration;
    }

    public String getSubsection() {
        return subsection;
    }

    public void setSubsection(String subsection) {
        this.subsection = subsection;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public double getConcentrationValue() {
        return concentrationValue;
    }

    public void setConcentrationValue(double concentrationValue) {
        this.concentrationValue = concentrationValue;
    }

    public Unit<MolarConcentration> getConcentrationUnit() {
        return concentrationUnit;
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

    public void setConcentrationUnit(Unit<MolarConcentration> concentrationUnit) {
        this.concentrationUnit = concentrationUnit;
    }


}
