package bio.singa.exchange.entities;

import bio.singa.simulation.entities.ChemicalEntities;
import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.EntityRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import bio.singa.exchange.Jsonizable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class EntityDataset implements Jsonizable {

    @JsonProperty
    private List<EntityRepresentation> entities;

    public EntityDataset() {
        entities = new ArrayList<>();
    }

    public static EntityDataset fromCache() {
        EntityDataset dataset = new EntityDataset();
        List<ChemicalEntity> entities = ChemicalEntities.sortByComplexDependencies(new ArrayList<>(EntityRegistry.getAll()));
        entities.stream()
                .map(EntityRepresentation::of)
                .forEach(dataset::addEntity);
        return dataset;
    }

    public List<ChemicalEntity> toModel() {
        List<ChemicalEntity> entities = new ArrayList<>();
        for (EntityRepresentation entityRepresentation : getEntities()) {
            ChemicalEntity toModel = entityRepresentation.toModel();
            entities.add(toModel);
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
