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

import org.tasktide.core.model.task.ItemTask;
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
     * @return boolean
     */
    @Override
    public boolean runCommand() {
        
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
                LOGGER.error("Error import action must be one of '{}'", actions);
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
        String data = (String) this.cmdSpec.getOptionsKey("Import String").get();
        
        JsonObject json = JsonUtils.stringToJson(data);
        ManagerTask task = new ManagerTask(json.getString("Task Name"), json.getString("Task Script"));
        // String workItemId = JsonUtils.fetchStringFieldFromJson("WorkItemId", json);
        String workItemId = json.getString("WorkItemId");
        
        return TaskTideManagerUtility.appendTask(task, workItemId);
    }
    
    
    /**
     * Imports a task supplied as a json string, for SingleTsak {@link WorkItem}/initiating
     * 
     * @return {@link WorkItem}
     */
    public WorkItem addWorkItem() {
        String step = (String) this.cmdSpec.getOptions().get().get("Step Name");
        String data = (String) this.cmdSpec.getOptions().get().get("Import String");
        
        JsonObject json = JsonUtils.stringToJson(data);
        ManagerTask task = new ManagerTask(json.getString("Task Name"), json.getString("Task Script"));
        return TaskTideManagerUtility.importTask(task, json.getString("Task Name"), step);
    }
    
    
    /**
     * Validates provided data for import
     * 
     * @return boolean
     */
    @Override
    public boolean validateCommand() {
        return true;
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
        String delimiter = (String) this.cmdSpec.getOptions().get().get("Delimiter");
        String nestedDelimiter = (String) this.cmdSpec.getOptions().get().get("Nested Delimiter");
        String stepName = (String) (String) this.cmdSpec.getOptions().get().get("Step Name");
        
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
                return fetchWorkItems(stepName, file, delimiter);
            }
            
            if ( nestedDelimiter.isEmpty() ) {
                return fetchWorkItems(stepName, file, delimiter);
            }
            
            // Use nested delimiter
            else {
                return fetchWorkItems(stepName, file, delimiter, nestedDelimiter);
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
            return output;
        }
        catch ( IOException ex ) {
            LOGGER.error("Error encountered during read '{}', displaying full stack trace", ex);
            ex.printStackTrace();
            return null;
        }
    }

    
    /**
     * Validate delimiter
     * 
     * @param delim
     * @return
     * @throws IllegalArgumentException 
     */
    public String handleDelim(String delim) throws IllegalArgumentException {
    
        // Handle delimiter
        if ( delim == null || delim.isBlank() || delim.isEmpty() ) {
            throw new IllegalArgumentException("Delimiter cannot be null or empty");
        }
        
        if ( delim.equals("|") ) {
            delim = "\\|";
        }
        return delim;
    }
    
    
    /**
     * Fetch nested {@link WorkItem} collection
     * 
     * @param resourcePath
     * @param stepName
     * @param delim
     * @param nestedDelim
     * @return List-{@link WorkItem}
     * 
     * @throws IOException
     * @throws IllegalArgumentException 
     */
    private List<WorkItem> fetchWorkItems(String resourcePath, String stepName, String delim, String nestedDelim) throws IOException, IllegalArgumentException {
    
        // Fetch reader
        List<WorkItem> results = new ArrayList<>();
        String line;
        int lineNumber = 1;
        BufferedReader reader = FileIO.fetchBufferedReader(resourcePath);
        while ((line = reader.readLine()) != null) {

            // Parse data
            String[] parts = line.split(delim);
            WorkItem data = parseWorkItem(parts, stepName, nestedDelim);

            // Throw error if null output
            if (data == null) {
                throw new IllegalArgumentException(
                    "Invalid format at line " + lineNumber + ": Expected 4 fields but got " + parts.length
                );
            }

            // Otherwise proceed
            results.add(data);
            lineNumber++;
        }

        // Return results
        return results;
    }
    
    
    /**
     * Fetch un-nested {@link WorkItem} collection
     * 
     * @param resourcePath
     * @param stepName
     * @param delim
     * @return List-{@link WorkItem}
     * 
     * @throws IOException
     * @throws IllegalArgumentException 
     */
    private List<WorkItem> fetchWorkItems(String resourcePath, String stepName, String delim) throws IOException, IllegalArgumentException {
    
        // Fetch reader
        List<WorkItem> results = new ArrayList<>();
        String line;
        int lineNumber = 1;
        BufferedReader reader = FileIO.fetchBufferedReader(resourcePath);
        while ((line = reader.readLine()) != null) {

            // Parse data
            String[] parts = line.split(delim);
            WorkItem data = parseWorkItem(parts, stepName);

            // Throw error if null output
            if (data == null) {
                throw new IllegalArgumentException(
                    "Invalid format at line " + lineNumber + ": Expected 3 fields but got " + parts.length
                );
            }

            // Otherwise proceed
            results.add(data);
            lineNumber++;
        }

        // Return results
        return results;
    }
    
    
    /**
     * Parse {@link WorkItem} from input arguments
     * 
     * @param parts
     * @param stepName
     * @return{@link WorkIten}
     * 
     * @throws IllegalArgumentException 
     */
    private WorkItem parseWorkItem(String[] parts, String stepName) throws IllegalArgumentException {
    
        // Split by delimiter, expecting 3 fields
        if ( parts.length == 2) {
            ItemTask task = new ManagerTask(parts[0], parts[1]).asItemTask();
            Workload workload = BuilderUtility.buildWorkload(task);
            String stepId = TaskTideManagerUtility.fetchStepId(stepName);
            return BuilderUtility.buildWorkItem(parts[0], workload, stepName, stepId);
        }
        throw new IllegalArgumentException(
            "Invalid format: Expected 2 fields but got " + parts.length
        );
    }
    
    
    /**
     * Parse {@link WorkIten} from input arguments, splitting on provided
     *  nested delimiter
     * 
     * @param parts
     * @param stepName
     * @param nestedDelim
     * @return {@link WorkItem}
     * 
     * @throws IllegalArgumentException 
     */
    private WorkItem parseWorkItem(String[] parts, String stepName, String nestedDelim) throws IllegalArgumentException {
    
        // Handle as nested task
        if ( parts[2].split(nestedDelim).length >= 2 ) {
            
            // Create a new line for each seq value
            List<ItemTask> nestedTasks = new ArrayList<>();
            int counter = 0;
            for ( String taskArg : parts[2].split(nestedDelim)) {
                String taskScript = parts[1] + " " + taskArg;
                String taskName = parts[0] + "-" + counter;
                ItemTask task = new ManagerTask(taskName, taskScript).asItemTask();
                nestedTasks.add(task);
                counter++;
            }
            Workload workload = BuilderUtility.buildWorkload(nestedTasks);
            String stepId = TaskTideManagerUtility.fetchStepId(stepName);
            return BuilderUtility.buildWorkItem(parts[0], workload, stepName, stepId);
        }
        
        // Handle single task
        else if ( parts[2].split(nestedDelim).length == 1 ) {
            String[] newParts = Arrays.copyOfRange(parts, 0, parts.length - 2);
            newParts = Arrays.copyOf(newParts, newParts.length + 1);
            newParts[newParts.length - 1] = parts[parts.length - 2] + " " + parts[parts.length - 1];
            return parseWorkItem(newParts, stepName);
        }
        
        // Otherwise raise exception
        throw new IllegalArgumentException(
            "Invalid format: Expected 3 fields but got " + parts.length
        );
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