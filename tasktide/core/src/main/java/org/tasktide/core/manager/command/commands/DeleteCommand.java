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

import jakarta.json.JsonObject;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.manager.file_handler.WorkItemFileProcessor;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.CommandType;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerTarget;


// For JavaDoc
import org.tasktide.core.manager.command.ManagerCommand;


/**
 * Class for handling the deletion of {@link TaskTideModel}
 * 
 * @author Brendan Kenna
 */
public class DeleteCommand extends AbstractCommand {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(DeleteCommand.class);
    
    
    /**
     * Construct delete command
     * 
     * @param action
     * @param target
     * @param cmdSpec 
     * @param cmdType 
     */
    @JsonbCreator
    public DeleteCommand(
        @JsonbProperty("Manager Action") ManagerAction action,
        @JsonbProperty("Manager Target") ManagerTarget target,
        @JsonbProperty("Command Spec") CommandSpec cmdSpec,
        @JsonbProperty("Command Type") CommandType cmdType
    ) {
        super(action, target, cmdSpec, cmdType);
    }
    
    
    /**
     * Deletes entire {@link WorkItem}
     * 
     * @return boolean as Object
     */
    public Object deleteWorkItem() {
        String itemId = (String) this.cmdSpec.getOptionsKey("Item Id").get();
        return TaskTideServiceManager.fetchWorkItemService().dropById(itemId);
    }
    
    
    /**
     * Delete {@link ItemTask} from {@link WorkItem}. Requires
     *  {@link CommandSpec} to have query string in below format.
     * <br>
     * '{"Item Id": "XXXX", "Task Id": "YYY"}'
     * <br>OR<br>
     * '{"Item Id": "XXXX", "Task Name": "YYY"}'
     * 
     * @return {@link WorkItem} as Object
     */
    public Object deleteTask() {
        
        // Fetch query string into json
        WorkItem item; ItemTask task;
        String queryString = this.cmdSpec.getQueryString().get();
        JsonObject json = JsonUtils.stringToJson(queryString);
        
        // Fetch work item
        String workItemId = json.getString("Item Id");
        item = TaskTideServiceManager.fetchWorkItemService().fetchById(workItemId);
        
        // Fetch task
        String taskStr = json.getString("Task Id", "");
        if ( taskStr.isEmpty() ) {
            taskStr = json.getString("Task Name");
            task = item.getWorkload().getTask(taskStr);
        }
        else {
            task = item.getWorkload().getById(taskStr);
        }
        
        // Drop task and update workitem
        item.dropTask(task);
        return TaskTideServiceManager.fetchWorkItemService().updateModel(item);
    }
    
    
    /**
     * Iteratively delete provided list of {@link WorkItem} Ids
     * 
     * @return int
     */
    public int deleteTasks() {
        
        // Fetch required args
        String targetFile = (String) this.cmdSpec.getFilePath().get();
        String delimiter;
        if ( this.cmdSpec.getOptionsKey("Delimiter").isPresent() ) {
            delimiter = (String) this.cmdSpec.getOptionsKey("Delimiter").get();
        }
        else {
            delimiter = "";
        }
        
        // Pass processing to processor
        return WorkItemFileProcessor.deleteTasks(targetFile, delimiter, LOGGER);
    }
    
    
    /**
     * Handles running correct {@link ManagerAction}
     * 
     * @return Object
     */
    @Override
    public Object runCommand() {
        switch ( this.action ) {
        
            case DELETE -> {
                
                if (
                    this.optionsHasStringValue("Item Id") &&
                    this.optionsHasStringValue("Step Name") &&
                    !this.cmdSpec.queryStringIsEmpty()
                ) {
                    String msg = String.format(
                        "Error 'Item Id', 'Step Name', and 'Query String' may all not be null:\n'%s'",
                        this.cmdSpec.toJsonDoc()
                    );
                    throw new IllegalArgumentException(msg);
                }
                
                if ( !this.cmdSpec.queryStringIsEmpty() ) {
                    LOGGER.info("Deleting ItemTask from WorkItem provided as JSON");
                    return this.deleteTask();
                }
                else {
                    LOGGER.info("Deleteing WorkItem");
                    return this.deleteWorkItem();
                }
            }
            
            case DELETE_LIST -> {
                LOGGER.info("Deleting tasks from provided file");
                return this.deleteTasks();
            }
            
            default -> {
                ManagerAction[] actions = { ManagerAction.DELETE, ManagerAction.DELETE_LIST };
                String msg = String.format("Reset command must be one of:\t'%s'", (Object[]) actions);
                throw new IllegalArgumentException(msg);
            }
        }
    }
    
    
    /**
     * Validates if {@link ManagerCommand} can occur
     * 
     * @return boolean
     */
    @Override
    public boolean validateCommand() {
        
        // Required for deleting task
        if ( this.cmdSpec.getQueryString().isPresent() ) {
            return true;
        }
        
        // Required for deleting work item
        if ( this.cmdSpec.getOptionsKey("Item Id").isPresent() ) {
            return true;
        }
        
        // Required for deleting a list of records
        if ( this.cmdSpec.getFilePath().isPresent() ) {
            return true;
        }
        
        // Unable to validate
        LOGGER.error("Error unable to validate DeleteCommand please review provided query string, target file, Item Id");
        return false;
    }
    
    
    /**
     * Verifies ItemId in {@link CommandSpec}
     * 
     * @param key
     * @return boolean
     */
    private boolean optionsHasStringValue(String key) {
        Object val;
        if ( this.cmdSpec.getOptionsKey(key).isPresent() ) {
            val = this.cmdSpec.getOptionsKey(key).get();
            return !((String) val).isBlank() ;
        }
        return false;
    }
}