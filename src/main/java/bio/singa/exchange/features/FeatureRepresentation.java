package bio.singa.exchange.features;

import bio.singa.exchange.IllegalConversionException;
import bio.singa.exchange.evidence.EvidenceRepresentation;
import bio.singa.exchange.variation.Variable;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.Feature;
import bio.singa.features.model.QualitativeFeature;
import bio.singa.simulation.features.InitialConcentrations;
import bio.singa.simulation.features.model.MultiEntityFeature;
import bio.singa.simulation.features.model.MultiStringFeature;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = QuantitativeFeatureRepresentation.class, name = "quantitative"),
        @JsonSubTypes.Type(value = QualitativeFeatureRepresentation.class, name = "qualitative"),
        @JsonSubTypes.Type(value = MultiEntityFeatureRepresentation.class, name = "entity-multi"),
        @JsonSubTypes.Type(value = MultiStringFeatureRepresentation.class, name = "string-multi"),
        @JsonSubTypes.Type(value = MultiConcentrationFeatureRepresentation.class, name = "concentration-multi")
})
@JsonPropertyOrder({"name"})
public abstract class FeatureRepresentation<Type> extends Variable<Type> {

    @JsonProperty
    private int identifier;

    @JsonProperty
    private String name;

    @JsonProperty
    private List<String> evidence;

    public FeatureRepresentation() {
        evidence = new ArrayList<>();
    }

    public static FeatureRepresentation of(Feature<?> feature) {
        if (feature.getContent() instanceof Quantity) {
            return QuantitativeFeatureRepresentation.of(feature);
        } else if (feature instanceof MultiEntityFeature) {
            return MultiEntityFeatureRepresentation.of(feature);
        } else if (feature instanceof MultiStringFeature) {
            return MultiStringFeatureRepresentation.of(feature);
        } else if (feature instanceof InitialConcentrations) {
            return MultiConcentrationFeatureRepresentation.of(feature);
        } else if (feature instanceof QualitativeFeature) {
            return QualitativeFeatureRepresentation.of(feature);
        }
        throw new IllegalConversionException("The feature " + feature + " could not be converted to its json representation.");
    }

    public Feature<?> toModel() {
        return FeatureFactory.create(this);
    }

    protected void baseSetup(Feature<?> feature) {
        setIdentifier(feature.getIdentifier());
        setName(feature.getClass().getSimpleName());
        addEvidence(feature.getAllEvidence());
        if (getEvidence().isEmpty()) {
            addEvidence(Evidence.NO_EVIDENCE);
        }
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> identfiiers) {
        this.evidence = identfiiers;
    }

    public void addEvidence(List<Evidence> evidences) {
        for (Evidence evidence : evidences) {
            this.evidence.add(EvidenceRepresentation.of(evidence).getIdentifier());
        }
    }

    public void addEvidence(Evidence evidence) {
        this.evidence.add(EvidenceRepresentation.of(evidence).getIdentifier());
    }

    public abstract Type fetchContent();

}
