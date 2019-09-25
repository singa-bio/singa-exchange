package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.features.model.Feature;
import singa.bio.exchange.model.features.FeatureRepresentation;

/**
 * @author cl
 */
public class SmallMoleculeRepresentation extends EntityRepresentation {

    public static SmallMoleculeRepresentation of(SmallMolecule smallMolecule) {
        SmallMoleculeRepresentation representation = new SmallMoleculeRepresentation();
        representation.setPrimaryIdentifier(smallMolecule.getIdentifier());
        representation.setMembraneBound(smallMolecule.isMembraneBound());
        for (Feature<?> feature : smallMolecule.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        return representation;
    }

    public SmallMolecule toModel() {
        SmallMolecule entity = SmallMolecule.create(getPrimaryIdentifier()).build();
        entity.setMembraneBound(isMembraneBound());
        appendFeatures(entity);
        EntityRegistry.put(entity);
        return entity;
    }

}
