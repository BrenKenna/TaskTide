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

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerTarget;

import org.tasktide.core.manager.file_handler.WorkItemFileProcessor;



/**
 * Command class handling resetting {@link TaskTideModel}
 * 
 * @author Brendan Kenna
 */
public class ResetCommand extends AbstractCommand {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(ResetCommand.class);
    
    
    /**
     * Construct reset command
     * 
     * @param action
     * @param target
     * @param cmdSpec
     */
    @JsonbCreator
    public ResetCommand(
        @JsonbProperty("Manager Action") ManagerAction action,
        @JsonbProperty("Manager Target") ManagerTarget target,
        @JsonbProperty("Command Spec") CommandSpec cmdSpec
    ) {
        super(action, target, cmdSpec);
    }

    
    /**
     * Handles whether to reset a single, or collection
     *  of {@link WorkItem}. Throwing
     *  IllegalStateException for all but RESET_ITEM, or RESET_ITEMS
     * 
     * @return 
     */
    @Override
    public Object runCommand() {
        
        // Handle whether to reset single or collection
        switch ( this.action ) {
            
            // Reset single
            case RESET_ITEM -> {
                return resetWorkItem() != null;
            }
            
            // Reset collection
            case RESET_ITEMS -> {
                return this.resetItems() >= 0;
            }
            
            // Otherwise throw error
            default -> {
                ManagerAction[] actions = { ManagerAction.RESET_ITEM, ManagerAction.RESET_ITEMS };
                String msg = String.format("Reset command must be one of:\t'%s'", (Object[]) actions);
                throw new IllegalArgumentException(msg);
            }
        }
    }

    
    /**
     * Validates whether command spec provided is
     *  sufficient for required {@link ManagerAction}. Throwing
     *  IllegalStateException for all but RESET_ITEM, or RESET_ITEMS
     * 
     * @return boolean
     */
    @Override
    public boolean validateCommand() {
        switch ( this.action ) {
        
            case RESET_ITEM -> {
                return this.cmdSpec.hasOptionsKey("Item Id");
            }
            
            case RESET_ITEMS -> {
                return
                    this.cmdSpec.getFilePath().isPresent()
                    && this.cmdSpec.hasOptionsKey("Delimiter");
            }
            
            default -> {
                ManagerAction[] actions = { ManagerAction.RESET_ITEM, ManagerAction.RESET_ITEMS };
                String msg = String.format("Reset command must be one of:\t'%s'", (Object[]) actions);
                throw new IllegalArgumentException(msg);
            }
        }
    }
    
    
    /**
     * Resets configured "Item Id", returning the updated
     *  {@link WorkItem}
     * 
     * @return {@link WorkItem}
     */
    public WorkItem resetWorkItem() {
        
        // Fetch item to reset
        String itemId = (String) this.cmdSpec.getOptionsKey("Item Id").get();
        WorkItem item = (WorkItem) TaskTideServiceManager.fetchWorkItemService().fetchById(itemId);
        
        // Reset and update
        item.resetModel();
        return TaskTideServiceManager.fetchWorkItemService().updateModel(item);
    }
    
    
    /**
     * Iteratively reset provided list of {@link WorkItem} Ids
     * 
     * @return int
     */
    public int resetItems() {
        
        // Fetch required args
        String targetFile = (String) this.cmdSpec.getFilePath().get();
        String delimiter  = (String) this.cmdSpec.getOptionsKey("Delimiter").get();
        
        // Pass processing to processor
        return WorkItemFileProcessor.resetItems(targetFile, delimiter, LOGGER);
    }
}