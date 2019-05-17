package singa.bio.exchange.model.variation;

import bio.singa.features.quantities.MolarConcentration;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cl
 */
public class Observation {

    private int simulationIndex = 1;

    private List<String> parameterIndices;
    private List<String> observationIndices;

    private Map<String, Double> parameters;
    private Map<Quantity<Time>, Map<String, Quantity<MolarConcentration>>> observationMap;

    private List<String> resultingObservations;

    public Observation() {
        parameterIndices = new ArrayList<>();
        observationIndices = new ArrayList<>();
        parameters = new HashMap<>();
        observationMap = new HashMap<>();
        resultingObservations = new ArrayList<>();
    }

    public void initializeParameters(Collection<String> parameterList) {
        parameterIndices.add("simulation");
        parameterIndices.addAll(parameterList);
        parameterIndices.add("time");
    }

    public void initializeObservations(List<ObservationSpecification> observationSpecifications) {
        observationIndices = observationSpecifications.stream()
                .map(ObservationSpecification::getAlias)
                .collect(Collectors.toList());
    }

    public void setParameters(Map<String, Double> parameters) {
        this.parameters = parameters;
    }

    public void addObservations(String alias, Map<Quantity<Time>, Quantity<MolarConcentration>> observations) {
        for (Map.Entry<Quantity<Time>, Quantity<MolarConcentration>> entry : observations.entrySet()) {
            if (!observationMap.containsKey(entry.getKey())) {
                observationMap.put(entry.getKey(), new HashMap<>());
            }
            observationMap.get(entry.getKey()).put(alias, entry.getValue());
        }
    }

    public String getHeader() {
        return Stream.concat(parameterIndices.stream(), observationIndices.stream()).collect(Collectors.joining(","));
    }

    public void flushObservations() {
        if (parameters.isEmpty() || observationMap.isEmpty()) {
            parameters.clear();
            observationMap.clear();
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(simulationIndex).append(",");
        simulationIndex++;

        for (String parameterIndex : parameterIndices) {
            if (parameterIndex.equals("time") || parameterIndex.equals("simulation")) {
                continue;
            }
            Double parameterValue = parameters.get(parameterIndex);
            builder.append(parameterValue).append(",");
        }
        String parameterString = builder.toString();
        builder.setLength(0);

        for (Quantity<Time> time : observationMap.keySet()) {
            builder.append(time.getValue().doubleValue());
            for (String observationIndex : observationIndices) {
                Quantity<MolarConcentration> observationValue = observationMap.get(time).get(observationIndex);
                builder.append(observationValue.getValue().doubleValue()).append(",");
            }
            String observationString = builder.toString().replaceAll(",$", "");
            builder.setLength(0);
            System.out.println(parameterString + observationString);
            resultingObservations.add(parameterString + observationString);
        }
        parameters.clear();
        observationMap.clear();
    }


}
