package bio.singa.exchange;

import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.DynamicViscosity;
import bio.singa.features.units.UnitRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public class EnvironmentRepresentation {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentRepresentation.class);

    @JsonProperty("node-distance")
    private double nodeDistance;

    @JsonProperty("node-distance-unit")
    private Unit<Length> nodeDistanceUnit;

    @JsonProperty("simulation-extend")
    private double simulationExtend;

    @JsonProperty("system-extend")
    private double systemExtend;

    @JsonProperty("system-extend-unit")
    private Unit<Length> systemExtendUnit;

    @JsonProperty("temperature")
    private double temperature;

    @JsonProperty("temperature-unit")
    private Unit<Temperature> temperatureUnit;

    @JsonProperty("viscosity")
    private double viscosity;

    @JsonProperty("viscosity-unit")
    private Unit<DynamicViscosity> viscosityUnit;

    @JsonProperty("initial-time-step")
    private double initialTimeStep;

    @JsonProperty("initial-time-step-unit")
    private Unit<Time> initialTimeStepUnit;

    public EnvironmentRepresentation() {

    }

    public static EnvironmentRepresentation fromSingleton() {
        EnvironmentRepresentation representation = new EnvironmentRepresentation();
        // node distance
        representation.setNodeDistance(UnitRegistry.getSpaceScale());
        representation.setNodeDistanceUnit(UnitRegistry.getSpaceUnit());
        // system extend
        representation.setSystemExtend(Environment.getSystemExtend().getValue().doubleValue());
        representation.setSystemExtendUnit(Environment.getSystemExtend().getUnit());
        // simulation extend
        representation.setSimulationExtend(Environment.getSimulationExtend());
        // initial time step
        representation.setInitialTimeStep(UnitRegistry.getTimeScale());
        representation.setInitialTimeStepUnit(UnitRegistry.getTimeUnit());
        // viscosity
        // TODO add macro viscosity
        representation.setViscosity(Environment.getMatrixViscosity().getValue().doubleValue());
        representation.setViscosityUnit(Environment.getMatrixViscosity().getUnit());
        // temperature
        representation.setTemperature(Environment.getTemperature().getValue().doubleValue());
        representation.setTemperatureUnit(Environment.getTemperature().getUnit());
        return representation;
    }

    public void setEnvironment() {
        Environment.reset();
        // node distance
        if (nodeDistance != 0.0 && Double.isFinite(nodeDistance) && nodeDistanceUnit != null) {
            UnitRegistry.setSpace(Quantities.getQuantity(getNodeDistance(), getNodeDistanceUnit()));
        } else {
            logger.warn("Node distance and/or unit have not been set, using default {}.", UnitRegistry.DEFAULT_SPACE);
            UnitRegistry.setSpace(UnitRegistry.DEFAULT_SPACE);
        }
        // system extend
        if (systemExtend != 0.0 && Double.isFinite(systemExtend) && systemExtendUnit != null) {
            Environment.setSystemExtend(Quantities.getQuantity(getSystemExtend(), getSystemExtendUnit()));
        } else {
            logger.warn("System extend and/or unit have not been set, using default {}.", Environment.DEFAULT_SYSTEM_EXTEND);
            Environment.setSystemExtend(Environment.DEFAULT_SYSTEM_EXTEND);
        }
        // simulation extend
        if (simulationExtend != 0.0 && Double.isFinite(simulationExtend)) {
            Environment.setSimulationExtend(getSimulationExtend());
        } else {
            logger.warn("Simulation extend has not been set, using default {}.", Environment.DEFAULT_SIMULATION_EXTEND);
            Environment.setSimulationExtend(Environment.DEFAULT_SIMULATION_EXTEND);
        }
        // initial time step
        if (initialTimeStep != 0.0 && Double.isFinite(initialTimeStep) && initialTimeStepUnit != null) {
            UnitRegistry.setTime(Quantities.getQuantity(getInitialTimeStep(), getInitialTimeStepUnit()));
        } else {
            logger.warn("Initial time step and/or unit have not been set, using default {}.", UnitRegistry.DEFAULT_TIME);
            UnitRegistry.setTime(UnitRegistry.DEFAULT_TIME);
        }
        // viscosity
        if (viscosity != 0.0 && Double.isFinite(viscosity) && viscosityUnit != null) {
            Environment.setMatrixViscosity(Quantities.getQuantity(getViscosity(), getViscosityUnit()));
        } else {
            logger.warn("Viscosity and/or unit have not been set, using default {}.", Environment.DEFAULT_MATRIX_VISCOSITY);
            Environment.setMatrixViscosity(Environment.DEFAULT_MATRIX_VISCOSITY);
        }
        // temperature
        if (temperature != 0.0 && Double.isFinite(temperature) && temperatureUnit != null) {
            Environment.setTemperature(Quantities.getQuantity(getTemperature(), getTemperatureUnit()));
        } else {
            logger.warn("Temperature and/or unit have not been set, using default {}.", Environment.DEFAULT_SYSTEM_TEMPERATURE);
            Environment.setTemperature(Environment.DEFAULT_SYSTEM_TEMPERATURE);
        }
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

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public Unit<Temperature> getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(Unit<Temperature> temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public double getViscosity() {
        return viscosity;
    }

    public void setViscosity(double viscosity) {
        this.viscosity = viscosity;
    }

    public Unit<DynamicViscosity> getViscosityUnit() {
        return viscosityUnit;
    }

    public void setViscosityUnit(Unit<DynamicViscosity> viscosityUnit) {
        this.viscosityUnit = viscosityUnit;
    }

    public double getInitialTimeStep() {
        return initialTimeStep;
    }

    public void setInitialTimeStep(double initialTimeStep) {
        this.initialTimeStep = initialTimeStep;
    }

    public Unit<Time> getInitialTimeStepUnit() {
        return initialTimeStepUnit;
    }

    public void setInitialTimeStepUnit(Unit<Time> initialTimeStepUnit) {
        this.initialTimeStepUnit = initialTimeStepUnit;
    }
}
