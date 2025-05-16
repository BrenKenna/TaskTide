/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker;

import java.util.List;

import org.tasktide.core.TaskTideModel;

import org.tasktide.engine.observer.TaskTideWorkerObserver;
import org.tasktide.engine.worker.tasktracker.ExecutionState;
import org.tasktide.engine.worker.tasktracker.TaskTracker;


/**
 * Abstract class for handling the logic of {@link WorkItem}/{@link ItemTask} processing based on states.
 * <br><br>
 * Would be useful to work towards embedding a {@link TaskTideService}, so progress logged.
 * 
 * @author bkenna
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 */
public abstract class StateObserver<T extends TaskTideModel<T>> implements TaskTideWorkerObserver<T> {

    // Tracker attribute
    protected final TaskTracker stateTracker;
    
    
    /**
     * Construct with {@link TaskTracker}
     * 
     * @param stateTracker 
     */
    public StateObserver(TaskTracker stateTracker) {
        this.stateTracker = stateTracker;
    }
    
    
    /**
     * Construct with {@link TaskTracker}
     * 
     * @param stateTracker 
     * @param workload 
     */
    public StateObserver(TaskTracker stateTracker, List<T> workload) {
        this.stateTracker = stateTracker;
        workload
            .stream()
            .parallel()
            .forEach(
                elm -> this.stateTracker.markTask(elm.getId(), ExecutionState.QUEUED)
            );
    }
}
