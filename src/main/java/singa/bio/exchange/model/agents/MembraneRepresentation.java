package singa.bio.exchange.model.agents;

import bio.singa.mathematics.topology.grids.rectangular.NeumannRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneFactory;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.sections.CellRegion;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import singa.bio.exchange.model.Converter;
import singa.bio.exchange.model.sections.RegionCache;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
@JsonPropertyOrder({"identifier", "inner-region", "membrane-region", "regions"})
public class MembraneRepresentation {

    private static String INNER_IS_WEST = "INNER_IS_WEST";
    private static String INNER_IS_EAST = "INNER_IS_EAST";

    @JsonProperty
    private String identifier;

    @JsonProperty("inner-region")
    private String innerRegion;

    @JsonProperty("membrane-region")
    private String membraneRegion;

    @JsonProperty
    private Map<String, List<VectorRepresentation>> regions;

    @JsonProperty("topological-hints")
    private List<String> topologicalHints;

    public MembraneRepresentation() {
        regions = new HashMap<>();
        topologicalHints = new ArrayList<>();
    }

    public static MembraneRepresentation of(Membrane membrane) {
        MembraneRepresentation representation = new MembraneRepresentation();
        representation.setIdentifier(membrane.getIdentifier());
        representation.setInnerRegion(membrane.getInnerRegion().getIdentifier());
        representation.setMembraneRegion(membrane.getMembraneRegion().getIdentifier());
        // add rendering hints
        if (membrane.getInnerDirection() != null) {
            NeumannRectangularDirection innerDirection = membrane.getInnerDirection();
            if (innerDirection == NeumannRectangularDirection.EAST) {
                representation.getTopologicalHints().add(INNER_IS_EAST);
            } else if (innerDirection == NeumannRectangularDirection.WEST) {
                representation.getTopologicalHints().add(INNER_IS_WEST);
            }
        }
        if (membrane.getRegionMap() != null) {
            // generate region map when present
            Map<CellRegion, Set<Vector2D>> regionMap = membrane.getRegionMap();
            for (Map.Entry<CellRegion, Set<Vector2D>> entry : regionMap.entrySet()) {
                ArrayList<VectorRepresentation> vectors = new ArrayList<>();
                for (Vector2D vector : entry.getValue()) {
                    vectors.add(VectorRepresentation.of(vector));
                }
                RegionCache.add(entry.getKey());
                representation.addRegion(entry.getKey().getIdentifier(), vectors);
            }
        } else {
            // reconstruct region map from nodes if possible
            Map<String, List<VectorRepresentation>> regions = representation.getRegions();
            for (MembraneSegment segment : membrane.getSegments()) {
                String region = segment.getNode().getCellRegion().getIdentifier();
                if (!regions.containsKey(region)) {
                    regions.put(region, new ArrayList<>());
                }
                List<VectorRepresentation> associatedNodes = regions.get(region);
                VectorRepresentation start = VectorRepresentation.of(segment.getStartingPoint());
                if (!associatedNodes.contains(start)) {
                    associatedNodes.add(start);
                }
                VectorRepresentation end = VectorRepresentation.of(segment.getEndingPoint());
                if (!associatedNodes.contains(end)) {
                    associatedNodes.add(end);
                }
            }
        }
        return representation;
    }

    public Membrane toModel() {
        List<Vector2D> vectors = regions.values().stream()
                .flatMap(Collection::stream)
                .map(VectorRepresentation::toModel)
                .collect(Collectors.toList());

        Map<Vector2D, CellRegion> mapping = new HashMap<>();
        for (Map.Entry<String, List<VectorRepresentation>> entry : regions.entrySet()) {
            CellRegion region = RegionCache.get(entry.getKey());
            for (VectorRepresentation representation : entry.getValue()) {
                mapping.put(representation.toModel(), region);
            }
        }

        if (vectors.get(0).equals(vectors.get(vectors.size() - 1))) {
            return MembraneFactory.createClosedMembrane(vectors, RegionCache.get(getInnerRegion()),
                    RegionCache.get(getMembraneRegion()), Converter.current.getGraph(), mapping);
        } else {
            List<String> topologicalHints = getTopologicalHints();
            NeumannRectangularDirection innerDirection;
            if (topologicalHints.contains(INNER_IS_EAST)) {
                innerDirection = NeumannRectangularDirection.EAST;
            } else {
                innerDirection = NeumannRectangularDirection.WEST;
            }
            return MembraneFactory.createLinearMembrane(vectors, RegionCache.get(getInnerRegion()),
                    RegionCache.get(getMembraneRegion()), innerDirection,
                    Converter.current.getGraph(), mapping, Converter.current.getSimulationRegion());
        }

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

    public List<String> getTopologicalHints() {
        return topologicalHints;
    }

    public void setTopologicalHints(List<String> topologicalHints) {
        this.topologicalHints = topologicalHints;
    }
}
