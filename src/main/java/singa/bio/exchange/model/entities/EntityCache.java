package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.parameters.Environment;

import java.util.HashMap;

/**
 * @author cl
 */
public class EntityCache {

    private static EntityCache instance;

    private HashMap<String, ChemicalEntity> cache;

    private static EntityCache getInstance() {
        if (instance == null) {
            synchronized (Environment.class) {
                instance = new EntityCache();
            }
        }
        return instance;
    }

    public EntityCache() {
        cache = new HashMap<>();
    }

    static void add(ChemicalEntity entity) {
        getInstance().cache.put(entity.getIdentifier().toString(), entity);
    }

    static ChemicalEntity get(String identifier) {
        return getInstance().cache.get(identifier);
    }

}
