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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.atomic.AtomicInteger;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.ItemTaskObserver;
import org.tasktide.engine.trackers.ExecutorServiceItem;
import org.tasktide.engine.trackers.FutureTrackers;
import org.tasktide.engine.trackers.TrackerWaiter;
import org.tasktide.engine.executor.ItemTaskExecutor;
import org.tasktide.engine.executor.TaskTideExecutor;


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
    
    
    /**
     * Constructs with default {@link ItemTaskObserver}
     * 
     */
    ItemTaskTraverser() {
        this.observer = new ItemTaskObserver();
        this.executor = new ItemTaskExecutor();
    }
    
    
    /**
     * Construct with {@link TaskTideEngineObserver}
     * 
     * @param observer 
     */
    ItemTaskTraverser(TaskTideEngineObserver<ItemTask> observer) {
        this.observer = observer;
        this.executor = new ItemTaskExecutor();
    }

    
    /**
     * Construct with {@link TaskTideEngineObserver} and {@link TaskTideExecutor}
     * 
     * @param observer 
     */
    ItemTaskTraverser(TaskTideEngineObserver<ItemTask> observer, TaskTideExecutor<ItemTask> itemTaskExec) {
        this.observer = observer;
        this.executor = (ItemTaskExecutor) itemTaskExec;
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
            boolean state = this.processTask(elm);
                
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

    
    /**
     * Process {@link ItemTask} workload
     * 
     * @param workload
     * 
     * @throws TraverserCheckedException 
     */
    @Override
    public void traverse(List<ItemTask> workload) throws TraverserCheckedException {
        
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
     * @param threadPool
     * 
     * @throws TraverserCheckedException 
     */
    @Override
    public void traverse(List<ItemTask> workload, ExecutorService threadPool) throws TraverserCheckedException {
        
        // Schedule tasks
        LOGGER.info("Submitting ItemTasks");
        for ( ItemTask task : workload ) {
            
            // Schedule async operation
            Future<Boolean> future = threadPool.submit(() -> {
                return this.processElm(task);
            });
            
            // Append to tracker for monitoring
            ExecutorServiceItem<ItemTask> item = new ExecutorServiceItem<>(task, future);
            FutureTrackers.ITEM_TASK_TRACKER.markTask(task.getId(), item);
            LOGGER.info(
                "Displaying registerred task:\n\n{}",
                JsonUtils.toJson(true, FutureTrackers.ITEM_TASK_TRACKER.getIds())
            );
        }
        
        // Fetch waiter for ItemTask workload
        LOGGER.info("Tasks submitted, fetching TrackerWaiter for workload");
        LOGGER.info(
            "Displaying elements:\n\n",
            JsonUtils.toJson(true, FutureTrackers.ITEM_TASK_TRACKER.getIds())
        );
        TrackerWaiter<ItemTask> trackerWaiter = FutureTrackers.ITEM_TASK_TRACKER.fetchWaiterFor(workload);
        LOGGER.info("Waiting on workload:\t'{}'", trackerWaiter.getId());
        trackerWaiter.waitForWorkload();
        LOGGER.info("Waiting complete");
    }
    
    
    /**
     * Processes provided task, entry point for passing to
     *  {@link ExecutorService}
     * 
     * @param task
     * @return boolean
     * 
     * @throws TraverserCheckedException 
     */
    private boolean processTask(ItemTask elm) throws TraverserCheckedException {
        
        
        // Pass if preprocessing fails
        if ( ! this.observer.onTaskProcessing(elm) ) {
            LOGGER.warn("Warning, preprocessing failed for task:\t'{}'", elm.getId());
            return false;
        }
        
        // Otherwise process
        else {
            try {
                if ( this.executor.executeTask(elm) ) {
                    LOGGER.info("Processing sucessful for task:\t'{}'", elm.getId());
                    return true;
                }
                else {
                    LOGGER.info("Processing unsuccessful for task:\t'{}'", elm.getId());
                    return false;
                }
            }
            catch ( IOException | InterruptedException ex ) {
                LOGGER.error(
                    "Error processing task:\t'{}'\n\n{}",
                    elm.getId(), ex
                );
                return false;
            }
        }
    }
}