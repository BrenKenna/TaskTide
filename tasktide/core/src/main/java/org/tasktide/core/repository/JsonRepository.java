/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository;

//import jakarta.enterprise.context.Dependent;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.json_repo.Compression;


/**
 * Class to support file backend in compressed json format. All linear operations
 *
 * @author bkenna
 * @param <T> of {@link TaskTideModel TaskTideModel}-{@link WorkItem WorkItem},{@link Step Step},{@link Workflow Workflow}
 */
//@Dependent
public abstract class JsonRepository<T extends TaskTideModel<T>> implements TaskTideRepository<T> {
    
    // Attributes
    protected final List<T> modelCollection;
    protected final Class<T> COLLECTION_CLASS;
    protected final String collectionName;
    protected final RepositoryType repoType;
    protected Compression compUtil;
    
    
    /**
     * Construct JSON Repository for list of model classes
     * 
     * @param modelCollection
     * @param modelClass
     * @param collectionName - As FilePath
     */
    public JsonRepository(
        List<T> modelCollection,
        Class<T> modelClass,
        String collectionName
    ) {
        this.modelCollection = modelCollection;
        this.COLLECTION_CLASS = modelClass;
        this.collectionName = collectionName;
        this.compUtil = new Compression();
        this.repoType = RepositoryType.JSON;
    }

    
    /**
     * Provide a map of repository meta data reference class,
     *   repository type (NoSQL, RocksDB etc), collecion name
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
     * Find index by Id
     * 
     * @param id
     * @return int
     */
    private int indexOf(String id) {
        int counter = 0;
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
     * Import provided model list, measuring imported count against expected
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public boolean extendModel(List<T> toAdd) {
        List<T> imported = toAdd.stream()
             .map(elm -> insertModel(elm))
             .toList();
        return imported.size() == toAdd.size();
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
        if ( index >= 0 ) {
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

    
    /**
     * Save to file
     * 
     * @return int
     */
    @Override
    public List<T> load() {
        
        // Return dataset if loaded
        String json = compUtil.decompressFromFile(collectionName + "-" + COLLECTION_CLASS.getSimpleName());
        if ( json != null ) {
            Jsonb jsonb = JsonbBuilder.create();
            
            @SuppressWarnings("unchecked")
            T[] array = (T[]) jsonb.fromJson(json, (Class<T[]>) Array.newInstance(COLLECTION_CLASS, 0).getClass());
            return Arrays.asList(array);
        }

        // Otherwise return null
        return null;
    }

    
    /**
     * Return input data as JSON array string
     * 
     * @param list
     * @return String
     */
    public String listToJson(List<? extends T> list) {
        String joined = list.stream()
            .parallel()
            .map(item -> item.toJson())
            .collect(Collectors.joining(",", "[", "]"));
        return joined;
    }
    
    
    /**
     * Save data to file
     * 
     * @return int
     */
    @Override
    public int save() {
        String output = listToJson(modelCollection);
        if ( compUtil.compressToFile(collectionName + "-" + COLLECTION_CLASS.getSimpleName(), output) ) {
            return modelCollection.size();
        }
        return -1;
    }
    
    
    /**
     * Get class of repository
     * 
     * @return Class-T
     */
    public Class<T> getCollectionClass() {
        return COLLECTION_CLASS;
    }

    
    /**
     * Get collection name
     * 
     * @return String
     */
    public String getCollectionName() {
        return collectionName;
    }

    
    /**
     * Get repository type
     * 
     * @return RepositoryType
     */
    public RepositoryType getRepoType() {
        return repoType;
    }

    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "JsonRepository{" +
            "COLLECTION_CLASS=" + COLLECTION_CLASS +
            ", collectionName=" + collectionName +
            ", repoType=" + repoType +
        '}';
    }
}
