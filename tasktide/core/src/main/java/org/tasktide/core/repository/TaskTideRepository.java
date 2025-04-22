/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.core.repository;

import java.util.List;
import java.util.Optional;


/**
 *
 * Interface for TaskTideRepository
 * 
 * @param <T> of ModelClass-WorkItem,Step,Workflow
 * @author bkenna
 */
public interface TaskTideRepository<T> {
    
    
    /**
     * Find model by Id
     * 
     * @param id
     * @return Optional-T
     */
    public Optional<T> findById(String id);
    
    
    /**
     * Insert model into repository
     * 
     * @param model
     * @return T
     */
    public T insertModel(T model);
    
    
    /**
     * Update the provided model on backend
     * 
     * @param model
     * @return T
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
     * @return List-T
     */
    public List<T> findByField(String field, Object value);
    
    
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
}
