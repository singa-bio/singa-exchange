package singa.bio.exchange.model.variation;

import bio.singa.features.formatter.ConcentrationFormatter;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.features.variation.VariationSet;

import javax.measure.Quantity;
import javax.measure.Unit;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Records observations from running simulations.
 *
 * @author cl
 */
public class ObservationRecorder {

    private List<String> observedAliases;
    private ObservationSpecifications observations;
    private ConcentrationFormatter formatter;

    private List<String> recordedVariations;
    private List<String> recordedObservations;

    public ObservationRecorder(ObservationSpecifications observations, Unit<MolarConcentration> concentrationUnit, VariationSet variationSet) {
        this.observations = observations;
        observedAliases = new ArrayList<>();
        recordedVariations = new ArrayList<>();
        recordedObservations = new ArrayList<>();
        formatter = ConcentrationFormatter.create(concentrationUnit);
        createObservationHeader();
        createVariationHeader(variationSet);
    }

    private void createObservationHeader() {
        collectAliases();
        recordedObservations.add(String.join(",", observedAliases));
    }

    private void collectAliases() {
        for (ObservationSpecification observation : observations.getObservations()) {
            observedAliases.add(observation.getAlias());
        }
    }

    private void createVariationHeader(VariationSet variationSet) {
        recordedVariations.add(variationSet.getAffectedParameters());
    }

    public void recordVariations(List<?> variations) {
        String variationsLine = variations.stream()
                .map(VariationSet::getValueString)
                .collect(Collectors.joining(","));
        recordedVariations.add(variationsLine);
    }

    public void recordVariations() {

    }

    public void recordObservations() {
        Map<String, Quantity<MolarConcentration>> concentrations = new HashMap<>();
        for (ObservationSpecification observation : observations.getObservations()) {
            Quantity<MolarConcentration> concentration = observation.observe();
            concentrations.put(observation.getAlias(), concentration);
        }
        String observationLine = observedAliases.stream()
                .map(observedAlias -> formatter.format(concentrations.get(observedAlias)))
                .collect(Collectors.joining(","));
        recordedObservations.add(observationLine);
    }

    public String getLatestVariations() {
        return recordedVariations.get(recordedVariations.size() - 1);
    }

    public String getLatestObservations() {
        return recordedObservations.get(recordedObservations.size() - 1);
    }

    public ObservationSpecifications getObservations() {
        return observations;
    }

    public void writeVariationResults(Path observationPath) {
        // pair variations and observations
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < recordedVariations.size(); i++) {
            result.append(recordedVariations.get(i))
                    .append(",")
                    .append(recordedObservations.get(i))
                    .append(System.lineSeparator());
        }
        // write to file
        try {
            Files.write(observationPath.resolve("variations_results.csv"), result.toString().getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to write variation results to " + observationPath + ".", e);
        }
    }

}
