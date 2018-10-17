package singa.bio.exchange.model.sbml.converter;

import bio.singa.simulation.model.modules.concentration.reactants.KineticLaw;
import bio.singa.simulation.model.parameters.ParameterStorage;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class SBMLFunctionConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLFunctionConverter.class);

    // the functions
    private static final Map<String, FunctionReference> functions = new HashMap<>();

    private KineticLaw currentExpression;

    public static void convert(ListOf<FunctionDefinition> listOfFunctions) {
        convertFunctionList(listOfFunctions);
    }

    public static void convertFunctionList(ListOf<FunctionDefinition> listOfFunctions) {
        for (FunctionDefinition function : listOfFunctions) {
            functions.put(function.getId(), new FunctionReference(function.getId(), function.getMath().toString()));
        }
    }

    public static KineticLaw convertKineticLaw(ASTNode sbmlExpression, ListOf<LocalParameter> additionalParameters) {
        SBMLFunctionConverter converter = new SBMLFunctionConverter();
        return converter.convertRawExpression(sbmlExpression, additionalParameters);
    }

    public KineticLaw convertRawExpression(ASTNode sbmlExpression, ListOf<LocalParameter> additionalParameters) {
        String expressionString = replaceFunction(sbmlExpression.toString());
        currentExpression = new KineticLaw(expressionString);
        assignLocalParameters(additionalParameters);
        assignGlobalParameters(expressionString);
        return currentExpression;
    }

    public KineticLaw convertRawExpression(ASTNode sbmlExpression) {
        String expressionString = replaceFunction(sbmlExpression.toString());
        currentExpression = new KineticLaw(expressionString);
        assignGlobalParameters(expressionString);
        return currentExpression;
    }

    private String replaceFunction(String kineticLawString) {
        String replacedString = kineticLawString;
        for (String functionIdentifier : functions.keySet()) {
            if (kineticLawString.contains(functionIdentifier)) {
                replacedString = functions.get(functionIdentifier).replaceInEquation(replacedString);
            }
        }
        return replacedString;
    }

    private void assignLocalParameters(ListOf<LocalParameter> additionalParameters) {
        for (LocalParameter parameter : additionalParameters) {
            currentExpression.referenceParameter(SBMLParameterConverter.convertLocalParameter(parameter));
        }
    }

    private void assignGlobalParameters(String kineticLawString) {
        for (String primaryIdentifier : ParameterStorage.getAll().keySet()) {
            Pattern pattern = Pattern.compile("(\\W|^)(" + primaryIdentifier + ")(\\W|$)");
            Matcher matcher = pattern.matcher(kineticLawString);
            if (matcher.find()) {
                currentExpression.referenceParameter(ParameterStorage.get(primaryIdentifier));
            }
        }
    }

}
