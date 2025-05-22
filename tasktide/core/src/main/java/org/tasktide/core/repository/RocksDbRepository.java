/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository;

import jakarta.enterprise.context.Dependent;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.util.List;
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
@Dependent
public abstract class RocksDbRepository<T extends TaskTideModel> implements TaskTideRepository<T> {

    // Attributes
    private final Class<T> modelClass;
    private final ItemStore repo;
    private final Jsonb JSON_BUILDER = JsonbBuilder.create();
    
    
    /**
     * Construct with {@link ItemStore}
     * 
     * @param itemStore
     * @param modelClass 
     * @throws java.lang.Exception 
     */
    public RocksDbRepository(ItemStore itemStore, Class<T> modelClass) throws Exception {
        this.modelClass = modelClass;
        this.repo = itemStore;
        repo.openConn(DbTarget.BOTH);
        repo.cacheMaster();
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
        return this.JSON_BUILDER.fromJson(item.getPayload(), modelClass);
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
            queried = this.repo.getByIdFromMaster(id);
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
            this.repo.saveItem(item);
            this.repo.syncToMaster();
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
            .map(this::toModel)
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
}
