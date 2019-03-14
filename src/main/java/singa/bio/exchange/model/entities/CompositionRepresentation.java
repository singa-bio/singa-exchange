package singa.bio.exchange.model.entities;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.EntityCompositionCondition;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.EntityReducer;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.IllegalConversionException;

/**
 * @author cl
 */
public class CompositionRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty
    private String entity;

    public CompositionRepresentation() {
    }

    public static CompositionRepresentation of(EntityCompositionCondition condition) {
        CompositionRepresentation representation = new CompositionRepresentation();
        representation.setIdentifier(condition.getIdentifier());
        representation.setEntity(EntityRepresentation.of(condition.getEntity()).getPrimaryIdentifier());
        return representation;
    }

    public EntityCompositionCondition toModel() {
        if (getIdentifier().equals("HAS_PART")) {
            return EntityReducer.hasPart(EntityCache.get(getEntity()));
        } else if (getIdentifier().equals("HAS_NOT_PART")) {
            return EntityReducer.hasNotPart(EntityCache.get(getEntity()));
        } else {
            throw new IllegalConversionException("Trying to create composition representation from unknown identifier "+getIdentifier()+".");
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
