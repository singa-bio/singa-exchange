package bio.singa.exchange.evidence;

import bio.singa.exchange.Jsonizable;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class EvidenceDataset implements Jsonizable {

    @JsonProperty
    private List<EvidenceRepresentation> evidence;

    public EvidenceDataset() {
        evidence = new ArrayList<>();
    }

    public static EvidenceDataset fromCache() {
        EvidenceDataset dataset = new EvidenceDataset();
        EvidenceCache.getAll().stream()
                .map(EvidenceRepresentation::of)
                .forEach(dataset::addEvidence);
        return dataset;
    }

    public void cache() {
        EvidenceCache.clear();
        for (EvidenceRepresentation representation : evidence) {
            EvidenceCache.add(representation.toModel());
        }
    }

    public List<EvidenceRepresentation> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<EvidenceRepresentation> evidence) {
        this.evidence = evidence;
    }

    public void addEvidence(EvidenceRepresentation representation) {
        evidence.add(representation);
    }

}
