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
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.exceptions.TaskTideEngineCheckedException;

import org.tasktide.engine.policies.TargetedAcquisitionPolicy;
import org.tasktide.engine.policies.WorkerExecutionPolicy;

import org.tasktide.engine.workerunit.container.WorkerUnitContainer;
import org.tasktide.engine.workerunit.container.WorkerUnitModelType;

import org.tasktide.engine.traversers.TaskTideWorkloadTraverser;
import org.tasktide.engine.traversers.TraverserCheckedException;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;
import org.tasktide.engine.policies.WorkflowAcquisitionPolicy;
import org.tasktide.engine.policies.workflow.WorkflowStrategyType;


/**
 * Engine worker using the {@link TargetedAcquisitionPolicy} interface
 *  to acquire a workload to process, and process them through the
 *  {@link TaskTideWorkloadTraverser} interface. Update simplifies the
 *  TaskTide-EngineClient, and EngineUtility methods
 *
 * @author Bren
 */
public class TaskTideEngineWorker implements Cloneable {
    
    // Worker unit container
    private final Logger LOGGER = LogManager.getLogger(TaskTideEngineWorker.class);
    private final WorkerUnitContainer engineComponents;
    private final Random RAND = new Random(); 
    private final TaskTideWorkloadAcquisitionPolicy policy;

    private List<WorkerTask> tasks;
    private final ExecutorService workerPool;
    private final int windowSize, iterationLimit;
    private int iterCounter = 0;
    private String activeStep;
    private final boolean shouldCycle;
    
    
    /**
     * Construct with {@link TargetedAcquisitionPolicy}. Defaults
     *  shouldCycle property for WorkflowAcquisitionPolicy-Sequential
     *  running in Service mode to false.
     * 
     * @param policy 
     */
    public TaskTideEngineWorker(TaskTideWorkloadAcquisitionPolicy policy) {
        this.engineComponents = WorkerUnitContainer.getInstance();
        this.policy = policy;
        this.windowSize = this.policy.getWindowSize();
        this.iterationLimit = this.policy.getIterationLimit();
        this.workerPool = this.engineComponents.getThreadPool(WorkerUnitModelType.WORKITEM);
        this.shouldCycle = false;
    }
    
    
    /**
     * Construct with {@link TargetedAcquisitionPolicy}. Supplying
     *  shouldCycle property for WorkflowAcquisitionPolicy-Sequential
     *  running in Service mode to false.
     * 
     * @param policy 
     * @param shouldCycle 
     */
    public TaskTideEngineWorker(TaskTideWorkloadAcquisitionPolicy policy, boolean shouldCycle) {
        this.engineComponents = WorkerUnitContainer.getInstance();
        this.policy = policy;
        this.windowSize = this.policy.getWindowSize();
        this.iterationLimit = this.policy.getIterationLimit();
        this.workerPool = this.engineComponents.getThreadPool(WorkerUnitModelType.WORKITEM);
        this.shouldCycle = shouldCycle;
    }
    
    
    /**
     * Get the {@link TargetedAcquisitionPolicy}
     * 
     * @return {@link TargetedAcquisitionPolicy}
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
        
        // Check iteration counter against configured limit
        if ( this.iterationLimit > 1 ) {
            if ( this.iterCounter > this.iterationLimit ) {
                LOGGER.warn(
                    "Iteration limit '{}' breached after '{}' iterations, initiating shutdown",
                    this.iterationLimit,
                    this.iterCounter
                );
                return false;
            }
            this.iterCounter++;
        }
        
        // Check whether policy has more work
        return this.policy.hasNext();
    }
    
    
    /**
     * Consumes random samplings of tasks from
     *  {@link TaskTideWorkloadAcquisitionPolicy} until
     *  all tasks have been processed
     * 
     */
    public void fetchAndRun() {
    
        // Process tasks until done
        int iters = 0;
        while ( this.hasTasks() ) {
            LOGGER.info(
                "Processing available tasks iteration:\t'{}'",
                iters
            );
            boolean processingState = this.processSampling();
            LOGGER.info(
                "Iteration '{}' completed with state:\t'{}'",
                iters, processingState
            );
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
        boolean shouldRun = true, isWithinIterLimit = true;
        
        // Note functional mis-configuration
        if ( shouldCycle && this.policy.workflowMode() ) {
            LOGGER.warn("Sequential cycling detected, recommend using Round Robin instead");
        }
        
        // Repeatedly calls fetchAndRun until stopped
        while ( shouldRun ) {
            
            // Run the engine
            this.fetchAndRun();
            TaskTideEngineUtility.waitSeconds(RAND.nextInt(0, 11));
            counter++;
            
            // Evaluate iteration couter
            if ( this.iterationLimit > 1 ) {
                if ( counter > this.iterationLimit ) {
                    LOGGER.info(
                        "Service operation detected iteration limit breached, terminating:\t'{}' of '{}'",
                        counter, this.iterationLimit
                    );
                    shouldRun = false;
                    isWithinIterLimit = false;
                }
            }
            
            // Handle workflow mode
            if ( this.policy.workflowMode() ) {
                WorkflowAcquisitionPolicy pol = (WorkflowAcquisitionPolicy) this.policy;
                if ( pol.getStrategyType().isWorkflowStrategyType(WorkflowStrategyType.SEQUENTIAL) ) {
                    LOGGER.info("Handling next iteration of sequential workflow mode");
                    if ( isWithinIterLimit ) {
                        if ( this.shouldCycle ) {
                            LOGGER.info("Sequential cycling enabled, reconfiguring policy queue");
                            pol.reconfigureQueue();
                            this.iterCounter = 0;
                        }
                        else {
                            LOGGER.info("Sequential cycling disabled or iteration limit breached, terminating engine");
                            shouldRun = false;
                            isWithinIterLimit = false;
                            this.iterCounter = 0;
                        }
                    }
                }
                else if ( pol.getStrategyType().isWorkflowStrategyType(WorkflowStrategyType.ROUND_ROBIN) ) {
                    LOGGER.info("Handling next run over of round robin workflow mode");
                    if ( isWithinIterLimit ) {
                        LOGGER.info("Round Robin cycling enabled, reconfiguring policy queue");
                        pol.reconfigureQueue();
                        this.iterCounter = 0;
                    }
                    else {
                        LOGGER.info("Sequential cycling disabled or iteration limit breached, terminating engine");
                        shouldRun = false;
                        isWithinIterLimit = false;
                        this.iterCounter = 0;
                    }
                }
            }
        }
        
        // Log completion
        LOGGER.info("Engine processing completed after '{}' iterations", counter);
    }

    
    
    /**
     * Fetches randomly sorted workload from acquisition policy.
     *  Sampling a collection of tasks if configured
     * 
     * @return List-{@link WorkItem}
     */
    private List<WorkItem> sampleWorkload() {
        
        // Fetch workload
        List<WorkItem> workload = this.policy.fetchWorkload();
        
        // Log empty workload
        if ( workload.isEmpty() ) {
            LOGGER.info(
                "No more available tasks detected for EngineWorker '{}' ceasing",
                Thread.currentThread().getName()
            );
            return workload;
        }
        
        // Shuffle workload
        if ( workload.size() > 1 ) {
            LOGGER.info("Shuffling workload before processing");
            LOGGER.info("Shuffling list of size:\t'{}'", workload.size());
            Collections.shuffle(workload);
        }
        
        // Fetch a slice for processing
        if ( workload.size() > this.windowSize ) {
            LOGGER.info(
                "Processing retrieved workload of size '{}'",
                workload.size()
            );
            if ( this.windowSize > 1 ) {
                LOGGER.info(
                    "Sampling '{}' tasks from available pool '{}'",
                    this.windowSize, workload.size()
                );
                return workload.subList(0, windowSize);
            }
        }
        
        // Return workload
        return workload;
    }
    
    
    /**
     * Fetch workload from {@link TargetedAcquisitionPolicy},
     *  and process asynchronously
     * 
     * @throws {@link TaskTideEngineCheckedException}
     */
    private void sampleAndTraverse() throws TaskTideEngineCheckedException {
        
        // Process each step in order provided
        LOGGER.info("Configuring WorkItem-Traverser");
        TaskTideWorkloadTraverser<WorkItem> traverser =
            this.engineComponents
            .getEngineWorkloadTraverser(WorkerUnitModelType.WORKITEM);
        
        // Fetch workload
        List<WorkItem> workload = this.sampleWorkload();
        if ( workload.isEmpty() ) {
            throw new TaskTideEngineCheckedException("Error, cannot process an empty workload");
        }
        else {
            LOGGER.info(
                "Processing workload of size '{}'",
                workload.size()
            );
        }
        
        // Process workload
        try {
            LOGGER.info(
                "Begining thread '{}' processing of sampled '{}' tasks '{}'",
                Thread.currentThread(),
                this.activeStep,
                workload.stream().map(WorkItem::getId).toList()
            );
            traverser.traverse(workload);
            LOGGER.info(
                "Completed thread '{}' processing of sampled '{}' tasks '{}'",
                Thread.currentThread(),
                this.activeStep,
                workload.stream().map(WorkItem::getId).toList()
            );
        }
                
        catch ( TraverserCheckedException ex ) {
            LOGGER.error("Error processing workload:\n\n'{}'", ex);
            this.activeStep = "";
        }
    }

    
    /**
     * Serial or parallel execution of smapleAndTraverse. Parallelism
     *  dictated by pool size attribute. Since the smapleAndTraverse,
     *  and engine logic are self-container. 1 to poolSize can simply
     *  perform this asynchronously and main thread can wait on them.
     * 
     * @return boolean
     */
    private boolean processSampling() {
        
        // Process serially
        if ( this.policy.getPoolSize() < 1 ) {
            LOGGER.info("Performing serial sample and traverse");
            try {
                this.sampleAndTraverse();
                return true;
            }
            catch ( TaskTideEngineCheckedException ex ) {
                return false;
            }
        }
        
        // Otherwise recruit additional workers
        else  {
            
            // Submit tasks
            LOGGER.info(
                "Configured '{}' Parallel EngineWorkers, submitting work for active step '{}'",
                this.policy.getPoolSize(),
                this.policy.getTarget()
            );
            this.tasks = new ArrayList<>();
            for ( int i = 0; i < this.policy.getPoolSize(); i++) {
                LOGGER.info("Starting engine 'Worker-{}'", i);
                Future<?> future = this.workerPool.submit( () -> {
                    TaskTideEngineWorker workerClone = this.clone();
                    try {
                        workerClone.sampleAndTraverse();
                        return true;
                    }
                    catch ( TaskTideEngineCheckedException ex ) {
                        return false;
                    }
                });
                WorkerTask task = new WorkerTask("Task-" + i, future);
                this.tasks.add(task);
                LOGGER.info("Engine 'Worker-{}' started", i);
                TaskTideEngineUtility.waitSeconds(RAND.nextInt(0, 11));
            }
            LOGGER.info(
                "Step for the next Engine-Worker iteration is '{}'. Queue pre-increment was '{}'",
                this.policy.getTarget(),
                ( (WorkflowAcquisitionPolicy) this.policy).getQueueTargets()
            );
            
            // Wait for them to finish
            LOGGER.info(
                "Waiting on '{}' tasks across window sizes of '{}'",
                this.tasks.size(), this.windowSize
            );
            int counter = 0;
            for ( WorkerTask task : this.tasks ) {
                LOGGER.info(
                    "Waiting on task:\t'{}'",
                    task.getLabel()
                );
                if ( task.waitOnTask() ) {
                    LOGGER.info(
                        "Task '{}' completed successfully",
                        task.getLabel()
                    );
                    counter++;
                }
                LOGGER.warn(
                    "Task '{}' failed to cmplete",
                    task.getLabel()
                );
            }
            
            // Check if all finished
            LOGGER.info(
                "Engine workers completed '{}' of '{}' successful",
                counter, this.tasks.size()
            );
            return counter == this.tasks.size();
        }
    }
    
    
    /**
     * Clone active {@link TaskTideEngineWorker}
     * 
     * @return {@link TaskTideEngineWorker}
     */
    @Override
    public TaskTideEngineWorker clone() {
        return new TaskTideEngineWorker(this.policy.clonePolicy());
    }
    
    
    /**
     * Inner model class to hold task label and future
     * 
     */
    private class WorkerTask {
        
        // Attributes
        private final String label;
        private final Future task;
        
        
        /**
         * Construct {@link WorkerTask}
         * 
         * @param label
         * @param task 
         */
        WorkerTask(String label, Future task) {
            this.label = label;
            this.task = task;
        }
        
        
        /**
         * Get task label
         * 
         * @return String
         */
        public String getLabel() {
            return this.label;
        }
        
        
        /**
         * Get future
         * 
         * @return {@link Future}
         */
        public Future getTask() {
            return this.task;
        }
        
        
        /**
         * Wait on task
         * 
         * @return boolean
         */
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