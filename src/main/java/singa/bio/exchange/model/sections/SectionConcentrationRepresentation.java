package singa.bio.exchange.model.sections;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.concentration.InitialConcentration;
import bio.singa.simulation.model.sections.concentration.SectionConcentration;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.entities.EntityCache;
import tec.uom.se.quantity.Quantities;

import javax.measure.Unit;

/**
 * @author cl
 */
public class SectionConcentrationRepresentation {

    @JsonProperty
    private String region;

    @JsonProperty
    private String subsection;

    @JsonProperty
    private String entity;

    @JsonProperty
    private double value;

    @JsonProperty
    private Unit<MolarConcentration> unit;

    public SectionConcentrationRepresentation() {

    }

    public static SectionConcentrationRepresentation of(SectionConcentration initialConcentration) {
        SectionConcentrationRepresentation representation = new SectionConcentrationRepresentation();
        if (initialConcentration.getRegion() != null) {
            representation.setRegion(initialConcentration.getRegion().getIdentifier());
        }
        representation.setSubsection(initialConcentration.getSubsection().getIdentifier());
        representation.setEntity(initialConcentration.getEntity().getIdentifier().toString());
        representation.setValue(initialConcentration.getConcentration().getValue().doubleValue());
        representation.setUnit(initialConcentration.getConcentration().getUnit());
        return representation;
    }

    public InitialConcentration toModel() {
        CellSubsection subsection = SubsectionCache.get(getSubsection());
        ChemicalEntity entity = EntityCache.get(getEntity());
        String region = getRegion();
        if (region == null || region.isEmpty()) {
            return new SectionConcentration(subsection, entity, Quantities.getQuantity(value, unit));
        } else {
            return new SectionConcentration(RegionCache.get(region), subsection, entity, Quantities.getQuantity(value, unit));
        }
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSubsection() {
        return subsection;
    }

    public void setSubsection(String subsection) {
        this.subsection = subsection;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Unit<MolarConcentration> getUnit() {
        return unit;
    }

    public void setUnit(Unit<MolarConcentration> unit) {
        this.unit = unit;
    }
}
