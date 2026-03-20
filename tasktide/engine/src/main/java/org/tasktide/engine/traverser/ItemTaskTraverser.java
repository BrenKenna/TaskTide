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
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.atomic.AtomicInteger;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.worker.executor.ItemTaskExecutor;


/**
 * Processes workload of {@link WorkItem}
 *
 * @author Bren
 */
public class ItemTaskTraverser implements WorkloadTraverser<ItemTask> {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(WorkItemTraverser.class);
    private final ItemTaskExecutor executor;
    protected static final AtomicInteger sharedCounter = new AtomicInteger(0);
    protected int processCount;
    protected final TaskTideEngineObserver<ItemTask> observer;
    
    
    /**
     * Construct with {@link TaskTideEngineObserver}
     * 
     * @param observer 
     */
    public ItemTaskTraverser(TaskTideEngineObserver<ItemTask> observer) {
        this.observer = observer;
        this.executor = new ItemTaskExecutor();
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
                LOGGER.info(
                    "Performing onTaskEnd chores:\t'{}'",
                    task.getId()
                );
                observer.onTaskEnd(task);
                LOGGER.info(
                    "Processing complete for task:\t'{}'",
                    task.getId()
                );
            }
            
            // Otherwise skip
            else {
                LOGGER.warn(
                    "Preprocessing failed for WorkItem:\t'{}'",
                    task.getId()
                );
                skipped++;
            }
        }
    }
}