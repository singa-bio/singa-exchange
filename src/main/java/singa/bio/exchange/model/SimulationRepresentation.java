package singa.bio.exchange.model;

import bio.singa.simulation.model.agents.membranes.MembraneLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.simulation.Simulation;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.entities.EntityDataset;
import singa.bio.exchange.model.graphs.GraphRepresentation;
import singa.bio.exchange.model.macroscopic.MembraneDataset;
import singa.bio.exchange.model.modules.ModuleDataset;
import singa.bio.exchange.model.origins.OriginDataset;
import singa.bio.exchange.model.sections.RegionDataset;
import singa.bio.exchange.model.sections.SubsectionDataset;

/**
 * @author cl
 */
public class SimulationRepresentation implements Jasonizable {

    @JsonProperty
    private EntityDataset entities;

    @JsonProperty
    private ModuleDataset modules;

    @JsonProperty
    private RegionDataset regions;

    @JsonProperty
    private SubsectionDataset subsections;

    @JsonProperty
    private OriginDataset origins;

    @JsonProperty
    private EnvironmentRepresentation environment;

    @JsonProperty
    private GraphRepresentation graph;

    @JsonProperty
    private MembraneDataset membranes;

    public SimulationRepresentation() {

    }

    public static Simulation to(SimulationRepresentation representation) {
        // initialize environment
        representation.getEnvironment().setEnvironment();
        // initialize subsections
        representation.getSubsections().cache();
        // initialize  regions, requires subsections
        representation.getRegions().cache();
        // initialize origins
        representation.getOrigins().cache();
        // initialize entities, requires origins
        representation.getEntities().toModel();
        // initialize modules, requires entities and origins, references them to simulation
        representation.getModules().toModel();
        // initialize graph and spatial representations
        AutomatonGraph graph = representation.getGraph().toModel();
        Converter.current.setGraph(graph);
        Converter.current.initializeGraph();
        Converter.current.initializeSpatialRepresentations();
        // initialize membranes, requires regions and graph
        MembraneLayer membraneLayer = new MembraneLayer();
        Converter.current.setMembraneLayer(membraneLayer);
        membraneLayer.addMembranes(representation.getMembranes().toModel());
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

    public OriginDataset getOrigins() {
        return origins;
    }

    public void setOrigins(OriginDataset origins) {
        this.origins = origins;
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

}
