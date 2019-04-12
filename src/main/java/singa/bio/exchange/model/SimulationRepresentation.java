package singa.bio.exchange.model;

import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.simulation.model.agents.linelike.LineLikeAgentLayer;
import bio.singa.simulation.model.agents.linelike.MicrotubuleOrganizingCentre;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.volumelike.VolumeLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import bio.singa.simulation.model.simulation.Simulation;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.agents.FilamentDataset;
import singa.bio.exchange.model.agents.MembraneDataset;
import singa.bio.exchange.model.agents.VesicleDataset;
import singa.bio.exchange.model.agents.VolumeDataset;
import singa.bio.exchange.model.entities.EntityDataset;
import singa.bio.exchange.model.evidence.EvidenceDataset;
import singa.bio.exchange.model.graphs.GraphRepresentation;
import singa.bio.exchange.model.modules.ModuleDataset;
import singa.bio.exchange.model.sections.InitialConcentrationDataset;
import singa.bio.exchange.model.sections.RegionDataset;
import singa.bio.exchange.model.sections.SubsectionDataset;

/**
 * @author cl
 */
public class SimulationRepresentation implements Jsonizable {

    @JsonProperty
    private Metadata metadata;

    @JsonProperty
    private EntityDataset entities;

    @JsonProperty
    private ModuleDataset modules;

    @JsonProperty
    private RegionDataset regions;

    @JsonProperty
    private SubsectionDataset subsections;

    @JsonProperty
    private EvidenceDataset evidence;

    @JsonProperty
    private EnvironmentRepresentation environment;

    @JsonProperty
    private GraphRepresentation graph;

    @JsonProperty
    private MembraneDataset membranes;

    @JsonProperty
    private FilamentDataset filaments;

    @JsonProperty
    private VesicleDataset vesicles;

    @JsonProperty
    private VolumeDataset volumes;

    @JsonProperty
    private InitialConcentrationDataset concentrations;

    public SimulationRepresentation() {

    }

    public static Simulation to(SimulationRepresentation representation) {
        Converter.current = new Simulation();
        // initialize environment
        representation.getEnvironment().setEnvironment();
        Converter.current.setSimulationRegion(new Rectangle(representation.getEnvironment().getSimulationExtend(), representation.getEnvironment().getSimulationExtend()));
        // initialize subsections
        representation.getSubsections().cache();
        // initialize regions, requires subsections
        representation.getRegions().cache();
        // initialize evidence
        representation.getEvidence().cache();
        // initialize entities, requires evidence
        representation.getEntities().toModel();
        // initialize modules, requires entities and evidence, references them to simulation
        representation.getModules().toModel();
        // initialize graph and spatial representations
        AutomatonGraph graph = representation.getGraph().toModel();
        Converter.current.setGraph(graph);
        // initialize membranes, requires regions and graph
        if (!representation.getMembranes().getMembranes().isEmpty()) {
            MembraneLayer membraneLayer = new MembraneLayer();
            Converter.current.setMembraneLayer(membraneLayer);
            membraneLayer.addMembranes(representation.getMembranes().toModel());
        }
        // initialize vesicles, requires graph and regions
        if (!representation.getVesicles().getVesicles().isEmpty()) {
            VesicleLayer vesicleLayer = new VesicleLayer(Converter.current);
            Converter.current.setVesicleLayer(vesicleLayer);
            vesicleLayer.addVesicles(representation.getVesicles().toModel());
        }
        // initialize filaments, requires membranes and graph
        if (!representation.getFilaments().getFilaments().isEmpty()) {
            LineLikeAgentLayer filamentLayer = new LineLikeAgentLayer(Converter.current, Converter.current.getMembraneLayer());
            Converter.current.setLineLayer(filamentLayer);
            filamentLayer.addFilaments(representation.getFilaments().toModel());
        }
        // initialize volumes, requires regions
        if (!representation.getVolumes().getVolumes().isEmpty()) {
            VolumeLayer volumeLayer = new VolumeLayer();
            Converter.current.setVolumeLayer(volumeLayer);
            volumeLayer.addAgents(representation.getVolumes().toModel());
        }
        if (representation.getFilaments().getMicrotubuleOrganizingCentre() != null) {
            MicrotubuleOrganizingCentre moc = representation.getFilaments().getMicrotubuleOrganizingCentre().toModel();
            Converter.current.getMembraneLayer().setMicrotubuleOrganizingCentre(moc);
        }
        // initialize concentration, requires entities, regions, and subsections
        ConcentrationInitializer initializer = representation.getConcentrations().toModel();
        Converter.current.setConcentrationInitializer(initializer);
        return Converter.current;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
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

    public RegionDataset getRegions() {
        return regions;
    }

    public void setRegions(RegionDataset regions) {
        this.regions = regions;
    }

    public SubsectionDataset getSubsections() {
        return subsections;
    }

    public void setSubsections(SubsectionDataset subsections) {
        this.subsections = subsections;
    }

    public EvidenceDataset getEvidence() {
        return evidence;
    }

    public void setEvidence(EvidenceDataset evidence) {
        this.evidence = evidence;
    }

    public EnvironmentRepresentation getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentRepresentation environment) {
        this.environment = environment;
    }

    public GraphRepresentation getGraph() {
        return graph;
    }

    public void setGraph(GraphRepresentation graph) {
        this.graph = graph;
    }

    public MembraneDataset getMembranes() {
        return membranes;
    }

    public void setMembranes(MembraneDataset membranes) {
        this.membranes = membranes;
    }

    public FilamentDataset getFilaments() {
        return filaments;
    }

    public void setFilaments(FilamentDataset filaments) {
        this.filaments = filaments;
    }

    public VesicleDataset getVesicles() {
        return vesicles;
    }

    public void setVesicles(VesicleDataset vesicles) {
        this.vesicles = vesicles;
    }

    public VolumeDataset getVolumes() {
        return volumes;
    }

    public void setVolumes(VolumeDataset volumes) {
        this.volumes = volumes;
    }

    public InitialConcentrationDataset getConcentrations() {
        return concentrations;
    }

    public void setConcentrations(InitialConcentrationDataset concentrations) {
        this.concentrations = concentrations;
    }

}
