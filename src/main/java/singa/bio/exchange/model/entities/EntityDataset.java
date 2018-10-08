package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ChemicalEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.Jasonizable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class EntityDataset implements Jasonizable {

    @JsonProperty
    private List<EntityRepresentation> entities;

    public EntityDataset() {
        entities = new ArrayList<>();
    }

    public static EntityDataset fromCache() {
        EntityDataset dataset = new EntityDataset();
        EntityCache.getAll().stream()
                .map(EntityRepresentation::of)
                .forEach(dataset::addEntity);
        return dataset;
    }


    public List<ChemicalEntity> toModel() {
        List<ChemicalEntity> entities = getEntities().stream()
                .map(EntityRepresentation::toModel)
                .collect(Collectors.toList());
        for (ChemicalEntity entity : entities) {
            EntityCache.add(entity);
        }
        return entities;
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
