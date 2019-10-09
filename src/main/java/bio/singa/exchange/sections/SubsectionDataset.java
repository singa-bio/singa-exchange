package bio.singa.exchange.sections;

import bio.singa.exchange.Jsonizable;
import com.fasterxml.jackson.annotation.JsonProperty;

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
