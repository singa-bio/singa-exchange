package bio.singa.exchange.sbml.converter;

import bio.singa.features.units.UnitRegistry;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.UnitDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bio.singa.exchange.units.UnitCache;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Unit;
import javax.measure.quantity.Time;

import static tech.units.indriya.AbstractUnit.ONE;
import static tech.units.indriya.unit.Units.*;

/**
 * Converts JSBML Units to UnitsOfMeasurement Units.
 *
 * @author cl
 */
public class SBMLUnitConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLUnitConverter.class);

    public SBMLUnitConverter() {
    }

    public static void convert(ListOf<UnitDefinition> sbmlUnits) {
        logger.info("Parsing units ...");
        for (UnitDefinition unitDefinition : sbmlUnits) {
            UnitCache.add(unitDefinition.getId(), convertUnit(unitDefinition));
        }
        determineSystemUnits();
    }

    private static void determineSystemUnits() {
        // first length
        for (Unit value : UnitCache.getAll().values()) {
            if (UnitRegistry.isLengthUnit(value)) {
                UnitRegistry.setSpaceUnit(value);
                break;
            }

        }
        // then time
        for (Unit value : UnitCache.getAll().values()) {
            if (UnitRegistry.isTimeUnit(value)) {
                UnitRegistry.setTimeUnit(value);
                break;
            }
            if (UnitRegistry.isInverseTimeUnit(value)) {
                UnitRegistry.setTimeUnit((Unit<Time>) ONE.divide(value));
                break;
            }
        }
        // then substance
        for (Unit value : UnitCache.getAll().values()) {
            if (UnitRegistry.isSubstanceUnit(value)) {
                UnitRegistry.setUnit(value);
                break;
            }
        }
        // finally concentration
        for (Unit value : UnitCache.getAll().values()) {
            if (UnitRegistry.isConcentrationUnit(value)) {
                UnitRegistry.setUnit(value);
                break;
            }
        }
    }

    public static Unit convertUnit(UnitDefinition unitDefinition) {
        Unit resultUnit = new ProductUnit();
        for (org.sbml.jsbml.Unit sbmlUnit : unitDefinition.getListOfUnits()) {
            Unit unitComponent = getUnitForKind(sbmlUnit.getKind());
            UnitPrefix prefix = UnitPrefix.getUnitPrefixFromScale(sbmlUnit.getScale());
            if (prefix != null) {
//                unitComponent = unitComponent.transform(prefix);
            }
            if (unitComponent.equals(SECOND) && sbmlUnit.getMultiplier() == 60.0) {
                unitComponent = MINUTE;
            } else {
                unitComponent = unitComponent.multiply(sbmlUnit.getMultiplier());
            }
            unitComponent = unitComponent.pow((int) sbmlUnit.getExponent());
            resultUnit = resultUnit.multiply(unitComponent);
        }
        logger.debug("Parsed unit {},", resultUnit.toString());
        return resultUnit;
    }

    private static Unit<?> getUnitForKind(org.sbml.jsbml.Unit.Kind kind) {
        switch (kind) {
            case AMPERE:
                return AMPERE;
            case AVOGADRO:
                return ONE.multiply(6.022140857E23);
            case BECQUEREL:
                return BECQUEREL;
            case CANDELA:
                return CANDELA;
            case CELSIUS:
                return CELSIUS;
            case COULOMB:
                return COULOMB;
            case FARAD:
                return FARAD;
            case GRAM:
                return GRAM;
            case GRAY:
                return GRAY;
            case HENRY:
                return HENRY;
            case HERTZ:
                return HERTZ;
            case JOULE:
                return JOULE;
            case KATAL:
                return KATAL;
            case KELVIN:
                return KELVIN;
            case KILOGRAM:
                return KILOGRAM;
            case LITER:
            case LITRE:
                return LITRE;
            case LUMEN:
                return LUMEN;
            case LUX:
                return LUX;
            case METER:
            case METRE:
                return METRE;
            case MOLE:
                return MOLE;
            case NEWTON:
                return NEWTON;
            case OHM:
                return OHM;
            case PASCAL:
                return PASCAL;
            case RADIAN:
                return RADIAN;
            case SECOND:
                return SECOND;
            case SIEMENS:
                return SIEMENS;
            case SIEVERT:
                return SIEVERT;
            case STERADIAN:
                return STERADIAN;
            case TESLA:
                return TESLA;
            case VOLT:
                return VOLT;
            case WATT:
                return WATT;
            case WEBER:
                return WEBER;
            case DIMENSIONLESS:
            default:
                return ONE;
        }
    }

}
