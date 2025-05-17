/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.engine.observer;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.worker.TaskTideWorkerUnit;


/**
 * Interface to decouple the {@link WorkerObserver} from their operation in chain
 * 
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 * @author bkenna
 */
public interface TaskTideEngineObserver<T extends TaskTideModel<T>> extends TaskTideWorkerUnit<T> {
    
    
    /**
     * Defines chained {@link WorkerObserver} actions to take on starting task
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    public ObserverResult onTaskStart(T task);
    
    
    /**
     * Defines chained {@link WorkerObserver} actions to take on processing task
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    public ObserverResult onTaskProcessing(T task);
    
    
    /**
     * Defines chained {@link WorkerObserver} actions to take on ending task
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    public ObserverResult onTaskEnd(T task);
}
