package singa.bio.exchange.model.sbml.converter;

import bio.singa.simulation.model.parameters.Parameter;
import bio.singa.simulation.model.parameters.ParameterStorage;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singa.bio.exchange.model.sbml.SBMLParser;
import singa.bio.exchange.model.units.UnitCache;
import tec.uom.se.quantity.Quantities;

import javax.measure.Unit;

import static tec.uom.se.AbstractUnit.ONE;

/**
 * Converts JSBML Parameters to SiNGA Parameters.
 *
 * @author cl
 */
public class SBMLParameterConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLParameterConverter.class);

    public static void convert(ListOf<org.sbml.jsbml.Parameter> sbmlParameters) {
        for (org.sbml.jsbml.Parameter parameter : sbmlParameters) {
            ParameterStorage.add(parameter.getId(), convertSimulationParameter(parameter));
        }
    }

    public static Parameter<?> convertSimulationParameter(org.sbml.jsbml.Parameter sbmlParameter) {
        return convertParameter(sbmlParameter.getId(), sbmlParameter.getValue(), sbmlParameter.getUnits());
    }

    public static Parameter<?> convertLocalParameter(LocalParameter sbmlLocalParameter) {
        return convertParameter(sbmlLocalParameter.getId(), sbmlLocalParameter.getValue(), sbmlLocalParameter.getUnits());
    }

    private static Parameter<?> convertParameter(String primaryIdentifier, double value, String unit) {
        Unit<?> parameterUnit;
        if (unit.equalsIgnoreCase("dimensionless") || unit.isEmpty()) {
            parameterUnit = ONE;
        } else {
            parameterUnit = UnitCache.get(unit);
        }
        Parameter<?> simulationParameter = new Parameter<>(primaryIdentifier, Quantities.getQuantity(value, parameterUnit), SBMLParser.DEFAULT_SBML_ORIGIN);
        logger.debug("Set parameter {} to {}.", simulationParameter.getIdentifier(), simulationParameter.getQuantity());
        return simulationParameter;
    }


}
