/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.core;


/**
 * Interface to support methods around model classes
 * 
 * @param <T> of {@link WorkItem},{@link Step},{@link Workflow}
 * @author bkenna
 */
public interface TaskTideModel<T> {
    
    
    /**
     * Represent model as JsonString
     * 
     * @return String
     */
    public String toJson();
    
    
    /**
     * Get Id from model
     * 
     * @return String
     */
    public String getId();

    
    /**
     * Get the value of field
     * 
     * @param field
     * @return Object
     */
    public Object getValueFromField(String field);
}
