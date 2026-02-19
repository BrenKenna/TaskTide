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
package org.tasktide.engine.worker.processor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;


import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.trackers.ExecutorServiceItem;
import org.tasktide.engine.trackers.FutureTrackers;

import org.tasktide.engine.worker.executor.ItemTaskExecutor;
import org.tasktide.engine.worker.executor.TaskTideExecutor;


/**
 * Concrete {@link ItemTask} {@link TaskTideProcessor}
 * 
 * @author bkenna
 */
public class ItemTaskProcessor extends TaskTideProcessor<ItemTask> {

    // Attributes
    private final ItemTaskExecutor worker;
    
    
    /**
     * Construct with workload
     * 
     * @param executorService
     */
    public ItemTaskProcessor(
        ExecutorService executorService
    ) {
        super(executorService, LogManager.getLogger(ItemTaskProcessor.class), ProcessorType.ITEM_TASK);
        this.worker = new ItemTaskExecutor();
    }
    
    
    /**
     * Construct with all attributes
     * 
     * @param executorService
     * @param executor 
     */
    public ItemTaskProcessor(
        ExecutorService executorService,
        TaskTideExecutor<ItemTask> executor
    ) {
        super(executorService, LogManager.getLogger(ItemTaskProcessor.class), ProcessorType.ITEM_TASK);
        this.worker = (ItemTaskExecutor) executor;
    }

    
    /**
     * Create a new sub processor from self
     * 
     * @return {@link TaskTideProcessor}-{@link ItemTask}
     */
    @Override
    protected TaskTideProcessor<ItemTask> newSubProcessor() {
        return new ItemTaskProcessor(executorService);
    }

    
    /**
     * Provide {@link ItemTask} worker
     * 
     * @return {@link TaskTideExecutor}-{@link ItemTask}
     */
    @Override
    protected TaskTideExecutor<ItemTask> getExecutor() {
        return this.worker;
    }
    
    
    /**
     * Add workload to the {@link FutureTrackers}
     * 
     * @param subList
     * @param future 
     */
    @Override
    protected void addTasksToTracker(List<ItemTask> subList, Future future) {
        for ( ItemTask task : subList ) {
            ExecutorServiceItem<ItemTask> item = new ExecutorServiceItem<>(task, future);
            FutureTrackers.ITEM_TASK_TRACKER.markTask(task.getId(), item);
        }
    }
    
    
    /**
     * Fetch records in ItemTask Future tracker
     * 
     * @return int
     */
    @Override
    protected int fetchTrackerTaskCount() {
        return FutureTrackers.ITEM_TASK_TRACKER.taskCount();
    }
}