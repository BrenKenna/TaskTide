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
package org.tasktide.tasktide.client.config.dependents;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.engine.policies.workflow.WorkflowStrategyMode;
import org.tasktide.engine.policies.workflow.WorkflowStrategyType;

import org.tasktide.parser.ArgumentTree;
import org.tasktide.parser.configuration.AbstractConfig;

import org.tasktide.parser.model.Argument;
import org.tasktide.parser.model.ArgumentType;


/**
 * Configurations for workload acquisition policy
 *
 * @author Bren
 */
public class WorkloadPolicyConfigurer extends AbstractConfig {

    // Acquisition mode
    @ConfigProperty(name = "tasktide.engine.policy.acquisition.workflow.mode", defaultValue = "Exhaust")
    WorkflowStrategyMode strategyMode;

    // Workflow strategy
    @ConfigProperty(name = "tasktide.engine.policy.acquisition.workflow.strategy", defaultValue = "Sequential")
    WorkflowStrategyType strategyType;
    
    // Iteration limit 
    @ConfigProperty(name = "tasktide.engine.policy.acquisition.iteration-limit", defaultValue = "-1")
    int iterationLimit = -1;
    
    // Iteration limit 
    @ConfigProperty(name = "tasktide.engine.policy.acquisition.should-cycle", defaultValue = "false")
    boolean shouldCycle;
    
    
    /**
     * Defaults {@link ArgumentTree} path to root
     * 
     */
    public WorkloadPolicyConfigurer() {
        super("engine");
    }
    
    /**
     * Sets {@link ArgumentTree} path to provided
     * 
     * @param path 
     */
    public WorkloadPolicyConfigurer(String path) {
        super(path);
    }
    
    
    /**
     * Initialize config with provided {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
    
        // Configure values
        this.strategyType();
        this.strategyMode();
        this.iterationLimit();
        this.shouldCycle();
        
        // Put argument map into tree
        if ( this.getPath().isEmpty() ) {
            this.extendPath(argTree, "engine", this.getArgumentMap());
        }
        else {
            this.extendPath(argTree, this.getPath(), this.getArgumentMap());
        }
    }
    
    
    /**
     * Configure help
     */
    @Override
    public void help() {
        Argument<Boolean> arg;
        arg = this.getArgumentBuilder()
            .withName("Help")
            .withDescription("Displays command-line documentation")
            .withShortFlag("-h")
            .withLongFlag("--help")
            .withArgType(ArgumentType.ACTION)
            .withValue(false, Boolean.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures acquisition mode
     * 
     */
    public void strategyMode() {
        Argument<WorkflowStrategyMode> arg;
        arg = this.getArgumentBuilder()
            .withName("Acquisition Mode")
            .withDescription("Specifies acqusition mode for workflow strategy")
            .withShortFlag("-am")
            .withLongFlag("--acquisition-mode")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(WorkflowStrategyType.class)
        .build();
        
        this.strategyMode = this.getConfigValue(
            "tasktide.engine.policy.acquisition.workflow.mode",
            WorkflowStrategyMode.class,
            WorkflowStrategyMode.EXHAUST
        );
        
        arg.setValue(this.strategyMode);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures strategy type
     * 
     */
    public void strategyType() {
        Argument<WorkflowStrategyType> arg;
        arg = this.getArgumentBuilder()
            .withName("Strategy Type")
            .withDescription("Specifies workflow acquisition strategy to use")
            .withShortFlag("-st")
            .withLongFlag("--strategy-type")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(WorkflowStrategyType.class)
        .build();
        
        this.strategyType = this.getConfigValue(
            "tasktide.engine.policy.acquisition.workflow.strategy",
            WorkflowStrategyType.class,
            WorkflowStrategyType.SEQUENTIAL
        );
        
        arg.setValue(this.strategyType);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures iteration limit
     * 
     */
    public void iterationLimit() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("Iteration Limit")
            .withDescription("Configures the wait time in seconds for locking an item")
            .withShortFlag("-il")
            .withLongFlag("--iteration-limit")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(Integer.class)
        .build();
        
        this.iterationLimit = this.getConfigValue("tasktide.engine.policy.acquisition.iteration-limit", Integer.class, -1);
        arg.setValue(this.iterationLimit);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure whether Engine should cycle on workflow policy.
     *  Resolves edge case where engine is running in Service-Mode, and
     *  Strategy is Sequential. So that the engine does not
     *  stay running in a closed unproductive state
     * 
     */
    public void shouldCycle() {
        Argument<Boolean> arg;
        arg = this.getArgumentBuilder()
            .withName("Should Cycle")
            .withDescription("Configure whether Engine should cycle on workflow policy")
            .withShortFlag("-sc")
            .withLongFlag("--should-cycle")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(Boolean.class)
        .build();
        
        this.shouldCycle = this.getConfigValue("tasktide.engine.policy.acquisition.should-cycle", Boolean.class, false);
        arg.setValue(this.shouldCycle);
        this.getArgumentMap().putArgument(arg);
    }
}