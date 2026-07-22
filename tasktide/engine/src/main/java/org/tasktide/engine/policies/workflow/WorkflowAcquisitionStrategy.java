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

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;


/**
 * Defines specifications for different ways to fetch workload
 *  from a collection of steps
 * 
 * @author Bren
 */
public interface WorkflowAcquisitionStrategy {
    
    
    /**
     * Fetches workload from step in provided collection
     * 
     * @param policies
     * @return List-{@link TaskTideWorkloadAcquisitionPolicym}
     */
    public List<WorkItem> fetchWorkload(List<TaskTideWorkloadAcquisitionPolicy> policies);
    
    
    /**
     * Checks whether there are any available tasks left
     *  in the active step
     * 
     * 0  = none
     * 1  = yes
     * -1 = not yet configured
     * 
     * @return int
     */
    public int hasNext();
    
    
    /**
     * Sets {@link WorkflowStrategyMode} currently SCANNER,
     *  or EXAHUST of strategy
     * 
     * @param mode 
     */
    public void setStrategyMode(WorkflowStrategyMode mode);
    
    
    /**
     * Provides {@link WorkflowStrategyMode} of strategy
     * 
     * @return {@link WorkflowStrategyMode}
     */
    public WorkflowStrategyMode getStrategyMode();
}
