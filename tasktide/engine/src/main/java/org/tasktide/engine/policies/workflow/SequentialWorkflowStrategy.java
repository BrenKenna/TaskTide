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
import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;


/**
 * Sequential consummation strategy for steps of a workflow
 *
 * @author Bren
 */
public class SequentialWorkflowStrategy implements WorkflowAcquisitionStrategy {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(SequentialWorkflowStrategy.class);
    private int currentIndex = 0;
    private TaskTideWorkloadAcquisitionPolicy activePolicy;
    private WorkflowStrategyMode mode = null;

    
    /**
     * Fetches {@link WorkItem} collection according to configured {@link TaskTideWorkloadAcquisitionPolicy}.
     *  Stateless sequential processing of steps in the workflow, where flow is determined by the configured
     *  {@link WorkflowStrategyMode}
     * 
     * @param policies
     * 
     * @return List-{@link WorkItem}
     */
    @Override
    public List<WorkItem> fetchWorkload(List<TaskTideWorkloadAcquisitionPolicy> policies) {
        
        // Default strategy mode
        if ( this.mode == null ) {
            LOGGER.info("Defaulting Sequential Strategy '{}' mode", WorkflowStrategyMode.EXHAUST);
            this.mode = WorkflowStrategyMode.EXHAUST;
        }
        
        // Sequentially consume provided workload acquisition policies
        LOGGER.info("Fetching workflow records for registered targets:\t'{}'", this.getTargetNames(policies));
        while ( currentIndex < policies.size() ) {
            
            // Fetch active policy
            LOGGER.info(
                "Examing collection '{}' in workflow '{}'",
                policies.get(currentIndex).getTarget(),
                this.getTargetNames(policies)
            );
            this.activePolicy = policies.get(currentIndex);
            List<WorkItem> workload = activePolicy.fetchWorkload();
            
            
            // Handle exhaustive mode
            if ( this.mode == WorkflowStrategyMode.EXHAUST ) {
                
                // Return workload if any tasks available
                if ( !workload.isEmpty() ) {
                    LOGGER.info(
                        "Reterived workload of size '{}' for target '{}'",
                        workload.size(), activePolicy.getTarget()
                    );
                    return workload;
                }
                
                // Otherwise let the loop hit the next target
                LOGGER.info(
                    "Active step in workflow has been consumed '{}', proceeding to the next step",
                    activePolicy.getTarget()
                );
                currentIndex++;
                this.activePolicy = null;
            }
            
            // Handle scanner mode
            else if ( this.mode == WorkflowStrategyMode.SCANNER ) {
                
                // Otherwise let the loop hit the next target
                LOGGER.info(
                    "Re-queueing active step in workflow '{}', and configuring the next step",
                    activePolicy.getTarget()
                );
                currentIndex++;
                this.activePolicy = null;
                
                // Return workload if any tasks available
                if ( !workload.isEmpty() ) {
                    LOGGER.info(
                        "Reterived workload of size '{}' for target '{}'",
                        workload.size(), activePolicy.getTarget()
                    );
                    return workload;
                }
            }

        }
        
        // Complete consumation
        LOGGER.info(
            "Processing of provided workflow completed under '{}' mode :\t'{}'",
            this.mode,
            this.getTargetNames(policies)
        );
        return Collections.emptyList();
    }

    
    /**
     * Checks whether available tasks in active step
     * 
     * @return 0, 1, -1
     */
    @Override
    public int hasNext() {
        return this.activePolicy == null
            ? -1
            : this.activePolicy.hasNext() ? 1 : 0;
    }
    
    
    /**
     * Get target names as a list
     * 
     * @param policies
     * @return String
     */
    public List<String> getTargetNames(List<TaskTideWorkloadAcquisitionPolicy> policies) {
        List<String> list = new ArrayList<>();
        policies
            .forEach(
                elm -> list.add(elm.getTarget())
        );
        return list;
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

    
    /**
     * Get active policy
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    @Override
    public TaskTideWorkloadAcquisitionPolicy getActive() {
        return this.activePolicy;
    }
}