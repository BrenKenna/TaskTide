/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer;

import org.tasktide.engine.observer.worker.ObserverType;


/**
 * Class to support handling of failed {@link WorkerObserver} action
 * 
 * @author bkenna
 */
public class ObserverResult {
 
    // Attributes
    private final boolean success;
    private final String failedObserver;
    private final ObserverType type;
    private final boolean canIgnore;

    
    /**
     * Construct with success flag, and observer
     * 
     * @param success
     * @param failedObserver 
     * @param type 
     */
    public ObserverResult(boolean success, String failedObserver, ObserverType type) {
        this.success = success;
        this.failedObserver = failedObserver;
        this.type = type;
        this.canIgnore = false;
    }

    
    /**
     * Construct with ignore flag
     * 
     * @param success
     * @param failedObserver
     * @param type
     * @param canIgnore 
     */
    public ObserverResult(boolean success, String failedObserver, ObserverType type, boolean canIgnore) {
        this.success = success;
        this.failedObserver = failedObserver;
        this.type = type;
        this.canIgnore = canIgnore;
    }
    
    
    /**
     * Check observer state
     * 
     * @return boolean
     */
    public boolean isSuccess() {
        return success;
    }

    
    /**
     * Return failed observer
     * 
     * @return String
     */
    public String getFailedObserver() {
        return failedObserver;
    }

    
    /**
     * Return {@link ObserverType}
     * 
     * @return {@link ObserverType} 
     */
    public ObserverType getType() {
        return type;
    }
    
    
    /**
     * Create successful observer result
     * 
     * @return ObserverResult 
     */
    public static ObserverResult success() {
        return new ObserverResult(true, null, null);
    }

    
    /**
     * Create failed observer result
     * 
     * @param observer
     * @return ObserverResult
     */
    public static ObserverResult failure(WorkerObserver observer) {
        return new ObserverResult(false, observer.getClass().getSimpleName(), observer.getType());
    }
    
    
    /**
     * Create failed observer result, flagging if can ignore
     * 
     * @param observer
     * @param canIgnore
     * @return ObserverResult
     */
    public static ObserverResult failure(WorkerObserver observer, boolean canIgnore) {
        return new ObserverResult(false, observer.getClass().getSimpleName(), observer.getType(), canIgnore);
    }
}