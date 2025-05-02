/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.engine.observer.worker;

import org.tasktide.core.TaskTideModel;
import org.tasktide.engine.worker.processor.TaskTideProcessor;


/**
 * Observer for the full processing life-cycle of an executor
 * 
 * @param <T> of {@link TaskTidModel}-{@link WorkItem},{@link ItemTask}
 * @param <U>
 * @author bkenna
 */
public interface ExecutorObserver<T extends TaskTideModel<T>, U extends TaskTideModel<U>> {
    
    
    /**
     * Monitor {@link TaskTideModel} processing until completion
     * 
     * @param task 
     */
    public void monitorUnitDone(T task);
    
    
    /**
     * Handle the post processing of {@link WorkItem}/{@link ItemTask}
     * 
     * @param task 
     */
    public void postProcess(T task);
    
    
    /**
     * Provide processor for {@link ItemTask}/{@link WorkItem}
     * 
     * @param task
     * @return {@link TaskTideProcessor}-{@link ItemTask},{@link WorkItem}
     */
    public TaskTideProcessor<U>  provideProcessor(T task);
}
