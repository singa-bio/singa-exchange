package singa.bio.exchange.model.sbml.converter;

import bio.singa.simulation.model.modules.concentration.reactants.KineticLaw;
import bio.singa.simulation.model.parameters.SimulationParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.sbml.FunctionReference;

import javax.measure.Unit;
import java.util.Map;

import static tec.uom.se.AbstractUnit.ONE;

/**
 * Converts JSBML KineticLaws to SiNGA KineticLaws
 *
 * @author cl
 */
public class SBMLKineticLawConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLKineticLawConverter.class);

    // requirements
    private final Map<String, Unit<?>> units;

    private final SBMLExpressionConverter expressionConverter;

    public SBMLKineticLawConverter(Map<String, Unit<?>> units, Map<String, FunctionReference> functions, Map<String, SimulationParameter<?>> globalParameters) {
        this.units = units;
        expressionConverter = new SBMLExpressionConverter(this.units, functions, globalParameters);
    }

    public KineticLaw convertKineticLaw(org.sbml.jsbml.KineticLaw sbmlKineticLaw) {
        if (!sbmlKineticLaw.getMath().toString().equals("NaN")) {
            String unitIdentifier = sbmlKineticLaw.getDerivedUnitDefinition().getId();
            Unit<?> parameterUnit;
            if (unitIdentifier.equalsIgnoreCase("dimensionless") || unitIdentifier.isEmpty()) {
                parameterUnit = ONE;
            } else {
                parameterUnit = units.get(unitIdentifier);
            }
            logger.debug("Creating kinetic law with expression {} ...", sbmlKineticLaw.getMath().toString());
            // TODO Fix dynamic expression parsing
            // AppliedExpression appliedExpression = expressionConverter.convertRawExpression(sbmlKineticLaw.getMath(), sbmlKineticLaw.getListOfLocalParameters(), parameterUnit);
            return new KineticLaw(null);
        } else {
            logger.warn("Could not parse a valid expression for this reaction.");
            return null;
        }
    }


}
