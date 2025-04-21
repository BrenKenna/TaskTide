/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.nosql_repo;

import jakarta.enterprise.context.Dependent;
import jakarta.nosql.Template;

import java.util.List;
import java.util.Optional;
import org.tasktide.core.repository.TaskTideModelRepository;


/**
 * 
 * Template Model Repository adding utility and collection info
 * 
 * @author bkenna
 * @param <T> of ModelClass-WorkItem,Step,Workflow
 */
@Dependent
public abstract class TemplateRepository<T> implements TaskTideModelRepository<T> {
    
    // Attributes
    protected final Template template;
    protected final Class<T> COLLECTION_CLASS;
    protected final String collectionName;
    
    
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
    }
    
    
    /**
     * Fetch WorkItem by its Id
     * 
     * @param id
     * @return WorkItem
     */
    @Override
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
}