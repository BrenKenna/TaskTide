/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker.stateobserver;

import java.util.List;
import org.tasktide.core.TaskTideModel;

import org.tasktide.engine.observer.TaskTideWorkerObserver;
import org.tasktide.engine.worker.tasktracker.ExecutionState;
import org.tasktide.engine.worker.tasktracker.TaskTracker;


/**
 *
 * 
 * @author bkenna
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 */
public abstract class StateObserver<T extends TaskTideModel<T>> implements TaskTideWorkerObserver<T> {

    // Tracker attribute
    protected final TaskTracker stateTracker;
    
    
    /**
     * Construct with tracker
     * 
     * @param stateTracker 
     */
    public StateObserver(TaskTracker stateTracker) {
        this.stateTracker = stateTracker;
    }
    
    
    /**
     * Construct with tracker
     * 
     * @param stateTracker 
     * @param workload 
     */
    public StateObserver(TaskTracker stateTracker, List<T> workload) {
        this.stateTracker = stateTracker;
        for (T elm : workload) {
            this.stateTracker.markTask(elm.getId(), ExecutionState.QUEUED);
        }
    }
    
    
    /**
     * Mark task as preparing
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskStart(T task) {
        stateTracker.markTask(task.getId(), ExecutionState.PREPARE);
        return true;
    }

    
    /**
     * Mark status for running
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskProcessing(T task) {
        stateTracker.markTask(task.getId(), ExecutionState.RUNNING);
        return true;
    }

    
    /**
     * Abstract method for handling task completion
     * 
     * @param task
     * @return boolean
     */
    @Override
    public abstract boolean onTaskEnd(T task);
}
