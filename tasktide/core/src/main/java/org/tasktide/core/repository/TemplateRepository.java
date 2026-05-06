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

import jakarta.nosql.Template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.CustomAnnotation;


/**
 * Template Model Repository adding utility and collection info
 * 
 * @author bkenna
 * @param <T> of {@link TaskTideModel}-WorkItem,Step,Workflow
 */
// @Dependent 
public abstract class TemplateRepository<T extends TaskTideModel<T>> implements TaskTideRepository<T> {
    
    // Attributes
    protected final Template template;
    protected final Class<T> COLLECTION_CLASS;
    protected final String collectionName;
    protected final RepositoryType repoType;
    
    
    /**
     * Construct with target model class, and collection name
     * 
     * @param template
     * @param modelClass
     * @param collectionName 
     */
    public TemplateRepository(Template template, Class<T> modelClass, String collectionName) {
        this.template = template;
        this.COLLECTION_CLASS = modelClass;
        this.collectionName = collectionName;
        this.repoType = RepositoryType.NOSQL;
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
     * Fetch WorkItem by its Id
     * 
     * @param id
     * @return WorkItem
     */
    @Override
    public Optional<T> findById(String id) {
        return template.find(this.COLLECTION_CLASS, id);
    }

    
    /**
     * Insert model into DB
     * 
     * @param model
     * @return T-Model
     */
    @Override
    public T insertModel(T model) {
        T result = template.insert(model);
        return result;
    }
    
    
    /**
     * Batch import provided list of records
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public boolean extendModel(List<T> toAdd) {
        Iterable<T> imported = template.insert(toAdd);
        return imported != null;
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
        return findById(id).isEmpty();
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
        return template.select(COLLECTION_CLASS)
            .where(field)
            .eq(value)
            .and(group)
            .eq(groupVal)
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
    
    
    /**
     * Not as useful here as file IO so just hashcode
     * 
     * @return 
     */
    @Override
    public int save() {
        return template.hashCode();
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
}