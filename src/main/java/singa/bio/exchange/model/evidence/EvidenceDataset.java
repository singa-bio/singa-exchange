package singa.bio.exchange.model.evidence;

import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.Jasonizable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class EvidenceDataset implements Jasonizable {

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
