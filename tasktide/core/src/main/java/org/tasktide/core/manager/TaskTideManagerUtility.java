/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     
    private static final String STEP_ID = "Step-" + BuilderUtility.fetchRandomId();
    private static final String WORKFLOW_ID = "Workflow-" + BuilderUtility.fetchRandomId();
    
    
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
            System.out.println("Analysing nested task");
            List<ItemTask> nestedTasks = new ArrayList<>();
            int counter = 0;
            for ( String taskArg : parts[2].split(nestedDelim)) {
                String taskScript = parts[1] + " " + taskArg;
                // String taskName = "Task-" + counter;
                String taskName = parts[0] + "-" + counter;
                ItemTask task = new ManagerTask(taskName, taskScript).asItemTask();
                nestedTasks.add(task);
                counter++;
            }
            Workload workload = BuilderUtility.buildWorkload(nestedTasks);
            System.out.println("\n\nDisplaying created workload:\n" + workload.toJsonDoc());
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
     * Provide reader for resource
     * 
     * @param resourcePath
     * @return {@link BufferedReader}
     * @throws IllegalArgumentException
     */
    public static BufferedReader provideResourceReader(String resourcePath) throws IllegalArgumentException{
        
        // Read resources
        InputStream inputStream = TaskTideManagerUtility.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + resourcePath);
        }

        // Parse lines
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader;
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
        BufferedReader reader = provideResourceReader(resourcePath);
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
        BufferedReader reader = provideResourceReader(resourcePath);
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