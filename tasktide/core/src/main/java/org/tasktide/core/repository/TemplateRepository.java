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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.CustomAnnotation;


/**
 * Template Model Repository adding utility and collection info
 * 
 * @author bkenna
 * @param <T> of {@link TaskTideModel}-WorkItem,Step,Workflow
 */
public abstract class TemplateRepository<T extends TaskTideModel<T>> extends AbstractRepository<T> {
    
    // Attributes
    protected final Template template;
    
    
    /**
     * Construct with target model class, and collection name
     * 
     * @param template
     * @param modelClass
     * @param collectionName 
     */
    public TemplateRepository(Template template, Class<T> modelClass, String collectionName) {
        super(modelClass, collectionName, RepositoryType.NOSQL);
        this.template = template;
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
        
        // Reduce to result set size
        if ( this.resultSetSize >= 1 ) {
            return template
                .select(COLLECTION_CLASS)
                .where(field)
                .eq(value)
                .limit(resultSetSize)
            .result();
        }
        
        // Otherwise all
        else {
            return template
                .select(COLLECTION_CLASS)
                .where(field)
                .eq(value)
            .result();
        }
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
        
        // Reduce to result set size
        if ( this.resultSetSize >= 1 ) {
            return template.select(COLLECTION_CLASS)
                .where(field)
                .eq(value)
                .and(group)
                .eq(groupVal)
                .limit(resultSetSize)
            .result();
        }
        
        // Otherwise all
        else {
            return template.select(COLLECTION_CLASS)
                .where(field)
                .eq(value)
                .and(group)
                .eq(groupVal)
            .result();
        }
    }
    
    
    /**
     * Fetch all records
     * 
     * @return List-T-Model
     */
    @Override
    public List<T> findAll() {
        
        // Reduce to result set size
        if ( this.resultSetSize >= 1 ) {
            return template
                .select(COLLECTION_CLASS)
                .limit(resultSetSize)
            .result();
        }
        
        // Otherwise all
        else {
            return template.select(COLLECTION_CLASS).result();
        }
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
}