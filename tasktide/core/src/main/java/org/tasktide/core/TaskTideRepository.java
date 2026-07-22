/*
 * Copyright 2026 Brendan Kenna.
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
package org.tasktide.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.tasktide.core.model.CustomAnnotation;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;


/**
 *
 * Interface for TaskTideRepository
 * 
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link Step},{@link Workflow}
 * @author bkenna
 */
public interface TaskTideRepository<T extends TaskTideModel<T>> {
    
    /**
     * Get result set size
     * 
     * @return int
     */
    public int getResultSetSize();
    
    
    /**
     * Set result set size
     * 
     * @param nRecords 
     */
    public void setResultSetSize(int nRecords);
    
    
    /**
     * Find model by Id
     * 
     * @param id
     * @return Optional-{@link TaskTideModel}
     */
    public Optional<T> findById(String id);
    
    
    /**
     * Insert model into repository
     * 
     * @param model
     * @return {@link TaskTideModel}
     */
    public T insertModel(T model);
    
    
    /**
     * Update the provided model on backend
     * 
     * @param model
     * @return {@link TaskTideModel}
     */
    public T updateModel(T model);
    
    
    /**
     * Delete model from backend with Id
     * 
     * @param id
     * @return boolean
     */
    public boolean deleteModel(String id);
    
    
    /**
     * Find model objects from backend with field having value
     * 
     * @param field
     * @param value
     * @return List-{@link TaskTideModel}
     */
    public List<T> findByField(String field, Object value);
    
    
    /**
     * Find {@link TaskTideModel} from backend with field and group
     *  having specified value. Step = Name, State = ToDo
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * 
     * @return List-{@link TaskTideModel}
     */
    public List<T> findByFieldForGroup(String field, Object value, String group, Object groupVal);
    
    
    /**
     * Filters records with provided {@link CustomAnnotation}
     * 
     * @param anno
     * 
     * @return List-{@link TaskTideModel}
     */
    public List<T> filterByAnnotation(CustomAnnotation anno);
    
    
    /**
     * Retrieve records with annotation key matching value
     * 
     * @param key
     * @param value
     * @return List-{@link TaskTideModel}
     */
    public List<T> filterByAnnotation(String key, Object value);
    
    
    /**
     * Retrieve records which have provided annotation key
     * 
     * @param key
     * @return List-{@link TaskTideModel}
     */
    public List<T> hasAnnotationField(String key);
    
    
    /**
     * Find {@link TaskTideModel} from backend with field and group
     *  having specified value with provided annotation.
     * <br>
     * Use case is for early task binding, using pre-defined pilot label
     * <br>
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @param annoKey
     * @param annoValue
     * @return List-{@link TaskTideModel}
     */
    public List<T> findByFieldForGroupWithAnno(String field, Object value, String group, Object groupVal, String annoKey, Object annoValue);
    
    
    /**
     * Find {@link TaskTideModel} from backend with field and group
     *  having specified value with provided annotation.
     * <br>
     * Use case is for early task binding, using pre-defined {@link CustomAnnotation}
     *  where all values for the keys provided in annotation must match record.
     *  Allowing for an annotation profile
     * <br>
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @param anno
     * @return List-{@link TaskTideModel}
     */
    public List<T> findByFieldForGroupWithAnno(String field, Object value, String group, Object groupVal, CustomAnnotation anno);
    
    
    /**
     * Provide all model 
     * 
     * @return List-T
     */
    public List<T> findAll();
    
    
    /**
     * Save model repository, returning count of items
     * 
     * @return int
     */
    public int save();
    
    
    /**
     * Load model repository
     * 
     * @return List-T
     */
    public List<T> load(); 
    
    
    /**
     * Extend repository with input list
     * 
     * @param toAdd
     * @return List-T
     */
    public boolean extendModel(List<T> toAdd);
    
    
    /**
     * Return a map of the repository metadata
     * 
     * @return Map-String, String
     */
    public Map<String, String> getRepositoryMetaData();
}
