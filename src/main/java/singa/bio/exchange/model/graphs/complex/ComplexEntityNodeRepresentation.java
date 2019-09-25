package singa.bio.exchange.model.graphs.complex;

import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.GraphComplexNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.entities.EntityRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class ComplexEntityNodeRepresentation {

    @JsonProperty
    private int identifier;

    @JsonProperty
    private String entity;

    @JsonProperty("binding-sites")
    private List<String> bindingSites;

    public ComplexEntityNodeRepresentation() {
        bindingSites = new ArrayList<>();
    }

    public static ComplexEntityNodeRepresentation of(GraphComplexNode node) {
        ComplexEntityNodeRepresentation representation = new ComplexEntityNodeRepresentation();
        representation.setIdentifier(node.getIdentifier());
        representation.setEntity(EntityRepresentation.of(node.getEntity()).getPrimaryIdentifier());
        node.getBindingSites().stream()
                .map(BindingSite::toString)
                .forEach( bindingSite -> representation.getBindingSites().add(bindingSite));
        return representation;
    }

    public GraphComplexNode toModel() {
        GraphComplexNode graphComplexNode = new GraphComplexNode(getIdentifier());
        graphComplexNode.setEntity(EntityRegistry.get(getEntity()));
        bindingSites.stream()
                .map(BindingSite::createNamed)
                .forEach(graphComplexNode::addBindingSite);
        return graphComplexNode;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public List<String> getBindingSites() {
        return bindingSites;
    }

    public void setBindingSites(List<String> bindingSites) {
        this.bindingSites = bindingSites;
    }

}
