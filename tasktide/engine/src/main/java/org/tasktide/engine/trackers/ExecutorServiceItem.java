/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.trackers;

import java.util.concurrent.Future;
import org.tasktide.core.TaskTideModel;

/**
 *
 * @param <T> {@link TaskTideModel} of type {@link WorkItem}/{@ItemTask}
 * @author bkenna
 */
public class ExecutorServiceItem<T extends TaskTideModel<T>> {
    
    private final TaskTideModel<T> task;
    private final Future<?> future;

    public ExecutorServiceItem(TaskTideModel<T> task, Future<?> future) {
        this.task = task;
        this.future = future;
    }
    
    
    public TaskTideModel<T> getModel() {
        return this.task;
    }
    
    public Future<?> getFuture() {
        return this.future;
    }
}
