package bio.singa.exchange.features;

import bio.singa.exchange.entities.EntityRepresentation;
import bio.singa.features.model.Feature;
import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.features.model.MultiEntityFeature;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MultiEntityFeatureRepresentation extends FeatureRepresentation<List<String>> {

    @JsonProperty
    private List<String> entities;

    public MultiEntityFeatureRepresentation() {
        entities = new ArrayList<>();
    }

    public static MultiEntityFeatureRepresentation of(Feature<?> feature) {
        MultiEntityFeatureRepresentation representation = new MultiEntityFeatureRepresentation();
        representation.baseSetup(feature);
        for (ChemicalEntity chemicalEntity : ((MultiEntityFeature) feature).getContent()) {
            representation.addEntity(EntityRepresentation.of(chemicalEntity).getPrimaryIdentifier());
        }
        return representation;
    }

    public List<String> getEntities() {
        return entities;
    }

    public void setEntities(List<String> entities) {
        this.entities = entities;
    }

    public void addEntity(String entity) {
        this.entities.add(entity);
    }

    @Override
    public List<String> fetchContent() {
        return entities;
    }
}
