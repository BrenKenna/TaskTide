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

import org.tasktide.engine.processingstrategy.WorkItemProcessingStrategy;
import org.tasktide.engine.processingstrategy.ProcessingStrategy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.engine.trackers.FutureTrackers;
import org.tasktide.engine.trackers.TrackerWaiter;
import org.tasktide.engine.trackers.ExecutorServiceItem;
import org.tasktide.engine.observer.TaskTideEngineObserver;

import org.tasktide.engine.workerunit.container.WorkerUnitContainer;
import org.tasktide.engine.workerunit.container.WorkerUnitModelType;

import org.tasktide.engine.executor.ItemTaskExecutor;
import org.tasktide.engine.executor.TaskTideExecutor;
import org.tasktide.engine.workerunit.provider.TaskTideExecutorServiceProvider;


/**
 * Class to hold logic for fetching a workload from the
 *  {@link TaskTideServiceManager}. Utilizing the
 *  {@link TaskTideEngineObserver}, {@link ItemTaskTraverser},
 *  and {@link ItemTaskExecutor} to handle how workload is
 *  consumed.
 *
 * @author Bren
 */
public class WorkItemTraverser implements TaskTideWorkloadTraverser<WorkItem> {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(WorkItemTraverser.class);
    
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    private final TaskTideEngineObserver<WorkItem> observer;
    private final ProcessingStrategy<WorkItem> processingStrat;
    private final WorkerUnitContainer workerUnits;

    private int processCount;
    
    private boolean PARALLEL_CONTEXT = TaskTideExecutorServiceProvider.getInstance().isParallelized();
    
    
    /**
     * Construct with default {@link TaskTideEngineObserver}
     * 
     * Throws {@link TaskTideEngineUncheckedException}
     */
    WorkItemTraverser() {
        this.workerUnits        = WorkerUnitContainer.getInstance();
        this.processingStrat    = new WorkItemProcessingStrategy();
        this.observer           = this.workerUnits.getEngineObserverChain(WorkerUnitModelType.WORKITEM);
        this.PARALLEL_CONTEXT   = TaskTideExecutorServiceProvider.getInstance().isParallelized();
    }
    
    
    /**
     * Construct with arguments
     * 
     * @param obs
     * @param exec 
     */
    WorkItemTraverser(TaskTideEngineObserver<WorkItem> obs, TaskTideExecutor<ItemTask> exec) {
        this.workerUnits = WorkerUnitContainer.getInstance();
        this.processingStrat = new WorkItemProcessingStrategy();
        this.observer = obs;
        this.PARALLEL_CONTEXT = TaskTideExecutorServiceProvider.getInstance().isParallelized();
    }
    
    
    /**
     * Construct with {@link TaskTideEngineObserver}
     * 
     * @param observer 
     */
    WorkItemTraverser(TaskTideEngineObserver<WorkItem> observer) {
        this.workerUnits = WorkerUnitContainer.getInstance();
        this.processingStrat = new WorkItemProcessingStrategy();
        this.observer = observer;
        this.PARALLEL_CONTEXT = TaskTideExecutorServiceProvider.getInstance().isParallelized();
    }

    
    /**
     * Traverse provided workload
     * 
     * @param workload
     * @throws TraverserCheckedException 
     */
    @Override
    public void traverse(List<WorkItem> workload) throws TraverserCheckedException {
        
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
     * Traverses provided workload processing elements passing validation
     * 
     * @param workload
     * 
     * @throws {@link TraverserCheckedException} 
     */
    public void serialSubmit(List<WorkItem> workload) throws TraverserCheckedException {
        
        // Initialize local coutners
        LOGGER.info("Traversing workload of size '{}' serially", workload.size());
        int skipped = 0, counter = 0;
        for ( WorkItem task : workload ) {
            LOGGER.info("Processing element '{}' of '':\t'{}'", counter, workload.size(), task.getId());
            if ( !this.processElm(task) ) {
                skipped++;
                LOGGER.info("Error processing task '' of '{}':\t", counter, workload.size(), task.getId());
            }
            LOGGER.info("Processing completed:\t'{}'", task.getId());
            counter++;
        }
        LOGGER.info("Processing completed for workload of size '{}'", workload.size());
    }
    
    
    /**
     * Schedules each {@link WorkItem} of provided workload into
     *  {@link ExecutorService} thread pool. Using the {@link FutureTrackers}
     *  container to fetch {@link TrackerWaiter}
     * 
     * @param workload
     * 
     * @throws {@link TraverserCheckedException} 
     */
    public void parallelSubmit(List<WorkItem> workload) throws TraverserCheckedException {
        
        // Schedule tasks
        LOGGER.info("Fetching thread pool");
        ExecutorService threadPool = this.workerUnits.getThreadPool(WorkerUnitModelType.WORKITEM);
        
        // Schedule tasks
        LOGGER.info("Processing workload of '{}' tasks", workload.size());
        for ( WorkItem task : workload ) {
            
            // Schedule async operation
            LOGGER.info("Submitting task:\t'{}'", task.getId());
            Future<Boolean> future = threadPool.submit(() -> {
                try {
                    return this.processElm(task);
                }
                catch ( Exception ex ) {
                    return false;
                }
            });
            
            // Append to tracker for monitoring
            LOGGER.info("Registering task in WorkItem-Tracker:\t'{}'", task.getId());
            ExecutorServiceItem<WorkItem> item = new ExecutorServiceItem<>(task, future);
            FutureTrackers.WORK_ITEM_TRACKER.markTask(task.getId(), item);
        }
        
        // Fetch waiter for WorkItem workload
        LOGGER.info("Tasks submitted, fetching TrackerWaiter for workload");
        TrackerWaiter<WorkItem> trackerWaiter = FutureTrackers.WORK_ITEM_TRACKER.fetchWaiterFor(workload);
        LOGGER.info("Waiting on workload:\t'{}'", trackerWaiter.getId());
        trackerWaiter.waitForWorkload();
        LOGGER.info("ItemTask traversal completed from waiter:\t'{}'", trackerWaiter.getId());
    }
    

    /**
     * Handles the pre-processing for {@link WorkItem}
     * 
     * @param elm
     * @return boolean
     * 
     * @throws {@link TraverserCheckedException} 
     */
    @Override
    public boolean processElm(WorkItem elm) throws TraverserCheckedException {
        
        // Check pending tasks
        LOGGER.info(
             "Configuring ItemTaskProcessor for Workload of size '{}' on thread '{}' for WorkItem:\t'{}'",
             elm.getTaskCount(), Thread.currentThread().getName(), elm.getId()
        );
            
        // Serialize preprocessing
        boolean shouldStart;
        synchronized ( this.observer ) {
            shouldStart = this.observer.onTaskStart(elm);
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
                    elm.getId(),
                    sharedCounter.incrementAndGet()
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
            LOGGER.info("Performing onTaskEnd chores:\t'{}'", elm.getId());
            this.observer.onTaskEnd(elm);
            LOGGER.info("Processing complete for task:\t'{}'", elm.getId());
            return state;
        }
            
        // Otherwise skip
        else {
            LOGGER.warn("Preprocessing failed for WorkItem:\t'{}'", elm.getId());
            return false;
        }
    }
}