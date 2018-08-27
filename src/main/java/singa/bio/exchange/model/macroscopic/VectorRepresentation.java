package singa.bio.exchange.model.macroscopic;

import bio.singa.mathematics.vectors.Vector2D;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class VectorRepresentation {

    @JsonProperty
    private double x;

    @JsonProperty
    private double y;

    public VectorRepresentation() {

    }

    public static VectorRepresentation of(Vector2D vector) {
        VectorRepresentation vectorRepresentation = new VectorRepresentation();
        vectorRepresentation.setX(vector.getX());
        vectorRepresentation.setY(vector.getY());
        return vectorRepresentation;
    }

    public Vector2D toModel() {
        return new Vector2D(x,y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
