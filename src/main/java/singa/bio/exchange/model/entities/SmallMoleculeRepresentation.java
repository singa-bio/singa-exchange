package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.model.Feature;
import singa.bio.exchange.model.features.FeatureRepresentation;

/**
 * @author cl
 */
public class SmallMoleculeRepresentation extends EntityRepresentation {

    public static SmallMoleculeRepresentation of(SmallMolecule smallMolecule) {
        SmallMoleculeRepresentation representation = new SmallMoleculeRepresentation();
        representation.setPrimaryIdentifier(smallMolecule.getIdentifier().toString());
        for (Feature<?> feature : smallMolecule.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        return representation;
    }

    public SmallMolecule toModel() {
        SmallMolecule entity = SmallMolecule.create(getPrimaryIdentifier()).build();
        appendFeatures(entity);
        EntityCache.add(entity);
        return entity;
    }

}
