package singa.bio.exchange.model.evidence;

import bio.singa.chemistry.features.databases.chebi.ChEBIDatabase;
import bio.singa.chemistry.features.molarvolume.MolarVolumePredictor;
import bio.singa.core.utility.Resources;
import bio.singa.features.model.Evidence;
import bio.singa.simulation.features.DefaultFeatureSources;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author cl
 */
class EvidenceDatasetTest {

    @Test
    @DisplayName("evidence - dataset to json")
    void jsonConversion() {
        EvidenceDataset evidenceDataset = new EvidenceDataset();
        evidenceDataset.addEvidence(EvidenceRepresentation.of(MolarVolumePredictor.OTT1992));
        evidenceDataset.addEvidence(EvidenceRepresentation.of(ChEBIDatabase.DEGTYARENKO2008));
        evidenceDataset.addEvidence(EvidenceRepresentation.of(DefaultFeatureSources.BINESH2015));
        evidenceDataset.addEvidence(EvidenceRepresentation.of(Evidence.NO_EVIDENCE));
        try {
            String actual = evidenceDataset.toJson();
            String fileLocation = Resources.getResourceAsFileLocation("evidence_dataset.json");
            String expected = String.join("\n", Files.readAllLines(Paths.get(fileLocation)));
            assertEquals(expected, actual);
        } catch (JsonProcessingException e) {
            fail("unable to create json.", e);
        } catch (IOException e) {
            fail("unable to read test file.", e);
        }
    }
}