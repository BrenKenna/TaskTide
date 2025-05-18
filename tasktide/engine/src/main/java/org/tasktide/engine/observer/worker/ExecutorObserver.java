/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.engine.observer.worker;

import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.engine.observer.WorkerObserver;


/**
 * Observer for the full processing life-cycle of an executor
 * 
 * @param <T> of {@link TaskTidModel}-{@link WorkItem},{@link ItemTask}
 * @param <U> subunit {@alink ItemTask} for now
 * @author bkenna
 */
public abstract class ExecutorObserver<T extends TaskTideModel<T>, U extends TaskTideModel<U>> implements WorkerObserver<T> {

    // Attributes
    protected final Logger logger;
    private ObserverType type;

    
    /**
     * Construct embedding logger
     * 
     * @param logger
     */
    public ExecutorObserver(Logger logger) {
        this.logger = logger;
        this.type = ObserverType.CRITICAL;
    }
    
    
    /**
     * Use {@link ObserverType} to check if ExecutorObserver is optional
     * 
     * @return boolean
     */
    @Override
    public boolean isOptional() {
        return type.isOptional();
    }

    
    /**
     * Return {@link ObserverType}
     * 
     * @return {@link ObserverType}
     */
    @Override
    public ObserverType getType() {
        return this.type;
    }
}
