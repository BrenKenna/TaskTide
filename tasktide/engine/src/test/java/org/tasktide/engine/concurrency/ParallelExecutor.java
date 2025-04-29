/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.concurrency;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.List;
import java.util.Random;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskLogging;


/**
 *
 * @author bkenna
 */
public class ParallelExecutor {
    
    // Attributes
    private final List<ItemTask> workload;
    private final int threshold;
    private final ProcessRunner runner;
    private final ExecutorService executor;
    private int processCount = 0;
    private final AtomicInteger sharedCounter;
    private Random rand;

    
    /**
     * 
     * @param workload
     * @param threshold
     * @param executor 
     */
    @Inject
    public ParallelExecutor(List<ItemTask> workload, int threshold, ExecutorService executor) {
        this.workload = workload;
        this.threshold = threshold;
        this.runner = new ProcessRunner();
        this.executor = executor;
        this.rand = new Random();
        this.processCount = 0;
        this.sharedCounter = new AtomicInteger(0);
    }
    
    
    /**
     * Process workload
     */
    public void execute() {
        
        // Process iteratively
        if ( this.workload.size() < this.threshold ) {
            runTasks();
        }
        
        // Half workload until threshold is reached
        else {
            // Fetch upper & lower list bounds
            ParallelExecutor left = subTasks(true);
            ParallelExecutor right = subTasks(false);

            // Process in parallel
            executor.submit( () -> left.execute());
            executor.submit( () -> right.execute());
        }
    }
    
    
    /**
     * Sub task current workload by half, returning ParallelItemTaskProcessor for them
     * 
     * @param flag
     * @return ParallelItemTaskProcessor
     */
    public ParallelExecutor subTasks(boolean flag) {
        
        // Initialize vars
        ParallelExecutor results;
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
        results = new ParallelExecutor(subTaskList, threshold, executor);
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
                processCount++; // logger.info("Thread %s — Executing task %d of %d%n", threadName, processCount, workload.size());
                sharedCounter.incrementAndGet(); // Fetched
                
            }
            catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
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
