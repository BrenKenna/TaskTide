/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository;

import jakarta.persistence.EntityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;


/**
 * JPA Entity Manager repository allowing subclasses to be persisted
 *  to Relational backends (ex Postgres, MySQL etc) 
 * 
 * @author bkenna
 * @param <T> of {@link TaskTideRepository} for {@link TaskTideModel}
 */
public abstract class JpaRepository<T extends TaskTideModel<T>> implements TaskTideRepository<T> {

    // Attributes
    protected final EntityManager entityManager;
    protected final Class<T> COLLECTION_CLASS;
    protected final String collectionName;
    protected final RepositoryType repoType;

    
    /**
     * Construct with target model class, and collection
     * 
     * @param entityManager
     * @param clazz
     * @param collectionName
     */
    public JpaRepository(EntityManager entityManager, Class<T> clazz, String collectionName) {
        this.entityManager = entityManager;
        this.COLLECTION_CLASS = clazz;
        this.collectionName = collectionName;
        this.repoType = RepositoryType.SQL;
    }

    
    /**
     * Provides a map of the reposiotry meta data reference class,
     *  repository type (NoSQL, JPA etc), and collection name
     * 
     * @return Map-String, String
     */
    @Override
    public Map<String, String> getRepositoryMetaData() {
        
        // Initialize results
        Map<String, String> results = new HashMap<>();
        
        // Append data
        results.put("Model Class", this.COLLECTION_CLASS.getSimpleName());
        results.put("Repository Type", this.repoType.toString());
        results.put("Collection Name", this.collectionName);
        
        // Return results
        return results;
    }
    
    
    /**
     * Find the {@link TaskTideModel} having id
     * 
     * @param id
     * @return Optional-T
     */
    @Override
    public Optional<T> findById(String id) {
        T result = entityManager.find(COLLECTION_CLASS, id);
        return Optional.ofNullable(result);
    }

    
    /**
     * Insert record
     * 
     * @param model
     * @return T
     */
    @Override
    public T insertModel(T model) {
        entityManager.persist(model);
        return entityManager.find(COLLECTION_CLASS, model.getId());
    }

    
    /**
     * Update provided model
     * 
     * @param model
     * @return T
     */
    @Override
    public T updateModel(T model) {
        return entityManager.merge(model);
    }

    
    /**
     * Remove model with provided id
     * 
     * @param id
     * @return boolean
     */
    @Override
    public boolean deleteModel(String id) {
        Optional<T> result = this.findById(id);
        result.ifPresent(
            elm -> entityManager.remove(elm)
        );
        return result.isPresent();
    }

    
    /**
     * Find records where provided field '==' value
     * 
     * @param field
     * @param value
     * @return List-T
     */
    @Override
    public List<T> findByField(String field, Object value) {
        String query = String.format(
            "SELECT e FROM %s e WHERE e.%s = :value",
            COLLECTION_CLASS, field
        );
        return entityManager
            .createQuery(query, COLLECTION_CLASS)
            .setParameter("value", value)
        .getResultList();
    }

    
    /**
     * Find {@link TaskTideModel} from backend with field and group
     *  having specified value. Step = Name, State = ToDo
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @return List-{@link TaskTideModel} 
     */
    @Override
    public List<T> findByFieldForGroup(String field, Object value, String group, Object groupVal) {
        String query = String.format(
            "SELECT e FROM %s e WHERE e.%s = :value AND e.%s = :groupVal",
            COLLECTION_CLASS, field, group
        );
        return entityManager
            .createQuery(query, COLLECTION_CLASS)
            .setParameter("value", value)
            .setParameter("groupVal", groupVal)
        .getResultList();
    }

    
    /**
     * Fetch all records
     * 
     * @return List-T 
     */
    @Override
    public List<T> findAll() {
        return entityManager
            .createQuery(
                String.format("SELECT e FROM %s s", COLLECTION_CLASS.getSimpleName()),
                    COLLECTION_CLASS
            )
        .getResultList();
    }

    
    /**
     * Persist all records
     * 
     * @return int
     */
    @Override
    public int save() {
        entityManager.flush();
        return (int) countRecords();
    }

    
    /**
     * Count records
     * 
     * @return long
     */
    public long countRecords() {
        String query = String.format(
            "SELECT COUNT(DISTINCT e.id) FROM %s e",
            COLLECTION_CLASS.getSimpleName()
        );
        return entityManager
            .createQuery(query, Long.class)
        .getSingleResult();
    }
    
    
    /**
     * Fetch all records
     * 
     * @return List-T
     */
    @Override
    public List<T> load() {
        return this.findAll();
    }

    
    /**
     * Insert provided records
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public boolean extendModel(List<T> toAdd) {
        int count = 0;
        for ( T elm : toAdd ) {
            if ( this.insertModel(elm) != null ) {
                count++;
            }
        }
        return count == toAdd.size();
    }
}
