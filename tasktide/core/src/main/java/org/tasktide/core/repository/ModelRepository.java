/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.nosql.document.DocumentTemplate;
import java.util.List;
import java.util.Optional;

import org.tasktide.core.supporting.Utils;


/**
 * 
 * DocumentTemplate Model Repository adding utility and collection info
 * 
 * @author bkenna
 */
@ApplicationScoped
public abstract class ModelRepository<T> implements RepositoryInterface<T> {
    
    @Inject
    protected DocumentTemplate template;
    protected final Class<T> COLLECTION_CLASS;
    protected final String collectionName;
    
    protected final Utils utils = new Utils();
    protected final int LOCKING_WAIT_TIME = 4;
    
    
    /**
     * Construct with target model class, and collection name
     * 
     * @param modelClass
     * @param collectionName 
     */
    public ModelRepository(Class<T> modelClass, String collectionName) {
        this.COLLECTION_CLASS = modelClass;
        this.collectionName = collectionName;
    }
    
    
    /**
     * Fetch WorkItem by its Id
     * 
     * @param id
     * @return WorkItem
     */
    public Optional<T> findById(String id) {
        return template.find(COLLECTION_CLASS, id);
    }

    
    /**
     * Insert model into DB
     * 
     * @param model
     * @return T-Model
     */
    @Override
    public T insertModel(T model) {
        return template.insert(model);
    }

    
    /**
     * Update model
     * 
     * @param model
     * @return T-Model
     */
    @Override
    public T updateModel(T model) {
        return template.update(model);
    }

    
    /**
     * Delete model if present
     * 
     * @param id
     * @return boolean
     */
    @Override
    public boolean deleteModel(String id) {
        template.delete(COLLECTION_CLASS, id);
        return findById(id).isPresent();
    }

    
    /**
     * Generic method to find list of WorkItems by field equally value
     * 
     * @param field
     * @param value
     * @return List-WorkItem
     */
    @Override
    public List<T> findByField(String field, Object value) {
        return template.select(COLLECTION_CLASS)
                .where(field)
                .eq(value)
                .result();
    }

    
    /**
     * Fetch all records
     * 
     * @return List-T-Model
     */
    @Override
    public List<T> findAll() {
        return template.select(COLLECTION_CLASS).result();
    }
    
    
    
}
