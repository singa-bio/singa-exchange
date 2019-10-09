package bio.singa.exchange.graphs.complex;

import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;
import bio.singa.chemistry.entities.complex.GraphComplexEdge;
import bio.singa.chemistry.entities.complex.GraphComplexNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import bio.singa.exchange.entities.EntityRepresentation;
import bio.singa.exchange.features.FeatureRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class ComplexEntityRepresentation extends EntityRepresentation {

    @JsonProperty
    private List<ComplexEntityNodeRepresentation> nodes;

    @JsonProperty
    private List<ComplexEntityEdgeRepresentation> edges;

    public ComplexEntityRepresentation() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public static ComplexEntityRepresentation of(ComplexEntity complexEntity) {
        ComplexEntityRepresentation representation = new ComplexEntityRepresentation();
        representation.setPrimaryIdentifier(complexEntity.getIdentifier());
        representation.setMembraneBound(complexEntity.isMembraneBound());

        complexEntity.getFeatures().stream()
                .map(FeatureRepresentation::of).
                forEach(representation::addFeature);

        complexEntity.getNodes().stream()
                .map(ComplexEntityNodeRepresentation::of)
                .forEach(representation::addNode);

        complexEntity.getEdges().stream()
                .map(ComplexEntityEdgeRepresentation::of)
                .forEach(representation::addEdge);

        return representation;
    }

    public ComplexEntity toModel() {
        ComplexEntity entity = new ComplexEntity();
        // add nodes
        nodes.stream()
                .map(ComplexEntityNodeRepresentation::toModel)
                .forEach(entity::addNode);
        // add edges
        for (ComplexEntityEdgeRepresentation edge : edges) {
            GraphComplexEdge complexEdge = new GraphComplexEdge(edge.getIdentifier());
            GraphComplexNode source = entity.getNode(edge.getSource());
            GraphComplexNode target = entity.getNode(edge.getTarget());
            complexEdge.setConnectedSite(BindingSite.createNamed(edge.getConnection()));
            entity.addEdgeBetween(complexEdge, source, target);
        }
        // set remaining attributes
        entity.setMembraneBound(isMembraneBound());
        entity.update();
        appendFeatures(entity);
        EntityRegistry.put(entity);
        return entity;
    }

    public List<ComplexEntityNodeRepresentation> getNodes() {
        return nodes;
    }

    public void addNode(ComplexEntityNodeRepresentation node) {
        nodes.add(node);
    }

    public void setNodes(List<ComplexEntityNodeRepresentation> nodes) {
        this.nodes = nodes;
    }

    public List<ComplexEntityEdgeRepresentation> getEdges() {
        return edges;
    }

    public void addEdge(ComplexEntityEdgeRepresentation edge) {
        edges.add(edge);
    }

    public void setEdges(List<ComplexEntityEdgeRepresentation> edges) {
        this.edges = edges;
    }
}
