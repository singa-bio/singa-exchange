package singa.bio.exchange.model.sbml;

import bio.singa.simulation.model.simulation.Simulation;
import singa.bio.exchange.model.SimulationRepresentation;
import singa.bio.exchange.model.entities.EntityDataset;
import singa.bio.exchange.model.graphs.GraphRepresentation;
import singa.bio.exchange.model.macroscopic.MembraneDataset;
import singa.bio.exchange.model.modules.ModuleDataset;
import singa.bio.exchange.model.sections.InitialConcentrationDataset;

/**
 * @author cl
 */
public class SBMLConverter {

    private SBMLConverter() {

    }

    public static Simulation current;

    public static EntityDataset getEntityDatasetFrom(Simulation simulation) {
        EntityDataset dataset = new EntityDataset();

        return dataset;
    }

    public static ModuleDataset getModuleDatasetFrom(Simulation simulation) {
        ModuleDataset dataset = new ModuleDataset();

        return dataset;
    }

    public static GraphRepresentation getGraphFrom(Simulation simulation) {
        return null;
    }

    public static MembraneDataset getMembranesFrom(Simulation simulation) {
        MembraneDataset dataset = new MembraneDataset();

        return dataset;
    }

    public static InitialConcentrationDataset getConcentrationsFrom(Simulation simulation) {
        InitialConcentrationDataset dataset = new InitialConcentrationDataset();

        return dataset;
    }

    public static SimulationRepresentation getRepresentationFrom(Simulation simulation) {
        SimulationRepresentation representation = new SimulationRepresentation();

        return representation;
    }




}
