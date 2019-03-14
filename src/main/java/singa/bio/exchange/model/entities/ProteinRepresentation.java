package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.Protein;
import bio.singa.features.model.Feature;
import singa.bio.exchange.model.features.FeatureRepresentation;

/**
 * @author cl
 */
public class ProteinRepresentation extends EntityRepresentation {

    public static ProteinRepresentation of(Protein protein) {
        ProteinRepresentation representation = new ProteinRepresentation();
        representation.setPrimaryIdentifier(protein.getIdentifier().toString());
        for (Feature<?> feature : protein.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        return representation;
    }

    public Protein toModel() {
        Protein entity = Protein.create(getPrimaryIdentifier()).build();
        appendFeatures(entity);
        EntityCache.add(entity);
        return entity;
    }

}
