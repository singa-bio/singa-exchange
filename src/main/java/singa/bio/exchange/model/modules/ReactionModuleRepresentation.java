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
    private List<ReactantRepresentation> substrates;

    @JsonProperty
    private List<ReactantRepresentation> products;

    @JsonProperty
    private String reaction;

    public ReactionModuleRepresentation() {
        substrates = new ArrayList<>();
        products = new ArrayList<>();
    }

    public List<ReactantRepresentation> getSubstrates() {
        return substrates;
    }

    public void setSubstrates(List<ReactantRepresentation> substrates) {
        this.substrates = substrates;
    }

    public void addSubstrate(ReactantRepresentation substrate) {
        substrates.add(substrate);
    }

    public List<ReactantRepresentation> getProducts() {
        return products;
    }

    public void setProducts(List<ReactantRepresentation> products) {
        this.products = products;
    }

    public void addProduct(ReactantRepresentation product) {
        products.add(product);
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
}
