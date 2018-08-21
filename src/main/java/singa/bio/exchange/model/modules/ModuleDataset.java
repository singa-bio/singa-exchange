package singa.bio.exchange.model.modules;

import bio.singa.simulation.model.modules.UpdateModule;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.Jasonizable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ModuleDataset implements Jasonizable {

    @JsonProperty
    private List<ModuleRepresentation> modules;

    public ModuleDataset() {
        modules = new ArrayList<>();
    }

    public List<UpdateModule> toModel() {
        return getModules().stream()
                .map(ModuleRepresentation::toModel)
                .collect(Collectors.toList());
    }

    public List<ModuleRepresentation> getModules() {
        return modules;
    }

    public void setModules(List<ModuleRepresentation> modules) {
        this.modules = modules;
    }

    public void addModule(ModuleRepresentation module) {
        modules.add(module);
    }

}
