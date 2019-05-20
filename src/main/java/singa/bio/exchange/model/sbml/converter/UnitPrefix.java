package singa.bio.exchange.model.sbml.converter;


import tech.units.indriya.unit.MetricPrefix;

/**
 * The unit prefixes required for SBML. This class provides a mapping between SBML and units of measurement.
 */
public enum UnitPrefix {

    TERA(12, "T", MetricPrefix.TERA),
    GIGA(9, "G", MetricPrefix.GIGA),
    MEGA(6, "M", MetricPrefix.MEGA),
    KILO(3, "k", MetricPrefix.KILO),
    HECTO(2, "h", MetricPrefix.HECTO),
    DECA(1, "da", MetricPrefix.DEKA),
    DECI(-1, "d", MetricPrefix.DECI),
    CENTI(-2, "c", MetricPrefix.CENTI),
    MILLI(-3, "m", MetricPrefix.MILLI),
    MICRO(-6, "\u00B5", MetricPrefix.MICRO),
    NANO(-9, "n", MetricPrefix.NANO),
    PICO(-12, "p", MetricPrefix.PICO),
    FEMTO(-15, "f", MetricPrefix.FEMTO);

    private final int scale;
    private final String symbol;
    private final MetricPrefix metricPrefix;

    UnitPrefix(int scale, String symbol, MetricPrefix prefix) {
        this.scale = scale;
        this.symbol = symbol;
        this.metricPrefix = prefix;
    }

    public int getScale() {
        return scale;
    }

    public String getSymbol() {
        return symbol;
    }

    public MetricPrefix getMetricPrefix() {
        return metricPrefix;
    }

    /**
     * Gets the {@link UnitPrefix} from the exponent of an unit.
     *
     * @param exponent The exponent.
     * @return The {@link UnitPrefix}.
     */
    public static UnitPrefix getUnitPrefixFromScale(int exponent) {
        for (UnitPrefix unitPrefix : UnitPrefix.values()) {
            if (exponent == unitPrefix.getScale()) {
                return unitPrefix;
            }
        }
        return null;
    }

}
