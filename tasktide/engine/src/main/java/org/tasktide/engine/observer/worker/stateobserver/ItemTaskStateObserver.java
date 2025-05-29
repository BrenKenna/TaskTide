/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker.stateobserver;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.engine.observer.ObserverResult;

import org.tasktide.engine.observer.worker.StateObserver;

import org.tasktide.engine.tasktracker.ExecutionState;
import org.tasktide.engine.tasktracker.TaskTracker;


/**
 * Class for handling {@link ItemTask} states
 * 
 * @author bkenna
 */
public class ItemTaskStateObserver extends StateObserver<ItemTask> {

    // Logger
    private final Logger logger;
    
    /**
     * Construct with state tracker
     * 
     * @param stateTracker 
     */
    public ItemTaskStateObserver(TaskTracker stateTracker) {
        super(stateTracker);
        this.logger = LogManager.getLogger(ItemTaskStateObserver.class);
    }

    
    /**
     * Construct feeding {@link ItemTask} workload into {@link TaskTracker}
     * 
     * @param stateTracker
     * @param workload 
     */
    public ItemTaskStateObserver(TaskTracker stateTracker, List<ItemTask> workload) {
        super(stateTracker, workload);
        this.logger = LogManager.getLogger(ItemTaskStateObserver.class);
    }
    
    
    /**
     * Check availability before processing
     * 
     * @param  task
     * @return boolean 
     */
    @Override
    public ObserverResult onTaskStart(ItemTask task) {
        if ( task.getTaskState() == TaskState.PENDING ) {
            stateTracker.markTask(task.getId(), ExecutionState.PREPARE);
            return ObserverResult.success();
        }
        else {
            stateTracker.markTask(task.getId(), ExecutionState.SKIPPED);
            logger.warn("Skipping non pending ItemTask:\t'{}' with '{}'", task.getId(), task.getState());
            return ObserverResult.failure(this);
        }
    }
    
    
    /**
     * Handle state for processing of task
     * 
     * @param task
     * @return boolean
     */
    @Override
    public ObserverResult onTaskProcessing(ItemTask task) {
        stateTracker.markTask(task.getId(), ExecutionState.RUNNING);
        task.setTaskState(TaskState.ACTIVE);
        return ObserverResult.success();
    }
    
    
    /**
     * Handle setting of {@link ItemTask} state following process execution
     * 
     * @param task
     * @return boolean
     */
    @Override
    public ObserverResult onTaskEnd(ItemTask task) {
        if (task.getTaskLog().getExitCode() == 0) {
            task.setTaskState(TaskState.COMPLETE);
            stateTracker.markTask(task.getId(), ExecutionState.COMPLETED);
            return ObserverResult.success();
        }
        
        // Otherwise acknowledge error
        else {
            task.setTaskState(TaskState.ERROR);
            stateTracker.markTask(task.getId(), ExecutionState.FAILED);
            logger.warn("Execution failed forItemTask:\t'{}'", task.getId());
            return ObserverResult.failure(this, true);
        }
    }
    
    
    /**
     * Return observer name
     * 
     * @return 
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
