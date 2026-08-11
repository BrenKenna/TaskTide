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
package org.tasktide.engine.policies.workflow;

import java.util.List;
import java.util.Collections;
import java.util.Deque;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.exceptions.TaskTideEngineUncheckedException;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;


/**
 * Sequential consummation strategy for steps of a workflow
 *
 * @author Bren
 */
public class SequentialWorkflowStrategy extends AbstractWorkflowStrategy {
    
    // Internal attributes
    private final Logger LOGGER = LogManager.getLogger(SequentialWorkflowStrategy.class);
    
    
    /**
     * Construct with {@link TaskTideWorkloadAcquisitionPolicy} list, {@link WorkflowStrategyMode}, and iteration limit
     * 
     * @param policies
     * @param mode
     * @param limit 
     */
    SequentialWorkflowStrategy(List<TaskTideWorkloadAcquisitionPolicy> policies, WorkflowStrategyMode mode, int limit) {
        super("SequentialWorkflowStrategy-" + UUID.randomUUID().toString(), policies, mode, limit);
    }
    
    
    /**
     * Construct with previous state
     * 
     * @param id
     * @param policies
     * @param workflow
     * @param activePolicy
     * @param mode
     * @param limit 
     */
    SequentialWorkflowStrategy(
        List<TaskTideWorkloadAcquisitionPolicy> policies,
        Deque<TaskTideWorkloadAcquisitionPolicy> stateWorkflow,
        TaskTideWorkloadAcquisitionPolicy stateActivePolicy,
        WorkflowStrategyMode mode,
        int limit
    ) {
        super("SequentialWorkflowStrategy-" + UUID.randomUUID().toString(), policies, stateWorkflow, stateActivePolicy, mode, limit);
    }
    
    /**
     * Fetches {@link WorkItem} collection according to configured {@link TaskTideWorkloadAcquisitionPolicy}.
     *  Stateless sequential processing of steps in the workflow, where flow is determined by the configured
     *  {@link WorkflowStrategyMode}
     * 
     * 
     * @return List-{@link WorkItem}
     */
    @Override
    public List<WorkItem> fetchWorkload() {
        
        // Default strategy mode
        this.hasRun = true;
        this.configureDefaultStrategyMode();

        // Check counter is in range
        if ( !this.evaluateIterationCounter() ) {
            LOGGER.info("Workflow iteration counter breached");
            return Collections.emptyList();
        }

        // Sequentially consume provided workload acquisition policies
        LOGGER.info(
            "Fetching workflow records for registered targets, queue size is now '{}':\t'{}'",
            this.workflow.size(),
            this.getTargetNames()
        );
        while ( !this.workflow.isEmpty() ) {
            
            // Fetch active policy
            if ( this.activePolicy == null ) {
                LOGGER.info("No active policy, detected polling first");
                this.activePolicy = this.workflow.pollFirst();
                if ( this.activePolicy == null ) {
                    LOGGER.info("Next task not detected, workflow processing complete");
                    return Collections.emptyList();
                }
            }
            
            // Fetch workload
            LOGGER.info(
                "Examing collection '{}' in workflow '{}'",
                this.activePolicy.getTarget(),
                this.getTargetNames()
            );
            List<WorkItem> workload = this.activePolicy.fetchWorkload();
            
            
            // Evaluate workload
            return this.evaluateWorkload(workload);
        }
        
        // Complete consumation
        LOGGER.info(
            "Processing of provided workflow completed under '{}' mode :\t'{}'",
            this.mode,
            this.getTargetNames()
        );
        return Collections.emptyList();
    }
    
    
    /**
     * Configure exhaustive {@link WorkflowStrategyMode}
     *  if not mode was not specified on construction
     * 
     */
    @Override
    public void configureDefaultStrategyMode() {
        if ( this.mode == null ) {
            LOGGER.info(
                "Defaulting Sequential Strategy '{}' mode",
                WorkflowStrategyMode.EXHAUST
            );
            this.mode = WorkflowStrategyMode.EXHAUST;
        }
    }
    
    
    /**
     * Evaluate workload based on configured {@lnk WorkflowStrategyMode}
     * 
     * @param workload
     * 
     * @return List-{@link WorkItem} 
     */
    @Override
    public List<WorkItem> evaluateWorkload(List<WorkItem> workload) {
    
        // Handle exhaustive mode
        if (this.mode == WorkflowStrategyMode.EXHAUST) {

            // Return workload if any tasks available
            if (!workload.isEmpty()) {
                LOGGER.info(
                    "Reterived workload of size '{}' for target '{}'",
                    workload.size(), this.activePolicy.getTarget()
                );
                return workload;
            }

            // Otherwise let the loop hit the next target
            if ( !this.workflow.isEmpty() ) {
                LOGGER.info(
                    "Active step in workflow has been consumed '{}', proceeding to the next step '{}'",
                    this.activePolicy.getTarget(), this.workflow.peekFirst().getTarget()
                );
                this.activePolicy = this.workflow.pollFirst();
                return Collections.emptyList();
            }
            
            else {
                LOGGER.info("Worflow has been consumed, triggering completion");
                this.activePolicy = null;
                this.workflow.clear();
                return Collections.emptyList();
            }
        }

        // Handle scanner mode
        else if (this.mode == WorkflowStrategyMode.SCANNER) {

            // Otherwise let the loop hit the next target
            String targetName = this.activePolicy.getTarget();
            LOGGER.info(
                "Clearing active step '{}' from, and configuring the next step",
                targetName
            );
            if ( !this.workflow.isEmpty() ) {
                LOGGER.info(
                    "Active step in workflow has been scanned '{}', proceeding to the next step '{}'",
                    activePolicy.getTarget(),
                    this.workflow.peekFirst().getTarget()
                );
                this.activePolicy = this.workflow.pollFirst();
            }
            else {
                LOGGER.info("Worflow scanning complete, triggering completion");
                this.activePolicy = null;
                this.workflow.clear();
            }

            // Return workload if any tasks available
            if ( !workload.isEmpty() ) {
                LOGGER.info(
                    "Reterived workload of size '{}' for target '{}'",
                    workload.size(), targetName
                );
                return workload;
            }
            return Collections.emptyList();
        }
        
        // Otherwise error
        throw new TaskTideEngineUncheckedException("No WorkflowStrategyMode configured");
    }
    
    
    /**
     * Implements own hasNExt logic depending on mode
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
        if ( this.activePolicy == null && this.workflow.isEmpty() ) {
            LOGGER.warn("Workflow processing completed");
            return false;
        }
        
        // Check whether has run
        if ( !hasRun ) {
            return true;
        }
        
        // Fetch next for scanner
        if ( this.mode.isWorkflowStrategyMode(WorkflowStrategyMode.SCANNER) ) {
            LOGGER.info("Sequential scanner polling next step");
            this.activePolicy = this.workflow.pollFirst();
            return this.activePolicy != null;
        }
        
        // Fetch for exhaustion, polling 
        else if ( this.mode.isWorkflowStrategyMode(WorkflowStrategyMode.EXHAUST) ) {
            LOGGER.info("Sequential exhaustion evaluating");
            if ( !this.activePolicy.hasNext() ) {
                LOGGER.info("Sequential exhaustion polling next step");
                this.activePolicy = this.workflow.pollFirst();
                return this.activePolicy != null;
            }
            LOGGER.info("More work detected in active task");
            return true;
        }
        return true;
    }
    
    
    /**
     * Set has run for cloning
     * 
     * @param val 
     */
    public void setHasRun(boolean val) {
        this.hasRun = val;
    }
}