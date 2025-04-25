/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.core;

import java.util.List;


/**
 * Marker interface for TaskTideService, want to restrict eventually.
 * <br><br>
 * Want to keep the {@link TaskTideRepository TaskTideRepository} away from anywhere except the {@link TaskTideService TaskTideService}
 * 
 * @param <T> of {@link WorkItem WorkItem},{@link Step Step},{@link Workflow Workflow}
 * @author bkenna
 */
public interface TaskTideService<T extends TaskTideModel> {
    
    
    /**
     * Append model to backend
     * 
     * @param model
     * @return {@link TaskTideModel TaskTideModel} of {@link WorkItem WorkItem},{@link Step Step},{@link Workflow Workflow}
     */
    public T appendModel(T model);
    
    
    /**
     * Query backend for models having field of value
     * 
     * @param field
     * @param value
     * @return List-{@link TaskTideModel TaskTideModel} of {@link WorkItem WorkItem},{@link Step Step},{@link Workflow Workflow}
     */
    public List<T> viewByField(String field, Object value);
    
    
    /**
     * Fetch model collection
     * 
     * @return List-{@link TaskTideModel TaskTideModel} of {@link WorkItem WorkItem},{@link Step Step},{@link Workflow Workflow}
     */
    public List<T> viewAll();
    
    
    /**
     * Query backend for models with Id
     * 
     * @param id
     * @return List-{@link TaskTideModel TaskTideModel} of {@link WorkItem WorkItem},{@link Step Step},{@link Workflow Workflow}
     */
    public T fetchById(String id);
    
    
    /**
     * Drop model with Id from backend
     * 
     * @param id
     * @return boolean
     */
    public boolean dropById(String id);
    
    
    /**
     * Update backend model
     * 
     * @param model
     * @return List-{@link TaskTideModel TaskTideModel} of {@link WorkItem WorkItem},{@link Step Step},{@link Workflow Workflow}
     */
    public T updateModel(T model);
    
    
    /**
     * Extend backend with list of {@link TaskTideModel TaskTideModel}, measuring imported count against expected
     * 
     * @param toAdd
     * @return boolean
     */
    public boolean extendModel(List<T> toAdd);
    
    
    public int save();
    
    // public int load(String uri);
}
