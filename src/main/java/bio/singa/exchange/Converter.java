package bio.singa.exchange;

import bio.singa.chemistry.entities.ChemicalEntities;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.exchange.agents.*;
import bio.singa.exchange.concentrations.InitialConcentrationDataset;
import bio.singa.exchange.concentrations.InitialConcentrationRepresentation;
import bio.singa.exchange.entities.EntityDataset;
import bio.singa.exchange.graphs.automaton.GraphRepresentation;
import bio.singa.exchange.sections.RegionCache;
import bio.singa.exchange.sections.SubsectionCache;
import bio.singa.exchange.units.UnitJacksonModule;
import bio.singa.simulation.model.agents.linelike.LineLikeAgent;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import bio.singa.simulation.model.concentrations.InitialConcentration;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.simulation.Simulation;
import com.fasterxml.jackson.databind.ObjectMapper;
import bio.singa.exchange.entities.EntityRepresentation;
import bio.singa.exchange.evidence.EvidenceDataset;
import bio.singa.exchange.modules.ModuleDataset;
import bio.singa.exchange.modules.ModuleRepresentation;
import bio.singa.exchange.sections.RegionDataset;
import bio.singa.exchange.sections.SubsectionDataset;

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
        List<ChemicalEntity> sortedEntities = ChemicalEntities.sortByComplexDependencies(new ArrayList<>(simulation.getChemicalEntities()));
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

    public static FilamentDataset getFilamentsFrom(Simulation simulation) {
        FilamentDataset dataset = new FilamentDataset();
        if (simulation.getLineLayer() != null) {
            for (LineLikeAgent filament : simulation.getLineLayer().getFilaments()) {
                dataset.addFilament(FilamentRepresentation.of(filament));
            }
            if (simulation.getMembraneLayer().getMicrotubuleOrganizingCentre() != null) {
                dataset.setMicrotubuleOrganizingCentre(MicrotubuleOrganizingCentreRepresentation.of(simulation.getMembraneLayer().getMicrotubuleOrganizingCentre()));
            }
        }
        return dataset;
    }

    public static VesicleDataset getVesiclesFrom(Simulation simulation) {
        VesicleDataset dataset = new VesicleDataset();
        if (simulation.getVesicleLayer() != null) {
            for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
                dataset.addVesicle(VesicleRepresentation.of(vesicle));
            }
            RegionCache.add(CellRegions.VESICLE_REGION);
            CellRegions.VESICLE_REGION.getSubsections().forEach(SubsectionCache::add);
        }
        return dataset;
    }

    public static VolumeDataset getVolumesFrom(Simulation simulation) {
        VolumeDataset dataset = new VolumeDataset();
        if (simulation.getVolumeLayer() != null) {
            for (VolumeLikeAgent volume : simulation.getVolumeLayer().getAgents()) {
                dataset.addVolume(VolumeRepresentation.of(volume));
            }
        }
        return dataset;
    }

    public static InitialConcentrationDataset getConcentrationsFrom(Simulation simulation) {
        InitialConcentrationDataset dataset = new InitialConcentrationDataset();
        for (InitialConcentration initialConcentration : simulation.getConcentrations()) {
            dataset.addConcentration(InitialConcentrationRepresentation.of(initialConcentration));
        }
        return dataset;
    }

    public static SimulationRepresentation getRepresentationFrom(Simulation simulation) {
        ModuleDataset moduleDataset = getModuleDatasetFrom(simulation);
        // only called to fill cache
        getEntityDatasetFrom(simulation);
        GraphRepresentation graph = getGraphFrom(simulation);
        MembraneDataset membranes = getMembranesFrom(simulation);
        FilamentDataset filaments = getFilamentsFrom(simulation);
        VesicleDataset vesicles = getVesiclesFrom(simulation);
        VolumeDataset volumes = getVolumesFrom(simulation);
        InitialConcentrationDataset concentrations = getConcentrationsFrom(simulation);

        SimulationRepresentation representation = new SimulationRepresentation();
        representation.setMetadata(Metadata.forSinga());
        representation.setEntities(EntityDataset.fromCache());
        representation.setModules(moduleDataset);
        representation.setGraph(graph);
        representation.setMembranes(membranes);
        representation.setFilaments(filaments);
        representation.setVesicles(vesicles);
        representation.setVolumes(volumes);
        representation.setEvidence(EvidenceDataset.fromCache());
        representation.setSubsections(SubsectionDataset.fromCache());
        representation.setRegions(RegionDataset.fromCache());
        representation.setEnvironment(EnvironmentRepresentation.fromSingleton());
        representation.setConcentrations(concentrations);
        return representation;
    }

    public static SimulationRepresentation getRepresentationFrom(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new UnitJacksonModule());
        return mapper.readValue(json, SimulationRepresentation.class);
    }

    public static Simulation getSimulationFrom(String json) throws IOException {
        current = new Simulation();
        return SimulationRepresentation.to(getRepresentationFrom(json));
    }

}
