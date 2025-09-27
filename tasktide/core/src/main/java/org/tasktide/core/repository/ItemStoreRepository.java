/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.core.repository;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.itemstore.Item;
import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.DbTarget;


/**
 * Utilize RocksDB as a {@link TaskTideRepository} through the {@link ItemStore}
 * 
 * @param <T> of {@link TaskTideModel}
 * @author bkenna
 */
public abstract class ItemStoreRepository<T extends TaskTideModel<T>> implements TaskTideRepository<T> {

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
    public ItemStoreRepository(ItemStore itemStore, Class<T> modelClass, String collectionName) {
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
        output = new Item<>(model.getId(), model.getState(), model.getCollection(), model.toJson());
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
            queried = repo.getById(DbTarget.MASTER, id);
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
            this.repo.saveItem(DbTarget.MASTER, item);
            return this.findById(model.getId()).get();
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
            this.repo.update(DbTarget.MASTER, item);
            
            // Fetch inserted record
            return this.findById(model.getId()).get();
        }
        catch ( Exception ex ) {
            return null;
        }
    }
    
    
    /**
     * Filters records with provided {@link CustomAnnotation}
     * 
     * @param anno
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<T> filterByAnnotation(CustomAnnotation anno) {
        return this.findAll()
            .stream()
            .parallel()
            .filter( elm ->
                elm.getAnnotations() != null
                ? elm.getAnnotations().queriedFieldsMatch(anno)
                : false
            )
        .collect(Collectors.toList());
    }
    
    
    /**
     * Filters records with provided annotation key and value
     * 
     * @param key
     * @param value
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<T> filterByAnnotation(String key, Object value) {
        return this.findAll()
            .stream()
            .parallel()
            .filter( elm -> 
                elm.getAnnotations() != null
                ? elm.getAnnotations().getKey(key).equals(value)
                : false
            )
        .collect(Collectors.toList());
    }
    
    
    /**
     * Filter records which have provided annotation key
     * 
     * @param key
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<T> hasAnnotationField(String key) {
        return this.findAll()
            .stream()
            .parallel()
            .filter( elm ->
                elm.getAnnotations() != null
                ? elm.getAnnotations().hasKey(key)
                : false
            )
        .collect(Collectors.toList());
    }
    
    
    /**
     * Extends collection, state query with annotation filtering
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @param annoKey
     * @param annoValue
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<T> findByFieldForGroupWithAnno(
            String field, Object value, String group,
            Object groupVal, String annoKey, Object annoValue
    ) {
        return this.findByFieldForGroup(field, value, group, groupVal)
            .stream()
            .parallel()
            .filter( elm ->
                elm.getAnnotations() != null
                ? elm.getAnnotations().getKey(annoKey).equals(annoValue)
                : false
            )
        .collect(Collectors.toList());
    }
    
    
    /**
     * Extends collection, state query with annotation filtering
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @param anno
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<T> findByFieldForGroupWithAnno(String field, Object value, String group, Object groupVal, CustomAnnotation anno) {
        return this.findByFieldForGroup(field, value, group, groupVal)
            .stream()
            .parallel()
            .filter( elm ->
                elm.getAnnotations() != null
                ? elm.getAnnotations().queriedFieldsMatch(anno)
                : false
            )
        .collect(Collectors.toList());
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
            Item item = this.repo.getById(DbTarget.MASTER, id);
            return this.repo.delete(DbTarget.MASTER, item);
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
        return this.repo.getAll(DbTarget.MASTER)
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
        
        // Fetch repo and scan records
        return this.repo.getAll(DbTarget.MASTER)
            .stream()
            .parallel()
            .map( this::toModel )
            .filter( elm -> {
                Object val = elm.getValueFromField(field);
                return val != null && val.equals(value);
            })
            .filter( elm -> {
                Object val = elm.getValueFromField(group);
                return val != null && val.equals(groupVal);
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
        return this.repo.getAll(DbTarget.MASTER)
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
     * Open connection to both master and prototype
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
            this.repo.saveItems(DbTarget.MASTER, forImport);
            return true;
        }
        catch ( Exception ex ) {
            //System.out.println("Debug >>>\nDisplaying stack trace");
            //ex.printStackTrace();
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
    
    
    /**
     * Removes prototype
     */
    public void clear() {
        this.repo.clearPrototype();
    }
}
