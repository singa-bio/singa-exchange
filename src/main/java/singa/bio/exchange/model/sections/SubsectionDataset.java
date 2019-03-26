package singa.bio.exchange.model.sections;

import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.Jsonizable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class SubsectionDataset implements Jsonizable {

    @JsonProperty
    private List<SubsectionRepresentation> subsections;

    public SubsectionDataset() {
        subsections = new ArrayList<>();
    }

    public static SubsectionDataset fromCache() {
        SubsectionDataset dataset = new SubsectionDataset();
        SubsectionCache.getAll().stream()
                .map(SubsectionRepresentation::of)
                .forEach(dataset::addSubsection);
        return dataset;
    }

    public void cache() {
        SubsectionCache.clear();
        for (SubsectionRepresentation subsection : subsections) {
            SubsectionCache.add(subsection.toModel());
        }
    }

    public List<SubsectionRepresentation> getSubsections() {
        return subsections;
    }

    public void setSubsections(List<SubsectionRepresentation> subsections) {
        this.subsections = subsections;
    }

    public void addSubsection(SubsectionRepresentation representation) {
        subsections.add(representation);
    }

}
