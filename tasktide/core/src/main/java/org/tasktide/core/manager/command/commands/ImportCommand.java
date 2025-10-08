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

import org.tasktide.core.manager.command.CommandType;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.annotation.JsonbProperty;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideService;

import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.manager.JsonbManagerTaskWorkItemAdapter;
import org.tasktide.core.manager.ManagerTask;
import org.tasktide.core.manager.TaskTideManagerUtility;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;

import org.tasktide.core.manager.file_handler.ImportCommandRecordProcessor;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;

import org.tasktide.core.supporting.JsonUtils;


/**
 * {@link ManagerCommand} for importing tasks from JSON
 *  query string, or file
 *
 * @author Brendan Kenna
 */
public class ImportCommand extends AbstractCommand{
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(ImportCommand.class);

    
    /**
     * Construct import command
     * 
     * @param action
     * @param target
     * @param cmdSpec
     * @param cmdType
     */
    public ImportCommand(
        @JsonbProperty("Manager Action") ManagerAction action,
        @JsonbProperty("Manager Target") ManagerTarget target,
        @JsonbProperty("Command Spec") CommandSpec cmdSpec,
        @JsonbProperty("Command Type") CommandType cmdType
    ) {
        super(action, target, cmdSpec, cmdType);
    }
    
    
    /**
     * Performs import command
     * 
     * @return Object
     */
    @Override
    public Object runCommand() {
        
        // Handle how to import
        switch (this.action) {
        
            // Import from file
            case IMPORT -> {
                try {
                    List<WorkItem> data = this.importFile();
                    TaskTideService<WorkItem> serv = TaskTideServiceManager.getService(this.target);
                    serv.extendModel(data);
                    return true;
                }
                catch (IOException ex) {
                    LOGGER.error("Error during importing process, displaying stack trace:\t'{}'", ex);
                    ex.printStackTrace();
                    return false;
                }
            }
            
            // Add workitem
            case ADD -> {
                return this.addWorkItem() != null;
            }
            
            // Append
            case APPEND -> {
                return this.appendToWorkItem() != null;
            }
            
            // Otherwise error
            default -> {
                ManagerAction[] actions = { ManagerAction.IMPORT, ManagerAction.ADD, ManagerAction.APPEND };
                LOGGER.error("Error import action must be one of '{}'", (Object[]) actions);
                return false;
            }
        }
    }
    
    
    /**
     * Add a task to a {@link WorkItem}
     * 
     * @return {@link WorkItem}
     */
    public WorkItem appendToWorkItem() {
        String data = this.cmdSpec.getQueryString().get();
        JsonObject json = JsonUtils.stringToJson(data);
        ManagerTask task = new ManagerTask(json.getString("Task Name"), json.getString("Task Script"));
        String workItemId = json.getString("WorkItemId");
        return appendTask(task, workItemId);
    }
    
    
    /**
     * Append task to target {@link WorkItem}
     * 
     * @param task
     * @param workItemId
     * @return {@link WorkItem}
     */
    public WorkItem appendTask(ManagerTask task, String workItemId) {
        WorkItem workItem = TaskTideServiceManager.fetchWorkItemService().fetchById(workItemId);
        workItem.addTask(task.asItemTask());
        return TaskTideServiceManager.fetchWorkItemService().updateModel(workItem);
    }
    
    
    /**
     * Imports a task supplied as a json string, for SingleTsak {@link WorkItem}/initiating
     * 
     * @return {@link WorkItem}
     */
    public WorkItem addWorkItem() {
        String step = (String) this.cmdSpec.getOptions().get().get("Step Name");
        String data = this.cmdSpec.getQueryString().get();
        
        LOGGER.debug("Creating json object from:\t'{}'", data);
        JsonObject json = JsonUtils.stringToJson(data);
        ManagerTask task = new ManagerTask(json.getString("Task Name"), json.getString("Task Script"));
        return this.importTask(task, json.getString("Task Name"), step);
    }
    
    
    /**
     * Import task providing updated {@link WorkItem}
     * 
     * @param task
     * @param workItemName
     * @param stepName
     * @return {@link WorkItem}
     */
    public WorkItem importTask(ManagerTask task, String workItemName, String stepName) {
        Workload workload = BuilderUtility.buildWorkload(task.asItemTask());
        String stepId = TaskTideManagerUtility.fetchStepId(stepName);
        WorkItem item = BuilderUtility.buildWorkItem(workItemName, workload, stepId);
        return TaskTideServiceManager.fetchWorkItemService().appendModel(item);
    }
    
    
    /**
     * Validates provided data for import
     * 
     * @return boolean
     */
    @Override
    public boolean validateCommand() {
        if ( this.cmdSpec.getFilePath().isEmpty() ) {
            return false;
        }
        return !this.cmdSpec.getOptionsKey("Delimiter").isEmpty();
    }
    
    
    /**
     * Imports data from file
     * 
     * @return boolean
     * @throws java.io.IOException
     */
    public List<WorkItem> importFile() throws IOException {
        
        // Fetch arguments
        String file = this.cmdSpec.getFilePath().get();
        String delimiter = (String) this.cmdSpec.getOptionsKey("Delimiter").get();
        String nestedDelimiter = (String) this.cmdSpec.getOptionsKey("Nested Delimiter").orElse("");
        
        // Import workload from JSON: Format argument instead
        if (delimiter.equalsIgnoreCase("json")) {
            LOGGER.info("Importing JSON file");
            if ( this.target.isManagerTarget(ManagerTarget.MANAGERTASK) ) {
                LOGGER.info("Importing ManagerTask for WorkItem");
                return this.importWorkItemFromManagerTaskJson(file);
            }
            else {
                return this.importJson(file);
            }
        }
        
        // Otherwise table
        else {
            
            // With no nested delimiter
            LOGGER.info("Evaluating nested delimiter of value '{}'", nestedDelimiter);
            if ( nestedDelimiter.isEmpty() ) {
                LOGGER.info("No nested delimiter detected, importing as single tasks");
                return ImportCommandRecordProcessor.parseSingleTaskWorkItem(this, LOGGER);
            }
            
            // Use nested delimiter
            else {
                nestedDelimiter = TaskTideManagerUtility.handleDelim(nestedDelimiter);
                LOGGER.info("Importing using nested delimiter of:'{}'", nestedDelimiter);
                return ImportCommandRecordProcessor.parseNestedTaskWorkItem(this, LOGGER);
            }
        }
    }

    
    /**
     * Deserialize {@link WorkItem} collection from 
     * 
     * @param file
     * @return List of {@link WorkItem}
     */
    private List<WorkItem> importJson(String file){
        LOGGER.info("Attempting to read JSON file:\t'{}'", file);
        try ( Reader inpStream = new FileReader(file) ) {
            Jsonb jsonb = JsonbBuilder.create();
            LOGGER.info("Streaming JSON data into WorkItem list");
            List<WorkItem> output = Arrays.asList(jsonb.fromJson(inpStream, WorkItem[].class));
            LOGGER.info("Streamed JSON data into WorkItem list");
            inpStream.close();
            return output;
        }
        catch ( IOException ex ) {
            LOGGER.error("Error encountered during read '{}', displaying full stack trace", ex.getMessage());
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    
    /**
     * Import {@link ManagerTask} from json formatted file
     * 
     * @param file
     * @return List-{@link ManagerTask}
     */
    private List<ManagerTask> importManagerTasksFromJson(String file) {
        LOGGER.info("Attempting to read JSON file:\t'{}'", file);
        try (Reader inpStream = new FileReader(file)) {
            Jsonb jsonb = JsonbBuilder.create();
            LOGGER.info("Streaming JSON data into ManagerTask list");
            List<ManagerTask> tasks;
            try {
                tasks = Arrays.asList(jsonb.fromJson(inpStream, ManagerTask[].class));
            }
            catch (JsonbException ex) {
                if ( ex.getMessage().contains("deserialize type") ) {
                    ManagerTask task = jsonb.fromJson(inpStream, ManagerTask.class);
                    tasks = List.of(task);
                }
                else {
                    throw ex;
                }
            }
            LOGGER.info("Streamed JSON data into ManagerTask list");
            inpStream.close();
            return tasks;
        }
        catch ( IOException ex ) {
            LOGGER.error("Error encountered during reading of '{}', displaying full stack trace", ex);
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    
    /**
     * Import {@link WorkItem} from json formatted file
     * 
     * @param file
     * @return List-{@link WorkItem}
     */
    private List<WorkItem> importWorkItemFromManagerTaskJson(String file) {
        LOGGER.info("Attempting to read JSON file:\t'{}'", file);
        try (Reader inpStream = new FileReader(file)) {
            Jsonb jsonb = JsonbBuilder.create(
                new JsonbConfig().withAdapters(new JsonbManagerTaskWorkItemAdapter())
            );
            LOGGER.info("Streaming JSON data into WorkItem list");
            List<WorkItem> tasks;
            try {
                tasks = Arrays.asList(jsonb.fromJson(inpStream, WorkItem[].class));
            }
            catch (JsonbException ex) {
                if ( ex.getMessage().contains("deserialize type") ) {
                    WorkItem task = jsonb.fromJson(inpStream, WorkItem.class);
                    tasks = List.of(task);
                }
                else {
                    throw ex;
                }
            }
            LOGGER.info("Streamed JSON data into WorkItem list");
            inpStream.close();
            return tasks;
        }
        catch ( Exception ex ) {
            LOGGER.error("Error encountered during reading of '{}', displaying full stack trace", ex);
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}