package singa.bio.exchange.model.agents;

import bio.singa.simulation.model.agents.linelike.LineLikeAgent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class FilamentDataset {

    @JsonProperty("microtubule-organizing-centre")
    private MicrotubuleOrganizingCentreRepresentation microtubuleOrganizingCentre;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FilamentRepresentation> filaments;

    public FilamentDataset() {
        filaments = new ArrayList<>();
    }

    public List<LineLikeAgent> toModel() {
        List<LineLikeAgent> lineLikeAgents = new ArrayList<>();
        for (FilamentRepresentation filament : filaments) {
            lineLikeAgents.add(filament.toModel());
        }
        return lineLikeAgents;
    }

    public List<FilamentRepresentation> getFilaments() {
        return filaments;
    }

    public void setFilaments(List<FilamentRepresentation> filaments) {
        this.filaments = filaments;
    }

    public void addFilament(FilamentRepresentation representation) {
        this.filaments.add(representation);
    }

    public MicrotubuleOrganizingCentreRepresentation getMicrotubuleOrganizingCentre() {
        return microtubuleOrganizingCentre;
    }

    public void setMicrotubuleOrganizingCentre(MicrotubuleOrganizingCentreRepresentation microtubuleOrganizingCentre) {
        this.microtubuleOrganizingCentre = microtubuleOrganizingCentre;
    }
}
