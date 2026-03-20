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
package org.tasktide.engine.traverser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.tasktide.engine.worker.executor.ItemTaskExecutor;


/**
 * Class to hold logic for fetching a workload from the
 *  {@link TaskTideServiceManager}. Utilizing the
 *  {@link TaskTideEngineObserver}, {@link ItemTaskTraverser},
 *  and {@link ItemTaskExecutor} to handle how workload is
 *  consumed.
 *
 * @author Bren
 */
public class WorkItemTraverser implements WorkloadTraverser<WorkItem> {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(WorkItemTraverser.class);
    private final ItemTaskExecutor executor;
    protected static final AtomicInteger sharedCounter = new AtomicInteger(0);
    protected int processCount;
    protected final TaskTideEngineObserver<WorkItem> observer;
    
    
    /**
     * Construct with {@link TaskTideEngineObserver}
     * 
     * @param observer 
     */
    public WorkItemTraverser(TaskTideEngineObserver<WorkItem> observer) {
        this.observer = observer;
        this.executor = new ItemTaskExecutor();
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
                        task.getId(), sharedCounter.incrementAndGet()
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
            }
            
            // Otherwise skip
            else {
                LOGGER.warn("Preprocessing failed for WorkItem:\t'{}'", task.getId());
                skipped++;
            }
        }
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
                // Pass toDo to ItemTaskTraverser
                return true;
            }
            
            // Otherwise process as single task work item
            else {
                try {
                    if ( this.executor.executeTask(toDo.get(0)) ) {
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