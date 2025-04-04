/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.core.model.builders;


/**
 *
 * Abstract for building model classes
 * 
 * @author bkenna
 */
public abstract class ModelBuilder {
    
    public ModelBuilder() {}
    
    /**
     * Build model object from provided fields
     * 
     * @return Object
     */
    public abstract Object build(); 
}
