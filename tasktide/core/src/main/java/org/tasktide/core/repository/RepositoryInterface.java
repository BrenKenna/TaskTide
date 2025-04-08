/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.core.repository;

import java.util.List;
import java.util.Optional;


/**
 *
 * Interface for ModelRepository
 * 
 * @author bkenna
 */
public interface RepositoryInterface<T> {
    
    
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
    public Object insertModel(T model);
    
    
    /**
     * Update the provided model on backend
     * 
     * @param model
     * @return T
     */
    public Object updateModel(T model);
    
    
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
    public List<T> findByField(String field, T value);
    
    
    /**
     * Provide all model 
     * 
     * @return List-T
     */
    public List<T> finAll();
}
