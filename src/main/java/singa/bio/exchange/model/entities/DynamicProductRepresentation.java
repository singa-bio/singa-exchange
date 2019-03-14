package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ComplexModification;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cl
 */
@JsonPropertyOrder({ "relevant-entity", "modifications" })
public class DynamicProductRepresentation extends ReactantRepresentation {

    @JsonProperty("relevant-entity")
    private String relevantEntity;

    @JsonProperty
    private List<ModificationRepresentation> modifications;


    public DynamicProductRepresentation() {
        modifications = new ArrayList<>();
    }

    public static DynamicProductRepresentation dynamicComplex() {
        DynamicProductRepresentation representation = new DynamicProductRepresentation();
        representation.relevantEntity = "[complex]";
        representation.modifications = Collections.emptyList();
        return representation;
    }

    public static DynamicProductRepresentation of(String relevantEntity, List<ComplexModification> modifications) {
        DynamicProductRepresentation representation = new DynamicProductRepresentation();
        representation.setRelevantEntity(relevantEntity);
        for (ComplexModification modification : modifications) {
            representation.getModifications().add(ModificationRepresentation.of(modification));
        }
        return representation;
    }

    public String getRelevantEntity() {
        return relevantEntity;
    }

    public void setRelevantEntity(String relevantEntity) {
        this.relevantEntity = relevantEntity;
    }

    public List<ModificationRepresentation> getModifications() {
        return modifications;
    }

    public void setModifications(List<ModificationRepresentation> modifications) {
        this.modifications = modifications;
    }
}
