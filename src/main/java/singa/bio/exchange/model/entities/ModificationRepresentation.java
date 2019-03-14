package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexModification;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.EnumTransformation;

/**
 * @author cl
 */
public class ModificationRepresentation {

    @JsonProperty
    private String operation;

    @JsonProperty
    private String modificator;

    @JsonProperty("modification-position")
    private String modificationPosition;

    public ModificationRepresentation() {

    }

    public static ModificationRepresentation of(ComplexModification condition) {
        ModificationRepresentation representation = new ModificationRepresentation();
        representation.setOperation(EnumTransformation.fromOperation(condition.getOperation()));
        if (condition.getModificator() != null) {
            representation.setModificator(EntityRepresentation.of(condition.getModificator()).getPrimaryIdentifier());
        }
        if (condition.getModificationPosition() != null) {
            representation.setModificationPosition(EntityRepresentation.of(condition.getModificationPosition()).getPrimaryIdentifier());
        }
        return representation;
    }

    public ComplexModification toModel() {
        ComplexModification.Operation operation = EnumTransformation.toOperation(getOperation());
        ChemicalEntity modificator = null;
        if (getModificator() != null) {
            modificator = EntityCache.get(getModificator());
        }
        ChemicalEntity modification = null;
        if (getModificationPosition() != null) {
            modification = EntityCache.get(getModificationPosition());
        }
        return new ComplexModification(operation, modificator, modification);
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getModificator() {
        return modificator;
    }

    public void setModificator(String modificator) {
        this.modificator = modificator;
    }

    public String getModificationPosition() {
        return modificationPosition;
    }

    public void setModificationPosition(String modificationPosition) {
        this.modificationPosition = modificationPosition;
    }
}
