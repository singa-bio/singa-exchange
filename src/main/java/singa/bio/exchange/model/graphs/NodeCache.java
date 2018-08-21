package singa.bio.exchange.model.graphs;

import bio.singa.simulation.model.graphs.AutomatonNode;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author cl
 */
public class NodeCache {

    private static NodeCache instance;

    private HashMap<String, AutomatonNode> cache;

    private static NodeCache getInstance() {
        if (instance == null) {
            synchronized (NodeCache.class) {
                instance = new NodeCache();
            }
        }
        return instance;
    }

    private NodeCache() {
        cache = new HashMap<>();
    }

    public static void add(AutomatonNode origin) {
        getInstance().cache.put(origin.getIdentifier().toString(), origin);
    }

    public static AutomatonNode get(String identifier) {
        return getInstance().cache.get(identifier);
    }

    public static Collection<AutomatonNode> getAll() {
        return getInstance().cache.values();
    }

}
