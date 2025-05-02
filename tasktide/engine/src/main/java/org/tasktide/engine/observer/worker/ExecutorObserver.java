/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.engine.observer.worker;

import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;

import org.tasktide.engine.observer.TaskTideWorkerObserver;
import org.tasktide.engine.worker.processor.TaskTideProcessor;


/**
 * Observer for the full processing life-cycle of an executor
 * 
 * @param <T> of {@link TaskTidModel}-{@link WorkItem},{@link ItemTask}
 * @param <U> subunit {@alink ItemTask} for now
 * @author bkenna
 */
public abstract class ExecutorObserver<T extends TaskTideModel<T>, U extends TaskTideModel<U>> implements TaskTideWorkerObserver<T> {

    
    // Attributes
    protected final Logger logger;
    

    /**
     * Construct embedding logger
     * 
     * @param logger
     */
    public ExecutorObserver(Logger logger) {
        this.logger = logger;
    }

    
    /**
     * Provide processor for {@link ItemTask}/{@link WorkItem}
     * 
     * @param task
     * @return {@link TaskTideProcessor}-{@link ItemTask},{@link WorkItem}
     */
    public abstract TaskTideProcessor<U> provideProcessor(T task);
}
