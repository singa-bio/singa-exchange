package bio.singa.exchange.entities;

import bio.singa.exchange.features.FeatureRepresentation;
import bio.singa.features.model.Feature;
import bio.singa.simulation.entities.EntityRegistry;
import bio.singa.simulation.entities.SimpleEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class SimpleEntityRepresentation extends EntityRepresentation {

    @JsonProperty("small")
    private boolean small;

    public static SimpleEntityRepresentation of(SimpleEntity simpleEntity) {
        SimpleEntityRepresentation representation = new SimpleEntityRepresentation();
        representation.setPrimaryIdentifier(simpleEntity.getIdentifier());
        representation.setMembraneBound(simpleEntity.isMembraneBound());
        representation.setSmall(simpleEntity.isSmall());
        for (Feature<?> feature : simpleEntity.getFeatures()) {
            representation.addFeature(FeatureRepresentation.of(feature));
        }
        return representation;
    }

    public SimpleEntity toModel() {
        SimpleEntity entity = SimpleEntity.create(getPrimaryIdentifier()).build();
        entity.setMembraneBound(isMembraneBound());
        entity.setSmall(isSmall());
        appendFeaturesTo(entity);
        EntityRegistry.put(entity);
        return entity;
    }

    public boolean isSmall() {
        return small;
    }

    public void setSmall(boolean small) {
        this.small = small;
    }
}
