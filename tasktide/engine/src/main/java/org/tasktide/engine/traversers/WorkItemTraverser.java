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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.trackers.ExecutorServiceItem;
import org.tasktide.engine.trackers.FutureTrackers;
import org.tasktide.engine.trackers.TrackerWaiter;
import org.tasktide.engine.worker.WorkerUnitContainer;
import org.tasktide.engine.worker.WorkerUnitModelType;
import org.tasktide.engine.worker.executor.ItemTaskExecutor;
import org.tasktide.engine.worker.executor.TaskTideExecutor;


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
    private int processCount;
    private final TaskTideEngineObserver<WorkItem> observer;
    private final TaskTideWorkloadTraverser<ItemTask> itemTaskTraverser;
    private final WorkerUnitContainer workerUnits;
    
    
    /**
     * Construct with {@link TaskTideEngineObserver}
     * 
     * @param observer 
     * @param itemTaskTraverser 
     */
    public WorkItemTraverser(TaskTideEngineObserver<WorkItem> observer, ItemTaskTraverser itemTaskTraverser) {
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
    public WorkItemTraverser(TaskTideEngineObserver<WorkItem> observer) {
        this.observer = observer;
        this.workerUnits = WorkerUnitContainer.getInstance();
        this.itemTaskExecutor = this.workerUnits.getEngineExecutor(WorkerUnitModelType.ITEMTASK);
        this.itemTaskTraverser = this.workerUnits.getEngineWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
    }
    
    
    /**
     * Construct with default {@link TaskTideEngineObserver}. 
     * Throws TaskTideEngineUncheckedException
     */
    public WorkItemTraverser() {
        this.workerUnits        = WorkerUnitContainer.getInstance();
        this.observer           = this.workerUnits.getEngineObserverChain(WorkerUnitModelType.WORKITEM);
        this.itemTaskExecutor   = this.workerUnits.getEngineExecutor(WorkerUnitModelType.ITEMTASK);
        this.itemTaskTraverser  = this.workerUnits.getEngineWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
    }
    
    
    /**
     * Traverses provided workload processing elements passing validation
     * 
     * @param workload
     * @throws TraverserCheckedException 
     */
    @Override
    public void traverse(List<WorkItem> workload) throws TraverserCheckedException {
        
        // Initialize local coutners
        int skipped = 0;
        
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
     * @throws TraverserCheckedException 
     */
    @Override
    public void traverse(List<WorkItem> workload, ExecutorService threadPool) throws TraverserCheckedException {
        
        // Schedule tasks
        for ( WorkItem task : workload ) {
            
            // Schedule async operation
            Future<Boolean> future = threadPool.submit(() -> {
                return this.processTask(task);
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
     * @throws TraverserCheckedException 
     */
    @Override
    public boolean processElm(WorkItem elm) throws TraverserCheckedException {
        
        // Pass if preprocessing fails
        if ( ! this.observer.onTaskProcessing(elm) ) {
            LOGGER.warn("Warning, preprocessing failed for task:\t'{}'", elm.getId());
            return false;
        }
        
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
                // this.itemTaskTraverser.traverse(toDo, this.threadPool);
                return true;
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
     * Processes provided task, entry point for passing to
     *  {@link ExecutorService}
     * 
     * @param task
     * @return boolean
     * 
     * @throws TraverserCheckedException 
     */
    private boolean processTask(WorkItem task) throws TraverserCheckedException {
        // Check pending tasks
        LOGGER.info(
             "Configuring ItemTaskProcessor for Workload of size '{}' on thread '{}' for WorkItem:\t'{}'",
             task.getTaskCount(), Thread.currentThread().getName(), task.getId()
        );
            
        // Serialize preprocessing
        boolean shouldStart;
        synchronized ( observer ) {
            shouldStart = observer.onTaskStart(task);
        }
            
        // Handle event outcome
        if ( shouldStart ) {
                
            // Process task
            LOGGER.info("Processing task:\t'{}'", task.getId());
            boolean state = this.processElm(task);
                
            // Log progress & increment counters
            if ( state ) {
                this.processCount++;
                LOGGER.info(
                    "Completed task '{}', global count:\t'{}'",
                    task.getId(),
                    sharedCounter.incrementAndGet()
                );
            }
                
            // Otherwise log failure
            else {
                LOGGER.warn(
                    "Warning, execution completed with error for task:\t'{}'",
                    task.getId()
                );
            }
                
            // Perform onTaskEnd chores
            LOGGER.info("Performing onTaskEnd chores:\t'{}'", task.getId());
            observer.onTaskEnd(task);
            LOGGER.info("Processing complete for task:\t'{}'", task.getId());
            return state;
        }
            
        // Otherwise skip
        else {
            LOGGER.warn("Preprocessing failed for WorkItem:\t'{}'", task.getId());
            return false;
        }
    }
    
    
    /**
     * Fetch every {@link WorkItem} marked {@link ItemState.TODO}
     * 
     * @return List-{@link WorkItem}
     */
    public List<WorkItem> fetchWorkload() {
        return TaskTideServiceManager
            .fetchWorkItemService()
            .viewByField(
                "itemState",
                ItemState.TODO
        );
    }
    
    
    /**
     * Fetch {@link WorkItem} marked {@link ItemState.TODO}
     *  across all tasks, random sampling
     * 
     * @param nTasks
     * @return List-{@link WorkItem}
     */
    public List<WorkItem> fetchWorkload(int nTasks) {
        
        // Fetch workload
        List<WorkItem> tasks = TaskTideServiceManager
            .fetchWorkItemService()
            .viewByField(
                "itemState",
                ItemState.TODO
        );
        
        // Handle no tasks
        if (tasks == null || tasks.isEmpty() || nTasks <= 0) {
            return Collections.emptyList();
        }
        
        // Shuffle and fetch sampling
        Collections.shuffle(tasks);
        int limit = Math.min(nTasks, tasks.size());
        return new ArrayList<>(tasks.subList(0, limit));
    }
    
    
    /**
     * Fetch {@link WorkItem} marked {@link ItemState.TODO}
     *  under the provided collection
     * 
     * @param collection
     * @return List-{@link WorkItem}
     */
    public List<WorkItem> fetchWorkload(String collection) {
        return TaskTideServiceManager
            .fetchWorkItemService()
            .viewByFieldForGroup(
                "itemState",
                ItemState.TODO,
                "stepName",
                collection
        );
    }
    
    
    /**
     * Fetch {@link WorkItem} marked {@link ItemState.TODO}
     *  under the provided collection, random sampling
     * 
     * @param collection
     * @param nTasks
     * @return List-{@link WorkItem}
     */
    public List<WorkItem> fetchWorkload(String collection, int nTasks) {
        
        // Fetch workload
        List<WorkItem> tasks = TaskTideServiceManager
            .fetchWorkItemService()
            .viewByFieldForGroup(
                "itemState",
                ItemState.TODO,
                "stepName",
                collection
        );
        
        // Handle no tasks
        if (tasks == null || tasks.isEmpty() || nTasks <= 0) {
            return Collections.emptyList();
        }
        
        // Shuffle and fetch sampling
        Collections.shuffle(tasks);
        int limit = Math.min(nTasks, tasks.size());
        return new ArrayList<>(tasks.subList(0, limit));
    }
    
    
    /**
     * Fetch to do work for collection with provided {@link CustomAnnotation} key-value
     * 
     * @param collection
     * @param key
     * @param value
     * 
     * @return List-{@link WorkItem}
     */
    public List<WorkItem> fetchToDoWorkTargetPilotLabel(String collection, String key, Object value) {
        return TaskTideServiceManager
            .fetchWorkItemService()
            .getRepo()
            .findByFieldForGroupWithAnno(
                "itemState",
                ItemState.TODO,
                "stepName",
                collection,
                key,
                value
        );
    }
    
    
    /**
     * Fetch todo for target collection with provided {@link CustomAnnotation}
     * 
     * @param collection
     * @param anno
     * @return List-{@link WorkItem}
     */
    public List<WorkItem> fetchToDoWorkTargetPilotLabel(String collection, CustomAnnotation anno) {
        return TaskTideServiceManager
            .fetchWorkItemService()
            .getRepo()
            .findByFieldForGroupWithAnno(
                "itemState",
                ItemState.TODO,
                "stepName",
                collection,
                anno
        );
    }
}