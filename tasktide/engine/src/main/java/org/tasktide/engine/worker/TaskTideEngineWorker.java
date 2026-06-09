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
package org.tasktide.engine.worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.exceptions.TaskTideEngineCheckedException;

import org.tasktide.engine.policies.WorkItemAcquisitionPolicy;
import org.tasktide.engine.policies.WorkerExecutionPolicy;

import org.tasktide.engine.workerunit.container.WorkerUnitContainer;
import org.tasktide.engine.workerunit.container.WorkerUnitModelType;

import org.tasktide.engine.traversers.TaskTideWorkloadTraverser;
import org.tasktide.engine.traversers.TraverserCheckedException;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;


/**
 * Engine worker using the {@link WorkItemAcquisitionPolicy} interface
 *  to acquire a workload to process, and process them through the
 *  {@link TaskTideWorkloadTraverser} interface. Update simplifies the
 *  TaskTide-EngineClient, and EngineUtility methods
 *
 * @author Bren
 */
public class TaskTideEngineWorker {
    
    // Worker unit container
    private final Logger LOGGER = LogManager.getLogger(TaskTideEngineWorker.class);
    private final WorkerUnitContainer engineComponents;
    private final Random RAND = new Random(); 
    private final TaskTideWorkloadAcquisitionPolicy<WorkItem> policy;

    private List<WorkerTask> tasks;
    private ExecutorService workerPool;
    private final int windowSize;
    
    
    /**
     * Construct with {@link WorkItemAcquisitionPolicy}
     * 
     * @param policy 
     */
    public TaskTideEngineWorker(TaskTideWorkloadAcquisitionPolicy<WorkItem> policy) {
        this.engineComponents = WorkerUnitContainer.getInstance();
        this.policy = policy;
        this.windowSize = this.policy.getWindowSize();
        if ( policy.getPoolSize() > 1 ) {
            this.workerPool = Executors.newFixedThreadPool(policy.getPoolSize());
        }
    }
    
    
    /**
     * Get the {@link WorkItemAcquisitionPolicy}
     * 
     * @return {@link WorkItemAcquisitionPolicy}
     */
    public TaskTideWorkloadAcquisitionPolicy getPolicy() {
        return this.policy;
    }
    
    
    /**
     * Checks whether there are available tasks. Perhaps 
     *  a more suitable method of the policy? As it is basically#
     *   asking is the poloicy valid
     * 
     * @return boolean
     */
    private boolean hasTasks() {
        List<WorkItem> workload = new ArrayList<>(this.policy.fetchWorkload());
        return !workload.isEmpty();
    }
    
    
    /**
     * 
     * 
     */
    public void fetchAndRun() {
    
        // Process tasks until done
        int iters = 0;
        while ( this.hasTasks() ) {
            LOGGER.info("Processing available tasks iteration:\t'{}'", iters);
            boolean processingState = this.processSampling();
            LOGGER.info("Iteration '{}' completed with state:\t'{}'", iters, processingState);
            iters++;
            
        }
        LOGGER.info("No active tasks, engine worker shutting down after '{}' iterations", iters);
    }
    
    
    /**
     * Entry-point method for implementing classes 
     * 
     * @param executionPolicy
     * 
     * @throws {@link TaskTideEngineCheckedException}
     */
    public void runEngine(WorkerExecutionPolicy executionPolicy) throws TaskTideEngineCheckedException {
    
        switch ( executionPolicy ) {
        
            case SERVICE -> {
                LOGGER.info("Operating in service mode");
                this.serviceOperation();
            }
            
            case BATCH -> {
                LOGGER.info("Operating in batch mode");
                this.fetchAndRun();
            }
            
            default -> {
                LOGGER.error(
                    "Invalid Execution Policy detected '{}'. Must be one of:\t'{}'",
                    executionPolicy,
                    WorkerExecutionPolicy.valuesString()
                );
            }
        }
    }
    
    
    /**
     * Continuously scans {@link TaskTideRepository} for
     *  work asynchronously
     * 
     * @throws {@link TaskTideEngineCheckedException}
     */
    private void serviceOperation() throws TaskTideEngineCheckedException {
    
        // Perhaps allow a queue like a file being written?
        int counter = 0;
        while ( true ) {
            this.fetchAndRun();
            TaskTideEngineUtility.waitSeconds(RAND.nextInt(0, 11));
            counter++;
        }
    }
    
    
    /**
     * Fetches engine workload, checking if pilot label
     *  was used
     * 
     * @return List-{@link WorkItem}
     */
    private List<WorkItem> sampleWorkload() {
        List<WorkItem> workload = new ArrayList<>(this.policy.fetchWorkload());
        if ( workload.size() > 1 ) {
            Collections.shuffle(workload);
        }
        if ( workload.size() > this.windowSize ) {
            if ( this.windowSize > 1 ) {
                LOGGER.info("Sampling '{}' tasks from available pool '{}'", this.windowSize, workload.size());
                return workload.subList(0, windowSize);
            }
        }
        LOGGER.info("Processing retrieved workload of size '{}'", workload.size());
        return workload;
    }
    
    
    /**
     * Fetch workload from {@link WorkItemAcquisitionPolicy},
     *  and process asynchronously
     * 
     * @throws {@link TaskTideEngineCheckedException}
     */
    private void sampleAndTraverse() throws TaskTideEngineCheckedException {
        
        // Process each step in order provided
        LOGGER.info("Configuring WorkItem-Traverser for processing");
        TaskTideWorkloadTraverser<WorkItem> traverser =
            this.engineComponents
            .getEngineWorkloadTraverser(WorkerUnitModelType.WORKITEM);
        
        // Fetch workload
        LOGGER.info("Fetching workload");
        List<WorkItem> workload = this.sampleWorkload();
        if ( workload.isEmpty() ) {
            throw new TaskTideEngineCheckedException("Error, cannot process an empty workload");
        }
        else {
            LOGGER.info("Processing workload of size '{}'", workload.size());
        }
        
        // Process workload
        try {
            LOGGER.info(
                "Thread '{}' processing sampled '{}'",
                Thread.currentThread(),
                workload.stream().map(WorkItem::getId).toList()
            );
            traverser.traverse(workload);
            LOGGER.info("Processing complete for step:\t'{}'", this.policy.getTarget());
        }
                
        catch ( TraverserCheckedException ex ) {
            LOGGER.error("Error processing workload:\n\n'{}'", ex);
        }
    }

    
    /**
     * Process a sampling of workload
     * 
     * @return boolean
     */
    private boolean processSampling() {
        
        // If parellel option
        if ( this.policy.getPoolSize() >= 2 ) {
            
            // Submit tasks
            LOGGER.info("Configured '{}' Parallel EngineWorkers, submitting work", this.policy.getPoolSize());
            this.tasks = new ArrayList<>();
            for ( int i = 0; i < this.policy.getPoolSize(); i++) {
                LOGGER.info("Starting engine worker-'{}'", i);
                Future<?> future = this.workerPool.submit( () -> {
                    try {
                        this.sampleAndTraverse();
                        return true;
                    }
                    catch ( TaskTideEngineCheckedException ex ) {
                        return false;
                    }
                });
                WorkerTask task = new WorkerTask("Task-" + i, future);
                this.tasks.add(task);
                LOGGER.info("Engine worker-'{}' started, caching for reference", i);
                TaskTideEngineUtility.waitSeconds( RAND.nextInt(0, 11) );
            }
            
            // Wait for them to finish
            LOGGER.info("Waiting on '{}' to process window sizes of '{}'", this.tasks.size(), this.windowSize);
            int counter = 0;
            for ( WorkerTask task : this.tasks ) {
                LOGGER.info("Waiting on task:\t'{}'", task.getLabel());
                if ( task.waitOnTask() ) {
                    LOGGER.info("Task '{}' completed successfully", task.getLabel());
                    counter++;
                }
                LOGGER.warn("Task '{}' failed to cmplete", task.getLabel());
            }
            
            // Check if all finished
            LOGGER.info("Engine workers completed '{}' of '{}' successful", counter, this.tasks.size());
            return counter == this.tasks.size();
        }
        
        
        // Otherwise sample and run
        else {
            LOGGER.info("Performing serial sample and traverse");
            try {
                this.sampleAndTraverse();
                return true;
            }
            catch ( TaskTideEngineCheckedException ex ) {
                return false;
            }
        }
    }
    
    
    private class WorkerTask {
        private final String label;
        private final Future task;
        
        WorkerTask(String label, Future task) {
            this.label = label;
            this.task = task;
        }
        
        public String getLabel() {
            return this.label;
        }
        
        public Future getTask() {
            return this.task;
        }
        
        public boolean waitOnTask() {
            try {
                this.task.get();
                return true;
            } catch ( InterruptedException | ExecutionException ex) {
                return false;
            }
        }
    }
}