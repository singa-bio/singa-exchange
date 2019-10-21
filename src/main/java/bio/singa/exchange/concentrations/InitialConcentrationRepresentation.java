package bio.singa.exchange.concentrations;

import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.exchange.EnumTransformation;
import bio.singa.exchange.concentrations.conditions.ConditionRepresentation;
import bio.singa.exchange.entities.EntityRepresentation;
import bio.singa.exchange.evidence.EvidenceCache;
import bio.singa.exchange.evidence.EvidenceRepresentation;
import bio.singa.exchange.sections.SubsectionCache;
import bio.singa.exchange.sections.SubsectionRepresentation;
import bio.singa.exchange.variation.Variable;
import bio.singa.features.model.Evidence;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.concentrations.InitialConcentration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class InitialConcentrationRepresentation extends Variable<Double> {

    @JsonProperty
    private int identifier;

    @JsonProperty
    private List<ConditionRepresentation> conditions;

    @JsonProperty
    private String subsection;

    @JsonProperty
    private String topology;

    @JsonProperty
    private String entity;

    @JsonProperty("concentration-value")
    private double concentrationValue;

    @JsonProperty("concentration-unit")
    private Unit<MolarConcentration> concentrationUnit;

    @JsonProperty("time-value")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private double timeValue;

    @JsonProperty("time-unit")
    private Unit<Time> timeUnit;

    @JsonProperty
    private boolean fixed;

    @JsonProperty
    private List<String> evidence;

    public InitialConcentrationRepresentation() {
        conditions = new ArrayList<>();
        evidence = new ArrayList<>();
    }

    public static InitialConcentrationRepresentation of(InitialConcentration initialConcentration) {
        InitialConcentrationRepresentation representation = new InitialConcentrationRepresentation();
        representation.setIdentifier(initialConcentration.getIdentifier());
        initialConcentration.getConditions().values().stream()
                .map(ConditionRepresentation::of)
                .forEach(representation::addCondition);
        if (initialConcentration.getSubsection() != null) {
            representation.setSubsection(SubsectionRepresentation.of(initialConcentration.getSubsection()).getIdentifier());
        }
        if (initialConcentration.getTopology() != null) {
            representation.setTopology(EnumTransformation.fromTopology(initialConcentration.getTopology()));
        }
        representation.setEntity(EntityRepresentation.of(initialConcentration.getEntity()).getPrimaryIdentifier());
        representation.setConcentrationValue(initialConcentration.getConcentration().getValue().doubleValue());
        representation.setConcentrationUnit(initialConcentration.getConcentration().getUnit());
        representation.setTimeValue(initialConcentration.getTime().getValue().doubleValue());
        representation.setTimeUnit(initialConcentration.getTime().getUnit());
        representation.setFixed(initialConcentration.isFix());
        representation.addEvidence(initialConcentration.getAllEvidence());
        if (representation.getEvidence().isEmpty()) {
            representation.addEvidence(Evidence.NO_EVIDENCE);
        }
        for (Quantity<MolarConcentration> alternativeContent : initialConcentration.getAlternativeContents()) {
            representation.addAlternativeValue(alternativeContent.to(representation.getConcentrationUnit()).getValue().doubleValue());
        }
        return representation;
    }

    public InitialConcentration toModel() {
        InitialConcentration concentration = new InitialConcentration();
        concentration.setIdentifier(getIdentifier());
        getConditions().stream()
                .map(ConditionRepresentation::toModel)
                .forEach(concentration::addCondition);
        if (getSubsection() != null) {
            concentration.setSubsection(SubsectionCache.get(getSubsection()));
        }
        if (getTopology() != null) {
            concentration.setTopology(EnumTransformation.toTopology(getTopology()));
        }
        concentration.setEntity(EntityRegistry.get(getEntity()));
        concentration.setConcentration(Quantities.getQuantity(getConcentrationValue(), getConcentrationUnit()));
        concentration.setTime(Quantities.getQuantity(getTimeValue(), getTimeUnit()));
        concentration.setFix(isFixed());
        for (String evidenceIdentifier : getEvidence()) {
            concentration.addEvidence(EvidenceCache.get(evidenceIdentifier));
        }
        for (Double alternativeValue : getAlternativeValues()) {
            concentration.addAlternativeContent(Quantities.getQuantity(alternativeValue, getConcentrationUnit()));
        }
        return concentration;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public List<ConditionRepresentation> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionRepresentation> conditions) {
        this.conditions = conditions;
    }

    public void addCondition(ConditionRepresentation conditionRepresentation) {
        conditions.add(conditionRepresentation);
    }

    public String getSubsection() {
        return subsection;
    }

    public void setSubsection(String subsection) {
        this.subsection = subsection;
    }

    public String getTopology() {
        return topology;
    }

    public void setTopology(String topology) {
        this.topology = topology;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
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

    public void setConcentrationUnit(Unit<MolarConcentration> concentrationUnit) {
        this.concentrationUnit = concentrationUnit;
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

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }

    public void addEvidence(List<Evidence> evidences) {
        for (Evidence evidence : evidences) {
            this.evidence.add(EvidenceRepresentation.of(evidence).getIdentifier());
        }
    }

    public void addEvidence(Evidence evidence) {
        this.evidence.add(EvidenceRepresentation.of(evidence).getIdentifier());
    }

}
