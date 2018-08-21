package singa.bio.exchange.model;

import bio.singa.features.parameters.Environment;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.sections.RegionRepresentation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Unit;
import javax.measure.quantity.Length;

/**
 * @author cl
 */
public class EnvironmentRepresentation {

    @JsonProperty("simulation-extend")
    private double simulationExtend;

    @JsonProperty("system-extend")
    private double systemExtend;

    @JsonProperty("system-extend-unit")
    private Unit<Length> systemExtendUnit;

    @JsonProperty("node-distance")
    private double nodeDistance;

    @JsonProperty("node-distance-unit")
    private Unit<Length> nodeDistanceUnit;

    @JsonProperty("standard-region")
    private RegionRepresentation standardRegion;

    public EnvironmentRepresentation() {

    }

    public static EnvironmentRepresentation fromSingleton() {
        EnvironmentRepresentation representation = new EnvironmentRepresentation();
        representation.setSimulationExtend(Environment.getSimulationExtend());
        representation.setSystemExtend(Environment.getSystemExtend().getValue().doubleValue());
        representation.setSystemExtendUnit(Environment.getSystemExtend().getUnit());
        representation.setNodeDistance(Environment.getNodeDistance().getValue().doubleValue());
        representation.setNodeDistanceUnit(Environment.getNodeDistance().getUnit());
        return representation;
    }

    public void setEnvironment() {
        Environment.setSimulationExtend(getSimulationExtend());
        Environment.setSystemExtend(Quantities.getQuantity(getSystemExtend(), getSystemExtendUnit()));
        Environment.setNodeDistance(Quantities.getQuantity(getNodeDistance(), getNodeDistanceUnit()));
    }

    public double getSimulationExtend() {
        return simulationExtend;
    }

    public void setSimulationExtend(double simulationExtend) {
        this.simulationExtend = simulationExtend;
    }

    public double getSystemExtend() {
        return systemExtend;
    }

    public void setSystemExtend(double systemExtend) {
        this.systemExtend = systemExtend;
    }

    public Unit<Length> getSystemExtendUnit() {
        return systemExtendUnit;
    }

    public void setSystemExtendUnit(Unit<Length> systemExtendUnit) {
        this.systemExtendUnit = systemExtendUnit;
    }

    public double getNodeDistance() {
        return nodeDistance;
    }

    public void setNodeDistance(double nodeDistance) {
        this.nodeDistance = nodeDistance;
    }

    public Unit<Length> getNodeDistanceUnit() {
        return nodeDistanceUnit;
    }

    public void setNodeDistanceUnit(Unit<Length> nodeDistanceUnit) {
        this.nodeDistanceUnit = nodeDistanceUnit;
    }

}
