package bio.singa.exchange.features;

import bio.singa.features.model.Feature;
import bio.singa.simulation.features.model.MultiStringFeature;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MultiStringFeatureRepresentation extends FeatureRepresentation<List<String>> {

    @JsonProperty
    private List<String> strings;

    public MultiStringFeatureRepresentation() {
        strings = new ArrayList<>();
    }

    public static MultiStringFeatureRepresentation of(Feature<?> feature) {
        MultiStringFeatureRepresentation representation = new MultiStringFeatureRepresentation();
        representation.baseSetup(feature);
        for (String string : ((MultiStringFeature) feature).getContent()) {
            representation.addString(string);
        }
        return representation;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public void addString(String entity) {
        this.strings.add(entity);
    }

    @Override
    public List<String> fetchContent() {
        return strings;
    }
}
