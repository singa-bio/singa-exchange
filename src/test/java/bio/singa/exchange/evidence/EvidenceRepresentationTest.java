package bio.singa.exchange.evidence;

import bio.singa.exchange.EnumTransformation;
import bio.singa.features.model.Evidence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class EvidenceRepresentationTest {

    @Test
    @DisplayName("evidence - model to representation")
    void modelToJson() {
        Evidence evidence = new Evidence(Evidence.SourceType.DATABASE, "PDB", "Berman, Helen M., et al. \"The protein data bank.\" Nucleic acids research 28.1 (2000): 235-242.");
        EvidenceRepresentation representation = EvidenceRepresentation.of(evidence);

        assertEquals(evidence.getIdentifier(), representation.getIdentifier());
        assertEquals(evidence.getDescription(), representation.getDescription());
        Assertions.assertEquals(EnumTransformation.fromSourceType(evidence.getType()), representation.getType());
    }

    @Test
    @DisplayName("evidence - representation to model")
    void jsonToModel() {
        EvidenceRepresentation representation = new EvidenceRepresentation();
        representation.setType(EnumTransformation.fromSourceType(Evidence.SourceType.DATABASE));
        representation.setIdentifier("PDB");
        representation.setDescription("Berman, Helen M., et al. \"The protein data bank.\" Nucleic acids research 28.1 (2000): 235-242.");
        Evidence evidence = representation.toModel();

        assertEquals(evidence.getIdentifier(), representation.getIdentifier());
        assertEquals(evidence.getDescription(), representation.getDescription());
        assertEquals(EnumTransformation.fromSourceType(evidence.getType()), representation.getType());
    }
}
