/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker.stateobserver;

import java.util.List;
import java.util.UUID;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.observer.ObserverResult;

import org.tasktide.engine.observer.worker.StateObserver;
import org.tasktide.engine.tasktracker.ExecutionState;
import org.tasktide.engine.tasktracker.TaskTracker;


/**
 * Class for observing the processing of {@link WorkItem}
 * 
 * @author bkenna
 */
public class WorkItemStateObserver extends StateObserver<WorkItem> {

    
    /**
     * Construct with {@link TaskTracker}
     * 
     * @param stateTracker 
     */
    public WorkItemStateObserver(TaskTracker stateTracker) {
        super(stateTracker);
    }

    
    /**
     * Construct feeding {@link WorkItem} workload into {@link TaskTracker}
     * 
     * @param stateTracker
     * @param workload 
     */
    public WorkItemStateObserver(TaskTracker stateTracker, List<WorkItem> workload) {
        super(stateTracker, workload);
    }

    
    /**
     * Verify {@link WorkItem} availability before processing
     * 
     * @param task
     * @return boolean
     */
    @Override
    public ObserverResult onTaskStart(WorkItem task) {
        
        // Check task is still available
        String taskId = task.getId();
        if ( task.getItemState() == ItemState.TODO ) {
            stateTracker.markTask(taskId, ExecutionState.PREPARE);
            
            // Verify task is still available
            if ( verifyItem(task) ) {
                stateTracker.markTask(taskId, ExecutionState.LOCKED);
                return ObserverResult.success();
            }
            
            // Mark task as skipped if nor running on another thread
            if ( !stateTracker.isRunning(taskId) ) {
                stateTracker.markTask(task.getId(), ExecutionState.SKIPPED);
                return ObserverResult.failure(this);
            }
            
            // Mark as skipped if running on another thread within JVM
            return ObserverResult.failure(this);
        }
        
        // Skip task if not running on another thread within JVM
        else {
            if ( !stateTracker.isRunning(taskId) ) {
                stateTracker.markTask(task.getId(), ExecutionState.SKIPPED);
            }
            return ObserverResult.failure(this);
        }
    }
    
    
    /**
     * Verify locking {@link WorkItem} before processing
     * 
     * @param task
     * @return boolean
     */
    private boolean verifyItem(WorkItem task) {
        
        // Lock item
        String lockId = UUID.randomUUID().toString();
        task.setItemState(ItemState.LOCKED);
        task.setLockDate( System.currentTimeMillis() );
        task.setLockId( lockId );
        
        // Wait a few seconds and check lock is the still the same
        return true;
    }

    
    /**
     * 
     * 
     * @param task
     * @return 
     */
    @Override
    public ObserverResult onTaskProcessing(WorkItem task) {
        stateTracker.markTask(task.getId(), ExecutionState.RUNNING);
        return ObserverResult.success();
    }

    
    /**
     * Handle assignment of {@link ItemState}.Done to {@link WorkItem}
     * 
     * @param task
     * @return boolean
     */
    @Override
    public ObserverResult onTaskEnd(WorkItem task) {
        
        // Update task counts
        task.setTaskCounts();
        
        // Set done date if all done
        if ( task.getTaskCount() == task.getTaskDone() ) {
            long doneTime = task.getWorkload().getLatestDone();
            task.setItemState(ItemState.DONE);
            task.setDoneDate(doneTime);
            stateTracker.markTask(task.getId(), ExecutionState.COMPLETED);
            return ObserverResult.success();
        }
        
        // Otherwise unlock work item
        else {
            task.setItemState(ItemState.ERROR);
            stateTracker.markTask(task.getId(), ExecutionState.ABORTED);
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