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
 * counterations under the License.
 */
package org.tasktide.engine.policies.workflow;

import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;


/**
 * Round robin strategy to consume steps of workflow FIFO queue
 *
 * @author Bren
 */
public class RoundRobinWorkflowStrategy extends AbstractWorkflowStrategy {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(RoundRobinWorkflowStrategy.class);
    private static AtomicBoolean canIncrement;
    
    
    /**
     * Construct with {@link TaskTideWorkloadAcquisitionPolicy} list, {@link WorkflowStrategyMode}, and iteration limit
     * 
     * @param policies
     * @param mode
     * @param limit 
     */
    RoundRobinWorkflowStrategy(List<TaskTideWorkloadAcquisitionPolicy> policies, WorkflowStrategyMode mode, int limit) {
        super("RoundRobinWorkflowStrategy-" + UUID.randomUUID().toString(), policies, mode, limit);
        RoundRobinWorkflowStrategy.canIncrement = new AtomicBoolean(false);
    }
    
    
    /**
     * Construct with previous state
     * 
     * @param policies
     * @param stateWorkflow
     * @param stateActivePolicy
     * @param mode
     * @param limit 
     */
    RoundRobinWorkflowStrategy(
        List<TaskTideWorkloadAcquisitionPolicy> policies,
        Deque<TaskTideWorkloadAcquisitionPolicy> stateWorkflow,
        TaskTideWorkloadAcquisitionPolicy stateActivePolicy,
        WorkflowStrategyMode mode,
        int limit
    ) {
        super("RoundRobinWorkflowStrategy-" + UUID.randomUUID().toString(), policies, mode, limit);
    }
    
    
    /**
     * Fetches {@link WorkItem} collection according to configured {@link TaskTideWorkloadAcquisitionPolicy}.
     *  Stateless round robin processing of steps in the workflow, where flow is determined by the configured
     *  {@link WorkflowStrategyMode}
     * 
     * 
     * @return List-{@link WorkItem}
     */
    @Override
    public List<WorkItem> fetchWorkload() {
        
        // Initialize variables
        List<WorkItem> output;
        if (policies.isEmpty()) {
            LOGGER.warn("No policies configured, passing");
            return Collections.emptyList();
        }
        
        // Evalute counter if configured
        if ( !this.evaluateIterationCounter() ) {
            LOGGER.info("Workflow iteration counter breached");
            return Collections.emptyList();
        }
        
        // Configure round robin mode
        this.configureDefaultStrategyMode();
        
        // Fetch workload
        LOGGER.info(
            "Fetching workload for active target:\t'{}'",
            this.activePolicy.getTarget()
        );
        output = this.activePolicy.fetchWorkload();
        
        // Handle results
        output = this.evaluateWorkload(output);
        if ( output.isEmpty() ) {
            LOGGER.info("Workflow processing complete");
        }
        else {
            LOGGER.info("N tasks detected = '{}'", output.size());
        }
        return output;
    }

    
    /**
     * Configure default {@link WorkflowStrategyMode.SCANNER}
     *  for {@link WorkflowAcquisitionStrategy}
     * 
     */
    @Override
    public void configureDefaultStrategyMode() {
        if ( this.mode == null ) {
            LOGGER.info(
                "Defaulting Round Robin mode to:\t'{}'",
                WorkflowStrategyMode.SCANNER
            );
            this.mode = WorkflowStrategyMode.SCANNER;
        }
    }

    
    /**
     * Evaluate retrieved workload to configured
     *  {@link WorkflowStrategyMode}
     * 
     * @param workload
     * @return List-{@link WorkItem}
     */
    @Override
    public List<WorkItem> evaluateWorkload(List<WorkItem> workload) {
        
        // Handle scanner mode
        if ( this.mode == WorkflowStrategyMode.SCANNER ) {
            
            // Handle next run
            String currentStep = this.activePolicy.getTarget();
            LOGGER.info(
                "Re-queuing active target '{}', and proceeding to next workflow step",
                currentStep
            );
            this.workflow.offerLast(this.activePolicy);
            this.activePolicy = this.workflow.pollFirst();
            boolean val = RoundRobinWorkflowStrategy.getOrSetCanIncrement(false, true);
            LOGGER.debug("RoundRobinWorkflowStrategy can now increment is '{}'", val);
            
            LOGGER.info(
                "Active policy for the next iteration is now:\t'{}'",
                this.activePolicy.getTarget()
            );
            
            // Return next batch of tasks if present
            if ( !workload.isEmpty() ) {
                LOGGER.info(
                    "Providing next batch of n = '{}' tasks from current step",
                    workload.size(),
                    currentStep
                );
                return workload;
            }
            else {
                LOGGER.warn("No tasks detected for active batch. Returning empty list");
                return Collections.emptyList();
            }
        }
        
        // Handle exhaust mode
        else if ( this.mode == WorkflowStrategyMode.EXHAUST ) {
            
            // Return next batch of tasks if present
            if ( !workload.isEmpty() ) {
                RoundRobinWorkflowStrategy.getOrSetCanIncrement(false, false);
                LOGGER.info(
                    "Providing next batch of n = '{}' tasks",
                    workload.size()
                );
                return workload;
            }

            // Enqueue next
            LOGGER.warn("No tasks detected for active batch. Returning empty list, and enqueing next workflow");
            this.workflow.offerLast(this.activePolicy);
            this.activePolicy = this.workflow.pollFirst();
            RoundRobinWorkflowStrategy.getOrSetCanIncrement(false, true);
            
            // Return empty list
            return Collections.emptyList();
        }
        
        // Return empty
        return Collections.emptyList();
    }
    
    
    /**
     * Checks whether there active tasks in workflow,
     *  bypassing count of records
     * 
     * @return boolean
     */
    @Override
    public boolean hasNext() {
        
        // Check iteration counter if configured
        if ( this.limit > 0 ) {
            if ( this.counter >= this.limit ) {
                LOGGER.warn(
                    "Workflow iteration counter breached '{}' limit",
                    this.limit
                );
                return false;
            }
        }
        
        // Check if queue is consumed
        if ( this.activePolicy == null ) {
            LOGGER.warn("Workflow processing completed");
            return false;
        }
        
        // Check if has next
        return true;
    }
    
    
    /**
     * Set active policy to first element in queue, an
     *  re-queue
     * 
     */
    @Override
    public void incrementWindow() {
        boolean currentVal = RoundRobinWorkflowStrategy.getOrSetCanIncrement(true, true);
        if ( currentVal ) {
            this.activePolicy = this.workflow.pollFirst();
            if ( this.activePolicy == null ) {
                return;
            }
            this.workflow.offerLast(activePolicy);
            LOGGER.info(
                "Workflow incremented active policy is now:\t'{}'",
                this.activePolicy.getTarget()
            );
        }
    }
    
    
    /**
     * Ensure only one thread can get set value at a time
     * 
     * @param flag true=get current value, false = set provided value
     * @param val 
     * 
     * @return boolean
     */
    private static synchronized boolean getOrSetCanIncrement(boolean flag, boolean val) {
        
        // Get current value
        if ( flag ) {
            boolean currentValue = RoundRobinWorkflowStrategy.canIncrement.get();
            RoundRobinWorkflowStrategy.canIncrement.set(currentValue);
            return currentValue;
        }
        
        // Otherwise just set
        RoundRobinWorkflowStrategy.canIncrement = new AtomicBoolean(val);
        return val;
    }
}