package bio.singa.exchange.concentrations.conditions;

import bio.singa.exchange.sections.RegionCache;
import bio.singa.simulation.model.concentrations.RegionCondition;
import bio.singa.simulation.model.sections.CellRegion;
import com.fasterxml.jackson.annotation.JsonProperty;
import bio.singa.exchange.sections.RegionRepresentation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class RegionConditionRepresentation extends ConditionRepresentation {

    @JsonProperty
    private List<String> regions;

    public static RegionConditionRepresentation of(RegionCondition condition) {
        RegionConditionRepresentation representation = new RegionConditionRepresentation();
        condition.getRegions().forEach(representation::addRegion);
        return representation;
    }

    private void addRegion(CellRegion region) {
        regions.add(RegionRepresentation.of(region).getIdentifier());
    }

    public RegionCondition toModel() {
        List<CellRegion> regions = this.regions.stream()
                .map(RegionCache::get)
                .collect(Collectors.toList());
        return RegionCondition.forRegions(regions);
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }
}
