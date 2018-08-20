package singa.bio.exchange.model;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.simulation.Simulation;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.entities.EntityDataset;
import singa.bio.exchange.model.modules.ModuleDataset;

import java.util.List;

/**
 * @author cl
 */
public class SimulationRepresentation implements Jasonizable {

    @JsonProperty
    private EntityDataset entities;
    @JsonProperty
    private ModuleDataset modules;

    public SimulationRepresentation() {

    }

    public static Simulation to(SimulationRepresentation representation) {
        List<ChemicalEntity> entities = EntityDataset.to(representation.getEntities());
        List<UpdateModule> modules = ModuleDataset.to(representation.getModules());
        // Converter.current.getModules().addAll(modules);
        // Converter.current.getChemicalEntities().addAll(entities);
        return Converter.current;
    }

    public EntityDataset getEntities() {
        return entities;
    }

    public void setEntities(EntityDataset entities) {
        this.entities = entities;
    }

    public ModuleDataset getModules() {
        return modules;
    }

    public void setModules(ModuleDataset modules) {
        this.modules = modules;
    }

}
