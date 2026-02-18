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

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.concurrent.Future;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.TaskTideExecutorServiceProvider;
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
     * @param workload
     * @param threshold
     * @param executorService
     */
    public ItemTaskProcessor(
        List<ItemTask> workload,
        @ConfigProperty(name = "task-tide.engine.worker.processor.threshold.itemtask", defaultValue = "2") int threshold,
        ExecutorService executorService
    ) {
        super(workload, threshold, executorService, LogManager.getLogger(ItemTaskProcessor.class), "WorkItemProcessor");
        this.worker = new ItemTaskExecutor();
    }
    
    
    /**
     * Construct with all attributes
     * 
     * @param workload
     * @param threshold
     * @param executorService
     * @param executor 
     */
    public ItemTaskProcessor(
        List<ItemTask> workload,
        @ConfigProperty(name = "task-tide.engine.worker.processor.threshold.itemtask", defaultValue = "2") int threshold,
        ExecutorService executorService,
        TaskTideExecutor<ItemTask> executor
    ) {
        super(workload, threshold, executorService, LogManager.getLogger(ItemTaskProcessor.class), "WorkItemProcessor");
        this.worker = (ItemTaskExecutor) executor;
    }

    
    /**
     * Create a new sub processor from self
     * 
     * @param subList
     * @return {@link TaskTideProcessor}-{@link ItemTask}
     */
    @Override
    protected TaskTideProcessor<ItemTask> newSubProcessor(List<ItemTask> subList) {
        return new ItemTaskProcessor(subList, threshold, executorService);
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
    
    
    /**
     * Divide workload across number of threads
     * 
     * @param workload
     * @return List-List-{@link ItemTask}
     */
    @Override
    protected List<List<ItemTask>> parallelChunks(List<ItemTask> workload) {
        
        // Initialize output
        int itemTaskThreads, batchSizes, totalChunks, totalTasks;
        List< List<ItemTask> > results = new ArrayList<>();
        
        // Pass on empty workload
        totalTasks = workload.size();
        if ( totalTasks == 0 ) {
            return results;
        }
        
        // Configure batches
        itemTaskThreads = TaskTideExecutorServiceProvider.getInstance().getItemTaskThreads();
        if ( itemTaskThreads <= 0 ) {
            throw new IllegalStateException("Error, ItemTask thread count must be > 0");
        }
        totalChunks = Math.min(itemTaskThreads, totalTasks);
        batchSizes = (int) Math.ceil( ( double ) totalTasks / totalChunks );
        
        // Fetch slices
        LOGGER.info(
            "Fetching N = '{}' batches of size '{}' for ItemTask workload",
            totalChunks,
            batchSizes
        );
        for ( int start = 0; start < workload.size(); start += batchSizes ) {
            int end = Math.min(start + batchSizes, totalTasks);
            results.add(workload.subList(start, end));
        }
        return results;
    }
}