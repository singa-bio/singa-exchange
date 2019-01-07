package singa.bio.exchange.model.features;

import bio.singa.features.model.Feature;
import bio.singa.simulation.features.MultiStringFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MultiStringFeatureRepresentation extends FeatureRepresentation {

    private List<String> strings;

    public MultiStringFeatureRepresentation() {
        strings = new ArrayList<>();
    }

    public static MultiStringFeatureRepresentation of(Feature<?> feature) {
        MultiStringFeatureRepresentation representation = new MultiStringFeatureRepresentation();
        representation.setName(feature.getClass().getSimpleName());
        for (String string : ((MultiStringFeature) feature).getContent()) {
            representation.addString(string);
        }
        representation.addEvidence(feature.getAllEvidence());
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

}
