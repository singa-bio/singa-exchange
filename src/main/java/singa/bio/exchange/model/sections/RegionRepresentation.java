package singa.bio.exchange.model.sections;

import bio.singa.features.identifiers.GoTerm;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.EnumTransformation;

import java.util.HashMap;
import java.util.Map;

import static bio.singa.simulation.model.sections.CellTopology.*;

/**
 * @author cl
 */
public class RegionRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty("go-term")
    private String goTerm;

    @JsonProperty
    private Map<String, String> subsections;


    public RegionRepresentation() {
        subsections = new HashMap<>();
    }

    public static RegionRepresentation of(CellRegion region) {
        RegionRepresentation representation = new RegionRepresentation();
        representation.setIdentifier(region.getIdentifier());
        if (region.getGoTerm() != null) {
            representation.setGoTerm(region.getGoTerm().getIdentifier());
        }
        representation.addSubsection(INNER, region.getInnerSubsection());
        representation.addSubsection(MEMBRANE, region.getMembraneSubsection());
        representation.addSubsection(OUTER, region.getOuterSubsection());
        RegionCache.add(region);
        return representation;
    }

    public CellRegion toModel() {
        CellRegion region;
        if (getGoTerm() != null) {
            region = new CellRegion(getIdentifier(), new GoTerm(getGoTerm()));
        } else {
            region = new CellRegion(getIdentifier());
        }
        for (Map.Entry<String, String> entry : getSubsections().entrySet()) {
            String topologyString = entry.getKey();
            String subsectionString = entry.getValue();
            region.addSubSection(EnumTransformation.toTopology(topologyString), SubsectionCache.get(subsectionString));
        }
        return region;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getGoTerm() {
        return goTerm;
    }

    public void setGoTerm(String goTerm) {
        this.goTerm = goTerm;
    }

    public Map<String, String> getSubsections() {
        return subsections;
    }

    public void setSubsections(Map<String, String> subsections) {
        this.subsections = subsections;
    }

    public void addSubsection(CellTopology topology, CellSubsection subsection) {
        if (subsection != null) {
            subsections.put(EnumTransformation.fromTopology(topology), subsection.getIdentifier());
            SubsectionCache.add(subsection);
        }
    }

}
