package bio.singa.exchange.sbml.converter;

import bio.singa.simulation.model.rules.AssignmentRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cl
 */
public class SBMLAssignmentRuleConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLAssignmentRuleConverter.class);

    public SBMLAssignmentRuleConverter() {
    }

    public AssignmentRule convertAssignmentRule(org.sbml.jsbml.AssignmentRule sbmlAssignmentRule) {
//        String unitIdentifier = sbmlAssignmentRule.getDerivedUnitDefinition().getId();
//        Unit<?> parameterUnit;
//        if (unitIdentifier.equalsIgnoreCase("dimensionless") || unitIdentifier.isEmpty()) {
//            parameterUnit = ONE;
//        } else {
//            parameterUnit = units.get(unitIdentifier);
//        }
//        final AppliedExpression appliedExpression = expressionConverter.convertRawExpression(sbmlAssignmentRule.getMath(), parameterUnit);
//        final ChemicalEntity targetEntity = entities.get(sbmlAssignmentRule.getVariable());
//        AssignmentRule assignmentRule = new AssignmentRule(targetEntity, appliedExpression);
//        // find referenced entities
//        for (String identifier : entities.keySet()) {
//            Pattern pattern = Pattern.compile("(\\W|^)(" + identifier + ")(\\W|$)");
//            Matcher matcher = pattern.matcher(sbmlAssignmentRule.getMath().toString());
//            if (matcher.find()) {
//                assignmentRule.referenceChemicalEntityToParameter(identifier, entities.get(identifier));
//            }
//        }
//        return assignmentRule;
        return null;
    }

}
