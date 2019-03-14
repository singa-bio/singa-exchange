package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ModificationSite;
import bio.singa.features.model.Feature;
import singa.bio.exchange.model.features.FeatureRepresentation;

/**
 * @author cl
 */
public class ModificationSiteRepresentation extends EntityRepresentation {

    public static ModificationSiteRepresentation of(ModificationSite modificationSite) {
        ModificationSiteRepresentation representation = new ModificationSiteRepresentation();
        representation.setPrimaryIdentifier(modificationSite.getIdentifier().toString());
        for (Feature<?> feature : modificationSite.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        return representation;
    }

    public ModificationSite toModel() {
        ModificationSite entity = ModificationSite.create(getPrimaryIdentifier()).build();
        appendFeatures(entity);
        EntityCache.add(entity);
        return entity;
    }

}
