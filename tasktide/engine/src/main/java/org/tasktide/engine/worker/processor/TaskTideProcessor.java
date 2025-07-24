/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.processor;

import jakarta.inject.Inject;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
    protected final Logger logger;
    protected final int threshold;
    protected final ExecutorService executorService;
    
    
    /**
     * Construct parallel executor
     * 
     * @param workload
     * @param threshold
     * @param executorService
     * @param logger
     */
    @Inject
    public TaskTideProcessor(
        List<T> workload,
        int threshold,
        ExecutorService executorService,
        Logger logger
    ) {
        this.workload = workload;
        this.threshold = threshold;
        this.executorService = executorService;
        this.logger = logger;
    }
    
    
    /**
     * Process workload of {@link WorkItem}, {@link ItemTask}
     */
    public void process() {
        
        // Process iteratively
        if ( this.workload.size() <= this.threshold ) {
            logger.info(
          "Processing tasks of workload thread:\t'{}', Size='{}', First ItemId = '{}'",
             Thread.currentThread().getName(), workload.size(), workload.get(0).getId()
            );
            if ( this.getExecutor() != null ) {
                this.getExecutor().runTasks(workload);
            }
            else {
                logger.warn(
              "Warning, workload size '{}' < threshold '{}' at start-up. Enqueing all tasks",
                    this.workload.size(), this.threshold
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
            // logger.debug("Displaying left & right sizes:\n\nLeft:\t'{}'\nRight:\t'{}'", left.size(), right.size());
            
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
        List<List<T>> chunks = parallelChunks(workload);
        
        // Submit
        for (List<T> chunk : chunks) {
            submitParallelChunks(chunk);
        }
    }
    
    
    /**
     * Wrapper method for processing chunks
     * 
     * @param workload 
     */
    private void submitParallelChunks(List<T> workload) {
        for ( T task : this.workload ) {
            this.submitParallelSubTask(List.of(task));
        }
    }
    
    
    /**
     * Submit task execution by {@link TaskTideExecutor} to the executor service, adding future
     *  to {@link TaskTrackers}
     * 
     * @param task
     * @return 
     */
    protected Future<?> submitParallelSubTask(List<T> task) {
        Future<?> item = executorService.submit(( () -> {
            newSubProcessor(task).getExecutor().runTasks(task);
        }));
        this.addTasksToTracker(task, item);
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
                logger.error("Error encountered waiting on workload: {}", ex);
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
