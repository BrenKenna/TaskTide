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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.CustomAnnotation;


/**
 * Abstract class to implement shared logic
 *
 * @author Bren
 */
public abstract class AbstractRepository<T extends TaskTideModel<T>> implements TaskTideRepository<T> {

    // Attributes
    protected final Class<T> COLLECTION_CLASS;
    protected final String collectionName;
    protected final RepositoryType repoType;
    protected int resultSetSize;
    
    
    /**
     * Construct with target model class, and collection name
     * 
     * @param template
     * @param modelClass
     * @param collectionName
     * @param repoType
     */
    public AbstractRepository(Class<T> modelClass, String collectionName, RepositoryType repoType) {
        this.COLLECTION_CLASS = modelClass;
        this.collectionName = collectionName;
        this.repoType = repoType;
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
            .filter( elm -> {
                if ( elm.getAnnotations() != null ) {
                    if ( elm.getAnnotations().hasKey(key) ) {
                        return elm.getAnnotations().getKey(key).equals(value);
                    }
                }
                return false;
            })
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
            .filter( elm -> {
                if ( elm.getAnnotations() != null ) {
                    if ( elm.getAnnotations().hasKey(annoKey)) {
                        return elm.getAnnotations().getKey(annoKey).equals(annoValue);
                    }
                }
                return false;
            })
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
     * Could paginate here
     * 
     * @return List-T
     */
    @Override
    public List<T> load() {
        return null;
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
     * Get results set size
     * 
     * @return int
     */
    @Override
    public int getResultSetSize() {
        return this.resultSetSize;
    }

    
    /**
     * Set results set size
     * 
     * @param nRecords 
     */
    @Override
    public void setResultSetSize(int nRecords) {
        this.resultSetSize = nRecords;
    }
}