package bio.singa.exchange.entities;

import bio.singa.chemistry.model.Protein;
import bio.singa.chemistry.model.SmallMolecule;
import bio.singa.exchange.IllegalConversionException;
import bio.singa.exchange.features.FeatureCarrier;
import bio.singa.exchange.entities.complex.ComplexEntityRepresentation;
import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.ComplexEntity;
import bio.singa.simulation.entities.EntityRegistry;
import bio.singa.simulation.entities.SimpleEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author cl
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleEntityRepresentation.class, name = "simple"),
        @JsonSubTypes.Type(value = ComplexEntityRepresentation.class, name = "complex"),
})
@JsonPropertyOrder({"primary-identifier", "type"})
public abstract class EntityRepresentation extends FeatureCarrier {

    @JsonProperty("primary-identifier")
    private String primaryIdentifier;

    @JsonProperty("membrane-bound")
    private boolean membraneBound;

    public EntityRepresentation() {
        super();
    }

    public static EntityRepresentation of(ChemicalEntity chemicalEntity) {
        EntityRegistry.put(chemicalEntity);
        if (chemicalEntity instanceof SimpleEntity) {
            return SimpleEntityRepresentation.of((SimpleEntity) chemicalEntity);
        } else if (chemicalEntity instanceof ComplexEntity) {
            return ComplexEntityRepresentation.of((ComplexEntity) chemicalEntity);
        }
        throw new IllegalConversionException("Trying to create entity representation from unknown model class " + chemicalEntity.getClass() + ".");
    }

    public ChemicalEntity toModel() {
        ChemicalEntity entity;
        if (this instanceof SimpleEntityRepresentation) {
            entity = ((SimpleEntityRepresentation) this).toModel();
        } else if (this instanceof ComplexEntityRepresentation) {
            entity = ((ComplexEntityRepresentation) this).toModel();
        } else {
            throw new IllegalConversionException("Trying to create entity representation from unknown representation class " + this.getClass() + ".");
        }
        return entity;
    }

    public String getPrimaryIdentifier() {
        return primaryIdentifier;
    }

    public void setPrimaryIdentifier(String primaryIdentifier) {
        this.primaryIdentifier = primaryIdentifier;
    }

    public boolean isMembraneBound() {
        return membraneBound;
    }

    public void setMembraneBound(boolean membraneBound) {
        this.membraneBound = membraneBound;
    }

}
