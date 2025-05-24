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
    public boolean onTaskStart(T task) {
        for ( WorkerObserver<T> obs : this.observers ) {
            ObserverResult result = obs.onTaskStart(task);
            if ( !result.isSuccess() && !result.getType().isOptional() ) {
                if ( !result.canIgnore() ) {
                    // System.out.println("\n\nnObserver failing out onTaskStart for '" + obs.getClass().getSimpleName() + "':\n" + task.toJson() + "\n\n");
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Run the onTaskProcessing method from each {@link WorkerObserver}.
     *  Returning {@link ObserverResult} flagging failed observer, or successful run.
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    @Override
    public boolean onTaskProcessing(T task) {
        for ( WorkerObserver<T> obs : this.observers ) {
            ObserverResult result = obs.onTaskProcessing(task);
            if ( !result.isSuccess() && !result.getType().isOptional() ) {
                if ( !result.canIgnore() ) {
                    // System.out.println("\n\nnObserver failing out onTaskProcessing for '" + obs.getClass().getSimpleName() + "':\n" + task.toJson() + "\n\n");
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Run the onTaskEnd method from each {@link WorkerObserver}.
     *  Returning {@link ObserverResult} flagging failed observer, or successful run.
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    @Override
    public boolean onTaskEnd(T task) {
        for ( WorkerObserver<T> obs : this.observers ) {
            ObserverResult result = obs.onTaskEnd(task);
            if ( !result.isSuccess() && !result.getType().isOptional() ) {
                if ( !result.canIgnore() ) {
                    // System.out.println("\n\nnObserver failing out onTaskEnd for '" + obs.getClass().getSimpleName() + "':\n" + task.toJson() + "\n\n");
                    return false;
                }
            }
        }
        return true;
    }
}
