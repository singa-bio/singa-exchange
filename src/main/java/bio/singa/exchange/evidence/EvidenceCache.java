package bio.singa.exchange.evidence;

import bio.singa.features.model.Evidence;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author cl
 */
public class EvidenceCache {

    private static EvidenceCache instance;

    private HashMap<String, Evidence> cache;

    private static EvidenceCache getInstance() {
        if (instance == null) {
            synchronized (EvidenceCache.class) {
                instance = new EvidenceCache();
            }
        }
        return instance;
    }

    private EvidenceCache() {
        cache = new HashMap<>();
    }

    public static void add(Evidence evidence) {
        getInstance().cache.put(evidence.getIdentifier(), evidence);
    }

    public static Evidence get(String identifier) {
        return getInstance().cache.get(identifier);
    }

    public static Collection<Evidence> getAll() {
        return getInstance().cache.values();
    }

    public static void clear() {
        getInstance().cache.clear();
    }
}
