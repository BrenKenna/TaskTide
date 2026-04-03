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
package org.tasktide.engine.deprecated_processor;

import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.engine.executor.TaskTideExecutor;
import org.tasktide.engine.workerunit.TaskTideWorkerUnit;
import org.tasktide.engine.trackers.TaskTrackers;


/**
 * Abstract class for coordinating the processing tasks
 * 
 * @author bkenna
 * @param <T> of {@link ItemTask} or {@link WorkItem}
 */
public abstract class TaskTideProcessor<T extends TaskTideModel<T>> implements TaskTideWorkerUnit<T> {
    
    // Attributes
    protected final Logger LOGGER;
    protected final ExecutorService executorService;
    protected final ProcessorType processorType;
    
    
    /**
     * Construct parallel executor
     * 
     * @param executorService
     * @param LOGGER
     * @param processorType
     */
    @Inject
    public TaskTideProcessor(
        ExecutorService executorService,
        Logger LOGGER,
        ProcessorType processorType
    ) {
        this.executorService = executorService;
        this.LOGGER = LOGGER;
        this.processorType = processorType;
    }
    
    
    /**
     * Subset workload into chunks based on requested number of threads
     * 
     * @param workload
     * @return List of List of {@link TaskTideModel}
     */
    protected List<List<T>> groupWorkload(List<T> workload) {
    
        // Initialize output
        int processorThreads, batchSizes, totalChunks, totalTasks;
        List< List<T> > results = new ArrayList<>();
        
        // Pass on empty workload
        totalTasks = workload.size();
        if ( totalTasks == 0 ) {
            return results;
        }
        
        // Initialize batch handler
        processorThreads = this.processorType.getExecutorServiceThreads();
        if ( processorThreads <= 0 ) {
            String msg = String.format(
               "Error, '{}-Processor' thread count must be > 0", 
               this.processorType
            );
            throw new IllegalStateException(msg);
        }
        totalChunks = Math.min(processorThreads, totalTasks);
        batchSizes = (int) Math.ceil( ( double ) totalTasks / totalChunks );
        
        // Fetch slices
        LOGGER.info(
            "Fetching N = '{}' batches of size '{}' for '{}' from '{}-Processor'",
            processorThreads,
            batchSizes,
            this.processorType
        );
        for ( int start = 0; start < workload.size(); start += batchSizes ) {
            int end = Math.min(start + batchSizes, totalTasks);
            results.add(workload.subList(start, end));
        }
        return results;
    }
    
    
    /**
     * Process workload across threads
     * 
     * @param workload 
     */
    public void process(List<T> workload) {
    
        // Initialize data
        LOGGER.info(
            "Shuffling, and grouping workload for ExecutorService for ProcessorType:\t'{}'",
            this.processorType
        );
        Collections.shuffle(workload);
        List<List<T>> chunks = this.groupWorkload(workload);
        
        // Submit
        List<Future<?>> tasks = new ArrayList<>();
        LOGGER.info(
            "Submitting '{}-Processor' workload of size:\t'{}'",
            this.processorType,
            chunks.size()
        );
        for (List<T> chunk : chunks) {
            Future<?> task = this.submitChunk(chunk);
            tasks.add(task);
        }
        
        // Log submission
        LOGGER.info(
            "'{}-Processor' submitted N = '{}' items for processing across N = '{}' batches",
            this.processorType,
            workload.size(),
            chunks.size()
        );
        
        // Wait on work
        LOGGER.info("Waiting on batch completion");
        for ( Future<?> task : tasks ) {
            try {
                task.get();
            }
            catch ( InterruptedException | ExecutionException ex ) {
                LOGGER.error(
                    "Displaying execution error:\t'{}'\n\n'{}'",
                    ex.getMessage(), ex
                );
            }
        }
        LOGGER.info("Processing complete");
    }
    
    
    /**
     * Wrapper method for processing chunks
     * 
     * @param chunk 
     */
    private Future<?> submitChunk(List<T> chunk) {
        LOGGER.info("Submitting sub-workload of size:\t'{}'", chunk.size());
        LOGGER.info("Elements of chunk displayed below:\n\n'{}'", JsonUtils.toJson(true, chunk));
        return this.submitSubTask(chunk);
    }
    
    
    /**
     * Submit task execution by {@link TaskTideExecutor} to the executor service, adding future
     *  to {@link TaskTrackers}
     * 
     * @param taskList
     * @return 
     */
    protected Future<?> submitSubTask(List<T> taskList) {
        Future<?> item = this.executorService.submit(( () -> {
           TaskTideProcessor<T> subProcessor = this.newSubProcessor();
           subProcessor.getExecutor().runTasks(taskList);
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
     * Create a sub processor
     * 
     * @return TaskTideProcessor-{@link WorkItem}, {@link ItemTask}
     */
    protected abstract TaskTideProcessor<T> newSubProcessor();
    
    
    /**
     * Abstract method to provide worker class to handle nuances 
     *  of workload execution, {@link WorkItem}, {@link ItemTask}
     * 
     * @return {@link TaskTideExecutor}-{@link WorkItem}, {@link ItemTask}
     */
    protected abstract TaskTideExecutor<T> getExecutor();
}