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

import org.tasktide.engine.exceptions.TaskTideEngineCheckedException;
import org.tasktide.engine.exceptions.TaskTideEngineUncheckedException;
import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;


/**
 * Builds required {@link WorkflowAcquisitionStrategy}
 *
 * @author Bren
 */
public class WorkflowStrategyBuilder {

    // Attributes
    private WorkflowStrategyType strategyType;
    private List<TaskTideWorkloadAcquisitionPolicy> policies;
    private WorkflowStrategyMode mode;
    private int limit = -1;
    
    
    /**
     * Construct with {@link WorkflowStrategyType}
     * 
     * @param strategyType 
     */
    WorkflowStrategyBuilder(WorkflowStrategyType strategyType) {
        this.strategyType = strategyType;
    }
    
    
    /**
     * Build with provided {@link TaskTideWorkloadAcquisitionPolicy} collection
     * 
     * @param policies
     * @return {@link WorkflowStrategyBuilder}
     */
    public WorkflowStrategyBuilder withPolicies(List<TaskTideWorkloadAcquisitionPolicy> policies) {
        this.policies = policies;
        return this;
    }
    
    
    /**
     * Build with provided {@link WorkflowStrategyMode}
     * 
     * @param mode
     * @return {@link WorkflowStrategyBuilder}
     */
    public WorkflowStrategyBuilder withStrategyMode(WorkflowStrategyMode mode) {
        this.mode = mode;
        return this;
    }
    
    
    /**
     * Build with provided iteration limit
     * 
     * @param limit
     * @return {@link WorkflowStrategyBuilder}
     */
    public WorkflowStrategyBuilder withIterationLimit(int limit) {
        this.limit = limit;
        return this;
    }
    
    
    /**
     * Build configured {@link WorkflowAcquisitionStrategy}, throwing
     *  {@link TaskTideEngineUncheckedException} if policy
     *  list is not configured
     * 
     * @return {@link WorkflowAcquisitionStrategy}
     * 
     * @throws {@link TaskTideEngineUncheckedException} 
     */
    public WorkflowAcquisitionStrategy build() throws TaskTideEngineUncheckedException {
        
        // Unchecked exception if no policies are provided
        if ( this.policies == null ) {
            throw new TaskTideEngineUncheckedException("Strategy cannot be configured without acqusition policies");
        }
        
        
        // Handle unconfigured strategy mode
        if ( this.mode == null ) {
            
            // Use scanner for roud robin
            if ( this.strategyType == WorkflowStrategyType.ROUND_ROBIN ) {
                this.mode = WorkflowStrategyMode.SCANNER;
            }
            
            // Use exhaustion for sequential
            else {
                this.mode = WorkflowStrategyMode.EXHAUST;
            }
        }
        
        // Construct roud robin strategy
        if ( this.strategyType == WorkflowStrategyType.ROUND_ROBIN ) {
            return new RoundRobinWorkflowStrategy(this.policies, this.mode, this.limit);
        }
        
        // Otherwise use sequential
        else {
            return new SequentialWorkflowStrategy(this.policies, this.mode, this.limit);
        }
    }
}