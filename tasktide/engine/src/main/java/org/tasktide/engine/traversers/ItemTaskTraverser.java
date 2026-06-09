/*
 * Copyright 2026 Bren.
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
package org.tasktide.engine.traversers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.atomic.AtomicInteger;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.ItemTaskObserver;

import org.tasktide.engine.trackers.ExecutorServiceItem;
import org.tasktide.engine.trackers.FutureTrackers;
import org.tasktide.engine.trackers.TrackerWaiter;

import org.tasktide.engine.executor.ItemTaskExecutor;
import org.tasktide.engine.executor.TaskTideExecutor;
import org.tasktide.engine.processingstrategy.ItemTaskProcessingStrategy;
import org.tasktide.engine.workerunit.container.WorkerUnitContainer;
import org.tasktide.engine.workerunit.container.WorkerUnitModelType;
import org.tasktide.engine.workerunit.provider.TaskTideExecutorServiceProvider;


/**
 * Processes workload of {@link WorkItem}
 *
 * @author Bren
 */
public class ItemTaskTraverser implements TaskTideWorkloadTraverser<ItemTask> {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(WorkItemTraverser.class);
    private final ItemTaskExecutor executor;
    protected static final AtomicInteger sharedCounter = new AtomicInteger(0);
    protected int processCount;
    protected final TaskTideEngineObserver<ItemTask> observer;
    private final ItemTaskProcessingStrategy processingStrat;
    
    private final boolean PARALLEL_CONTEXT;
    private final WorkerUnitContainer workerUnits;
    
    
    /**
     * Constructs with default {@link ItemTaskObserver}
     * 
     */
    ItemTaskTraverser() {
        this.workerUnits = WorkerUnitContainer.getInstance();
        this.observer = new ItemTaskObserver();
        this.executor = new ItemTaskExecutor();
        this.processingStrat = new ItemTaskProcessingStrategy(this.observer, this.executor);
        this.PARALLEL_CONTEXT = TaskTideExecutorServiceProvider.getInstance().isParallelized();
    }
    
    
    /**
     * Construct with {@link TaskTideEngineObserver}
     * 
     * @param observer 
     */
    ItemTaskTraverser(TaskTideEngineObserver<ItemTask> observer) {
        this.workerUnits = WorkerUnitContainer.getInstance();
        this.observer = observer;
        this.executor = new ItemTaskExecutor();
        this.processingStrat = new ItemTaskProcessingStrategy(this.observer, this.executor);
        this.PARALLEL_CONTEXT = TaskTideExecutorServiceProvider.getInstance().isParallelized();
    }

    
    /**
     * Construct with {@link TaskTideEngineObserver} and {@link TaskTideExecutor}
     * 
     * @param observer 
     * @param itemTaskExec 
     */
    ItemTaskTraverser(TaskTideEngineObserver<ItemTask> observer, TaskTideExecutor<ItemTask> itemTaskExec) {
        this.workerUnits = WorkerUnitContainer.getInstance();
        this.observer = observer;
        this.executor = (ItemTaskExecutor) itemTaskExec;
        this.processingStrat = new ItemTaskProcessingStrategy(this.observer, this.executor);
        this.PARALLEL_CONTEXT = TaskTideExecutorServiceProvider.getInstance().isParallelized();
    }
    
    
    /**
     * Traverse provided workload
     * 
     * @param workload
     * @throws TraverserCheckedException 
     */
    @Override
    public void traverse(List<ItemTask> workload) throws TraverserCheckedException {
        
        // Parallel processing
        LOGGER.info("Determining how to process workload");
        if ( this.PARALLEL_CONTEXT ) {
            LOGGER.info("Processing workload in parallel");
            this.parallelSubmit(workload);
        }
        
        // Serial processing
        else {
            LOGGER.info("Processing workload serially");
            this.serialSubmit(workload);
        }
    }
    
    
    /**
     * Process {@link ItemTask} workload
     * 
     * @param workload
     * 
     * @throws TraverserCheckedException 
     */
    public void serialSubmit(List<ItemTask> workload) throws TraverserCheckedException {
        int skipped = 0;
        for ( ItemTask task : workload ) {
            if ( !this.processElm(task) ) {
                skipped++;
            }
        }
    }
    
    
    /**
     * Schedules each {@link ItemTask} of provided workload into
     *  {@link ExecutorService} thread pool. Using the {@link FutureTrackers}
     *  container to fetch {@link TrackerWaiter}
     * 
     * @param workload
     * 
     * @throws TraverserCheckedException 
     */
    public void parallelSubmit(List<ItemTask> workload) throws TraverserCheckedException {
        
        // Schedule tasks
        LOGGER.info("Fetching thread pool");
        ExecutorService threadPool = this.workerUnits.getThreadPool(WorkerUnitModelType.ITEMTASK);
        
        // Schedule tasks
        for ( ItemTask task : workload ) {
            
            // Schedule async operation
            Future<Boolean> future = threadPool.submit(() -> {
                try {
                    return this.processElm(task);
                }
                catch ( Exception ex ) {
                    return false;
                }
            });
            
            // Append to tracker for monitoring
            ExecutorServiceItem<ItemTask> item = new ExecutorServiceItem<>(task, future);
            FutureTrackers.ITEM_TASK_TRACKER.markTask(task.getId(), item);
        }
        
        // Fetch waiter for ItemTask workload
        LOGGER.info("Tasks submitted, fetching TrackerWaiter for workload");
        TrackerWaiter<ItemTask> trackerWaiter = FutureTrackers.ITEM_TASK_TRACKER.fetchWaiterFor(workload);
        LOGGER.info("Waiting on workload:\t'{}'", trackerWaiter.getId());
        trackerWaiter.waitForWorkload();
        LOGGER.info("ItemTask traversal completed from waiter:\t'{}'", trackerWaiter.getId());
    }
    

    /**
     * Process {@link ItemTask}
     * 
     * @param elm
     * @return boolean
     * 
     * @throws TraverserCheckedException 
     */
    @Override
    public boolean processElm(ItemTask elm) throws TraverserCheckedException {
        
        // Serialize preprocessing
        boolean shouldStart;
        synchronized ( observer ) {
            shouldStart = observer.onTaskStart(elm);
        }
            
        // Handle event outcome
        if ( shouldStart ) {
                
            // Process task
            LOGGER.info("Processing task:\t'{}'", elm.getId());
            boolean state = this.processingStrat.processTask(elm);
                
            // Log progress & increment counters
            if ( state ) {
                this.processCount++;
                LOGGER.info(
                    "Completed task '{}', global count:\t'{}'",
                    elm.getId(), sharedCounter.incrementAndGet()
                );
            }
                
            // Otherwise log failure
            else {
                LOGGER.warn(
                    "Warning, execution completed with error for task:\t'{}'",
                    elm.getId()
                );
            }
                
            // Perform onTaskEnd chores
            LOGGER.info(
                "Performing onTaskEnd chores:\t'{}'",
                elm.getId()
            );
            this.observer.onTaskEnd(elm);
            LOGGER.info(
                "Processing complete for task:\t'{}'",
                elm.getId()
            );
            return state;
        }
            
        // Otherwise skip
        else {
            LOGGER.warn(
                "Preprocessing failed for WorkItem:\t'{}'",
                elm.getId()
            );
            return false;
        }
    }
}