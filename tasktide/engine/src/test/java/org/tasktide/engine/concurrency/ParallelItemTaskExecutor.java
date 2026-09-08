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
package org.tasktide.engine.concurrency;

import jakarta.inject.Inject;
import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskLogging;
import org.tasktide.core.model.task.TaskState;


/**
 * Supporting test class to support executing work related to an {@link ItemTask}
 * 
 * @author bkenna
 */
@Deprecated
public class ParallelItemTaskExecutor {
    
    // Attributes
    private final Logger logger = LogManager.getLogger(ParallelItemTaskExecutor.class);
    private final List<ItemTask> workload;
    private final int threshold;
    private final ProcessRunner runner;
    private final ExecutorService executor;
    private int processCount = 0;
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);

    
    /**
     * Construct parallel executor
     * 
     * @param workload
     * @param threshold
     * @param executor 
     */
    @Inject
    public ParallelItemTaskExecutor(List<ItemTask> workload, int threshold, ExecutorService executor) {
        this.workload = workload;
        this.threshold = threshold;
        this.runner = new ProcessRunner();
        this.executor = executor; // Executors.newFixedThreadPool(10)
        this.processCount = 0;
    }
    
    
    /**
     * Process workload
     */
    public void execute() throws InterruptedException {
        
        // Process iteratively
        if ( this.workload.size() < this.threshold ) {
            logger.info("Processing tasks of workload thread:\t" + Thread.currentThread().getName() );
            runTasks();
        }
        
        // Half workload until threshold is reached
        else {
            
            // Fetch upper & lower list bounds
            logger.debug("Sub-tasking workload left on thread:\t" + Thread.currentThread().getName() );
            ParallelItemTaskExecutor left = subTasks(true);
            logger.debug("Sub-tasking workload right on thread:\t" + Thread.currentThread().getName() );
            ParallelItemTaskExecutor right = subTasks(false);

            // Process in parallel
            logger.debug("Executing workload left on thread:\t" + Thread.currentThread().getName() );
            executor.submit(()->{ try{left.execute();} catch(InterruptedException ex) {Thread.currentThread().interrupt();}} );
            logger.debug("Executing workload right on thread:\t" + Thread.currentThread().getName() );
            executor.submit(()->{ try{right.execute();} catch(InterruptedException ex) {Thread.currentThread().interrupt();}} );
        }
    }
    
    
    /**
     * Sub task current workload by half, returning ParallelItemTaskProcessor for them
     * 
     * @param flag
     * @return ParallelItemTaskProcessor
     */
    public ParallelItemTaskExecutor subTasks(boolean flag) {
        
        // Initialize vars
        ParallelItemTaskExecutor results;
        List<ItemTask> subTaskList;
        
        // Handle left of the workload mid-point
        if ( flag ) {
            logger.debug("Subetting workload on left:\t" + Thread.currentThread().getName() );
            subTaskList = this.workload.subList(0, this.workload.size()/2);
        }
        
        // Otherwise handle right of the workload mid-point
        else {
            logger.debug("Subetting workload on right:\t" + Thread.currentThread().getName() );
            subTaskList = this.workload.subList(this.workload.size()/2, this.workload.size());
        }
        
        // Return results
        results = new ParallelItemTaskExecutor(subTaskList, threshold, executor);
        return results;
    }

    
    /**
     * Coordinate execution of task across threads
     */
    public void runTasks() {
        
        // Syncronize task execution across threads
        int done = 0, failed = 0, skipped = 0;
        logger.info("Begining workload processing of N = '{}' tasks", workload.size());
        for (ItemTask task : workload) {
            synchronized (task) {
                
                // Process task if availble
                if (shouldExecute(task)) {
                    try {
                        executeTask(task);
                        done++;
                    } catch (IOException | InterruptedException ex) {
                        handleFailure(task, ex);
                        failed++;
                    }
                }
                
                // Otherwise pass
                else {
                    logger.info("Skipping already processed task: {}", task.getTaskName());
                    skipped++;
                }
            }
        }
        
        // Log summary
        logger.info(
     "\n\nWorkload processing complete, displaying summary:\nTotal = '{}', Done = '{}', Failed = '{}', Skipped = '{}'",
        workload.size(), done, failed, skipped
        );
    }

    
    /**
     * Handle whether task can be executed
     * 
     * @param task
     * @return boolean
     */
    private boolean shouldExecute(ItemTask task) {
        return task.getTaskState() == TaskState.PENDING;
    }

    
    /**
     * Execute task
     * 
     * @param task
     * @throws IOException
     * @throws InterruptedException 
     */
    private void executeTask(ItemTask task) throws IOException, InterruptedException {
        
        // Acknowledge task execution
        logger.info("Executing task on thread '{}':{}", Thread.currentThread().getName(), task.getTask());
        task.setTaskState(TaskState.ACTIVE);
        TaskLogging taskLog = runner.execute(task.getTask());
        task.setTaskLog(taskLog);

        // Handle successful task execution
        if (taskLog.getExitCode() == 0) {
            processCount++;
            int globalCount = sharedCounter.incrementAndGet();
            task.setTaskState(TaskState.COMPLETE);
            logger.info("Completed task '{}', global count: {}", task.getTaskName(), globalCount);
        }
        
        // Otherwise acknowledge error
        else {
            task.setTaskState(TaskState.ERROR);
            logger.error(
                "Task '{}' failed on thread '{}' with exit code {}\n",
                task.getTaskName(), Thread.currentThread().getName(), taskLog.getExitCode()
            );
        }

        // Debugger message
        logger.debug("Task after execution:\n{}\n\n", task.toJsonDoc());
    }

    
    /**
     * Handle failed task execution
     * 
     * @param task
     * @param ex 
     */
    private void handleFailure(ItemTask task, Exception ex) {
        task.setTaskState(TaskState.ERROR);
        logger.error(
            "Exception while executing task '{}' on thread '{}': {}",
            task.getTaskName(), Thread.currentThread().getName(), ex.getMessage(), ex
        );
        if (ex instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    
    /**
     * Return count of total tasks processed across all threads
     * 
     * @return int
     */
    public int getTotalExecuted() {
        return sharedCounter.get();
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
