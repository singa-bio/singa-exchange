package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.complex.ComplexEntity;
import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.features.model.Feature;
import com.fasterxml.jackson.annotation.*;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.features.FeatureRepresentation;
import singa.bio.exchange.model.graphs.complex.ComplexEntityRepresentation;

import java.util.ArrayList;
import java.util.List;

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
public abstract class EntityRepresentation {

    @JsonProperty("primary-identifier")
    private String primaryIdentifier;

    @JsonProperty("membrane-bound")
    private boolean membraneBound;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FeatureRepresentation> features;

    public EntityRepresentation() {
        features = new ArrayList<>();
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

    protected void appendFeatures(ChemicalEntity entity) {
        for (FeatureRepresentation featureRepresentation : getFeatures()) {
            Feature feature = featureRepresentation.toModel();
            entity.setFeature(feature);
        }
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

    public List<FeatureRepresentation> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureRepresentation> features) {
        this.features = features;
    }

    public void addFeature(FeatureRepresentation feature) {
        features.add(feature);
    }

}
