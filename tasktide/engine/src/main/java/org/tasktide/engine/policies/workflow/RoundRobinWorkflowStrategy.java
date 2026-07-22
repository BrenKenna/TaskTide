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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;


/**
 * Round robin strategy to consume steps of workflow
 *
 * @author Bren
 */
public class RoundRobinWorkflowStrategy implements WorkflowAcquisitionStrategy {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(RoundRobinWorkflowStrategy.class);
    
    private boolean initilized = false;
    private Deque<TaskTideWorkloadAcquisitionPolicy> workflow;
    private TaskTideWorkloadAcquisitionPolicy active;
    private WorkflowStrategyMode mode = null;
    private int attempts = 0;
    
    
    /**
     * Fetches {@link WorkItem} collection according to configured {@link TaskTideWorkloadAcquisitionPolicy}.
     *  Stateless round robin processing of steps in the workflow, where flow is determined by the configured
     *  {@link WorkflowStrategyMode}
     * 
     * @param policies
     * 
     * @return List-{@link WorkItem}
     */
    @Override
    public List<WorkItem> fetchWorkload(List<TaskTideWorkloadAcquisitionPolicy> policies) {
        
        // Initialize variables
        List<WorkItem> output;
        if (policies.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Configure round robin mode
        if ( this.mode == null ) {
            LOGGER.info(
                "Defaulting Round Robin mode to:\t'{}'",
                WorkflowStrategyMode.SCANNER
            );
            this.mode = WorkflowStrategyMode.SCANNER;
        }
        this.attempts++;
        
        // Configure workload queue
        if ( !this.initilized ) {
            LOGGER.info("Initializing Round-Robin workflow consumer");
            this.workflow = new ArrayDeque<>();
            policies
                .forEach(
                    elm -> workflow.offerLast(elm)
            );
            this.initilized = true;
        }
        
        // Fetch next task set
        LOGGER.info("Fetching next workflow to consume");
        this.active = this.workflow.pollFirst();
        
        // Fetch workload
        LOGGER.info("Fetching workload for active target:\t'{}'", this.active.getTarget());
        output = this.active.fetchWorkload();
        
        // Handle scanner mode
        if ( this.mode == WorkflowStrategyMode.SCANNER ) {
            
            // Handle next run
            LOGGER.info(
                "Re-queuing active target '{}', and proceeding to next workflow step",
                this.active.getTarget()
            );
            this.workflow.offerLast(this.active);
            this.active = null;
            
            // Return next batch of tasks if present
            if ( !output.isEmpty() ) {
                LOGGER.info("Providing next batch of n = '{}' tasks", output.size());
                return output;
            }
            else {
                LOGGER.warn("No tasks detected for active batch. Returning empty list");
                return Collections.emptyList();
            }
        }
        
        // Handle exhaust mode
        else if ( this.mode == WorkflowStrategyMode.EXHAUST ) {
            
            // Return next batch of tasks if present
            if ( !output.isEmpty() ) {
                LOGGER.info("Providing next batch of n = '{}' tasks", output.size());
                return output;
            }

            // Enqueue next
            LOGGER.warn("No tasks detected for active batch. Returning empty list, and enqueing next workflow");
            this.workflow.offerLast(this.active);
            this.active = null;
            
            // Return empty list
            return Collections.emptyList();
        }
        
        // Return empty
        return Collections.emptyList();
    }
    
    
    /**
     * Checks whether available tasks in active step
     * 
     * @return 0, 1, -1
     */
    @Override
    public int hasNext() {
        return this.active == null
            ? -1
            : this.active.hasNext() ? 1 : 0;
    }

    
    /**
     * Sets {@link WorkflowStrategyMode}
     * 
     * @param mode 
     */
    @Override
    public void setStrategyMode(WorkflowStrategyMode mode) {
        this.mode = mode;
    }

    
    /**
     * Gets {@link WorkflowStrategyMode}
     * 
     * @return {@link WorkflowStrategyMode}
     */
    @Override
    public WorkflowStrategyMode getStrategyMode() {
        return this.mode;
    }
}