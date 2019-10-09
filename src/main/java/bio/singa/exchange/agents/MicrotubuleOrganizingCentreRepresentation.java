package bio.singa.exchange.agents;

import bio.singa.exchange.Converter;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.simulation.model.agents.linelike.MicrotubuleOrganizingCentre;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class MicrotubuleOrganizingCentreRepresentation {

    @JsonProperty
    private VectorRepresentation centre;

    @JsonProperty
    private double radius;

    public MicrotubuleOrganizingCentreRepresentation() {

    }

    public static MicrotubuleOrganizingCentreRepresentation of(MicrotubuleOrganizingCentre agent) {
        MicrotubuleOrganizingCentreRepresentation representation = new MicrotubuleOrganizingCentreRepresentation();
        representation.setCentre(VectorRepresentation.of(agent.getCircleRepresentation().getMidpoint()));
        representation.setRadius(agent.getCircleRepresentation().getRadius());
        return representation;
    }

    public MicrotubuleOrganizingCentre toModel() {
        return new MicrotubuleOrganizingCentre(Converter.current.getMembraneLayer(), new Circle(getCentre().toModel(), getRadius()));
    }

    public VectorRepresentation getCentre() {
        return centre;
    }

    public void setCentre(VectorRepresentation centre) {
        this.centre = centre;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

}
