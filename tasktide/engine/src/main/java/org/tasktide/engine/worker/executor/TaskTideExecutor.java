/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.executor;

import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;

import java.util.List;

import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideWorkerObserver;
import org.tasktide.engine.worker.TaskTideWorkerUnit;

 
/**
 * Abstract class to handle the nuances of workload execution
 * 
 * @author bkenna
 * @param <T> of {@link ItemTask},{@link WorkItem}
 */
public abstract class TaskTideExecutor<T extends TaskTideModel<T>> implements TaskTideWorkerUnit<T> {
    
    
    // Attributes 
    protected final Logger logger;
    protected static final AtomicInteger sharedCounter = new AtomicInteger(0);
    protected final ProcessExecutor processExecutor;
    protected int processCount;
    protected TaskTideWorkerObserver<T> timeKeeper;
    
    
    /**
     * Construct worker with workload and process runner
     * 
     * @param observer
     * @param logger
     */
    public TaskTideExecutor(TaskTideWorkerObserver<T> observer, Logger logger) {
        this.timeKeeper = observer;
        this.logger = logger;
        this.processExecutor = new ProcessExecutor();
    }
    
    
    /**
     * Thread safe execution of tasks in a workload
     * 
     * @param workload of {@link ItemTask}, {@link WorkItem}
     */
    public void runTasks(List<T> workload) {
        
        // Process workload
        int done = 0, failed = 0, skipped = 0;
        logger.info("Begining workload processing of N = '{}' tasks", workload.size());
        for (T task : workload) {
            synchronized(task) {
            
                // Sanity check task is available
                //  timeKeeper.onTaskStart kind of breaks abstraction direction
                //  shouldExecute()  needs to be swapped out for StateObserver 
                if ( shouldExecute(task) && timeKeeper.onTaskStart(task) ) {
                    try {
                        
                        // Handle successful task execution
                        if ( executeTask(task) ) {
                            
                            // Increment logging counters
                            sharedCounter.incrementAndGet();
                            incrementCount();
                          
                            // Log progress
                            logger.info("Completed task '{}', global count: {}", task.getId(), sharedCounter.get());
                        }
                        
                        // Handle next task
                        timeKeeper.onTaskEnd(task);
                    }
                    catch ( IOException | InterruptedException ex ) {
                        handleFailure(task, ex);
                        failed++;
                    }
                }
                
                // Otherwise skip task
                else {
                    logger.info("Skipping previously processed task '{}'", task.getId());
                    skipped++;
                }
            }
        }
        
        // Log summary
        logger.info(
     "\n\nWorkload processing complete, displaying summary:\nThread Total = '{}', Thread Done = '{}', Done = '{}', Failed = '{}', Skipped = '{}'",
         workload.size(), done, getGlobalCount(), failed, skipped
        );
    }

    
    /**
     * Abstract method to allow sub-classes to define logic around whether
     *  a task should be processed, or skipped
     * 
     * @param task
     * @return boolean
     */
    protected abstract boolean shouldExecute(T task);
    
    
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
    protected abstract void handleFailure(T task, Exception ex);

    
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