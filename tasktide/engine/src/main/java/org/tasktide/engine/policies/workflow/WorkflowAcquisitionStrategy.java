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
     * Evaluate retrieved workload
     * 
     * @param workload
     * 
     * @return List={@link WorkItem} 
     */
    public List<WorkItem> evaluateWorkload(List<WorkItem> workload);
    
    
    /**
     * Fetches workload from step in provided collection
     * 
     * @return List-{@link TaskTideWorkloadAcquisitionPolicym}
     */
    public List<WorkItem> fetchWorkload();
    
    
    /**
     * Checks whether there are any available tasks left
     *  in the active step, or throws
     *  {@link TaskTideEngineUncheckedException} if not yet configured
     * 
     * @return boolean
     */
    public boolean hasNext();
    
    
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
    
    
    /**
     * Returns active policy
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public TaskTideWorkloadAcquisitionPolicy getActive();
    
    
    /**
     * Set an iteration on algorithm
     * 
     * @param limit 
     */
    public void setIterationLimit(int limit);
    
    
    /**
     * Get iteration limit
     * 
     * @return int
     */
    public int getIterationLimit();

    
    /**
     * Get target names as a list
     * 
     * @return String
     */
    public List<String> getTargetNames();
    
    
    /**
     * Get Id
     * 
     * @return String
     */
    public String getId();
    
    
    /**
     * Configure default {@link WorkflowStrategyMode}
     * 
     */
    public void configureDefaultStrategyMode();
    
    
    /**
     * Evaluate iteration counter
     * 
     * @return boolean
     */
    public boolean evaluateIterationCounter();
    
    
    /**
     * Reset policy queue
     * 
     */
    public void resetPolicyQueue();
    
    
    /**
     * Fetch target names from workload queue
     * 
     * @return List-String
     */
    public List<String> getQueueTargets();
    
    
    /**
     * Get whether strategy has run
     * 
     * @return boolean
     */
    public boolean getHasRun();
    
    
    /**
     * Set whether strategy has run
     * 
     * @param val 
     */
    public void setHasRun(boolean val);
}
