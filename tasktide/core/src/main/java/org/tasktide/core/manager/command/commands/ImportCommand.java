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
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.io.BufferedReader;
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

import org.tasktide.core.supporting.FileIO;
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
    
    // Whether import is via query or file
    @JsonbProperty("Import Type")
    private final ImportType importType;
    
    
    /**
     * Construct import command
     * 
     * @param action
     * @param target
     * @param cmdSpec
     */
    public ImportCommand(
        @JsonbProperty("Manager Action") ManagerAction action,
        @JsonbProperty("Manager Target") ManagerTarget target,
        @JsonbProperty("Command Spec") CommandSpec cmdSpec
    ) {
        super(action, target, cmdSpec);
        this.importType = null;
    }
    
    
    /**
     * Construct import command
     * 
     * @param action
     * @param target
     * @param cmdSpec
     * @param importType 
     */
    @JsonbCreator
    public ImportCommand(
        @JsonbProperty("Manager Action") ManagerAction action,
        @JsonbProperty("Manager Target") ManagerTarget target,
        @JsonbProperty("Command Spec") CommandSpec cmdSpec,
        @JsonbProperty("Import Type") ImportType importType
    ) {
        super(action, target, cmdSpec);
        this.importType = importType;
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
        String nestedDelimiter = (String) this.cmdSpec.getOptionsKey("Nested Delimiter").get();
        
        // Import workload from JSON: Format argument instead
        if (delimiter.equalsIgnoreCase("json")) {
            LOGGER.info("Importing JSON file");
            return this.importJson(file);
        }
        
        // Otherwise table
        else {
            
            // With no nested delimiter
            LOGGER.info("Evaluating nested delimiter of value '{}'", nestedDelimiter);
            if (nestedDelimiter == null) {
                LOGGER.info("No nested delimiter detected, importing as single tasks");
                return ImportCommandRecordProcessor.parseSingleTaskWorkItem(this, LOGGER);
            }
            
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
            LOGGER.error("Error encountered during read '{}', displaying full stack trace", ex);
            ex.printStackTrace();
            return null;
        }
    }

    
    /**
     * Provides {@link ImportType}
     * 
     * @return {@link ImportType}
     */
    public ImportType getImportType() {
        return importType;
    }
}