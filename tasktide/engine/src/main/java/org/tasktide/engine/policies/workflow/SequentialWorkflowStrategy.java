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
        this.configureDefaultStrategyMode();

        // Check counter is in range
        if ( !this.evaluateIterationCounter() ) {
            LOGGER.info("Workflow iteration counter breached");
            return Collections.emptyList();
        }

        // Sequentially consume provided workload acquisition policies
        LOGGER.info(
            "Fetching workflow records for registered targets:\t'{}'",
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
     * Configure {@link WorkflowStrategyMode.EXHAUST}
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
            if (!this.workflow.isEmpty()) {
                LOGGER.info(
                    "Active step in workflow has been consumed '{}', proceeding to the next step '{}'",
                    this.activePolicy.getTarget(), this.workflow.peekFirst()
                );
                this.activePolicy = null;
                return Collections.emptyList();
            }
            
            else {
                LOGGER.info("Worflow has been consumed, triggering completion");
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
            if (!this.workflow.isEmpty()) {
                LOGGER.info(
                        "Active step in workflow has been scanned '{}', proceeding to the next step",
                        activePolicy.getTarget()
                );
                this.activePolicy = null;
            } else {
                LOGGER.info("Worflow scanning complete, triggering completion");
            }

            // Return workload if any tasks available
            if (!workload.isEmpty()) {
                LOGGER.info(
                        "Reterived workload of size '{}' for target '{}'",
                        workload.size(), targetName
                );
                return workload;
            }
        }
        
        // Otherwise error
        throw new TaskTideEngineUncheckedException("No WorkflowStrategyMode configured");
    }
}