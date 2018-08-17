package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ChemicalEntity;
import singa.bio.exchange.model.Jasonizable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class EntityDataset implements Jasonizable {

    private List<EntityRepresentation> entities;

    public EntityDataset() {
        entities = new ArrayList<>();
    }

    public static List<ChemicalEntity> to(EntityDataset entites) {
        return entites.getEntities().stream()
                .map(EntityRepresentation::to)
                .collect(Collectors.toList());
    }

    public List<EntityRepresentation> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityRepresentation> entities) {
        this.entities = entities;
    }

    public void addEntity(EntityRepresentation entity) {
        entities.add(entity);
    }

}
