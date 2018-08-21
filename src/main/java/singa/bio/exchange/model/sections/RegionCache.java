package singa.bio.exchange.model.sections;

import bio.singa.simulation.model.sections.CellRegion;
import singa.bio.exchange.model.IllegalConversionException;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author cl
 */
public class RegionCache {

    private static RegionCache instance;

    private HashMap<String, CellRegion> cache;

    private static RegionCache getInstance() {
        if (instance == null) {
            synchronized (RegionCache.class) {
                instance = new RegionCache();
            }
        }
        return instance;
    }

    private RegionCache() {
        cache = new HashMap<>();
    }

    public static void add(CellRegion region) {
        getInstance().cache.put(region.getIdentifier(), region);
    }

    public static CellRegion get(String identifier) {
        return getInstance().cache.get(identifier);
    }

    public static CellRegion getInner(String subsectionIdentifier) {
        for (CellRegion value : getInstance().cache.values()) {
            if (!value.hasMembrane()) {
                if (value.getInnerSubsection().getIdentifier().equals(subsectionIdentifier)) {
                    return value;
                }
            }
        }
        throw new IllegalConversionException("Unable to get cell region for inner subsection " +subsectionIdentifier);
    }

    public static Collection<CellRegion> getAll() {
        return getInstance().cache.values();
    }

    public static void clear() {
        getInstance().cache.clear();
    }
}
