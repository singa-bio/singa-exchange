package bio.singa.exchange.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.features.diffusivity.ConcentrationDiffusivity;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.exchange.sections.RegionCache;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.Feature;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellRegions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class FeatureRepresentationTest {

    private static List<Evidence> evidences;
    private static ChemicalEntity dynein;
    private static CellRegion region;
    private static Protein vamp3;
    private static Protein vamp2;

    @BeforeAll
    static void initialize() {
        evidences = new ArrayList<>();
        evidences.add(new Evidence(Evidence.SourceType.LITERATURE, "test 1", "test 1 publication"));
        evidences.add(new Evidence(Evidence.SourceType.LITERATURE, "test 2", "test 2 publication"));

        dynein = Protein.create("Dynein").build();
        vamp2 = Protein.create("VAMP2").build();
        vamp3 = Protein.create("VAMP3").build();

        region = CellRegions.CYTOPLASM_REGION;
        RegionCache.add(region);
    }

    @Test
    @DisplayName("features - quantitative feature to json")
    void quantitativeModelToJson() {
        ConcentrationDiffusivity feature = new ConcentrationDiffusivity(Quantities.getQuantity(1.0, SQUARE_CENTIMETRE_PER_SECOND), evidences);
        FeatureRepresentation representation = FeatureRepresentation.of(feature);

        assertEquals(feature.getClass().getSimpleName(), representation.getName());
        assertEquals(feature.getContent().getValue().doubleValue(), ((QuantitativeFeatureRepresentation) representation).getQuantity());
        assertEquals(feature.getContent().getUnit(), ((QuantitativeFeatureRepresentation) representation).getUnit());
        assertEquals(feature.getAllEvidence().size(), representation.getEvidence().size());
    }

    @Test
    @DisplayName("features - quantitative feature to model")
    void quantitativeJsonToModel() {
        QuantitativeFeatureRepresentation representation = new QuantitativeFeatureRepresentation();
        representation.setName(Diffusivity.class.getSimpleName());
        representation.setQuantity(1.0);
        representation.setUnit(SQUARE_CENTIMETRE_PER_SECOND);
        representation.addEvidence(evidences);
        Feature feature = representation.toModel();

        assertEquals(representation.getName(), feature.getClass().getSimpleName());
        assertEquals(representation.getQuantity(), ((Diffusivity) feature).getValue().doubleValue());
        assertEquals(representation.getUnit(), ((Diffusivity) feature).getUnit());
        assertEquals(representation.getEvidence().size(), feature.getAllEvidence().size());
    }

    @Test
    @DisplayName("features - string based feature to json")
    void qualitativeStringModelToJson() {
        SimpleStringIdentifier feature = new SimpleStringIdentifier("TesT");
        FeatureRepresentation representation = FeatureRepresentation.of(feature);

        assertEquals(feature.getClass().getSimpleName(), representation.getName());
        assertEquals(feature.getContent(), ((QualitativeFeatureRepresentation) representation).fetchContent());
        assertEquals(1, representation.getEvidence().size());
    }

    @Test
    @DisplayName("features - string based feature to model")
    void qualitativeStringJsonToModel() {
        QualitativeFeatureRepresentation representation = new QualitativeFeatureRepresentation();
        representation.setName(SimpleStringIdentifier.class.getSimpleName());
        representation.setContent("TesT");
        representation.addEvidence(evidences);
        Feature feature = representation.toModel();

        assertEquals(representation.getName(), feature.getClass().getSimpleName());
        assertEquals(representation.fetchContent(), feature.getContent().toString());
        assertEquals(representation.getEvidence().size(), feature.getAllEvidence().size());
    }

    @Test
    @DisplayName("features - multi string based feature to json")
    void qualitativeMultiStringModelToJson() {
        BlackListVesicleStates feature = new BlackListVesicleStates(Arrays.asList(VesicleStateRegistry.MEMBRANE_TETHERED, VesicleStateRegistry.TETHERED));
        FeatureRepresentation representation = FeatureRepresentation.of(feature);

        assertEquals(feature.getClass().getSimpleName(), representation.getName());
        assertEquals(feature.getContent(), ((MultiStringFeatureRepresentation) representation).getStrings());
        assertEquals(1, representation.getEvidence().size());
    }

    @Test
    @DisplayName("features - multi string based feature to model")
    void qualitativeMultiStringJsonToModel() {
        MultiStringFeatureRepresentation representation = new MultiStringFeatureRepresentation();
        representation.setName(BlackListVesicleStates.class.getSimpleName());
        representation.addString(VesicleStateRegistry.ACTIN_PROPELLED);
        representation.addEvidence(evidences);
        Feature feature = representation.toModel();

        assertEquals(representation.getName(), feature.getClass().getSimpleName());
        assertEquals(String.join(",", representation.getStrings()), String.join(",", ((MultiStringFeature) feature).getContent()));
        assertEquals(representation.getEvidence().size(), feature.getAllEvidence().size());
    }


    @Test
    @DisplayName("features - entity based feature to json")
    void qualitativeEntityModelToJson() {
        AttachedMotor feature = new AttachedMotor(dynein, evidences);
        FeatureRepresentation representation = FeatureRepresentation.of(feature);

        assertEquals(feature.getClass().getSimpleName(), representation.getName());
        assertEquals(feature.getContent(), EntityRegistry.get(((QualitativeFeatureRepresentation) representation).fetchContent()));
        assertEquals(feature.getAllEvidence().size(), representation.getEvidence().size());
    }

    @Test
    @DisplayName("features - entity based feature to model")
    void qualitativeEntityJsonToModel() {
        QualitativeFeatureRepresentation representation = new QualitativeFeatureRepresentation();
        representation.setName(AttachedMotor.class.getSimpleName());
        representation.setContent("Dynein");
        representation.addEvidence(evidences);
        Feature feature = representation.toModel();

        assertEquals(representation.getName(), feature.getClass().getSimpleName());
        assertEquals(EntityRegistry.get(representation.fetchContent()), feature.getContent());
        assertEquals(representation.getEvidence().size(), feature.getAllEvidence().size());
    }

    @Test
    @DisplayName("features - multi entity based feature to json")
    void qualitativeMultiEntityModelToJson() {
        MatchingQSnares feature = new MatchingQSnares(Arrays.asList(vamp2, vamp3));
        FeatureRepresentation representation = FeatureRepresentation.of(feature);

        assertEquals(feature.getClass().getSimpleName(), representation.getName());
        assertEquals(feature.getContent().size(), ((MultiEntityFeatureRepresentation) representation).getEntities().size());
        assertEquals(1, representation.getEvidence().size());
    }

    @Test
    @DisplayName("features - multi entity based feature to model")
    void qualitativeMultiEntityJsonToModel() {
        MultiEntityFeatureRepresentation representation = new MultiEntityFeatureRepresentation();
        representation.setName(MatchingQSnares.class.getSimpleName());
        representation.addEntity(vamp2.getIdentifier());
        representation.addEntity(vamp3.getIdentifier());
        Feature feature = representation.toModel();

        assertEquals(representation.getName(), feature.getClass().getSimpleName());
        assertEquals(representation.getEntities().size(), ((MultiEntityFeature) feature).getContent().size());
        assertEquals(representation.getEvidence().size(), feature.getAllEvidence().size());
    }

    @Test
    @DisplayName("features - region based feature to json")
    void qualitativeRegionModelToJson() {
        AffectedRegion feature = new AffectedRegion(region);
        FeatureRepresentation representation = FeatureRepresentation.of(feature);

        assertEquals(feature.getClass().getSimpleName(), representation.getName());
        assertEquals(feature.getContent(), RegionCache.get(((QualitativeFeatureRepresentation) representation).fetchContent()));
        assertEquals(1, representation.getEvidence().size());
    }

    @Test
    @DisplayName("features - region based feature to model")
    void qualitatitveRegionJsonToModel() {
        QualitativeFeatureRepresentation representation = new QualitativeFeatureRepresentation();
        representation.setName(AffectedRegion.class.getSimpleName());
        representation.setContent("cytoplasm");
        representation.addEvidence(evidences);
        Feature feature = representation.toModel();

        assertEquals(representation.getName(), feature.getClass().getSimpleName());
        assertEquals(RegionCache.get(representation.fetchContent()), feature.getContent());
        assertEquals(representation.getEvidence().size(), feature.getAllEvidence().size());
    }


}