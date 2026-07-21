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

    private final Logger LOGGER = LogManager.getLogger(RoundRobinWorkflowStrategy.class);
    private boolean initilized = false;
    private Deque<TaskTideWorkloadAcquisitionPolicy> workflow;
    private TaskTideWorkloadAcquisitionPolicy active;
    
    
    /**
     * Exhaustively consumes policies round robin approach where active element
     *  is enqueued once consumed
     * 
     * @param policies
     * @return List-{@link WorkItem}
     */
    @Override
    public List<WorkItem> fetchWorkload(List<TaskTideWorkloadAcquisitionPolicy> policies) {
        
        // Initialize variables
        List<WorkItem> output;
        if (policies.isEmpty()) {
            return Collections.emptyList();
        }
        
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
        
        // Fetch first task set
        if ( this.active == null ) {
            LOGGER.info("Fetching next workflow to consume");
            this.active = this.workflow.pollFirst();
        }
        
        // Fetch workload
        LOGGER.info("Fetching workload for active target:\t'{}'", this.active.getTarget());
        output = this.active.fetchWorkload();
        
        // Return next batch of tasks if present
        if ( !output.isEmpty() ) {
            LOGGER.info("Providing next batch of n = '{}' tasks", output.size());
            return output;
        }
        
        // Handle next run
        LOGGER.info("Re-queuing active target '{}', and proceeding to next workflow step");
        this.workflow.offerLast(active);
        this.active = null;
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
}