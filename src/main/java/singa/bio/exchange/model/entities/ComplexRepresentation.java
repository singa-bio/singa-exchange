package singa.bio.exchange.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class ComplexRepresentation extends EntityRepresentation {

    @JsonProperty
    private Map<String, Integer> components;

    public ComplexRepresentation() {
        components = new HashMap<>();
    }

    public Map<String, Integer> getComponents() {
        return components;
    }

    public void setComponents(Map<String, Integer> components) {
        this.components = components;
    }

    public void addComponent(String identifier, Integer number) {
        components.put(identifier, number);
    }

}
