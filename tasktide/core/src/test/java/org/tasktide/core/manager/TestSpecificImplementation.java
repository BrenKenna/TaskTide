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
package org.tasktide.core.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;

/**
 *
 * @author Brendan Kenna
 */
public class TestSpecificImplementation {
    
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
    public static List<WorkItem> importTasksForTesting(String stepName, String resourcePath, String delim) throws IOException, IllegalArgumentException {
        
        // Try read test resource
        try {
            String stepId = UUID.randomUUID().toString();
            return fetchWorkItemsForTesting(resourcePath, stepId, stepName, delim);
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
    public static List<WorkItem> importTasksForTesting(String stepName, String resourcePath, String delim, String nestedDelim) throws IOException, IllegalArgumentException {

        // Try read test resource
        try {
            String stepId = UUID.randomUUID().toString();        
            return fetchNestedWorkItemsForTesting(resourcePath, stepName, stepId, delim, nestedDelim);
        }
        
        // Otherwise throw resource not found
        catch (Exception ex) {
            throw ex;
        }
    }
    
    
    /**
     * Fetch {@link WorkItem WorkItem} list from resource
     * 
     * @param resourcePath
     * @param stepId
     * @param stepName
     * @param delim
     * 
     * @return List-{@link WorkItem}
     * <br><br>
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static List<WorkItem> fetchWorkItemsForTesting(String resourcePath, String stepId, String stepName, String delim) throws IOException, IllegalArgumentException {
    
        // Fetch reader
        List<WorkItem> results = new ArrayList<>();
        String line;
        int lineNumber = 1;
        BufferedReader reader = provideResourceReader(resourcePath);
        while ((line = reader.readLine()) != null) {

            // Parse data
            String[] parts = line.split(delim);
            WorkItem data = parseWorkItemForTesting(parts, stepName);
            data.setStepId(stepId);

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
     * Handle parsing of data line to {@link WorkItem}
     * 
     * @param parts
     * @param stepName
     * @return {@link WorkItem WorkItem}
     * <br><br>
     * @throws IllegalArgumentException 
     */
    public static WorkItem parseWorkItemForTesting(String[] parts, String stepName) throws IllegalArgumentException {
    
        // Split by delimiter, expecting 3 fields
        if ( parts.length == 2) {
            ItemTask task = new ManagerTask(parts[0], parts[1]).asItemTask();
            Workload workload = BuilderUtility.buildWorkload(task);
            return BuilderUtility.buildWorkItem(parts[0], workload, stepName);
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
    public static WorkItem parseWorkItemForTesting(String[] parts, String stepName, String nestedDelim) throws IllegalArgumentException {
    
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
            return BuilderUtility.buildWorkItem(parts[0], workload, stepName);
        }
        
        // Handle single task
        else if ( parts[2].split(nestedDelim).length == 1 ) {
            String[] newParts = Arrays.copyOfRange(parts, 0, parts.length - 2);
            newParts = Arrays.copyOf(newParts, newParts.length + 1);
            newParts[newParts.length - 1] = parts[parts.length - 2] + " " + parts[parts.length - 1];
            return parseWorkItemForTesting(newParts, stepName);
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
     * Fetch nested list of work items
     * 
     * @param resourcePath
     * @param stepName
     * @param stepId
     * @param delim
     * @param nestedDelim
     * @return List-{@link WorkItem}
     * <br><br>
     * @throws IOException
     * @throws IllegalArgumentException 
     */
    public static List<WorkItem> fetchNestedWorkItemsForTesting(String resourcePath, String stepName, String stepId, String delim, String nestedDelim) throws IOException, IllegalArgumentException {
    
        // Fetch reader
        List<WorkItem> results = new ArrayList<>();
        String line;
        int lineNumber = 1;
        BufferedReader reader = provideResourceReader(resourcePath);
        while ((line = reader.readLine()) != null) {

            // Parse data
            String[] parts = line.split(delim);
            WorkItem data = parseWorkItemForTesting(parts, stepName, nestedDelim);
            data.setStepId(stepId);

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
}
