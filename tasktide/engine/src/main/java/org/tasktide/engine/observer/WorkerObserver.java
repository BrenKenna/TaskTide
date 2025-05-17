/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.engine.observer;

import org.tasktide.core.TaskTideModel;
import org.tasktide.engine.observer.worker.ObserverType;

import org.tasktide.engine.worker.TaskTideWorkerUnit;


/**
 * Observer interface for observing {@link Workitem}, and {@link ItemTask} execution
 * 
 * @param <T> of {@link TaskTideModel}-{@link Workitem}, {@link ItemTask}
 * @author bkenna
 */
public interface WorkerObserver<T extends TaskTideModel<T>> extends TaskTideWorkerUnit<T> {
    
    
    /**
     * Defines action to take on starting task execution
     * 
     * @param task
     * @return boolean
     */
    boolean onTaskStart(T task);
    
    
    /**
     * Defines actions to take during processing
     * 
     * @param task
     * @return boolean
     */
    boolean onTaskProcessing(T task);
    
    
    /**
     * Defines action to take when task completes
     * 
     * @param task
     * @return boolean
     */
    boolean onTaskEnd(T task);
    
    
    /**
     * Defines whether implementing observer is optional
     * 
     * @return boolean
     */
    boolean isOptional();
    
    
    /**
     * Defines {@link ObserverType} of implementing Observer
     * 
     * @return {@link ObserverType}
     */
    ObserverType getType();
}
