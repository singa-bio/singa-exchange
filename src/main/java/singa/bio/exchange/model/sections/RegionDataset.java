package singa.bio.exchange.model.sections;

import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.Jsonizable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class RegionDataset implements Jsonizable {

    @JsonProperty
    private List<RegionRepresentation> regions;

    public RegionDataset() {
        regions = new ArrayList<>();
    }

    public static RegionDataset fromCache() {
        RegionDataset dataset = new RegionDataset();
        RegionCache.getAll().stream()
                .map(RegionRepresentation::of)
                .forEach(dataset::addRegion);
        return dataset;
    }

    public void cache() {
        RegionCache.clear();
        for (RegionRepresentation region : regions) {
            RegionCache.add(region.toModel());
        }
    }

    public List<RegionRepresentation> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionRepresentation> regions) {
        this.regions = regions;
    }

    private void addRegion(RegionRepresentation representation) {
        regions.add(representation);
    }
}
