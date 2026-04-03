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

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
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
    private final TaskTideExecutor<ItemTask> itemTaskExecutor;
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    private final TaskTideEngineObserver<WorkItem> observer;
    private final TaskTideWorkloadTraverser<ItemTask> itemTaskTraverser;
    private final WorkerUnitContainer workerUnits;

    private int processCount;
    private boolean PARALLEL_CONTEXT = false;
    
    
    /**
     * Construct with default {@link TaskTideEngineObserver}
     * 
     * Throws {@link TaskTideEngineUncheckedException}
     */
    WorkItemTraverser() {
        this.workerUnits        = WorkerUnitContainer.getInstance();
        this.observer           = this.workerUnits.getEngineObserverChain(WorkerUnitModelType.WORKITEM);
        this.itemTaskExecutor   = this.workerUnits.getEngineExecutor(WorkerUnitModelType.ITEMTASK);
        this.itemTaskTraverser  = this.workerUnits.getEngineWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
    }
    
    
    /**
     * Construct with arguments
     * 
     * @param obs
     * @param iTT
     * @param exec 
     */
    WorkItemTraverser(TaskTideEngineObserver<WorkItem> obs, TaskTideWorkloadTraverser<ItemTask> iTT, TaskTideExecutor<ItemTask> exec) {
        this.workerUnits = WorkerUnitContainer.getInstance();
        this.observer = obs;
        this.itemTaskTraverser = iTT;
        this.itemTaskExecutor = exec;
    }
    
    
    /**
     * Construct with {@link TaskTideEngineObserver}
     * 
     * @param observer 
     * @param itemTaskTraverser 
     */
    WorkItemTraverser(TaskTideEngineObserver<WorkItem> observer, ItemTaskTraverser itemTaskTraverser) {
        this.workerUnits = WorkerUnitContainer.getInstance();
        this.observer = observer;
        this.itemTaskExecutor = this.workerUnits.getEngineExecutor(WorkerUnitModelType.ITEMTASK);
        this.itemTaskTraverser = itemTaskTraverser;
    }
    
    
    /**
     * Construct with {@link TaskTideEngineObserver}
     * 
     * @param observer 
     */
    WorkItemTraverser(TaskTideEngineObserver<WorkItem> observer) {
        this.observer = observer;
        this.workerUnits = WorkerUnitContainer.getInstance();
        this.itemTaskExecutor = this.workerUnits.getEngineExecutor(WorkerUnitModelType.ITEMTASK);
        this.itemTaskTraverser = this.workerUnits.getEngineWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
    }

    
    /**
     * Traverses provided workload processing elements passing validation
     * 
     * @param workload
     * 
     * @throws {@link TraverserCheckedException} 
     */
    @Override
    public void traverse(List<WorkItem> workload) throws TraverserCheckedException {
        
        // Initialize local coutners
        int skipped = 0;
        this.PARALLEL_CONTEXT = false;
        
        // Traverse through workload
        for ( WorkItem task : workload ) {
            if ( ! this.processTask(task) ) {
                skipped++;
            }
        }
    }
    
    
    /**
     * Schedules each {@link WorkItem} of provided workload into
     *  {@link ExecutorService} thread pool. Using the {@link FutureTrackers}
     *  container to fetch {@link TrackerWaiter}
     * 
     * @param workload
     * @param threadPool
     * 
     * @throws {@link TraverserCheckedException} 
     */
    @Override
    public void traverse(List<WorkItem> workload, ExecutorService threadPool) throws TraverserCheckedException {
        
        // Schedule tasks
        this.PARALLEL_CONTEXT = true;
        for ( WorkItem task : workload ) {
            
            // Schedule async operation
            Future<Boolean> future = threadPool.submit(() -> {
                return this.processElm(task);
            });
            
            // Append to tracker for monitoring
            ExecutorServiceItem<WorkItem> item = new ExecutorServiceItem<>(task, future);
            FutureTrackers.WORK_ITEM_TRACKER.markTask(task.getId(), item);
        }
        
        // Fetch waiter for WorkItem workload
        TrackerWaiter<WorkItem> trackerWaiter = FutureTrackers.WORK_ITEM_TRACKER.fetchWaiterFor(workload);
        trackerWaiter.waitForWorkload();
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
            boolean state = this.processTask(elm);
                
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
    
    
    /**
     * Processes provided task, entry point for passing to
     *  {@link ExecutorService}
     * 
     * @param task
     * @return boolean
     * 
     * @throws {@link TraverserCheckedException} 
     */
    private boolean processTask(WorkItem elm) throws TraverserCheckedException {
        
        // Pass if preprocessing fails
        //if ( ! this.observer.onTaskProcessing(elm) ) {
        //    LOGGER.warn("Warning, preprocessing failed for task:\t'{}'", elm.getId());
        //    return false;
        //}
        
        // Pass if no active tasks
        List<ItemTask> toDo = elm.getWorkload().fetchByState().get(TaskState.PENDING);
        if ( toDo.isEmpty() ) {
            LOGGER.warn("Warning, no active tasks under WorkItem:\t'{}'", elm.getId());
            return false;
        }
        
        // Otherwise process
        else {
            
            // Determine if work item has multiple item tasks
            if ( toDo.size() > 1 ) {
                LOGGER.info(
                    "Configuring ItemTaskTraverser for nested workload of:\t'{}'",
                    elm.getId()
                );
                return this.handleParallelContext(elm, toDo);
            }
            
            // Otherwise process as single task work item
            else {
                try {
                    if ( this.itemTaskExecutor.executeTask(toDo.get(0)) ) {
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
    
    
    /**
     * Uses the parallel context boolean field to determine
     *  how to use the {@link ItemTaskTraverser}
     * 
     * @param elm
     * @param toDo
     * @return boolean
     * 
     * @throws {@link TraverserCheckedException} 
     */
    private boolean
        handleParallelContext(WorkItem elm, List<ItemTask> toDo)
    throws TraverserCheckedException {
            
        // Passes ItemTask workload to executor service
        if ( PARALLEL_CONTEXT ) {
            ExecutorService execServ = this.workerUnits.getThreadPool(WorkerUnitModelType.ITEMTASK);
            try {
                LOGGER.info("Delegating WorkItem processing to ItemTask executor:\t'{}'", elm.getId());
                this.itemTaskTraverser.traverse(toDo, execServ);
                LOGGER.info("Execution completed");
                return true;
            }
            
            catch (TraverserCheckedException ex) {
                LOGGER.error("Error during WorkItem processing:\t'{}'\n\n{}", elm.getId(), ex);
                throw ex;
            }
        }
        
        // Otherwise processed seraially
        else {
            try {
                LOGGER.info("Delegating WorkItem processing to ItemTask executor:\t'{}'", elm.getId());
                this.itemTaskTraverser.traverse(toDo);
                LOGGER.info("Execution completed");
                return true;
            }
            
            catch (TraverserCheckedException ex) {
                LOGGER.error("Error during WorkItem processing:\t'{}'\n\n{}", elm.getId(), ex);
                throw ex;
            }
        }
    }
}