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

import org.tasktide.engine.trackers.ExecutionState;
import org.tasktide.engine.trackers.TaskTrackers;


/**
 * Class for observing the processing of {@link WorkItem}
 * 
 * @author bkenna
 */
public class WorkItemStateObserver extends StateObserver<WorkItem> {

    
    /**
     * Construct with {@link TaskTracker}
     * 
     */
    public WorkItemStateObserver() {
        super();
    }

    
    /**
     * Construct feeding {@link WorkItem} workload into {@link TaskTracker}
     * 
     * @param workload 
     */
    public WorkItemStateObserver(List<WorkItem> workload) {
        super();
        workload
            .stream()
            .parallel()
            .forEach(
                elm -> TaskTrackers.WORK_ITEM_TRACKER.markTask(elm.getId(), ExecutionState.QUEUED)
            );
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
            TaskTrackers.WORK_ITEM_TRACKER.markTask(taskId, ExecutionState.PREPARE);
            
            // Verify task is still available
            if ( verifyItem(task) ) {
                TaskTrackers.WORK_ITEM_TRACKER.markTask(taskId, ExecutionState.LOCKED);
                return ObserverResult.success();
            }
            
            // Mark task as skipped if nor running on another thread
            if ( !TaskTrackers.WORK_ITEM_TRACKER.isRunning(taskId) ) {
                TaskTrackers.WORK_ITEM_TRACKER.markTask(task.getId(), ExecutionState.SKIPPED);
                return ObserverResult.failure(this);
            }
            
            // Mark as skipped if running on another thread within JVM
            return ObserverResult.failure(this);
        }
        
        // Skip task if not running on another thread within JVM
        else {
            if ( !TaskTrackers.WORK_ITEM_TRACKER.isRunning(taskId) ) {
                TaskTrackers.WORK_ITEM_TRACKER.markTask(task.getId(), ExecutionState.SKIPPED);
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
        TaskTrackers.WORK_ITEM_TRACKER.markTask(task.getId(), ExecutionState.RUNNING);
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
            TaskTrackers.WORK_ITEM_TRACKER.markTask(task.getId(), ExecutionState.COMPLETED);
            return ObserverResult.success();
        }
        
        // Otherwise unlock work item
        else {
            task.setItemState(ItemState.ERROR);
            TaskTrackers.WORK_ITEM_TRACKER.markTask(task.getId(), ExecutionState.ABORTED);
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