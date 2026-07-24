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
public class WorkflowPolicyConfigurer extends AbstractConfig {

    // Acquisition mode
    @ConfigProperty(name = "tasktide.engine.policy.acquisition.workflow.mode", defaultValue = "Exhaust")
    WorkflowStrategyMode strategyMode;
    

    // Workflow strategy
    @ConfigProperty(name = "tasktide.engine.policy.acquisition.workflow.strategy", defaultValue = "Sequential")
    WorkflowStrategyType strategyType;
    
    
    /**
     * Defaults {@link ArgumentTree} path to root
     * 
     */
    public WorkflowPolicyConfigurer() {
        super("engine");
    }
    
    /**
     * Sets {@link ArgumentTree} path to provided
     * 
     * @param path 
     */
    public WorkflowPolicyConfigurer(String path) {
        super(path);
    }
    
    
    /**
     * Initialize config with provided {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
    
         // Put argument map into tree
        if ( this.getPath().isEmpty() ) {
            argTree.getTree().getRoot().setData(this.getArgumentMap());
        }
        else {
            argTree.getTree().getRoot().getData().extend(this.getArgumentMap());
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
}