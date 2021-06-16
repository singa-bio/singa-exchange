package bio.singa.exchange.variation;

import bio.singa.exchange.graphs.NodeCache;
import bio.singa.simulation.model.simulation.Updatable;
import bio.singa.exchange.agents.VesicleCache;

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

}
