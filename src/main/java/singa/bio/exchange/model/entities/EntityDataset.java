package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ChemicalEntities;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.DynamicChemicalEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.Jsonizable;

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
        List<ChemicalEntity> entities = ChemicalEntities.sortByComplexDependencies(new ArrayList<>(EntityCache.getAll()));
        entities.stream()
                .filter(chemicalEntity -> !(chemicalEntity instanceof DynamicChemicalEntity))
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
