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
package org.tasktide.engine.worker.executor;

import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;

import java.util.List;

import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideEngineObserver;

import org.tasktide.engine.worker.TaskTideWorkerUnit;
 

/**
 * Abstract class to handle the nuances of workload execution
 * 
 * @author bkenna
 * @param <T> of {@link ItemTask},{@link WorkItem}
 */
public abstract class TaskTideExecutor<T extends TaskTideModel<T>> implements TaskTideWorkerUnit<T> {
    
    // Attributes 
    protected final Logger LOGGER;
    protected static final AtomicInteger sharedCounter = new AtomicInteger(0);
    protected final ProcessExecutor processExecutor;
    protected int processCount;
    protected final TaskTideEngineObserver<T> observer;
    
    
    /**
     * Construct worker with workload and process runner
     * 
     * @param observer
     * @param LOGGER
     */
    public TaskTideExecutor(TaskTideEngineObserver<T> observer, Logger LOGGER) {
        this.observer = observer;
        this.LOGGER = LOGGER;
        this.processExecutor = new ProcessExecutor();
    }
    
    
    /**
     * Thread safe execution of tasks in a workload
     * 
     * @param workload of {@link ItemTask}, {@link WorkItem}
     */
    public void runTasks(List<T> workload) {
        
        // Process workload
        boolean allowed;
        int done = 0, failed = 0, skipped = 0;
        LOGGER.info(
            "Begining workload processing of N = '{}' tasks",
            workload.size()
        );
        for (T task : workload) {
            
            // Serialize preprocessing
            synchronized ( observer ) {
                allowed = observer.onTaskStart(task);
            }
            
            // Check state
            if ( !allowed ) {
                LOGGER.warn(
                    "Warning, skipping task failing Observer Preprocessing checks for task:\t'{}'", 
                    task.getId()
                );
                skipped++;
                continue;
            }
            
            // Try execute task
            try {
                
                // Execute task
                allowed = this.executeTask(task);
                if ( allowed ) {
                    
                    // Log progress & increment counters
                    this.processCount++;
                    LOGGER.info(
                        "Completed task '{}', global count:\t'{}'",
                        task.getId(), sharedCounter.incrementAndGet()
                    );
                }
                
                // Otherwise log failed
                else {
                    LOGGER.warn(
                        "Warning, execution completed with error for task:\t'{}'",
                        task.getId()
                    );
                }
                
                // Handle task clean-up
                //synchronized ( observer ) {
                    allowed = observer.onTaskEnd(task);
                //}
                if ( allowed ) {
                    LOGGER.info(
                        "Observer PostProcessing completed successfully for:\t'{}'",
                        task.getId()
                    );
                }
                else {
                    LOGGER.warn(
                        "Observer PostProcessing failed for:\t'{}'",
                        task.getId()
                    );
                }
            }
            
            catch ( IOException | InterruptedException ex ) {
                this.handleFailure(task, ex);
                failed++;
            }

            finally {
                LOGGER.info(
                    "Workload processing complete, displaying summary:\nThread Total = '{}', Thread Done = '{}', Done = '{}', Failed = '{}', Skipped = '{}'",
                    workload.size(), done, getGlobalCount(), failed, skipped
                );
            }
        }
    }
    
    
    /**
     * Abstract method to allow sub-classes to define logic around
     *  task execution
     * 
     * @param task
     * @return boolean
     * 
     * @throws IOException
     * @throws InterruptedException 
     */
    protected abstract boolean executeTask(T task) throws IOException, InterruptedException;
    
    
    /**
     * Abstract method to allow sub-classes to define logic around
     *  handling failed task execution
     * 
     * @param task
     * @param ex 
     */
    public void handleFailure(T task, Exception ex) {
        LOGGER.error(
            "Exception while executing task '{}' on thread '{}':\t'{}'\n\n{}",
            task.getId(), Thread.currentThread().getName(), ex.getMessage(), ex
        );
        if (ex instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    
    /**
     * Fetch value atomic counter
     * 
     * @return int
     */
    public static int getGlobalCount() {
        return sharedCounter.get();
    }
    
    
    /**
     * Get process count
     * 
     * @return int
     */
    public int getProcesCount() {
        return this.processCount;
    }
    
    
    /**
     * Increment process count
     */
    public void incrementCount() {
        this.processCount++;
    }
    
    
    /**
     * Decrement count
     */
    public void decrementCount() {
        this.processCount--;
    }
}