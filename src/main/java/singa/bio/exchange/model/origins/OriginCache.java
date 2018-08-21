package singa.bio.exchange.model.origins;

import bio.singa.features.model.FeatureOrigin;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author cl
 */
public class OriginCache {

    private static OriginCache instance;

    private HashMap<String, FeatureOrigin> cache;

    private static OriginCache getInstance() {
        if (instance == null) {
            synchronized (OriginCache.class) {
                instance = new OriginCache();
            }
        }
        return instance;
    }

    private OriginCache() {
        cache = new HashMap<>();
    }

    public static void add(FeatureOrigin origin) {
        getInstance().cache.put(origin.getName(), origin);
    }

    public static FeatureOrigin get(String identifier) {
        return getInstance().cache.get(identifier);
    }

    public static Collection<FeatureOrigin> getAll() {
        return getInstance().cache.values();
    }

    public static void clear() {
        getInstance().cache.clear();
    }
}
