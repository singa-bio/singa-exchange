package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.model.Feature;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import singa.bio.exchange.model.features.FeatureRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SmallMoleculeRepresentation.class, name = "small molecule"),
        @JsonSubTypes.Type(value = ProteinRepresentation.class, name = "protein"),
        @JsonSubTypes.Type(value = ComplexRepresentation.class, name = "complex")
})
@JsonPropertyOrder({ "primary-identifier", "type" })
public abstract class EntityRepresentation {

    @JsonProperty("primary-identifier")
    private String primaryIdentifier;

    @JsonProperty
    private List<FeatureRepresentation> features;

    public EntityRepresentation() {
        features = new ArrayList<>();
    }

    public static EntityRepresentation of(ChemicalEntity chemicalEntity) {
        if (chemicalEntity instanceof SmallMolecule) {
            SmallMoleculeRepresentation representation = new SmallMoleculeRepresentation();
            representation.setPrimaryIdentifier(chemicalEntity.getIdentifier().toString());
            for (Feature<?> feature : chemicalEntity.getFeatures()) {
                representation.addFeature(FeatureRepresentation.of(feature));
            }
            return representation;
        } else if (chemicalEntity instanceof ComplexedChemicalEntity) {
            ComplexedChemicalEntity complex = (ComplexedChemicalEntity) chemicalEntity;
            ComplexRepresentation representation = new ComplexRepresentation();
            representation.setPrimaryIdentifier(complex.getIdentifier().toString());
            for (Feature<?> feature : complex.getFeatures()) {
                representation.addFeature(FeatureRepresentation.of(feature));
            }
            for (Map.Entry<ChemicalEntity, Integer> entry : complex.getAssociatedParts().entrySet()) {
                representation.addComponent(entry.getKey().getIdentifier().toString(), entry.getValue());
            }
            return representation;
        } else {
            ProteinRepresentation representation = new ProteinRepresentation();
            representation.setPrimaryIdentifier(chemicalEntity.getIdentifier().toString());
            for (Feature<?> feature : chemicalEntity.getFeatures()) {
                representation.addFeature(FeatureRepresentation.of(feature));
            }
            return representation;
        }

    }

    public ChemicalEntity toModel() {
        if (this instanceof SmallMoleculeRepresentation) {
            SmallMolecule entity = SmallMolecule.create(getPrimaryIdentifier()).build();
            for (FeatureRepresentation featureRepresentation : getFeatures()) {
                Feature feature = featureRepresentation.toModel();
                entity.setFeature(feature);
            }
            EntityCache.add(entity);
            return entity;
        } else if (this instanceof ProteinRepresentation) {
            Protein entity = new Protein.Builder(getPrimaryIdentifier()).build();
            for (FeatureRepresentation featureRepresentation : getFeatures()) {
                Feature feature = featureRepresentation.toModel();
                entity.setFeature(feature);
            }
            EntityCache.add(entity);
            return entity;
        } else {
            ComplexedChemicalEntity.Builder builder = new ComplexedChemicalEntity.Builder(getPrimaryIdentifier());
            for (Map.Entry<String, Integer> entry : ((ComplexRepresentation) this).getComponents().entrySet()) {
                builder.addAssociatedPart(EntityCache.get(entry.getKey()), entry.getValue());
            }
            ComplexedChemicalEntity entity = builder.build();
            for (FeatureRepresentation featureRepresentation : getFeatures()) {
                Feature feature = featureRepresentation.toModel();
                entity.setFeature(feature);
            }
            EntityCache.add(entity);
            return entity;
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
