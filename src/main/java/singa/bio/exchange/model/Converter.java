package singa.bio.exchange.model;

import bio.singa.chemistry.entities.ChemicalEntities;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.sections.concentration.InitialConcentration;
import bio.singa.simulation.model.sections.concentration.SectionConcentration;
import bio.singa.simulation.model.simulation.Simulation;
import com.fasterxml.jackson.databind.ObjectMapper;
import singa.bio.exchange.model.entities.EntityDataset;
import singa.bio.exchange.model.entities.EntityRepresentation;
import singa.bio.exchange.model.evidence.EvidenceDataset;
import singa.bio.exchange.model.graphs.GraphRepresentation;
import singa.bio.exchange.model.macroscopic.MembraneDataset;
import singa.bio.exchange.model.macroscopic.MembraneRepresentation;
import singa.bio.exchange.model.modules.ModuleDataset;
import singa.bio.exchange.model.modules.ModuleRepresentation;
import singa.bio.exchange.model.sections.InitialConcentrationDataset;
import singa.bio.exchange.model.sections.SectionConcentrationRepresentation;
import singa.bio.exchange.model.sections.RegionDataset;
import singa.bio.exchange.model.sections.SubsectionDataset;
import singa.bio.exchange.model.units.UnitJacksonModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class Converter {

    private Converter() {

    }

    public static Simulation current;

    public static EntityDataset getEntityDatasetFrom(Simulation simulation) {
        EntityDataset dataset = new EntityDataset();
        List<ChemicalEntity> sortedEntities = ChemicalEntities.sortByComplexDependencies(new ArrayList<>(simulation.getAllChemicalEntities()));
        for (ChemicalEntity chemicalEntity : sortedEntities) {
            dataset.addEntity(EntityRepresentation.of(chemicalEntity));
        }
        return dataset;
    }

    public static ModuleDataset getModuleDatasetFrom(Simulation simulation) {
        ModuleDataset dataset = new ModuleDataset();
        for (UpdateModule module : simulation.getModules()) {
            dataset.addModule(ModuleRepresentation.of(module));
        }
        return dataset;
    }

    public static GraphRepresentation getGraphFrom(Simulation simulation) {
        return GraphRepresentation.of(simulation.getGraph());
    }

    public static MembraneDataset getMembranesFrom(Simulation simulation) {
        MembraneDataset dataset = new MembraneDataset();
        if (simulation.getMembraneLayer() != null) {
            for (Membrane membrane : simulation.getMembraneLayer().getMembranes()) {
                dataset.addMembrane(MembraneRepresentation.of(membrane));
            }
        }
        return dataset;
    }

    public static InitialConcentrationDataset getConcentrationsFrom(Simulation simulation) {
        InitialConcentrationDataset dataset = new InitialConcentrationDataset();
        for (InitialConcentration initialConcentration : simulation.getConcentrationInitializer().getInitialConcentrations()) {
            if (initialConcentration instanceof SectionConcentration) {
                dataset.addConcentration(SectionConcentrationRepresentation.of((SectionConcentration) initialConcentration));
            }
            // TODO  membrane concentrations
        }
        return dataset;
    }

    public static SimulationRepresentation getRepresentationFrom(Simulation simulation) {
        ModuleDataset moduleDataset = getModuleDatasetFrom(simulation);
        EntityDataset entityDataset = getEntityDatasetFrom(simulation);
        GraphRepresentation graph = getGraphFrom(simulation);
        MembraneDataset membranes = getMembranesFrom(simulation);
        InitialConcentrationDataset concentrations = getConcentrationsFrom(simulation);

        SimulationRepresentation representation = new SimulationRepresentation();
        representation.setMetadata(Metadata.forSinga());
        representation.setEntities(entityDataset);
        representation.setModules(moduleDataset);
        representation.setGraph(graph);
        representation.setMembranes(membranes);
        representation.setEvidence(EvidenceDataset.fromCache());
        representation.setSubsections(SubsectionDataset.fromCache());
        representation.setRegions(RegionDataset.fromCache());
        representation.setEnvironment(EnvironmentRepresentation.fromSingleton());
        representation.setConcentrations(concentrations);
        return representation;
    }

    public static Simulation getSimulationFrom(String json) throws IOException {
        current = new Simulation();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new UnitJacksonModule());
        return SimulationRepresentation.to(mapper.readValue(json, SimulationRepresentation.class));
    }

}
