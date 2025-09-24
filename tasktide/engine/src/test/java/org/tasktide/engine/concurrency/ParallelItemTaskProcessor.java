/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.concurrency;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskLogging;


/**
 * Test class to support parallely processing of {@link ItemTask}
 * 
 * @author bkenna
 */
public class ParallelItemTaskProcessor extends RecursiveAction {
    
    // Attributes
    private final List<ItemTask> workload;
    private final int threshold;
    private final ProcessRunner runner;
    private int processCount; // Means to just log work per thread
    private final AtomicInteger sharedCounter; // Track work across threads
    
    
    /**
     * Construct parallel item task processor
     * 
     * @param workload
     * @param threshold 
     */
    public ParallelItemTaskProcessor(List<ItemTask> workload, int threshold) {
        this.workload = workload;
        this.threshold = threshold;
        this.runner = new ProcessRunner();
        this.processCount = 0;
        this.sharedCounter = new AtomicInteger(0);
    }

    
    /**
     * Process the workload
     */
    @Override
    protected void compute() {
        
        // Process iteratively
        if( this.workload.size() <= this.threshold ) {
            // Useful to log how much
            runTasks();
            // Useful to log any other details
        }
        
        // Half workload until threshold is reached
        else {
            
            // Fetch upper & lower list bounds
            ParallelItemTaskProcessor left = subTasks(true);
            ParallelItemTaskProcessor right = subTasks(false);

            // Process in parallel
            invokeAll(left, right);
        }
    }

    
    /**
     * Sub task current workload by half, returning ParallelItemTaskProcessor for them
     * 
     * @param flag
     * @return ParallelItemTaskProcessor
     */
    public ParallelItemTaskProcessor subTasks(boolean flag) {
        
        // Initialize vars
        ParallelItemTaskProcessor results;
        List<ItemTask> subTaskList;
        
        // Handle left of the workload mid-point
        if ( flag ) {
            subTaskList = this.workload.subList(0, this.workload.size()/2);
        }
        
        // Otherwise handle right of the workload mid-point
        else {
            subTaskList = this.workload.subList(this.workload.size()/2, this.workload.size());
        }
        
        // Return results
        results = new ParallelItemTaskProcessor(subTaskList, threshold);
        return results;
    }

    
    /**
     * Run each task from workload
     */
    public void runTasks() {
        for (ItemTask task : workload) {
            try {
                TaskLogging taskLog = runner.execute(task.getTask());
                task.setTaskLog(taskLog);
                processCount++;
                sharedCounter.incrementAndGet(); // Fetched
                
            }
            catch (IOException | InterruptedException ex) {
                ex.printStackTrace(); // Will log
            }
        }
    }
    
    
    /**
     * Return count of total tasks processed across all threads
     * 
     * @return int
     */
    public int getTotalExecuted() {
        return this.sharedCounter.get();
    }
    
    
    /**
     * Return process count of this thread
     * 
     * @return int
     */
    public int getProcessCount() {
        return processCount;
    }
}
