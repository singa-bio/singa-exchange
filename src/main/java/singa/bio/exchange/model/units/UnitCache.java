package singa.bio.exchange.model.units;


import javax.measure.Unit;
import java.util.HashMap;

/**
 * @author cl
 */
public class UnitCache {

    private static UnitCache instance;

    private HashMap<String, Unit> cache;

    private static UnitCache getInstance() {
        if (instance == null) {
            synchronized (UnitCache.class) {
                instance = new UnitCache();
            }
        }
        return instance;
    }

    private UnitCache() {
        cache = new HashMap<>();
    }

    public static void add(String identifier, Unit unit) {
        getInstance().cache.put(identifier, unit);
    }

    public static Unit get(String identifier) {
        return getInstance().cache.get(identifier);
    }

    public static HashMap<String, Unit> getAll() {
        return getInstance().cache;
    }

}
