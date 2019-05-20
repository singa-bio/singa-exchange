package singa.bio.exchange.model.sections;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.concentration.InitialConcentration;
import bio.singa.simulation.model.sections.concentration.MembraneConcentration;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.entities.EntityCache;
import singa.bio.exchange.model.entities.EntityRepresentation;
import singa.bio.exchange.model.evidence.EvidenceCache;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Unit;
import javax.measure.quantity.Area;

/**
 * @author cl
 */
public class MembraneConcentrationRepresentation extends InitialConcentrationRepresentation<Double> {

    @JsonProperty("area-value")
    private double areaValue;

    @JsonProperty("area-unit")
    private Unit<Area> areaUnit;

    @JsonProperty("number-of-molecules")
    private double numberOfMolecules;

    public MembraneConcentrationRepresentation() {

    }

    public static MembraneConcentrationRepresentation of(MembraneConcentration initialConcentration) {
        MembraneConcentrationRepresentation representation = new MembraneConcentrationRepresentation();
        representation.setRegion(RegionRepresentation.of(initialConcentration.getRegion()).getIdentifier());
        representation.setEntity(EntityRepresentation.of(initialConcentration.getEntity()).getPrimaryIdentifier());
        if (initialConcentration.getArea() != null) {
            representation.setAreaValue(initialConcentration.getArea().getValue().doubleValue());
            representation.setAreaUnit(initialConcentration.getArea().getUnit());
        }
        representation.setNumberOfMolecules(initialConcentration.getNumberOfMolecules());
        representation.addEvidence(initialConcentration.getEvidence());
        return representation;
    }

    @Override
    public InitialConcentration toModel() {
        ChemicalEntity entity = EntityCache.get(getEntity());
        CellRegion region = RegionCache.get(getRegion());
        Evidence evidence = EvidenceCache.get(getEvidence());
        if (areaUnit == null) {
            return new MembraneConcentration(region, entity, null, numberOfMolecules, evidence);
        }
        return new MembraneConcentration(region, entity, Quantities.getQuantity(areaValue, areaUnit), numberOfMolecules, evidence);
    }

    public double getAreaValue() {
        return areaValue;
    }

    public void setAreaValue(double areaValue) {
        this.areaValue = areaValue;
    }

    public Unit<Area> getAreaUnit() {
        return areaUnit;
    }

    public void setAreaUnit(Unit<Area> areaUnit) {
        this.areaUnit = areaUnit;
    }

    public double getNumberOfMolecules() {
        return numberOfMolecules;
    }

    public void setNumberOfMolecules(double numberOfMolecules) {
        this.numberOfMolecules = numberOfMolecules;
    }

}