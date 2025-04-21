/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.core.repository;


/**
 * Interface to support methods around model classes
 * 
 * @author bkenna
 */
public interface TaskTideModel {
    
    
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
     * Get the value for model field
     * 
     * @param field
     * @param value
     * @return Object
     */
    public Object getValueByField(String field, Object value);
    
    
    /**
     * Get the value of field
     * 
     * @param field
     * @return Object
     */
    public Object getValueFromField(String field);
}
