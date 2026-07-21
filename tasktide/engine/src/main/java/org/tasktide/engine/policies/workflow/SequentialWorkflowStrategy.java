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
    
    private final Logger LOGGER = LogManager.getLogger(SequentialWorkflowStrategy.class);
    private int currentIndex = 0;
    private TaskTideWorkloadAcquisitionPolicy activePolicy;

    @Override
    public List<WorkItem> fetchWorkload(List<TaskTideWorkloadAcquisitionPolicy> policies) {
        
        // Sequentially consume provided workload acquisition policies
        LOGGER.info("Fetching workflow records for registered targets:\n'{}'", this.getTargetNames(policies));
        while ( currentIndex < policies.size() ) {
            
            // Fetch active policy
            LOGGER.info("Examing index '{}' in workflow collection:\n'{}'", currentIndex);
            this.activePolicy = policies.get(currentIndex);
            
            // Fetch workload
            LOGGER.info("Fetching workload for target:\t'{}'", activePolicy.getTarget());
            List<WorkItem> workload = activePolicy.fetchWorkload();
            
            // Return workload if any tasks available
            if ( !workload.isEmpty() ) {
                LOGGER.info(
                    "Reterived workload of size '{}' for target '{}'",
                    workload.size(), activePolicy.getTarget()
                );
                return workload;
            }
            
            // Otherwise let the loop hit the next target
            LOGGER.info("Active step in workflow consumed '{}', proceeding to next step", activePolicy.getTarget());
            currentIndex++;
            this.activePolicy = null;
        }
        
        // Complete consumation
        LOGGER.info("Processing of provided workflow completed:'{}'", this.getTargetNames(policies));
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
}