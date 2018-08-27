package singa.bio.exchange.model.macroscopic;

import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.membranes.Membrane;
import bio.singa.simulation.model.agents.membranes.MembraneTracer;
import bio.singa.simulation.model.sections.CellRegion;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.Converter;
import singa.bio.exchange.model.sections.RegionCache;

import java.util.*;

/**
 * @author cl
 */
public class MembraneRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty("inner-region")
    private String innerRegion;

    @JsonProperty("membrane-region")
    private String membraneRegion;

    @JsonProperty
    private Map<String, List<VectorRepresentation>> regions;

    public MembraneRepresentation() {
        regions = new HashMap<>();
    }

    public static MembraneRepresentation of(Membrane membrane) {
        MembraneRepresentation representation = new MembraneRepresentation();
        representation.setIdentifier(membrane.getIdentifier());
        representation.setInnerRegion(membrane.getInnerRegion().getIdentifier());
        representation.setMembraneRegion(membrane.getMembraneRegion().getIdentifier());
        Map<CellRegion, Set<Vector2D>> regionMap = membrane.getRegionMap();
        for (Map.Entry<CellRegion, Set<Vector2D>> entry : regionMap.entrySet()) {
            ArrayList<VectorRepresentation> vectors = new ArrayList<>();
            for (Vector2D vector : entry.getValue()) {
                vectors.add(VectorRepresentation.of(vector));
            }
            RegionCache.add(entry.getKey());
            representation.addRegion(entry.getKey().getIdentifier(), vectors);
        }
        return representation;
    }

    public Membrane toModel() {
        Membrane membrane = new Membrane(identifier);
        CellRegion innerRegion = RegionCache.get(this.innerRegion);
        membrane.setInnerRegion(innerRegion);
        CellRegion membraneRegion = RegionCache.get(this.membraneRegion);
        membrane.setMembraneRegion(membraneRegion);
        Map<CellRegion, Set<Vector2D>> regionMap = new HashMap<>();
        for (Map.Entry<String, List<VectorRepresentation>> entry : regions.entrySet()) {
            CellRegion region = RegionCache.get(entry.getKey());
            Set<Vector2D> vectors = new HashSet<>();
            for (VectorRepresentation vectorRepresentation : entry.getValue()) {
                vectors.add(vectorRepresentation.toModel());
            }
            regionMap.put(region, vectors);
        }
        return MembraneTracer.membraneToRegion(innerRegion, membraneRegion, regionMap, Converter.current.getGraph());
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Map<String, List<VectorRepresentation>> getRegions() {
        return regions;
    }

    public void setRegions(Map<String, List<VectorRepresentation>> regions) {
        this.regions = regions;
    }

    public void addRegion(String region, List<VectorRepresentation> vectors) {
        this.regions.put(region, vectors);
    }

    public String getInnerRegion() {
        return innerRegion;
    }

    public void setInnerRegion(String innerRegion) {
        this.innerRegion = innerRegion;
    }

    public String getMembraneRegion() {
        return membraneRegion;
    }

    public void setMembraneRegion(String membraneRegion) {
        this.membraneRegion = membraneRegion;
    }
}
