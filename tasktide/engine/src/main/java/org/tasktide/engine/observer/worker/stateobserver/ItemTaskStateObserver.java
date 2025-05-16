/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker.stateobserver;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;

import org.tasktide.engine.observer.worker.StateObserver;

import org.tasktide.engine.worker.tasktracker.ExecutionState;
import org.tasktide.engine.worker.tasktracker.TaskTracker;


/**
 * Class for handling {@link ItemTask} states
 * 
 * @author bkenna
 */
public class ItemTaskStateObserver extends StateObserver<ItemTask> {

    
    /**
     * Construct with state tracker
     * 
     * @param stateTracker 
     */
    public ItemTaskStateObserver(TaskTracker stateTracker) {
        super(stateTracker);
    }

    
    /**
     * Construct feeding {@link ItemTask} workload into {@link TaskTracker}
     * 
     * @param stateTracker
     * @param workload 
     */
    public ItemTaskStateObserver(TaskTracker stateTracker, List<ItemTask> workload) {
        super(stateTracker, workload);
    }
    
    
    /**
     * Check availability before processing
     * 
     * @param  task
     * @return boolean 
     */
    @Override
    public boolean onTaskStart(ItemTask task) {
        if ( task.getTaskState() == TaskState.PENDING ) {
            stateTracker.markTask(task.getId(), ExecutionState.PREPARE);
            return true;
        }
        else {
            stateTracker.markTask(task.getId(), ExecutionState.SKIPPED);
            return false;
        }
    }
    
    
    /**
     * Handle state for processing of task
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskProcessing(ItemTask task) {
        stateTracker.markTask(task.getId(), ExecutionState.RUNNING);
        task.setTaskState(TaskState.ACTIVE);
        return true;
    }
    
    
    /**
     * Handle setting of {@link ItemTask} state following process execution
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskEnd(ItemTask task) {
        if (task.getTaskLog().getExitCode() == 0) {
            task.setTaskState(TaskState.COMPLETE);
            stateTracker.markTask(task.getId(), ExecutionState.COMPLETED);
            return true;
        }
        
        // Otherwise acknowledge error
        else {
            task.setTaskState(TaskState.ERROR);
            stateTracker.markTask(task.getId(), ExecutionState.FAILED);
            return false;
        }
    }
}
