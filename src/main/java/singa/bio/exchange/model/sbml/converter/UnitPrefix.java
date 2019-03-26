package singa.bio.exchange.model.sbml.converter;


import tec.uom.se.AbstractConverter;
import tec.uom.se.unit.MetricPrefix;

import javax.measure.UnitConverter;

/**
 * The unit prefixes required for SBML. This class provides a mapping between SBML and units of measurement.
 */
public enum UnitPrefix {

    TERA(12, "T", MetricPrefix.TERA.getConverter()),
    GIGA(9, "G", MetricPrefix.GIGA.getConverter()),
    MEGA(6, "M", MetricPrefix.MEGA.getConverter()),
    KILO(3, "k", MetricPrefix.KILO.getConverter()),
    HECTO(2, "h", MetricPrefix.HECTO.getConverter()),
    DECA(1, "da", MetricPrefix.DEKA.getConverter()),
    DECI(-1, "d", MetricPrefix.DECI.getConverter()),
    CENTI(-2, "c", MetricPrefix.CENTI.getConverter()),
    MILLI(-3, "m", MetricPrefix.MILLI.getConverter()),
    MICRO(-6, "\u00B5", MetricPrefix.MICRO.getConverter()),
    NANO(-9, "n", MetricPrefix.NANO.getConverter()),
    PICO(-12, "p", MetricPrefix.PICO.getConverter()),
    FEMTO(-15, "f", MetricPrefix.FEMTO.getConverter()),
    NO_PREFIX(0, "", AbstractConverter.IDENTITY);

    private final int scale;
    private final String symbol;
    private final UnitConverter correspondingConverter;

    UnitPrefix(int scale, String symbol, UnitConverter correspondingConverter) {
        this.scale = scale;
        this.symbol = symbol;
        this.correspondingConverter = correspondingConverter;
    }

    public int getScale() {
        return scale;
    }

    public String getSymbol() {
        return symbol;
    }

    public UnitConverter getCorrespondingConverter() {
        return correspondingConverter;
    }

    /**
     * Gets the {@link UnitPrefix} from the exponent of an unit.
     *
     * @param exponent The exponent.
     * @return The {@link UnitPrefix}.
     */
    public static UnitPrefix getUnitPrefixFromScale(int exponent) {
        for (UnitPrefix unitPrefix : UnitPrefix.values())
            if (exponent == unitPrefix.getScale())
                return unitPrefix;
        return UnitPrefix.NO_PREFIX;
    }

}
