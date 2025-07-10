/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.containerprovider;

import jakarta.enterprise.inject.spi.Extension;


/**
 * CDI container interface to provide
 * 
 * @author bkenna
 * @param <T>
 */
public interface CdiContainerProvider<T> {
    
    
    /**
     * Allows implementing classes to define initializer
     *  before container
     */
    public void initialize();
    
    
    /**
     * Allows implementing classes to define how to start
     *  CDI container
     */
    public void start();
    
    
    /**
     * Allows implementing classes to define how to 
     *  shutdown CDI container
     */
    public void shutdown();
    
    
    /**
     * Allows implementing classes to define returning
     *  their container
     * 
     * @return T
     */
    public T getContainer();
    
    
    /**
     * Allows implementing classes to define fetching
     *  a bean from active instance
     * 
     * @param <U>
     * @param clazz
     * @return instance of U
     */
    public <U> U getBean(Class<U> clazz);
    
    
    /**
     * Allows implementing classes to define injecting
     *  classes into container
     * 
     * @param <V>
     * @param clazz 
     */
    @SuppressWarnings("unchecked")
    public <V> void addPackage(Class<V>... clazz);
    
    
    /**
     * Allows implementing classes to define injecting
     *  classes into container
     * 
     * @param <V>
     * @param clazz 
     */
    @SuppressWarnings("unchecked")
    public <V extends Extension> void addExtension(Class<V>... clazz);
}
