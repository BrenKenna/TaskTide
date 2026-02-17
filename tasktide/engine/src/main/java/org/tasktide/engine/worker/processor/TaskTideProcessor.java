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

import jakarta.inject.Inject;

import java.util.Random;
import java.util.List;
import java.util.Collections;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.TaskTideWorkerUnit;
import org.tasktide.engine.trackers.TaskTrackers;


/**
 * Abstract class for coordinating the processing tasks
 * 
 * @author bkenna
 * @param <T> of {@link ItemTask} or {@link WorkItem}
 */
public abstract class TaskTideProcessor<T extends TaskTideModel<T>> implements TaskTideWorkerUnit<T> {
    
    // Attributes
    private final List<T> workload;
    protected final Logger LOGGER;
    protected final int threshold;
    protected final ExecutorService executorService;
    private final Random RAND = new Random();
    protected final String processorType;
    
    
    /**
     * Construct parallel executor
     * 
     * @param workload
     * @param threshold
     * @param executorService
     * @param LOGGER
     * @param processorType
     */
    @Inject
    public TaskTideProcessor(
        List<T> workload,
        int threshold,
        ExecutorService executorService,
        Logger LOGGER,
        String processorType
    ) {
        this.workload = workload;
        this.threshold = threshold;
        this.executorService = executorService;
        this.LOGGER = LOGGER;
        this.processorType = processorType;
    }
    
    
    /**
     * Process workload of {@link WorkItem}, {@link ItemTask}
     */
    public void process() {
        
        // Process iteratively
        if ( this.workload.size() <= this.threshold ) {
            LOGGER.info(
                "Processing tasks of workload thread:\t'{}', Size='{}', First ItemId = '{}'",
                Thread.currentThread().getName(),
                workload.size(),
                workload.get(0).getId()
            );
            if ( this.getExecutor() != null ) {
                try {TimeUnit.MILLISECONDS.sleep(RAND.nextInt(5+1));}
                catch(InterruptedException ex) {}
                this.getExecutor().runTasks(workload);
            }
            else {
                LOGGER.warn(
                    "Warning, workload size '{}' < threshold '{}' at start-up. Enqueing all tasks",
                    this.workload.size(),
                    this.threshold
                );
                for ( T task : this.workload ) {
                    this.submitSubTask(List.of(task));
                }
            }
        }
        
        // Recursively chunk and process
        else {
            
            // Handle left & right of midpoint
            int mid = workload.size() / 2;
            List<T> left = workload.subList(0, mid);
            List<T> right = workload.subList(mid, workload.size());
            // LOGGER.debug("Displaying left & right sizes:\n\nLeft:\t'{}'\nRight:\t'{}'", left.size(), right.size());
            
            // Submit tasks to processor
            submitSubTask(left);
            submitSubTask(right);
        }
    }
    
    
    /**
     * Submit list for processing and add to {@link TaskTrackers}
     * 
     * @param subList 
     * @return Future
     */
    protected Future<?> submitSubTask(List<T> subList) {
        Future<?> item = executorService.submit(( () -> {
            newSubProcessor(subList).process();
        }));
        this.addTasksToTracker(subList, item);
        return item;
    }
    
    
    /**
     * Subset workload into chunks based on requested number of threads
     * 
     * @param workload
     * @return List of List of {@link TaskTideModel}
     */
    protected abstract List<List<T>> parallelChunks(List<T> workload);
    
    
    /**
     * Process workload across threads
     * 
     * @param workload 
     */
    public void processChunks(List<T> workload) {
    
        // Initialize data
        LOGGER.info(
            "Shuffling, and grouping workload for ExecutorService for ProcessorType:\t'{}'",
            this.processorType
        );
        Collections.shuffle(workload);
        List<List<T>> chunks = this.parallelChunks(workload);
        
        // Submit
        LOGGER.info(
            "Submitting '{}' workload of size:\t'{}'",
            this.processorType,
            chunks.size()
        );
        for (List<T> chunk : chunks) {
            this.submitParallelChunks(chunk);
        }
        LOGGER.info(
            "Submitted N = '{}' items for workload '{}'",
            this.fetchTrackerTaskCount(),
            this.processorType
        );
    }
    
    
    /**
     * Wrapper method for processing chunks
     * 
     * @param chunk 
     */
    private void submitParallelChunks(List<T> chunk) {
        LOGGER.info("Submitting sub-workload of size:\t'{}'", chunk.size());
        for ( T task : chunk ) {
            this.submitParallelSubTask(List.of(task));
        }
    }
    
    
    /**
     * Submit task execution by {@link TaskTideExecutor} to the executor service, adding future
     *  to {@link TaskTrackers}
     * 
     * @param taskList
     * @return 
     */
    protected Future<?> submitParallelSubTask(List<T> taskList) {
        Future<?> item = this.executorService.submit(( () -> {
            newSubProcessor(taskList).getExecutor().runTasks(taskList);
        }));
        this.addTasksToTracker(taskList, item);
        return item;
    }
    
    
    /**
     * Adds tasks from subList to {@link TaskTrackers}
     * 
     * @param subList 
     * @param future 
     */
    protected abstract void addTasksToTracker(List<T> subList, Future future);
    
    
    /**
     * Fetch count of tasks registered in tracker
     * 
     * @return int
     */
    protected abstract int fetchTrackerTaskCount();
    
    
    /**
     * Wait for future list
     * 
     * @param futures 
     */
    private void waitForAll(List<Future<?>> futures) {
        for( Future<?> future : futures ) {
            try {
                future.get();
            }
            catch ( InterruptedException | ExecutionException ex) {
                LOGGER.error("Error encountered waiting on workload:\n{}", ex);
            }
        }
    }
    
    
    /**
     * Create sub processor from sublist
     * 
     * @param subList
     * @return TaskTideProcessor-{@link WorkItem}, {@link ItemTask}
     */
    protected abstract TaskTideProcessor<T> newSubProcessor(List<T> subList);
    
    
    /**
     * Abstract method to provide worker class to handle nuances 
     *  of workload execution, {@link WorkItem}, {@link ItemTask}
     * 
     * @return {@link TaskTideExecutor}-{@link WorkItem}, {@link ItemTask}
     */
    protected abstract TaskTideExecutor<T> getExecutor();
}