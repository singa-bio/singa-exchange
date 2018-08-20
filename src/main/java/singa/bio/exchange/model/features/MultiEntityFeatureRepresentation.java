package singa.bio.exchange.model.features;

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
