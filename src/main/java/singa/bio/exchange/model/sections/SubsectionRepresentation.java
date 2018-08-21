package singa.bio.exchange.model.sections;

import bio.singa.features.identifiers.GoTerm;
import bio.singa.simulation.model.sections.CellSubsection;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class SubsectionRepresentation {

    @JsonProperty
    private String identifier;

    @JsonProperty("go-term")
    private String goTerm;

    public SubsectionRepresentation() {

    }

    public static SubsectionRepresentation of(CellSubsection subsection) {
        SubsectionRepresentation representation = new SubsectionRepresentation();
        representation.setIdentifier(subsection.getIdentifier());
        representation.setGoTerm(subsection.getGoTerm().getIdentifier());
        return representation;
    }

    public CellSubsection toModel() {
        return new CellSubsection(getIdentifier(), new GoTerm(getGoTerm()));
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getGoTerm() {
        return goTerm;
    }

    public void setGoTerm(String goTerm) {
        this.goTerm = goTerm;
    }

}
