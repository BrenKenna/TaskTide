/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker;

import org.tasktide.engine.observer.WorkerObserver;
import java.util.List;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.tasktracker.ExecutionState;
import org.tasktide.engine.tasktracker.TaskTracker;


/**
 * Abstract class for handling the logic of {@link WorkItem}/{@link ItemTask} processing based on states.
 * <br><br>
 * Would be useful to work towards embedding a {@link TaskTideService}, so progress logged.
 * 
 * @author bkenna
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 */
public abstract class StateObserver<T extends TaskTideModel<T>> implements WorkerObserver<T> {

    // Tracker attribute
    protected final TaskTracker stateTracker;
    private final ObserverType type;
            
    
    /**
     * Construct with {@link TaskTracker}
     * 
     * @param stateTracker 
     */
    public StateObserver(TaskTracker stateTracker) {
        this.stateTracker = stateTracker;
        this.type = ObserverType.CRITICAL;
    }
    
    
    /**
     * Construct with {@link TaskTracker}
     * 
     * @param stateTracker 
     * @param workload 
     */
    public StateObserver(TaskTracker stateTracker, List<T> workload) {
        this.type = ObserverType.CRITICAL;
        this.stateTracker = stateTracker;
        workload
            .stream()
            .parallel()
            .forEach(
                elm -> this.stateTracker.markTask(elm.getId(), ExecutionState.QUEUED)
            );
    }
    
    
    /**
     * Use {@link ObserverType} to check if StateObserver is optional
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
