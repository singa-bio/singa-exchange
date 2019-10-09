package bio.singa.exchange.entities;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantSet;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ReactantSetRepresentation {

    @JsonProperty
    private List<ReactantRepresentation> substrates;
    @JsonProperty
    private List<ReactantRepresentation> products;
    @JsonProperty
    private List<ReactantRepresentation> catalysts;

    public ReactantSetRepresentation() {
        substrates = new ArrayList<>();
        products = new ArrayList<>();
        catalysts = new ArrayList<>();
    }

    public static ReactantSetRepresentation of(ReactantSet reactantSet) {
        ReactantSetRepresentation representation = new ReactantSetRepresentation();
        reactantSet.getSubstrates().stream()
                .map(ReactantRepresentation::of)
                .forEach(representation::addSubstrate);
        reactantSet.getProducts().stream()
                .map(ReactantRepresentation::of)
                .forEach(representation::addProduct);
        reactantSet.getCatalysts().stream()
                .map(ReactantRepresentation::of)
                .forEach(representation::addCatalyst);
        return representation;
    }

    public ReactantSet toModel() {
        List<Reactant> substrates = this.substrates.stream()
                .map(ReactantRepresentation::toModel)
                .collect(Collectors.toList());
        List<Reactant> products = this.products.stream()
                .map(ReactantRepresentation::toModel)
                .collect(Collectors.toList());
        List<Reactant> catalysts = this.catalysts.stream()
                .map(ReactantRepresentation::toModel)
                .collect(Collectors.toList());
        return new ReactantSet(substrates, products, catalysts);
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

    public List<ReactantRepresentation> getCatalysts() {
        return catalysts;
    }

    public void setCatalysts(List<ReactantRepresentation> catalysts) {
        this.catalysts = catalysts;
    }

    public void addCatalyst(ReactantRepresentation catalyst) {
        catalysts.add(catalyst);
    }

}
