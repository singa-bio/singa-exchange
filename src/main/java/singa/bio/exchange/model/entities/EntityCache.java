package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ChemicalEntity;

import java.util.HashMap;

/**
 * @author cl
 */
public class EntityCache {

    private static EntityCache instance;

    private HashMap<String, ChemicalEntity> cache;

    private static EntityCache getInstance() {
        if (instance == null) {
            synchronized (EntityCache.class) {
                instance = new EntityCache();
            }
        }
        return instance;
    }

    public EntityCache() {
        cache = new HashMap<>();
    }

    public static void add(ChemicalEntity entity) {
        getInstance().cache.put(entity.getIdentifier().toString(), entity);
    }

    public static ChemicalEntity get(String identifier) {
        return getInstance().cache.get(identifier);
    }

}
