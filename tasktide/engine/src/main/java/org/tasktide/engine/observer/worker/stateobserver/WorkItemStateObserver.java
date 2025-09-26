/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.engine.observer.worker.stateobserver;

import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasktide.core.manager.TaskTideManagerUtility;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.supporting.JsonUtils;

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

    private final Logger LOGGER = LogManager.getLogger(WorkItemStateObserver.class);
    private final Random RAND = new Random();
    
    
    /**
     * Construct with {@link TaskTrackers}
     * 
     */
    public WorkItemStateObserver() {
        super();
    }

    
    /**
     * Construct feeding {@link WorkItem} workload into {@link TaskTrackers}
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
                task.setJobEnvId(this.JOB_ENV_ID);
                return ObserverResult.success();
            }
            
            // Mark task as skipped if marked running on another thread
            if ( !TaskTrackers.WORK_ITEM_TRACKER.isRunning(taskId) ) {
                LOGGER.warn("Item locked already running");
                TaskTrackers.WORK_ITEM_TRACKER.markTask(task.getId(), ExecutionState.SKIPPED);
                return ObserverResult.failure(this);
            }
            
            // Mark as skipped if running on another thread within other JVM
            LOGGER.warn("Item could not be verified");
            return ObserverResult.failure(this);
        }
        
        // Skip task if not running on another thread within JVM
        else {
            if ( !TaskTrackers.WORK_ITEM_TRACKER.isRunning(taskId) ) {
                TaskTrackers.WORK_ITEM_TRACKER.markTask(task.getId(), ExecutionState.SKIPPED);
            }
            LOGGER.warn("Item not to do state. Displaying for reference:\n'{}'", JsonUtils.toJson(true, task));
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
        String msg = System.currentTimeMillis() + UUID.randomUUID().toString();
        String lockId = Base64.getEncoder().encodeToString(msg.getBytes());
        task.setItemState(ItemState.LOCKED);
        task.setLockDate( System.currentTimeMillis() );
        task.setLockId( lockId );
        
        // Update item
        TaskTideServiceManager.fetchWorkItemService().updateModel(task);
        try {TimeUnit.SECONDS.sleep(RAND.nextInt(4+1));}
        catch(InterruptedException ex) {return false;}
        
        // Fetch and verify active locked the task
        WorkItem check = TaskTideServiceManager.fetchWorkItemService().fetchById(task.getId());
        if (check != null) {
            return lockId.equals( check.getLockId() );
        }
        return false;
    }

    
    /**
     * Handles actions when task is being processed
     * 
     * @param task
     * @return {@link ObserverResult}
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
            TaskTideServiceManager.fetchWorkItemService().updateModel(task);
            return ObserverResult.success();
        }
        
        // Otherwise unlock work item
        else {
            task.setItemState(ItemState.ERROR);
            TaskTrackers.WORK_ITEM_TRACKER.markTask(task.getId(), ExecutionState.ABORTED);
            TaskTideServiceManager.fetchWorkItemService().updateModel(task);
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