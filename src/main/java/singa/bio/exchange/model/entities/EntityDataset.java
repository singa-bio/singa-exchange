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

    public static List<ChemicalEntity> to(EntityDataset entityDataset) {
        List<ChemicalEntity> entities = entityDataset.getEntities().stream()
                .map(EntityRepresentation::to)
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
