package bio.singa.exchange.features;

import bio.singa.chemistry.entities.ChemicalEntity;
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
        representation.baseSetup(feature);
        if (feature.getContent() instanceof ChemicalEntity) {
            // entity feature
            representation.setContent(((ChemicalEntity) feature.getContent()).getIdentifier());
        } else if (feature.getContent() instanceof CellRegion) {
            // region feature
            representation.setContent(((CellRegion) feature.getContent()).getIdentifier());
        } else {
            representation.setContent(feature.getContent().toString());
        }
        return representation;
    }

    @Override
    public String fetchContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
