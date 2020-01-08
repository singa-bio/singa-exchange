package bio.singa.exchange;

import bio.singa.exchange.features.FeatureDataset;
import bio.singa.exchange.features.FeatureRepresentation;
import bio.singa.exchange.units.UnitJacksonModule;
import bio.singa.features.quantities.MolarConcentration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author cl
 */
public class ProcessingTicket implements Jsonizable {

    @JsonProperty
    private String identifier;
    @JsonProperty
    private String simulation;

    @JsonProperty("total-time-quantity")
    private double totalTimeQuantity;
    @JsonProperty("total-time-unit")
    private Unit<Time> totalTimeUnit;

    @JsonProperty("observation-time-quantity")
    private double observationTimeQuantity;
    @JsonProperty("observation-time-unit")
    private Unit<Time> observationTimeUnit;

    @JsonProperty("observation-concentration-unit")
    private Unit<MolarConcentration> observedConcentrationUnit;
    @JsonProperty("observed-time-unit")
    private Unit<Time> observedTimeUnit;

    @JsonProperty
    private List<FeatureRepresentation<?>> features;

    private ObjectMapper mapper;

    public ProcessingTicket() {
        initializeMapper();
    }

    private void initializeMapper() {
        mapper = new ObjectMapper();
        mapper.registerModule(new UnitJacksonModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSimulation() {
        return simulation;
    }

    public void setSimulation(String simulation) {
        this.simulation = simulation;
    }

    public double getTotalTimeQuantity() {
        return totalTimeQuantity;
    }

    public void setTotalTimeQuantity(double totalTimeQuantity) {
        this.totalTimeQuantity = totalTimeQuantity;
    }

    public Unit<Time> getTotalTimeUnit() {
        return totalTimeUnit;
    }

    public void setTotalTimeUnit(Unit<Time> totalTimeUnit) {
        this.totalTimeUnit = totalTimeUnit;
    }

    @JsonIgnore
    public void setTotalTime(Quantity<Time> totalTime) {
        totalTimeQuantity = totalTime.getValue().doubleValue();
        totalTimeUnit = totalTime.getUnit();
    }

    @JsonIgnore
    public Quantity<Time> getTotalTime() {
        return Quantities.getQuantity(totalTimeQuantity, totalTimeUnit);
    }

    public double getObservationTimeQuantity() {
        return observationTimeQuantity;
    }

    public void setObservationTimeQuantity(double observationTimeQuantity) {
        this.observationTimeQuantity = observationTimeQuantity;
    }

    public Unit<Time> getObservationTimeUnit() {
        return observationTimeUnit;
    }

    public void setObservationTimeUnit(Unit<Time> observationTimeUnit) {
        this.observationTimeUnit = observationTimeUnit;
    }

    @JsonIgnore
    public void setObservationTime(Quantity<Time> observationTime) {
        observationTimeQuantity = observationTime.getValue().doubleValue();
        observationTimeUnit = observationTime.getUnit();
    }

    @JsonIgnore
    public Quantity<Time> getObservationTime() {
        return Quantities.getQuantity(observationTimeQuantity, observationTimeUnit);
    }

    public Unit<MolarConcentration> getObservedConcentrationUnit() {
        return observedConcentrationUnit;
    }

    public void setObservedConcentrationUnit(Unit<MolarConcentration> observedConcentrationUnit) {
        this.observedConcentrationUnit = observedConcentrationUnit;
    }

    public Unit<Time> getObservedTimeUnit() {
        return observedTimeUnit;
    }

    public void setObservedTimeUnit(Unit<Time> observedTimeUnit) {
        this.observedTimeUnit = observedTimeUnit;
    }

    public List<FeatureRepresentation<?>> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureRepresentation<?>> features) {
        this.features = features;
    }

    public static ProcessingTicket fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new UnitJacksonModule());
        return mapper.readValue(json, ProcessingTicket.class);
    }

    public void writeFeatureSet(Path path) throws IOException {
        FeatureDataset fd = new FeatureDataset();
        fd.setFeatures(getFeatures());
        mapper.writeValue(path.toFile(), fd);
    }

}
