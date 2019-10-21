package bio.singa.exchange.entities;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.complex.ComplexEntity;
import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.exchange.IllegalConversionException;
import bio.singa.exchange.features.FeatureCarrier;
import bio.singa.exchange.graphs.complex.ComplexEntityRepresentation;
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
        @JsonSubTypes.Type(value = SmallMoleculeRepresentation.class, name = "small molecule"),
        @JsonSubTypes.Type(value = ProteinRepresentation.class, name = "protein"),
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
        if (chemicalEntity instanceof SmallMolecule) {
            return SmallMoleculeRepresentation.of((SmallMolecule) chemicalEntity);
        } else if (chemicalEntity instanceof Protein) {
            return ProteinRepresentation.of((Protein) chemicalEntity);
        } else if (chemicalEntity instanceof ComplexEntity) {
            return ComplexEntityRepresentation.of((ComplexEntity) chemicalEntity);
        }
        throw new IllegalConversionException("Trying to create entity representation from unknown model class " + chemicalEntity.getClass() + ".");
    }

    public ChemicalEntity toModel() {
        ChemicalEntity entity;
        if (this instanceof SmallMoleculeRepresentation) {
            entity = ((SmallMoleculeRepresentation) this).toModel();
        } else if (this instanceof ProteinRepresentation) {
            entity = ((ProteinRepresentation) this).toModel();
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
