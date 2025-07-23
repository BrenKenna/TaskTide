/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Find {@link TaskTideModel} from backend with field and group
     *  having specified value. Step = Name, State = ToDo
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @return List-{@link TaskTideModel}
     */
    public List<T> findByFieldForGroup(String field, Object value, String group, Object groupVal);
    
    
    
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
