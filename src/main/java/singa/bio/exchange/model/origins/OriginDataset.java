package singa.bio.exchange.model.origins;

import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.Jasonizable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class OriginDataset implements Jasonizable {

    @JsonProperty
    private List<OriginRepresentation> origins;

    public OriginDataset() {
        origins = new ArrayList<>();
    }

    public static OriginDataset fromCache() {
        OriginDataset dataset = new OriginDataset();
        OriginCache.getAll().stream()
                .map(OriginRepresentation::of)
                .forEach(dataset::addOrigin);
        return dataset;
    }

    public void cache() {
        OriginCache.clear();
        for (OriginRepresentation origin : origins) {
            OriginCache.add(origin.toModel());
        }
    }

    public List<OriginRepresentation> getOrigins() {
        return origins;
    }

    public void setOrigins(List<OriginRepresentation> origins) {
        this.origins = origins;
    }

    public void addOrigin(OriginRepresentation representation) {
        origins.add(representation);
    }

}
