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
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;


/**
 * {@link AbstractWorkflowStrategy} to hold shared operational logic
 *  of concrete {@link WorkflowStrategyType} instances. Enforcing a queue
 *  ordering based on the provided {@link TaskTideWorkloadAcquisitionPolicy}
 *
 * @author Bren
 */
public abstract class AbstractWorkflowStrategy implements WorkflowAcquisitionStrategy {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(AbstractWorkflowStrategy.class);
    
    protected final String id;
    protected final List<TaskTideWorkloadAcquisitionPolicy> policies;
    protected Deque<TaskTideWorkloadAcquisitionPolicy> workflow;
    protected TaskTideWorkloadAcquisitionPolicy activePolicy;
    protected WorkflowStrategyMode mode = null;
    protected int limit = -1, counter = 0;

    
    /**
     * Construct with {@link TaskTideWorkloadAcquisitionPolicy} policies, 
     *  {@link WorkflowStrategyMode}, and iteration limit
     * 
     * @param id
     * @param policies
     * @param mode
     * @param limit 
     */
    AbstractWorkflowStrategy(String id, List<TaskTideWorkloadAcquisitionPolicy> policies, WorkflowStrategyMode mode, int limit) {
        this.id = id;
        this.policies = policies;
        this.workflow = new ArrayDeque<>();
            policies
                .forEach(
                    elm -> workflow.offerLast(elm)
        );
        this.activePolicy = policies.get(0);
        this.mode = mode;
        this.limit = -1;
        this.counter = 0;
        this.limit = limit;
    }

    
    /**
     * Checks whether there active tasks in workflow
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
        if ( this.activePolicy == null ) {
            LOGGER.warn("Workflow processing completed");
            return false;
        }
        
        // Check if has next
        return this.activePolicy.hasNext();
    }

    
    /**
     * Set {@link WorkflowStrategyMode}
     * 
     * @param mode 
     */
    @Override
    public void setStrategyMode(WorkflowStrategyMode mode) {
        this.mode = mode;
    }

    
    /**
     * Get {@link WorkflowStrategyMode}
     * 
     * @return {@link WorkflowStrategyMode}
     */
    @Override
    public WorkflowStrategyMode getStrategyMode() {
        return this.mode;
    }

    
    /**
     * Get active {@link TaskTideWorkloadAcquisitionPolicy}
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    @Override
    public TaskTideWorkloadAcquisitionPolicy getActive() {
        return this.activePolicy;
    }

    
    /**
     * Set iteration limit
     * 
     * @param limit 
     */
    @Override
    public void setIterationLimit(int limit) {
        this.limit = limit;
    }

    
    /**
     * Get iteration limit
     * 
     * @return int
     */
    @Override
    public int getIterationLimit() {
        return this.limit;
    }
    
    
    /**
     * Get target names as a list
     * 
     * @return String
     */
    @Override
    public List<String> getTargetNames() {
        List<String> list = new ArrayList<>();
        this.policies
            .forEach(
                elm -> list.add(elm.getTarget())
        );
        return list;
    }
    
    
    /**
     * Get Id
     * 
     * @return String 
     */
    @Override
    public String getId() {
        return this.id;
    }
    
    
    /**
     * Handles iteration counter if configured
     * 
     * @return boolean
     */
    @Override
    public boolean evaluateIterationCounter() {
        
        // Evaluate if configured
        if ( this.limit > 0 ) {
            
            // Log evaluation
            LOGGER.info(
                "Checking workflow strategy iteration counter is within range:\t'{}'",
                this.limit
            );
            
            // Clear if limit is breached
            if ( this.counter >= this.limit ) {
                LOGGER.warn("Workflow iteration limit breached, clearing workflow queue");
                this.workflow.clear();
                return false;
            }
            
            // Otherwise proceed
            else {
                this.counter++;
                LOGGER.info(
                    "Incremented workflow iteration counter, value is now:\t'{}'",
                    this.counter
                );
                return true;
            }
        }
        
        // Otherwise pass
        else {
            return true;
        }
    }
}