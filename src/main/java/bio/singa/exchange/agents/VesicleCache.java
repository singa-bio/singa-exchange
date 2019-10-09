package bio.singa.exchange.agents;

import bio.singa.simulation.model.agents.pointlike.Vesicle;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author cl
 */
public class VesicleCache {

    private static VesicleCache instance;

    private HashMap<String, Vesicle> cache;

    private static VesicleCache getInstance() {
        if (instance == null) {
            synchronized (VesicleCache.class) {
                instance = new VesicleCache();
            }
        }
        return instance;
    }

    private VesicleCache() {
        cache = new HashMap<>();
    }

    public static void add(Vesicle vesicle) {
        getInstance().cache.put(vesicle.getStringIdentifier(), vesicle);
    }

    public static Vesicle get(String identifier) {
        return getInstance().cache.get(identifier);
    }

    public static Collection<Vesicle> getAll() {
        return getInstance().cache.values();
    }

    public static boolean contains(String vesicle) {
        return getInstance().cache.containsKey(vesicle);
    }

}
