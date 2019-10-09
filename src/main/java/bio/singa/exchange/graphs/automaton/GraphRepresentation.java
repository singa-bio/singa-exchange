package bio.singa.exchange.graphs.automaton;

import bio.singa.simulation.model.graphs.AutomatonEdge;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import bio.singa.exchange.sections.RegionRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class GraphRepresentation {

    @JsonProperty
    private int rows;

    @JsonProperty
    private int columns;

    @JsonProperty
    private List<NodeRepresentation> nodes;

    @JsonProperty
    private List<EdgeRepresentation> edges;

    public GraphRepresentation() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public static GraphRepresentation of(AutomatonGraph graph) {
        GraphRepresentation representation = new GraphRepresentation();
        representation.setRows(graph.getNumberOfRows());
        representation.setColumns(graph.getNumberOfColumns());
        for (AutomatonNode node : graph.getNodes()) {
            representation.addNode(NodeRepresentation.of(node));
            RegionRepresentation.of(node.getCellRegion());
        }
        for (AutomatonEdge edge : graph.getEdges()) {
            representation.addEdge(EdgeRepresentation.of(edge));
        }
        return representation;
    }

    public AutomatonGraph toModel() {
        AutomatonGraph graph = new AutomatonGraph(getColumns(), getRows());
        for (NodeRepresentation node : getNodes()) {
            graph.addNode(node.toModel());
        }
        for (EdgeRepresentation edge : getEdges()) {
            graph.addEdgeBetween(edge.getIdentifier(), NodeCache.get(edge.getSource()), NodeCache.get(edge.getTarget()));
        }
        return graph;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public List<NodeRepresentation> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeRepresentation> nodes) {
        this.nodes = nodes;
    }

    public void addNode(NodeRepresentation node) {
        this.nodes.add(node);
    }

    public List<EdgeRepresentation> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeRepresentation> edges) {
        this.edges = edges;
    }

    public void addEdge(EdgeRepresentation edge) {
        edges.add(edge);
    }

}
