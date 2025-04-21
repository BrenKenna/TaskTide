/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.json_repo;

import jakarta.enterprise.context.Dependent;

import java.util.List;
import java.util.Optional;

import org.tasktide.core.repository.TaskTideModel;
import org.tasktide.core.repository.TaskTideModelRepository;


/**
 * Class to support file backend in compressed json format. All linear operations
 *
 * @author bkenna
 * @param <T> of ModelClass-WorkItem,Step,Workflow
 */
@Dependent
public abstract class JsonRepository<T extends TaskTideModel> implements TaskTideModelRepository<T> {
    
    // Attributes
    protected final List<T> modelCollection;
    protected final Class<T> COLLECTION_CLASS;
    protected final String collectionName;
    protected final Compression compUtil;
    
    
    /**
     * Construct JSON Repository for list of model classes
     * 
     * @param modelCollection
     * @param modelClass
     * @param collectionName - As FilePath
     */
    public JsonRepository(List<T> modelCollection, Class<T> modelClass, String collectionName) {
        this.modelCollection = modelCollection;
        this.COLLECTION_CLASS = modelClass;
        this.collectionName = collectionName;
        this.compUtil = new Compression();
    }
    
    
    /**
     * Find index by Id
     * 
     * @param id
     * @return int
     */
    private int indexOf(String id) {
        int counter = -1;
        while (counter < modelCollection.size()) {
            T current = modelCollection.get(counter);
            if ( current.getId().equals(id) ) {
                return counter;
            }
            counter++;
        }
        return -1;
    }
    
    
    /**
     * Search for Items by Id
     * 
     * @param id
     * @return WorkItem
     */
    @Override
    public Optional<T> findById(String id) {
        return modelCollection.stream()
                .filter(item -> item.getId().equals(id) )
                .findFirst();
    }
    
    
    /**
     * Insert model, duplicates allowed
     * 
     * @param model
     * @return WorkItem
     */
    @Override
    public T insertModel(T model) {
        modelCollection.add(model);
        return modelCollection.get( modelCollection.size() -1 );
    }
    
    
    /**
     * Delete model by provided id
     * 
     * @param id
     * @return boolean
     */
    @Override
    public boolean deleteModel(String id) {
        int index = indexOf(id);
        if ( index >= 0 ) {
            modelCollection.remove(index);
            return true;
        }
        else {
            return false;
        }
    }
    
    
    /**
     * Return dataset
     * 
     * @return List-
     */
    @Override
    public List<T> findAll() {
        return modelCollection;
    }

    
    /**
     * Swap old model for new, index changes
     * 
     * @param model
     * @return WorkItem
     */
    @Override
    public T updateModel(T model) {
        int index = indexOf(model.getId());
        if ( index > 0 ) {
            modelCollection.remove(index);
            modelCollection.add(model);
            return modelCollection.get(modelCollection.size() - 1);
        }
        else {
            return null;
        }
    }
    
    
    /**
     * Return list of models with field matching value
     * 
     * @param field
     * @param value
     * @return List-T
     */
    @Override
    public List<T> findByField(String field, Object value) { 
        return modelCollection.stream()
                .parallel()
                .filter(item -> item.getValueFromField(field) == value )
                .toList();
    }
}
