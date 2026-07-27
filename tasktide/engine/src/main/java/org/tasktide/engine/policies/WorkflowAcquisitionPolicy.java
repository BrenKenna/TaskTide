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
package org.tasktide.engine.policies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.policies.workflow.RoundRobinWorkflowStrategy;
import org.tasktide.engine.policies.workflow.SequentialWorkflowStrategy;

import org.tasktide.engine.policies.workflow.WorkflowAcquisitionStrategy;
import org.tasktide.engine.policies.workflow.WorkflowStrategyMode;
import org.tasktide.engine.policies.workflow.WorkflowStrategyType;


/**
 * <p>
 * {@link AbstractAcquisitionPolicy} for user-defined workflow as an order provided
 *  list of Step names. With configurable {@link WorkflowAcquisitionStrategy}
 *  for the consummation of each step, as {@link SequentialWorkflowStrategy} or
 *  {@link RoundRobinWorkflowStrategy}.
 * </p>
 * 
 * <p>
 * Realistically sequential processing
 *  would be for batch mode, and round robin for service. However, the
 *  design pattern here caters for development.
 * </p>
 *
 * @author Bren
 */
public class WorkflowAcquisitionPolicy extends AbstractAcquisitionPolicy {

    // Logger
    private final Logger LOGGER = LogManager.getLogger(AbstractAcquisitionPolicy.class);
    
    // Attributes
    private final List<String> steps;
    private final List<TaskTideWorkloadAcquisitionPolicy> policies;
    private final WorkflowStrategyType strategyType;
    protected WorkflowAcquisitionStrategy strategy;
    

    /**
     * Constructs policy, building {@link WorkflowAcquisitionStrategy}#
     *  internally
     * 
     * @param steps
     * @param stratType 
     */
    WorkflowAcquisitionPolicy(List<String> steps, WorkflowStrategyType stratType) {
        super(AcquisitionPolicyMode.WORKFLOW);
        this.steps = steps;
        this.strategyType = stratType;

        this.policies = new ArrayList<>();
        this.steps
            .forEach(
                elm -> this.policies.add(this.getPolicyForTarget(elm))
        );
        
        this.strategy = this.strategyType
            .initializeStrategyBuilder()
            .withPolicies(this.policies)
        .build();
    }
    
    
    /**
     * Constructs policy
     * 
     * @param steps
     * @param stratType
     * @param strategy mode
     */
    WorkflowAcquisitionPolicy(
        List<String> steps,
        WorkflowStrategyType stratType,
        WorkflowStrategyMode strategyMode,
        int poolSize,
        int windowSize,
        int iterationLimit
    ) {
        super(AcquisitionPolicyMode.WORKFLOW);
        this.steps = steps;
        this.strategyType = stratType;
        
        this.iterationLimit = iterationLimit;
        this.poolSize = poolSize;
        this.windowSize = windowSize;

        this.policies = new ArrayList<>();
        this.steps
            .forEach(
                elm -> this.policies.add(this.getPolicyForTarget(elm))
        );

        this.strategy = this.strategyType
            .initializeStrategyBuilder()
            .withPolicies(this.policies)
            .withStrategyMode(strategyMode)
            .withIterationLimit(this.iterationLimit)
        .build();
    }


    
    /**
     * Fetch {@link WorkItem} workload through
     *  {@link WorkflowAcquisitionStrategy} interface
     * 
     * @return List-{@link WorkItem}
     */
    @Override
    public List<WorkItem> fetchWorkload() {
        
        // Initialize output
        List<WorkItem> output;
        
        // Evaluate iteration limit if provided
        if ( this.iterationLimit > 0 ) {
            LOGGER.info(
                "Evaluating iteration counter '{}' against configured limit '{}'",
                this.counter, this.iterationLimit
            );
            if ( this.counter >= this.iterationLimit ) {
                return Collections.emptyList();
            }
            this.counter++;
        }
        
        // Fetch workload
        output = this.strategy.fetchWorkload();
        
        // Handle null list
        if ( output == null ) {
            return Collections.emptyList();
        }
        
        // Otherwise pass as is
        return output;
    }

    
    /**
     * Determines whether available task in
     *  active policy
     * 
     * @return boolean
     */
    @Override
    public boolean hasNext() {
        if ( this.iterationLimit > 1 ) {
            if (this.counter > this.iterationLimit) {
                return false;
            }
        }
        return this.strategy.hasNext();
    }

    
    /**
     * Fetch policy for active target
     * 
     * @param step
     * @return {@link TaskTideWorkloadAcquisitionPolicy}-{@link WorkItem}
     */
    public TaskTideWorkloadAcquisitionPolicy getPolicyForTarget(String step) {
        
        // Build policy
        TaskTideWorkloadAcquisitionPolicy policy = new AcquisitionPolicyBuilder(AcquisitionPolicyMode.TARGETED)
            .withTarget(step)
            .withItemState(ItemState.TODO)
            .withAnno(this.getAnno())
            .withPoolSize(this.poolSize)
            .withWindowSize(this.windowSize)
            .withIterationLimit(this.iterationLimit)
        .build();
        
        // Return policy
        return policy;
    }
    
    
    /**
     * Fetch clone of step list
     * 
     * @return List-String
     */
    public List<String> getStepList() {
        List<String> list = new ArrayList<>();
        this.steps
            .forEach(
                elm -> list.add(elm)
        );
        return list;
    }

    
    /**
     * Get active {@link TaskTideWorkloadAcquisitionPolicy} from 
     *  {@link WorkflowAcquisitionStrategy}
     * 
     * @return String
     */
    @Override
    public String getTarget() {
        if ( this.strategy.getActive() == null ) {
            return null;
        }
        return this.strategy.getActive().getTarget();
    }

    
    /**
     * Clone the {@link WorkflowAcquisitionPolicy}
     * 
     * @return {@link WorkflowAcquisitionPolicy}
     */
    @Override
    public TaskTideWorkloadAcquisitionPolicy clonePolicy() {
        WorkflowAcquisitionPolicy policy = new WorkflowAcquisitionPolicy(this.steps, this.strategyType);
        policy.setAnno(this.getAnno());
        policy.setState(this.getState());
        
        policy.setPoolSize(this.poolSize);
        policy.setWindowSize(this.windowSize);
        
        policy.strategy = this.strategy;

        return policy;
    }
    
    
    /**
     * Delegates incrementing to the specific
     *  {@link WorkflowStrategyType} of this
     *  policy
     * 
     */
    @Override
    public void incrementWindow() {
        this.strategy.incrementWindow();
    }
}