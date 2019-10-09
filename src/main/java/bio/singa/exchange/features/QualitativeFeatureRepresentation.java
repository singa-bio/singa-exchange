package bio.singa.exchange.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.sections.CellRegion;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class QualitativeFeatureRepresentation extends FeatureRepresentation<String> {

    @JsonProperty
    private String content;

    public QualitativeFeatureRepresentation() {

    }

    public static QualitativeFeatureRepresentation of(Feature<?> feature) {
        QualitativeFeatureRepresentation representation = new QualitativeFeatureRepresentation();
        representation.setName(feature.getClass().getSimpleName());
        if (feature.getContent() instanceof ChemicalEntity) {
            // entity feature
            representation.setContent(((ChemicalEntity) feature.getContent()).getIdentifier().toString());
        } else if (feature.getContent() instanceof CellRegion) {
            // region feature
            representation.setContent(((CellRegion) feature.getContent()).getIdentifier());
        } else {
            representation.setContent(feature.getContent().toString());
        }
        representation.addEvidence(feature.getAllEvidence());
        if (representation.getEvidence().isEmpty()) {
            representation.addEvidence(Evidence.NO_EVIDENCE);
        }
        return representation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
