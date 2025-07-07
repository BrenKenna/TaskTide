/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository;

// import jakarta.enterprise.context.Dependent;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;

import org.tasktide.itemstore.Item;
import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.stores.DbTarget;


/**
 * Utilize RocksDB as a {@link TaskTideRepository} through the {@link ItemStore}
 * 
 * @param <T> of {@link TaskTideModel}
 * @author bkenna
 */
// @Dependent
public abstract class RocksDbRepository<T extends TaskTideModel<T>> implements TaskTideRepository<T> {

    // Attributes
    private final String collectionName;
    private final Class<T> COLLECTION_CLASS;
    private final ItemStore repo;
    private final Jsonb JSON_BUILDER = JsonbBuilder.create();
    protected final RepositoryType repoType;
    
    
    /**
     * Construct with {@link ItemStore}
     * 
     * @param itemStore
     * @param modelClass 
     * @param collectionName 
     */
    public RocksDbRepository(ItemStore itemStore, Class<T> modelClass, String collectionName) {
        this.COLLECTION_CLASS = modelClass;
        this.repo = itemStore;
        this.collectionName = collectionName;
        this.repoType = RepositoryType.ITEMSTORE;
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
     * Helper method to model to repo record
     * 
     * @param model
     * @return {@link Item}
     */
    private Item<T> toItem(T model) {
        Item<T> output;
        output = new Item<>(model.getId(), model.getState(), model.toJson());
        return output;
    }
    
    
    /**
     * Helper method to convert repo record to model
     * 
     * @param item
     * @return T of {@link TaskTideModel}
     */
    private T toModel(Item item) {
        return this.JSON_BUILDER.fromJson(item.getPayload(), COLLECTION_CLASS);
    }

    
    /**
     * Find {@link TaskTideModel} from {@link ItemStore} with provided id
     * 
     * @param id
     * @return Optional-T of {@link TaskTideModel}
     */
    @Override
    public Optional<T> findById(String id) {
        
        // Initialize required variables
        T result;
        Item queried;
        
        // Fetch and convert to model
        try {
            queried = this.repo.getById(id);
            System.out.println(queried);
            result = this.toModel(queried);
            return Optional.ofNullable(result);
        }
        
        // Otherwise empty result
        catch (Exception ex) {
            return Optional.empty();
        }
    }

    
    /**
     * Insert record into repo, providing inserted record
     * 
     * @param model
     * @return T of {@link TaskTideModel}
     */
    @Override
    public T insertModel(T model) {
        
        // Insert record
        try {
            Item item = this.toItem(model);
            this.repo.saveItemToMaster(item);
            item = this.repo.getById(model.getId());
            return this.toModel(item);
        }
        
        catch (Exception ex) {
            return null;
        }
    }

    
    /**
     * Update provided model in backend DB
     * 
     * @param model
     * @return T of {@link TaskTideModel}
     */
    @Override
    public T updateModel(T model) {
        
        // Try update record
        Item item = this.toItem(model);
        try {
            
            // Save and sync to master
            this.repo.saveItem(item);
            this.repo.syncToMaster();
            
            // Fetch inserted record
            item = this.repo.getById(model.getId());
            return this.toModel(item);
        }
        catch ( Exception ex ) {
            return null;
        }
    }

    
    /**
     * Delete record from DB
     * 
     * @param id
     * @return boolean
     */
    @Override
    public boolean deleteModel(String id) {
        try {
            return this.repo.deleteFromMaster(this.repo.getById(id));
        }
        catch (Exception ex) {
            return false;
        }
    }

    
    /**
     * Fetch records where value of target field matches queried
     * 
     * @param field
     * @param value
     * @return List of {@link TaskTideModel}
     */
    @Override
    public List<T> findByField(String field, Object value) {
        
        // Fetch repo and scan records
        List<Item> queried = this.repo.getAll(true);
        return queried
            .stream()
            .parallel()
            .map( this::toModel )
            .filter( elm -> {
                Object val = elm.getValueFromField(field);
                return val != null && val.equals(value);
            })
            .collect(Collectors.toList());
    }

    
    /**
     * Retrieve all records
     * 
     * @return List of {@link TaskTideModel}
     */
    @Override
    public List<T> findAll() {
        return this.repo.getAll(true)
            .stream()
            .parallel()
            .map(elm -> this.toModel(elm))
            .collect(Collectors.toList());
    }

    
    /**
     * Commit prototype cache to master
     * 
     * @return true 1, false -1
     */
    @Override
    public int save() {
        try {
            repo.syncToMaster();
            return 1;
        }
        catch (Exception ex) {
            return -1;
        }
    }

    
    /**
     * Open connection to both master & prototype
     * 
     * @return List of {@link TaskTideModel}
     */
    @Override
    public List<T> load() {
        this.repo.openConn(DbTarget.BOTH);
        return this.findAll();
    }

    
    /**
     * Add record list to repo
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public boolean extendModel(List<T> toAdd) {
        
        // Convert list to records
        List<Item> forImport = toAdd
            .stream()
            .parallel()
            .map(this::toItem)
            .collect(Collectors.toList());
        
        // Import
        try {
            this.repo.saveItemsToMaster(forImport);
            return true;
        }
        catch ( Exception ex ) {
            return false;
        }
    }
    
    
    /**
     * Return collection name
     * 
     * @return String
     */
    public String getCollectionName() {
        return this.collectionName;
    }
}