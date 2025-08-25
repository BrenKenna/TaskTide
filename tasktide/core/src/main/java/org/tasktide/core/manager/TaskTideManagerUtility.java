/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;




/**
 * Collection of static methods to support {@link ManagerTask} to {@link TaskTideModel}.
 * 
 * @author bkenna
 */
public class TaskTideManagerUtility {
    
    // Attributes
    private static final Logger LOGGER = LogManager.getLogger(TaskTideManagerUtility.class);
    private static final String STEP_ID = "Step-" + BuilderUtility.fetchRandomId();
    private static final String WORKFLOW_ID = "Workflow-" + BuilderUtility.fetchRandomId();
    
    
    /**
     * Fetches reader stream from provided file
     * 
     * @param resourcePath
     * @return BufferedReader for file
     * @throws IOException 
     */
    public static BufferedReader fetchReaderStream(String resourcePath) throws IOException {
        Path path = Paths.get(resourcePath);
        return Files.newBufferedReader(path);
    }
    
    
    /**
     * Reset the items listed in provided file, a delimiter can be
     *  used to specify that only targetted itemTask is reset. Returns
     *  count of updates performed.
     * 
     * @param resourcePath
     * @param delimiter
     * @return int
     */
    public static int resetItems(String resourcePath, String delimiter) {
        try (BufferedReader reader = fetchReaderStream(resourcePath)) {
        
            // Parse lines
            int counter = 0;
            String line;
            LOGGER.debug("Splitting file on delimiter:\t'{}'", delimiter);
            while ( (line = reader.readLine()) != null ) {
                
                // No delimiter is treated as list
                if ( delimiter.isEmpty() ) {
                    WorkItem item = TaskTideServiceManager.fetchWorkItemService().fetchById(line);
                    item.resetModel();
                    TaskTideServiceManager.fetchWorkItemService().updateModel(item);
                }
                
                // Otherwise acknowledge tanle
                else {
                    String[] arr = line.split(delimiter);
                    switch ( arr.length ) {
                        case 2 -> {
                            String workItemId = arr[0];
                            String itemTaskId = arr[1];
                            WorkItem item = TaskTideServiceManager.fetchWorkItemService().fetchById(workItemId);
                            item.resetTask(itemTaskId);
                            TaskTideServiceManager.fetchWorkItemService().updateModel(item);
                        }

                        default -> {
                            LOGGER.warn("Skipping malformed line:\t'{}', array length = '{}'", line, arr.length);
                        }
                    }
                }
                counter++;
            }
            
            return counter;
        }
        
        catch (IOException ex) {
            return -1;
        }
    }
    
    
    
    /**
     * Import task
     * 
     * @param task
     * @param workItemName
     * @param stepName
     * @return WorkItem
     */
    public static WorkItem importTask(ManagerTask task, String workItemName, String stepName) {
        Workload workload = BuilderUtility.buildWorkload(task.asItemTask());
        String stepId = TaskTideManagerUtility.fetchStepId(stepName);
        WorkItem item = BuilderUtility.buildWorkItem(workItemName, workload, stepId);
        return TaskTideServiceManager.fetchWorkItemService().appendModel(item);
    }
    
    
    /**
     * Append a task to work itme
     * 
     * @param task
     * @param workItemId
     * @return {@link WorkItem}
     */
    public static WorkItem appendTask(ManagerTask task, String workItemId) {
        WorkItem workItem = TaskTideServiceManager.fetchWorkItemService().fetchById(workItemId);
        workItem.addTask(task.asItemTask());
        return TaskTideServiceManager.fetchWorkItemService().updateModel(workItem);
    }
    
    
    /**
     * Handle delimiters
     * 
     * @param delim
     * @return String
     * <br><br>
     * @throws IllegalArgumentException
     */
    public static String handleDelim(String delim) throws IllegalArgumentException{
    
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
     * Import provided data
     * 
     * @param stepName
     * @param resourcePath
     * @param delim
     * @return List-{@link WorkItem}
     * <br><br>
     * @throws java.io.IOException 
     * @throws IllegalArgumentException 
     */
    public static List<WorkItem> importTasks(String stepName, String resourcePath, String delim) throws IOException, IllegalArgumentException {
        
        // Handle delimiter
        delim = handleDelim(delim);
        
        // Try read test resource
        try {
            return fetchWorkItems(resourcePath, stepName, delim);
        }
        
        // Otherwise throw resource not found
        catch (Exception ex) {
            throw ex;
        }
    }

    
    /**
     * Import provided data
     * 
     * @param stepName
     * @param resourcePath
     * @param delim
     * @param nestedDelim
     * @return List-{@link WorkItem}
     * <br><br>
     * @throws IOException
     * @throws IllegalArgumentException 
     */
    public static List<WorkItem> importTasks(String stepName, String resourcePath, String delim, String nestedDelim) throws IOException, IllegalArgumentException {
        
        // Intialize results
        delim = handleDelim(delim);
        
        // Try read test resource
        try {
            return fetchNestedWorkItems(resourcePath, stepName, delim, nestedDelim);
        }
        
        // Otherwise throw resource not found
        catch (Exception ex) {
            throw ex;
        }
    }
    
    
    /**
     * Handle parsing of data line to {@link WorkItem}
     * 
     * @param parts
     * @param stepName
     * @return {@link WorkItem WorkItem}
     * <br><br>
     * @throws IllegalArgumentException 
     */
    public static WorkItem parseWorkItem(String[] parts, String stepName) throws IllegalArgumentException {
    
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
     * Parse line as a nested task
     * 
     * @param parts
     * @param stepName
     * @param nestedDelim
     * @return {@link WorkItem}
     * <br><br>
     * @throws IllegalArgumentException 
     */
    public static WorkItem parseWorkItem(String[] parts, String stepName, String nestedDelim) throws IllegalArgumentException {
    
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
     * Fetch {@link WorkItem WorkItem} list from resource
     * 
     * @param resourcePath
     * @param stepName
     * @param delim
     * @return List-{@link WorkItem}
     * <br><br>
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static List<WorkItem> fetchWorkItems(String resourcePath, String stepName, String delim) throws IOException, IllegalArgumentException {
    
        // Fetch reader
        List<WorkItem> results = new ArrayList<>();
        String line;
        int lineNumber = 1;
        BufferedReader reader = fetchReaderStream(resourcePath);
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
     * Fetch nested list of work items
     * 
     * @param resourcePath
     * @param stepName
     * @param delim
     * @param nestedDelim
     * @return List-{@link WorkItem}
     * <br><br>
     * @throws IOException
     * @throws IllegalArgumentException 
     */
    public static List<WorkItem> fetchNestedWorkItems(String resourcePath, String stepName, String delim, String nestedDelim) throws IOException, IllegalArgumentException {
    
        // Fetch reader
        List<WorkItem> results = new ArrayList<>();
        String line;
        int lineNumber = 1;
        BufferedReader reader = fetchReaderStream(resourcePath);
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
     * Fetch stepId for provided step, or register as new
     * 
     * @param stepName
     * @return String
     */
    public static String fetchStepId(String stepName) {
        List<Step> steps = TaskTideServiceManager.fetchStepService().viewByField("stepName", stepName);
        if ( steps.isEmpty() ) {
            TaskTideManagerUtility.configureNewStep(stepName);
            return STEP_ID;
        }
        else {
            return steps.get(0).getId();
        }
    }
    
    
    /**
     * Configures a new step
     * 
     * @param stepName 
     */
    public static void configureNewStep(String stepName) {
        Step step = BuilderUtility.buildStep(STEP_ID, stepName);
        
        Workflow workflow = BuilderUtility.buildEmptyWorkflow();
        workflow.setWorkflowName(stepName);
        workflow.setWorkflowId(WORKFLOW_ID);
        workflow.setWorkflowSteps(Map.of(stepName, step));
        
        step.setWorkflowId(WORKFLOW_ID);
        step.setWorkflowId(workflow);
        
        TaskTideServiceManager.fetchWorkflowService().appendModel(workflow);
        TaskTideServiceManager.fetchStepService().appendModel(step);
    }
    
    
    /**
     * Fetch workflowId for provided step
     * 
     * @param workflowName
     * @return String
     */
    public static String fetchWorkflowId(String workflowName) {
        List<Workflow> workflows = TaskTideServiceManager.fetchWorkflowService().viewByField("WorkflowName", workflowName);
        if ( !workflows.isEmpty() ) {
            configureNewStep(workflowName);
            return WORKFLOW_ID;
        }
        else {
            return workflows.get(0).getId();
        }
    }
}