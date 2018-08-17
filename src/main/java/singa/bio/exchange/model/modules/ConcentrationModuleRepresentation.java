package singa.bio.exchange.model.modules;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class ConcentrationModuleRepresentation extends ModuleRepresentation {

    @JsonProperty("affected-entities")
    private List<String> affectedEntities;

    public List<String> getAffectedEntities() {
        return affectedEntities;
    }

    public ConcentrationModuleRepresentation() {
        affectedEntities = new ArrayList<>();
    }

    public void setAffectedEntities(List<String> affectedEntities) {
        this.affectedEntities = affectedEntities;
    }

    public void addAffectedEntity(String affectedEntity) {
        affectedEntities.add(affectedEntity);
    }

}
