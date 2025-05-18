/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer;

import java.util.List;

import org.tasktide.core.TaskTideModel;


/**
 * Hold {@link WorkerObserver}s in a list, with onStart, onProcessing, and onEnd
 *  methods operating iteratively where the failure of one breaks exeuction chain. To 
 *  context failure methods a {@link ObserverResult} are returned.
 * 
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 * @author bkenna
 */
public abstract class ObserverChain<T extends TaskTideModel<T>> implements TaskTideEngineObserver<T>{

    // Attributes
    protected final List<WorkerObserver<T>> observers;
    
    
    /**
     * Construct with {@link WorkerObserver} list
     * 
     * @param observers 
     */
    public ObserverChain(List<WorkerObserver<T>> observers) {
        this.observers = observers;
    }
    
    
    /**
     * Run the onTaskStart method from each {@link WorkerObserver}.
     *  Returning {@link ObserverResult} flagging failed observer, or successful run.
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    @Override
    public ObserverResult onTaskStart(T task) {
        for ( WorkerObserver<T> obs : this.observers ) {
            if ( !obs.onTaskStart(task) ) {
                return ObserverResult.failure(obs);
            }
        }
        return ObserverResult.success();
    }


    /**
     * Run the onTaskProcessing method from each {@link WorkerObserver}.
     *  Returning {@link ObserverResult} flagging failed observer, or successful run.
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    @Override
    public ObserverResult onTaskProcessing(T task) {
        for ( WorkerObserver<T> obs : this.observers ) {
            if ( !obs.onTaskProcessing(task) ) {
                if ( !obs.getClass().getSimpleName().equals("WorkItemExecutor")) {
                    return ObserverResult.failure(obs);
                }
            }
        }
        return ObserverResult.success();
    }


    /**
     * Run the onTaskEnd method from each {@link WorkerObserver}.
     *  Returning {@link ObserverResult} flagging failed observer, or successful run.
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    @Override
    public ObserverResult onTaskEnd(T task) {
        for ( WorkerObserver<T> obs : this.observers ) {
            if ( !obs.onTaskEnd(task) ) {
                return ObserverResult.failure(obs);
            }
        }
        return ObserverResult.success();
    }
}
