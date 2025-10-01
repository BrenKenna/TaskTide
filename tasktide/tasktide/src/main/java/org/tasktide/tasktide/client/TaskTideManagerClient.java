/*
 * Copyright 2025 Brendan Kenna.
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
package org.tasktide.tasktide.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.ArgumentMap;


/**
 * Manager client for providing IO functionality for 
 *  Workloads, Steps, and WorkItems
 * 
 * @author bkenna
 */
public class TaskTideManagerClient extends TaskTideClient {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(TaskTideEngineClient.class);
    private final ArgumentMap managerArgs;
    private final ArgumentMap globalArgs;
    private ManagerTarget target;
    private ManagerAction action;


    /**
     * Construct engine client
     * 
     * @param configMap
     */
    public TaskTideManagerClient(ClientConfigMap configMap) {
        super(configMap);
        managerArgs = this.getArgTree().getTree().getDataForAddress("manager");
        globalArgs = this.getArgTree().getTree().getDataForAddress("");
    }
    
    
    /**
     * Constructor for unit-tests, uses null {@link ClientConfigMap},
     *   and {@link ArgumentTree}
     * 
     * @param argTree 
     */
    public TaskTideManagerClient(ArgumentTree argTree) {
        super(null);
        managerArgs = argTree.getTree().getDataForAddress("manager");
        globalArgs = argTree.getTree().getDataForAddress("");
    }

    
    /**
     * Validate client arguments are configurable
     * 
     * @return boolean
     */
    @Override
    protected boolean configureClient() {
        if ( ((String) this.managerArgs.getArgument("Method").getValue()).isEmpty() ) {
            return false;
        }
        // String inFile = ((String) this.managerArgs.getArgument("Target File").getValue());
        // return !inFile.isEmpty();
        return true;
    }
    
    
    /**
     * Clear out temp files etc
     * 
     */
    @Override
    protected void cleanUp() {
    }
    

    /**
     * Executes the configured manager method to by mapping client action
     */
    @Override
    protected void performClientTask() {
        
        // Initialize vars
        ManagerCommand cmd;
        Object result;
        
        // Fetch command to run
        cmd = this.getManagerCommand();
        
        // Run command
        LOGGER.info("Executing ManagerCommand:\n'{}'", JsonUtils.toJson(true, cmd));
        result = cmd.execute();
        
        // Handle logging based on action
        LOGGER.info("Displaying results:\t'{}'", JsonUtils.toJson(true, result));
    }
    
    
    /**
     * Maps values from {@link ClientConfigMap} to {@link CommandSpec}
     * 
     * @return {@link CommandSpec}
     */
    public CommandSpec mapToCommandSpec() {

        // Target file & query string
        String targetFile = (String) this.managerArgs.getArgument("Target File").getValue();
        String queryString = (String) this.managerArgs.getArgument("Import String").getValue();
        
        // Setup options map
        Map<String, Object> opts = new HashMap<>();
        opts.put("Step Name", (String) this.globalArgs.getArgument("Step Name").getValue());
        opts.put("Delimiter", (String) this.managerArgs.getArgument("Delimiter").getValue());
        opts.put("Nested Delimiter", (String) this.managerArgs.getArgument("Nested Delimiter").getValue());
        opts.put("Item Id", (String) this.managerArgs.getArgument("ItemId").getValue());
        
        // Return command spec
        return new CommandSpec(targetFile, queryString, opts);
    }
    
    
    /**
     * Fetch configured {@link ManagerCommand} to run,
     *  internally {@link CommandSpec} for peripheral
     *  parameters required outside of this {@link ManagerAction}.makeComand()
     * 
     * @return {@link ManagerCommand}
     */
    public ManagerCommand getManagerCommand() {
    
        // Fetch task to perform
        String actVal = (String) this.managerArgs.getArgument("Method").getValue();
        ManagerAction action = ManagerAction.get(actVal);
        this.setAction(action);
        
        // Fetch target of action
        String tgtVal = (String) this.managerArgs.getArgument("Target").getValue();
        ManagerTarget target = ManagerTarget.get(tgtVal);
        this.setTarget(target);
        
        // Fetch command spec
        CommandSpec cmdSpec = this.mapToCommandSpec();
        
        // Provide manager command
        return action.makeCommand(target, cmdSpec);
    }
    
    
    /**
     * Sets {@link ManagerTarget}
     * 
     * @param target 
     */
    public void setTarget(ManagerTarget target) {
        this.target = target;
    }
    
    
    /**
     * Sets {@link ManagerAction}
     * 
     * @param action 
     */
    public void setAction(ManagerAction action) {
        this.action = action;
    }
}