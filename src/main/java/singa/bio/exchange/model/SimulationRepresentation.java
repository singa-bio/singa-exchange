package singa.bio.exchange.model;

import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import bio.singa.simulation.model.simulation.Simulation;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.entities.EntityDataset;
import singa.bio.exchange.model.evidence.EvidenceDataset;
import singa.bio.exchange.model.graphs.GraphRepresentation;
import singa.bio.exchange.model.macroscopic.MembraneDataset;
import singa.bio.exchange.model.modules.ModuleDataset;
import singa.bio.exchange.model.sections.InitialConcentrationDataset;
import singa.bio.exchange.model.sections.RegionDataset;
import singa.bio.exchange.model.sections.SubsectionDataset;

/**
 * @author cl
 */
public class SimulationRepresentation implements Jasonizable {

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
    private InitialConcentrationDataset concentrations;

    public SimulationRepresentation() {

    }

    public static Simulation to(SimulationRepresentation representation) {
        // initialize environment
        representation.getEnvironment().setEnvironment();
        // initialize subsections
        representation.getSubsections().cache();
        // initialize  regions, requires subsections
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
        MembraneLayer membraneLayer = new MembraneLayer();
        Converter.current.setMembraneLayer(membraneLayer);
        membraneLayer.addMembranes(representation.getMembranes().toModel());
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

    public InitialConcentrationDataset getConcentrations() {
        return concentrations;
    }

    public void setConcentrations(InitialConcentrationDataset concentrations) {
        this.concentrations = concentrations;
    }
}
