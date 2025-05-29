/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.processor;

import jakarta.inject.Inject;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.Logger;

import java.util.List;

import org.tasktide.core.TaskTideModel;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.TaskTideWorkerUnit;


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
        if ( this.workload.size() < this.threshold ) {
            logger.info("Processing tasks of workload thread:\t'{}'", Thread.currentThread().getName() );
            getExecutor().runTasks(workload);
        }
        
        // Recursively chunk and process
        else {
            
            // Handle left & right of midpoint
            int mid = workload.size() / 2;
            List<T> left = workload.subList(0, mid);
            List<T> right = workload.subList(mid, workload.size());
            
            // Submit tasks to processor
            submitSubTask(left);
            submitSubTask(right);
        }
    }
    
    
    /**
     * Submit list for processing
     * 
     * @param subList 
     */
    protected void submitSubTask(List<T> subList) {
        executorService.submit( () -> {
            newSubProcessor(subList).process();
        });
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
