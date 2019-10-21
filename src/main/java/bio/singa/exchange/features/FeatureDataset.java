package bio.singa.exchange.features;

import bio.singa.exchange.Jsonizable;
import bio.singa.exchange.units.UnitJacksonModule;
import bio.singa.features.model.FeatureRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class FeatureDataset implements Jsonizable {

    @JsonProperty
    private List<FeatureRepresentation<?>> features;

    public FeatureDataset() {
        features = new ArrayList<>();
    }

    public static FeatureDataset registryDataset() {
        FeatureDataset dataset = new FeatureDataset();

        FeatureRegistry.getScalableQuantitativeFeatures().stream()
                .map(FeatureRepresentation::of)
                .forEach(dataset::addFeatureRepresentation);

        FeatureRegistry.getQuantitativeFeatures().stream()
                .map(FeatureRepresentation::of)
                .forEach(dataset::addFeatureRepresentation);

        FeatureRegistry.getQualitativeFeatures().stream()
                .map(FeatureRepresentation::of)
                .forEach(dataset::addFeatureRepresentation);

        return dataset;
    }

    public static FeatureDataset variationDataset() {
        FeatureDataset dataset = new FeatureDataset();

        FeatureRegistry.getScalableQuantitativeFeatures().stream()
                .filter(feature -> !feature.getAlternativeContents().isEmpty())
                .map(FeatureRepresentation::of)
                .forEach(dataset::addFeatureRepresentation);

        FeatureRegistry.getQuantitativeFeatures().stream()
                .filter(feature -> !feature.getAlternativeContents().isEmpty())
                .map(FeatureRepresentation::of)
                .forEach(dataset::addFeatureRepresentation);

        FeatureRegistry.getQualitativeFeatures().stream()
                .filter(feature -> !feature.getAlternativeContents().isEmpty())
                .map(FeatureRepresentation::of)
                .forEach(dataset::addFeatureRepresentation);

        return dataset;
    }

    public static List<FeatureRepresentation<?>> fromDatasetRepresentation(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new UnitJacksonModule());
        return mapper.readValue(json, FeatureDataset.class).getFeatures();
    }

    public static String generateVariableFeatureLog() throws JsonProcessingException {
        return variationDataset().toJson();
    }

    public List<FeatureRepresentation<?>> getFeatures() {
        return features;
    }

    public void addFeatureRepresentation(FeatureRepresentation<?> featureRepresentation) {
        features.add(featureRepresentation);
    }

    public void setFeatures(List<FeatureRepresentation<?>> features) {
        this.features = features;
    }

}
