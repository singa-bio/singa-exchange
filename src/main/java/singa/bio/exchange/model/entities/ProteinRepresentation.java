package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.features.model.Feature;
import singa.bio.exchange.model.features.FeatureRepresentation;

/**
 * @author cl
 */
public class ProteinRepresentation extends EntityRepresentation {

    public static ProteinRepresentation of(Protein protein) {
        ProteinRepresentation representation = new ProteinRepresentation();
        representation.setPrimaryIdentifier(protein.getIdentifier());
        representation.setMembraneBound(protein.isMembraneBound());
        for (Feature<?> feature : protein.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        return representation;
    }

    public Protein toModel() {
        Protein entity = Protein.create(getPrimaryIdentifier()).build();
        entity.setMembraneBound(isMembraneBound());
        appendFeatures(entity);
        EntityRegistry.put(entity);
        return entity;
    }

}
