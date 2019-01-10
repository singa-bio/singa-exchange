package singa.bio.exchange.model.sections;

import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.sections.concentration.InitialConcentration;
import bio.singa.simulation.model.sections.concentration.MembraneConcentration;
import bio.singa.simulation.model.sections.concentration.SectionConcentration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.evidence.EvidenceRepresentation;

/**
 * @author cl
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MembraneConcentrationRepresentation.class, name = "membrane"),
        @JsonSubTypes.Type(value = SectionConcentrationRepresentation.class, name = "section")
})
@JsonPropertyOrder({"name"})
public abstract class InitialConcentrationRepresentation {

    @JsonProperty
    private String region;

    @JsonProperty
    private String entity;

    @JsonProperty
    private String evidence;

    public InitialConcentrationRepresentation() {

    }

    public static InitialConcentrationRepresentation of(InitialConcentration initialConcentration) {
        if (initialConcentration instanceof SectionConcentration) {
            return SectionConcentrationRepresentation.of((SectionConcentration) initialConcentration);
        } else if (initialConcentration instanceof MembraneConcentration) {
            return MembraneConcentrationRepresentation.of((MembraneConcentration) initialConcentration);
        }
        throw new IllegalConversionException("Illegal initial concentration format.");
    }

    public abstract InitialConcentration toModel();

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public void addEvidence(Evidence evidence) {
        this.evidence = EvidenceRepresentation.of(evidence).getIdentifier();
    }

}
