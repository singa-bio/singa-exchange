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

    @JsonProperty
    private boolean membrane;

    public SubsectionRepresentation() {

    }

    public static SubsectionRepresentation of(CellSubsection subsection) {
        SubsectionRepresentation representation = new SubsectionRepresentation();
        representation.setIdentifier(subsection.getIdentifier());
        representation.setMembrane(subsection.isMembrane());
        if (subsection.getGoTerm() != null) {
            representation.setGoTerm(subsection.getGoTerm().getContent());
        }
        return representation;
    }

    public CellSubsection toModel() {
        CellSubsection cellSubsection = new CellSubsection(getIdentifier());
        cellSubsection.setMembrane(getMembrane());
        if (getGoTerm() != null) {
            cellSubsection.setGoTerm(new GoTerm(getGoTerm()));
            return cellSubsection;
        } else {
            return cellSubsection;
        }
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

    public boolean getMembrane() {
        return membrane;
    }

    public void setMembrane(boolean membrane) {
        this.membrane = membrane;
    }
}
