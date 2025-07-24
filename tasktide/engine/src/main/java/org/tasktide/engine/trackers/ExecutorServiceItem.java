/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.trackers;

import java.util.concurrent.Future;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;


/**
 * Holds reference for the future {@link TaskTideModel} so that
 *  their processing can be tracked as a collection.
 * 
 * @param <T> {@link TaskTideModel} of type {@link WorkItem}/{@link ItemTask}
 * @author bkenna
 */
public class ExecutorServiceItem<T extends TaskTideModel<T>> {
    
    // Attributes
    private final TaskTideModel<T> task;
    private final Future<?> future;

    
    /**
     * Construct with task and future
     * 
     * @param task
     * @param future 
     */
    public ExecutorServiceItem(TaskTideModel<T> task, Future<?> future) {
        this.task = task;
        this.future = future;
    }
    
    
    /**
     * Get the model class
     * 
     * @return {@link TaskTideModel}
     */
    public TaskTideModel<T> getModel() {
        return this.task;
    }
    
    
    /**
     * Get the future
     * 
     * @return Future
     */
    public Future<?> getFuture() {
        return this.future;
    }
}
