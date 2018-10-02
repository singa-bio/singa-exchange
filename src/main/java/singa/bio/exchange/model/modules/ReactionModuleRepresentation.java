package singa.bio.exchange.model.modules;

import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.entities.ReactantRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */

public class ReactionModuleRepresentation extends ModuleRepresentation {

    @JsonProperty
    private List<ReactantRepresentation> reactants;

    @JsonProperty
    private String reaction;

    public ReactionModuleRepresentation() {
        reactants = new ArrayList<>();
    }

    public List<ReactantRepresentation> getReactants() {
        return reactants;
    }

    public void setReactants(List<ReactantRepresentation> reactants) {
        this.reactants = reactants;
    }

    public void addReactant(ReactantRepresentation reactant) {
        this.reactants.add(reactant);
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

}
