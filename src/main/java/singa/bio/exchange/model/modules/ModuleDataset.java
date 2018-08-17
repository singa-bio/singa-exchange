package singa.bio.exchange.model.modules;

import singa.bio.exchange.model.Jasonizable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class ModuleDataset implements Jasonizable {

    private List<ModuleRepresentation> modules;

    public ModuleDataset() {
        modules = new ArrayList<>();
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
