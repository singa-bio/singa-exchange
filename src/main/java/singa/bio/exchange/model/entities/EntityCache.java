package singa.bio.exchange.model.entities;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;

import java.util.Collection;
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

    public static ChemicalEntity draft(String identifier) {
        if (getInstance().cache.containsKey(identifier)) {
            return get(identifier);
        }
        return SmallMolecule.create(identifier).build();
    }

    public static Collection<ChemicalEntity> getAll() {
        return getInstance().cache.values();
    }

    public static boolean contains(String entity) {
        return getInstance().cache.containsKey(entity);
    }

}
