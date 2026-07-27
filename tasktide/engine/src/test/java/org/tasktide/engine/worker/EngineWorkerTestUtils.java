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
package org.tasktide.engine.worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.workitem.ItemState;

import org.tasktide.engine.exceptions.TaskTideEngineCheckedException;

import org.tasktide.engine.policies.AcquisitionPolicyMode;
import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;
import org.tasktide.engine.policies.workflow.WorkflowStrategyMode;
import org.tasktide.engine.policies.workflow.WorkflowStrategyType;

import org.tasktide.engine.workerunit.container.WorkerUnitContainer;
import org.tasktide.engine.workerunit.container.WorkerUnitModelType;


/**
 * Utility methods to support unit testing
 *
 * @author Bren
 */
public class EngineWorkerTestUtils {
    
    private static final Logger LOGGER = LogManager.getLogger(TaskTideEngineWorkerTests.class);
    
    
    /**
     * Fetch {@link TaskTideWorkloadAcquisitionPolicy}
     * 
     * @param policyMode
     * @param step
     * @param strategy
     * @param mode
     * @param iterationLimit
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public static TaskTideWorkloadAcquisitionPolicy getAcquisitionPolicy(
        AcquisitionPolicyMode policyMode,
        String step,
        WorkflowStrategyType strategy,
        WorkflowStrategyMode mode,
        int iterationLimit
    ) {
        LOGGER.info("Configuring acqusition policy with iteration limit\t'{}'", iterationLimit);
        return policyMode
            .initBuilder()
            .withTarget(step)
            .withItemState(ItemState.TODO)
            .withWindowSize(2)
            .withPoolSize(1)
            .withIterationLimit(iterationLimit)
            .withStrategyMode(mode)
            .withStrategyType(strategy)
        .build();
    }
    
    /**
     * Fetch {@link TaskTideWorkloadAcquisitionPolicy}
     * 
     * @param policyMode
     * @param step
     * @param strategy
     * @param mode
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public static TaskTideWorkloadAcquisitionPolicy getAcquisitionPolicy(
        AcquisitionPolicyMode policyMode,
        String step,
        WorkflowStrategyType strategy,
        WorkflowStrategyMode mode
    ) {
        return policyMode
            .initBuilder()
            .withTarget(step)
            .withItemState(ItemState.TODO)
            .withWindowSize(2)
            .withPoolSize(1)
            .withStrategyMode(mode)
            .withStrategyType(strategy)
        .build();
    }
    
    
    /**
     * Configure {@link TaskTideWorkloadAcquisitionPolicy} with parallelism options
     * 
     * @param policyMode
     * @param step
     * @param strategy
     * @param mode
     * @param poolSize
     * @param windowSize
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public static TaskTideWorkloadAcquisitionPolicy getAcquisitionPolicy(
        AcquisitionPolicyMode policyMode,
        String step,
        WorkflowStrategyType strategy,
        WorkflowStrategyMode mode,
        int poolSize,
        int windowSize
    ) {
        return policyMode
            .initBuilder()
            .withTarget(step)
            .withItemState(ItemState.TODO)
            .withWindowSize(windowSize)
            .withPoolSize(poolSize)
            .withStrategyMode(mode)
            .withStrategyType(strategy)
        .build();
    }
    
    
    /**
     * Configure {@link TaskTideWorkloadAcquisitionPolicy} with parallelism options
     * 
     * @param policyMode
     * @param step
     * @param strategy
     * @param mode
     * @param poolSize
     * @param windowSize
     * @param iterationLimit
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public static TaskTideWorkloadAcquisitionPolicy getAcquisitionPolicy(
        AcquisitionPolicyMode policyMode,
        String step,
        WorkflowStrategyType strategy,
        WorkflowStrategyMode mode,
        int poolSize,
        int windowSize,
        int iterationLimit
    ) {
        return policyMode
            .initBuilder()
            .withTarget(step)
            .withItemState(ItemState.TODO)
            .withWindowSize(windowSize)
            .withPoolSize(poolSize)
            .withIterationLimit(iterationLimit)
            .withStrategyMode(mode)
            .withStrategyType(strategy)
        .build();
    }
    
    
    /**
     * Configure {@link TaskTideEngineWorker} with parallelism
     *  parameters and iteration limit
     * 
     * @param policyMode
     * @param step
     * @param strategy
     * @param mode
     * @param poolSize
     * @param windowSize
     * @param iterationLimit
     * 
     * @return {@link TaskTideEngineWorker}
     */
    public static TaskTideEngineWorker getEngineWorker(
        AcquisitionPolicyMode policyMode,
        String step,
        WorkflowStrategyType strategy,
        WorkflowStrategyMode mode,
        int poolSize,
        int windowSize,
        int iterationLimit
    ) {
        
        // Initialize vars
        WorkerUnitContainer workerUnit;
        TaskTideWorkloadAcquisitionPolicy acquisitionPolicy;
        
        // Try configure process executor
        try {
            
            // Configure engine componenets
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(poolSize, poolSize);
            
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, -1);
            workerUnit.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
            
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.WORKITEM, -1);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.WORKITEM);
            
            // Configures worker
            acquisitionPolicy = EngineWorkerTestUtils.getAcquisitionPolicy(
                policyMode, step, strategy, mode,
                poolSize, windowSize, iterationLimit
            );
            return new TaskTideEngineWorker(acquisitionPolicy);
        }
        
        catch ( TaskTideEngineCheckedException ex ) {
            LOGGER.error("Could not instantiate WorkItemTraverser:\n\n{}", ex);
            return null;
        }
    }

    
    /**
     * Fetch {@link TaskTideEngineWorker} configured
     *  through {@link WorkerUnitContainer}
     * 
     * @param policyMode
     * @param step
     * @param strategy
     * @param mode
     * @return {@link TaskTideEngineWorker}
     */
    public static TaskTideEngineWorker getEngineWorker(
        AcquisitionPolicyMode policyMode,
        String step,
        WorkflowStrategyType strategy,
        WorkflowStrategyMode mode
    ) {
        
        // Initialize vars
        WorkerUnitContainer workerUnit;
        TaskTideWorkloadAcquisitionPolicy acquisitionPolicy;
        
        // Try configure process executor
        try {
            
            // Configure engine componenets
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(1, 1);
            
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, -1);
            workerUnit.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
            
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.WORKITEM, -1);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.WORKITEM);
            
            // Configures worker
            acquisitionPolicy = EngineWorkerTestUtils.getAcquisitionPolicy(policyMode, step, strategy, mode);
            return new TaskTideEngineWorker(acquisitionPolicy);
        }
        
        catch ( TaskTideEngineCheckedException ex ) {
            LOGGER.error("Could not instantiate WorkItemTraverser:\n\n{}", ex);
            return null;
        }
    }
    
    
    /**
     * Fetch {@link TaskTideEngineWorker} configured
     *  through {@link WorkerUnitContainer}
     * 
     * @param policyMode
     * @param step
     * @param strategy
     * @param mode
     * @param iterationLimit
     * 
     * @return {@link TaskTideEngineWorker}
     */
    public static TaskTideEngineWorker getEngineWorker(
        AcquisitionPolicyMode policyMode,
        String step,
        WorkflowStrategyType strategy,
        WorkflowStrategyMode mode,
        int iterationLimit
    ) {
        
        // Initialize vars
        WorkerUnitContainer workerUnit;
        TaskTideWorkloadAcquisitionPolicy acquisitionPolicy;
        
        // Try configure process executor
        try {
            
            // Configure engine componenets
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(1, 1);
            
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, -1);
            workerUnit.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
            
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.WORKITEM, -1);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.WORKITEM);
            
            // Configures worker
            acquisitionPolicy = EngineWorkerTestUtils.getAcquisitionPolicy(policyMode, step, strategy, mode, iterationLimit);
            LOGGER.info(
                "Acquisition Policy Iteration Limit = '{}'",
                iterationLimit
            );
            return new TaskTideEngineWorker(acquisitionPolicy);
        }
        
        catch ( TaskTideEngineCheckedException ex ) {
            LOGGER.error("Could not instantiate WorkItemTraverser:\n\n{}", ex);
            return null;
        }
    }
    
    
    /**
     * Configure {@link TaskTideEngineWorker} with provided parallelism options
     * 
     * @param policyMode
     * @param step
     * @param strategy
     * @param mode
     * @param poolSize
     * @param windowSize
     * 
     * @return {@link TaskTideEngineWorker}
     */
    public TaskTideEngineWorker getEngineWorker(
        AcquisitionPolicyMode policyMode,
        String step,
        WorkflowStrategyType strategy,
        WorkflowStrategyMode mode,
        int poolSize,
        int windowSize
    ) {
        
        // Initialize vars
        WorkerUnitContainer workerUnit;
        TaskTideWorkloadAcquisitionPolicy acquisitionPolicy;
        
        // Try configure process executor
        try {
            
            // Configure engine componenets
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(poolSize, poolSize);
            
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, -1);
            workerUnit.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
            
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.WORKITEM, -1);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.WORKITEM);
            
            // Configures worker
            acquisitionPolicy = EngineWorkerTestUtils.getAcquisitionPolicy(
                policyMode, step, strategy,
                mode, poolSize, windowSize
            );
            return new TaskTideEngineWorker(acquisitionPolicy);
        }
        
        catch ( TaskTideEngineCheckedException ex ) {
            LOGGER.error("Could not instantiate WorkItemTraverser:\n\n{}", ex);
            return null;
        }
    }
}