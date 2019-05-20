package singa.bio.exchange.model.sections;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.concentration.InitialConcentration;
import bio.singa.simulation.model.sections.concentration.SectionConcentration;
import com.fasterxml.jackson.annotation.JsonProperty;
import singa.bio.exchange.model.entities.EntityCache;
import singa.bio.exchange.model.entities.EntityRepresentation;
import singa.bio.exchange.model.evidence.EvidenceCache;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Unit;

/**
 * @author cl
 */
public class SectionConcentrationRepresentation extends InitialConcentrationRepresentation<Double> {

    @JsonProperty
    private String subsection;

    @JsonProperty("concentration-value")
    private double concentrationValue;

    @JsonProperty("concentration-unit")
    private Unit<MolarConcentration> concentrationUnit;

    public SectionConcentrationRepresentation() {

    }

    public static SectionConcentrationRepresentation of(SectionConcentration initialConcentration) {
        SectionConcentrationRepresentation representation = new SectionConcentrationRepresentation();
        if (initialConcentration.getRegion() != null) {
            representation.setRegion(RegionRepresentation.of(initialConcentration.getRegion()).getIdentifier());
        }
        representation.setSubsection(initialConcentration.getSubsection().getIdentifier());
        representation.setEntity(EntityRepresentation.of(initialConcentration.getEntity()).getPrimaryIdentifier());
        representation.setConcentrationValue(initialConcentration.getConcentration().getValue().doubleValue());
        representation.setConcentrationUnit(initialConcentration.getConcentration().getUnit());
        representation.addEvidence(initialConcentration.getEvidence());
        return representation;
    }

    @Override
    public InitialConcentration toModel() {
        CellSubsection subsection = SubsectionCache.get(getSubsection());
        ChemicalEntity entity = EntityCache.get(getEntity());
        Evidence evidence = EvidenceCache.get(getEvidence());
        String region = getRegion();
        if (region == null || region.isEmpty()) {
            SectionConcentration sectionConcentration = new SectionConcentration(subsection, entity, Quantities.getQuantity(concentrationValue, concentrationUnit));
            sectionConcentration.setEvidence(evidence);
            return sectionConcentration;
        } else {
            SectionConcentration sectionConcentration = new SectionConcentration(RegionCache.get(region), subsection, entity, Quantities.getQuantity(concentrationValue, concentrationUnit));
            sectionConcentration.setEvidence(evidence);
            return sectionConcentration;
        }
    }

    public String getSubsection() {
        return subsection;
    }

    public void setSubsection(String subsection) {
        this.subsection = subsection;
    }

    public double getConcentrationValue() {
        return concentrationValue;
    }

    public void setConcentrationValue(double concentrationValue) {
        this.concentrationValue = concentrationValue;
    }

    public Unit<MolarConcentration> getConcentrationUnit() {
        return concentrationUnit;
    }

    public void setConcentrationUnit(Unit<MolarConcentration> concentrationUnit) {
        this.concentrationUnit = concentrationUnit;
    }

}
