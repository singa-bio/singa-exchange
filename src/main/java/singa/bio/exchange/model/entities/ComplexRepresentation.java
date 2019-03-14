package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.ComplexEntityParser;
import bio.singa.features.model.Feature;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.features.FeatureRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class ComplexRepresentation extends EntityRepresentation {

    @JsonProperty
    private List<String> components;

    public ComplexRepresentation() {
        components = new ArrayList<>();
    }

    public static ComplexRepresentation of(ComplexEntity complexEntity) {
        ComplexRepresentation representation = new ComplexRepresentation();
        representation.setPrimaryIdentifier(complexEntity.getIdentifier().toString());
        for (Feature<?> feature : complexEntity.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        for (ChemicalEntity entry : complexEntity.getLeafData()) {
            representation.addComponent(EntityRepresentation.of(entry).getPrimaryIdentifier());
        }
        return representation;
    }

    public ComplexEntity toModel() {
        // collect reference
        List<ChemicalEntity> reference = new ArrayList<>();
        for (String component : components) {
            reference.add(EntityCache.get(component));
        }
        // parse newick from identifier
        ComplexEntity entity = ComplexEntityParser.parseNewick(getPrimaryIdentifier(), reference);
        appendFeatures(entity);
        EntityCache.add(entity);
        return entity;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public void addComponent(String identifier) {
        components.add(identifier);
    }

}
