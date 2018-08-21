package singa.bio.exchange.model.sections;

import bio.singa.simulation.model.sections.CellSubsection;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author cl
 */
public class SubsectionCache {

    private static SubsectionCache instance;

    private HashMap<String, CellSubsection> cache;

    private static SubsectionCache getInstance() {
        if (instance == null) {
            synchronized (SubsectionCache.class) {
                instance = new SubsectionCache();
            }
        }
        return instance;
    }

    private SubsectionCache() {
        cache = new HashMap<>();
    }

    public static void add(CellSubsection origin) {
        getInstance().cache.put(origin.getIdentifier(), origin);
    }

    public static CellSubsection get(String identifier) {
        return getInstance().cache.get(identifier);
    }

    public static Collection<CellSubsection> getAll() {
        return getInstance().cache.values();
    }

    public static void clear() {
        getInstance().cache.clear();
    }

}
