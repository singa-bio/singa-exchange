package bio.singa.exchange.variation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public abstract class Variable<VariableType> {

    @JsonProperty("alternative-values")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<VariableType> alternativeValues;

    public Variable() {
        alternativeValues = new ArrayList<>();
    }

    public List<VariableType> getAlternativeValues() {
        return alternativeValues;
    }

    public void setAlternativeValues(List<VariableType> alternativeValues) {
        this.alternativeValues = alternativeValues;
    }

    public void addAlternativeValue(VariableType alternativeValue) {
        this.alternativeValues.add(alternativeValue);
    }

}
