package singa.bio.exchange.model.entities;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.DynamicChemicalEntity;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.EntityCompositionCondition;
import bio.singa.simulation.model.sections.CellTopology;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.EnumTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class DynamicSubstrateRepresentation extends ReactantRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty
    private List<CompositionRepresentation> composition;

    @JsonProperty
    private List<String> topology;

    public DynamicSubstrateRepresentation() {
        composition = new ArrayList<>();
        topology = new ArrayList<>();
    }

    public static DynamicSubstrateRepresentation of(DynamicChemicalEntity dynamicEntity) {
        DynamicSubstrateRepresentation representation = new DynamicSubstrateRepresentation();
        representation.setIdentifier(dynamicEntity.getIdentifier().getContent());
        for (EntityCompositionCondition condition : dynamicEntity.getComposition()) {
            representation.composition.add(CompositionRepresentation.of(condition));
        }
        for (CellTopology possibleTopology : dynamicEntity.getPossibleTopologies()) {
            representation.topology.add(EnumTransformation.fromTopology(possibleTopology));
        }
        EntityCache.add(dynamicEntity);
        return representation;
    }

    public DynamicChemicalEntity toModel() {
        DynamicChemicalEntity entity = new DynamicChemicalEntity(getIdentifier());
        for (CompositionRepresentation compositionRepresentation : getComposition()) {
            entity.addCompositionCondition(compositionRepresentation.toModel());
        }
        for (String topologyString : getTopology()) {
            entity.addPossibleTopology(EnumTransformation.toTopology(topologyString));
        }
        return entity;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<CompositionRepresentation> getComposition() {
        return composition;
    }

    public void setComposition(List<CompositionRepresentation> composition) {
        this.composition = composition;
    }

    public List<String> getTopology() {
        return topology;
    }

    public void setTopology(List<String> topology) {
        this.topology = topology;
    }

}
