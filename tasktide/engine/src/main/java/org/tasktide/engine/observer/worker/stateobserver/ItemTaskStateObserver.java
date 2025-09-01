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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.ObserverResult;
import org.tasktide.engine.observer.worker.StateObserver;

import org.tasktide.engine.trackers.ExecutionState;
import org.tasktide.engine.trackers.TaskTrackers;


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
     */
    public ItemTaskStateObserver() {
        super();
        this.logger = LogManager.getLogger(ItemTaskStateObserver.class);
    }

    
    /**
     * Construct feeding {@link ItemTask} workload into {@link TaskTrackers}
     * 
     * @param workload 
     */
    public ItemTaskStateObserver(List<ItemTask> workload) {
        super();
        this.logger = LogManager.getLogger(ItemTaskStateObserver.class);
        workload
            .stream()
            .parallel()
            .forEach(
                elm -> TaskTrackers.ITEM_TASK_TRACKER.markTask(elm.getId(), ExecutionState.QUEUED)
            );
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
            TaskTrackers.ITEM_TASK_TRACKER.markTask(task.getId(), ExecutionState.PREPARE);
            return ObserverResult.success();
        }
        else {
            TaskTrackers.ITEM_TASK_TRACKER.markTask(task.getId(), ExecutionState.SKIPPED);
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
        TaskTrackers.ITEM_TASK_TRACKER.markTask(task.getId(), ExecutionState.RUNNING);
        task.setTaskState(TaskState.ACTIVE);
        this.handleWorkItemUpdate(task);
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
            TaskTrackers.ITEM_TASK_TRACKER.markTask(task.getId(), ExecutionState.COMPLETED);
            this.handleWorkItemUpdate(task);
            return ObserverResult.success();
        }
        
        // Otherwise acknowledge error
        else {
            task.setTaskState(TaskState.ERROR);
            TaskTrackers.ITEM_TASK_TRACKER.markTask(task.getId(), ExecutionState.FAILED);
            this.handleWorkItemUpdate(task);
            logger.warn("Execution failed forItemTask:\t'{}'", task.getId());
            return ObserverResult.failure(this, true);
        }
    }
    
    
    /**
     * Handle updating parent {@link WorkItem} with {@link ItemTask}
     * 
     * @param task 
     */
    public void handleWorkItemUpdate(ItemTask task) {
        WorkItem ref = TaskTideServiceManager.fetchWorkItemService().fetchById(task.getWorkItemId());
        ref.dropTask(task);
        ref.addTask(task);
        TaskTideServiceManager.fetchWorkItemService().updateModel(ref);
    }
    
    
    /**
     * Return observer name
     * 
     * @return String
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
