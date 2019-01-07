package singa.bio.exchange.model.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.simulation.features.MultiEntityFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MultiEntityFeatureRepresentation extends FeatureRepresentation {

    private List<String> entities;

    public MultiEntityFeatureRepresentation() {
        entities = new ArrayList<>();
    }

    public static MultiEntityFeatureRepresentation of(Feature<?> feature) {
        MultiEntityFeatureRepresentation representation = new MultiEntityFeatureRepresentation();
        representation.setName(feature.getClass().getSimpleName());
        for (ChemicalEntity chemicalEntity : ((MultiEntityFeature) feature).getContent()) {
            representation.addEntity(chemicalEntity.getIdentifier().toString());
        }
        representation.addEvidence(feature.getAllEvidence());
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

}
