package singa.bio.exchange.model.macroscopic;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cl
 */
public class SegmentRepresentation {

    @JsonProperty
    private double sx;

    @JsonProperty
    private double sy;

    @JsonProperty
    private double ex;

    @JsonProperty
    private double ey;

    public SegmentRepresentation() {

    }

    public static SegmentRepresentation of(LineSegment lineSegment) {
        SegmentRepresentation representation = new SegmentRepresentation();
        representation.setSx(lineSegment.getStartingPoint().getX());
        representation.setSy(lineSegment.getStartingPoint().getY());
        representation.setEx(lineSegment.getEndingPoint().getX());
        representation.setEy(lineSegment.getEndingPoint().getY());
        return representation;
    }

    public LineSegment toModel() {
        return new SimpleLineSegment(sx, sy, ex, ey);
    }

    public double getSx() {
        return sx;
    }

    public void setSx(double sx) {
        this.sx = sx;
    }

    public double getSy() {
        return sy;
    }

    public void setSy(double sy) {
        this.sy = sy;
    }

    public double getEx() {
        return ex;
    }

    public void setEx(double ex) {
        this.ex = ex;
    }

    public double getEy() {
        return ey;
    }

    public void setEy(double ey) {
        this.ey = ey;
    }
}
