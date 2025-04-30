/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.processor;

import jakarta.inject.Inject;

import java.util.List;
import java.util.concurrent.ExecutorService;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.engine.executor.TaskTideExecutor;
import org.tasktide.engine.TaskTideEngineUnit;


/**
 * Abstract class for coordinating the processing tasks
 * 
 * @author bkenna
 * @param <T> of {@link ItemTask} or {@link WorkItem}
 */
public abstract class Processor<T extends TaskTideModel<T>> implements TaskTideEngineUnit<T> {
    
    
    // Attributes
    private final List<T> workload;
    protected final Logger logger = LogManager.getLogger(getClass());
    protected final int threshold;
    protected final ExecutorService executorService;
    
    
    /**
     * Construct parallel executor
     * 
     * @param workload
     * @param threshold
     * @param executorService
     */
    @Inject
    public Processor(List<T> workload, int threshold, ExecutorService executorService) {
        this.workload = workload;
        this.threshold = threshold;
        this.executorService = executorService;
    }
    
    
    /**
     * Process workload of {@link WorkItem}, {@link ItemTask}
     * 
     */
    public void execute() {
        
        // Process iteratively
        if ( this.workload.size() < this.threshold ) {
            logger.info("Processing tasks of workload thread:\t" + Thread.currentThread().getName() );
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
            newSubProcessor(subList).execute();
        });
    }
    
    
    /**
     * Create sub processor from sublist
     * 
     * @param subList
     * @return Processor-{@link WorkItem}, {@link ItemTask}
     */
    protected abstract Processor<T> newSubProcessor(List<T> subList);
    
    
    /**
     * Abstract method to provide worker class to handle nuances 
     *  of workload execution, {@link WorkItem}, {@link ItemTask}
     * 
     * @return 
     */
    protected abstract TaskTideExecutor<T> getExecutor();
}
