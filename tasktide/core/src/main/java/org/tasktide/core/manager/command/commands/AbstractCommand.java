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
package org.tasktide.core.manager.command.commands;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.CommandType;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;

// For JavaDoc
import org.tasktide.core.TaskTideRepository;


/**
 * Abstract class to wrap standardize class fields
 *  across manager commands, getters/setters etc, JSON (de)serialization,
 *  wraps the execution process with a validation step. Since 
 *  {@link ManagerCommand} can be built, and returned as that interface,
 *  this abstract carries a lot of useful foundational work on behalf of
 *  the implementing classes which they all can use, without stepping into
 *  the complexity of them.
 * 
 * @author Brendan Kenna
 */
public abstract class AbstractCommand implements ManagerCommand {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(AbstractCommand.class);
    
    @JsonbProperty("Manager Action")
    protected final ManagerAction action;
    
    @JsonbProperty("Manager Target")
    protected final ManagerTarget target;
    
    @JsonbProperty("Command Spec")
    protected final CommandSpec cmdSpec;
    
    @JsonbProperty("Command Type")
    protected final CommandType cmdType;
    
    
    /**
     * Construct with requriements
     * 
     * @param action
     * @param target
     * @param cmdSpec 
     * @param cmdType 
     */
    @JsonbCreator
    public AbstractCommand(
        @JsonbProperty("Manager Action") ManagerAction action,
        @JsonbProperty("Manager Target") ManagerTarget target,
        @JsonbProperty("Command Spec") CommandSpec cmdSpec,
        @JsonbProperty("Command Type") CommandType cmdType
    ) {
        this.action = action;
        this.target = target;
        this.cmdSpec = cmdSpec;
        this.cmdType = cmdType;
    }

    
    /**
     * Execution is wrapped into the abstract validation, then runCommand calls
     * 
     * @return boolean
     */
    @Override
    public Object execute() {
        if ( this.validateCommand() ) {
            return this.runCommand();
        }
        return false;
    }
    
    
    /**
     * Defines how a concrete {@link ManagerCommand}
     *  executes a validated command
     * 
     * @return Object
     */
    public abstract Object runCommand();

    
    /**
     * Get configured action for command
     * 
     * @return {@link ManagerAction}
     */
    public ManagerAction getAction() {
        return this.action;
    }

    
    /**
     * Get target for command
     * 
     * @return {@link ManagerTarget}
     */
    public ManagerTarget getTarget() {
        return this.target;
    }

    
    /**
     * Get command specifications for task
     * 
     * @return {@link CommandSpec}
     */
    public CommandSpec getCmdSpec() {
        return this.cmdSpec;
    }

    
    /**
     * Get {@link CommandType} of {@link ManagerAction}
     *  taken against the {@link TaskTideRepository}
     * 
     * @return {@link CommandType}
     */
    public CommandType getCmdType() {
        return this.cmdType;
    }
    
    
    /**
     * Represent as JSON string
     * 
     * @return String
     */
    @Override
    public String toJsonString() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Represent as JSON document
     * 
     * @return String
     */
    @Override
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }
    
    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "AbstractCommand{" +
            "action="    + this.action +
            ", target="  + this.target +
            ", cmdSpec=" + this.cmdSpec +
            ", cmdType=" + this.cmdType +
        '}';
    }
}