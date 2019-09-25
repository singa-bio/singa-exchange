package singa.bio.exchange.model.variation;

import bio.singa.simulation.model.simulation.Updatable;
import singa.bio.exchange.model.agents.VesicleCache;
import singa.bio.exchange.model.graphs.automaton.NodeCache;

/**
 * @author cl
 */
public class UpdatableCacheManager {

    public static Updatable get(String updatable) {
        if (NodeCache.contains(updatable)) {
            return NodeCache.get(updatable);
        } else if (VesicleCache.contains(updatable)) {
            return VesicleCache.get(updatable);
        }
        return null;
    }

    public static boolean isAvailable(String updatable) {
        return NodeCache.contains(updatable) || VesicleCache.contains(updatable);
    }

}
