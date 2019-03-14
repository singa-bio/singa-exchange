package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.*;
import bio.singa.features.model.Feature;
import com.fasterxml.jackson.annotation.*;
import singa.bio.exchange.model.IllegalConversionException;
import singa.bio.exchange.model.features.FeatureRepresentation;

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
        @JsonSubTypes.Type(value = ComplexRepresentation.class, name = "complex"),
        @JsonSubTypes.Type(value = DynamicSubstrateRepresentation.class, name = "dynamic"),
        @JsonSubTypes.Type(value = ModificationSiteRepresentation.class, name = "modification site")
})
@JsonPropertyOrder({ "primary-identifier", "type" })
public abstract class EntityRepresentation {

    @JsonProperty("primary-identifier")
    private String primaryIdentifier;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FeatureRepresentation> features;

    public EntityRepresentation() {
        features = new ArrayList<>();
    }

    public static EntityRepresentation of(ChemicalEntity chemicalEntity) {
        EntityCache.add(chemicalEntity);
        if (chemicalEntity instanceof SmallMolecule) {
            return SmallMoleculeRepresentation.of(((SmallMolecule) chemicalEntity));
        } else if (chemicalEntity instanceof Protein) {
            return ProteinRepresentation.of(((Protein) chemicalEntity));
        } else if (chemicalEntity instanceof ComplexEntity) {
            return ComplexRepresentation.of(((ComplexEntity) chemicalEntity));
        } else if (chemicalEntity instanceof ModificationSite) {
            return ModificationSiteRepresentation.of(((ModificationSite) chemicalEntity));
        }
        throw new IllegalConversionException("Trying to create entity representation from unknown model class "+chemicalEntity.getClass()+".");
    }

    public ChemicalEntity toModel() {
        ChemicalEntity entity;
        if (this instanceof SmallMoleculeRepresentation) {
            entity = ((SmallMoleculeRepresentation) this).toModel();
        } else if (this instanceof ProteinRepresentation) {
            entity = ((ProteinRepresentation) this).toModel();
        } else if (this instanceof ComplexRepresentation) {
            entity = ((ComplexRepresentation) this).toModel();
        } else if (this instanceof ModificationSiteRepresentation) {
            entity = ((ModificationSiteRepresentation) this).toModel();
        } else {
            throw new IllegalConversionException("Trying to create entity representation from unknown representation class "+this.getClass()+".");
        }
        return entity;
    }

    void appendFeatures(ChemicalEntity entity) {
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
