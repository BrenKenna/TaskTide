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
package org.tasktide.core.manager.file_handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.manager.ManagerTask;
import org.tasktide.core.manager.TaskTideManagerUtility;

import org.tasktide.core.manager.command.commands.AbstractCommand;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;
import org.tasktide.core.model.workitem.ItemType;
import org.tasktide.core.supporting.FileIO;


/**
 * Parsing {@link WorkItem} collection from target file
 * 
 * @author Brendan Kenna
 */
public class ImportCommandRecordProcessor {
    
    
    /**
     * Process lines
     * 
     * @param cmd
     * @param handler
     * @param LOGGER
     * 
     * @return List-{@link WorkItem}
     */
    private static List<WorkItem> recordProcessor(AbstractCommand cmd, Logger LOGGER, ImportCommandRecordHandler handler) {
    
        // Fetch args
        String stepName = (String) cmd.getCmdSpec().getOptionsKey("Step Name").get();
        String targetFile = cmd.getCmdSpec().getFilePath().get();
        String delim = (String) cmd.getCmdSpec().getOptionsKey("Delimiter").get();
        delim = TaskTideManagerUtility.handleDelim(delim);
        
        // Fetch reader
        List<WorkItem> results = new ArrayList<>();
        String line;
        int lineNumber = 1;
        
        try {
            BufferedReader reader = FileIO.fetchBufferedReader(targetFile);
            while ((line = reader.readLine()) != null) {

                // Parse data
                String[] parts = line.split(delim);
                WorkItem data = handler.parseRecord(parts, stepName, cmd, LOGGER);

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
        // Log error
        catch ( IOException ex ) {
            LOGGER.error("Unable to process target file:\t'{}'", targetFile, ex);
            return null;
        }
    }
    
    
    /**
     * Parse {@link ItemType}.Single from active line
     * 
     * @param abstractCMD
     * @param LOGGER
     * @return List-{@link WorkItem}
     */
    public static List<WorkItem> parseSingleTaskWorkItem(AbstractCommand abstractCMD, Logger LOGGER) {
        return recordProcessor(abstractCMD, LOGGER, (parts, stepName, cmd, logger) -> {
        
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
        });
    }
    
    
    /**
     * Parse nested {@link WorkItem} from line
     * 
     * @param abstractCMD
     * @param LOGGER
     * @return List-{@link WorkItem}
     */
    public static List<WorkItem> parseNestedTaskWorkItem(AbstractCommand abstractCMD, Logger LOGGER) {
        return recordProcessor(abstractCMD, LOGGER, (parts, stepName, cmd, logger) -> {
        
            // Fetch nested delimiter
            String nestedDelim = (String) cmd.getCmdSpec().getOptionsKey("Nested Delimiter").get();
            TaskTideManagerUtility.handleDelim(nestedDelim);
            
            // Handle as nested task
            String stepId = TaskTideManagerUtility.fetchStepId(stepName);
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
                return BuilderUtility.buildWorkItem(parts[0], workload, stepName, stepId);
            }

            // Handle single task
            else if ( parts[2].split(nestedDelim).length == 1 ) {
                String[] newParts = Arrays.copyOfRange(parts, 0, parts.length - 2);
                newParts = Arrays.copyOf(newParts, newParts.length + 1);
                newParts[newParts.length - 1] = parts[parts.length - 2] + " " + parts[parts.length - 1];
                return parseWorkItem(newParts, stepId);
            }

            // Otherwise raise exception
            throw new IllegalArgumentException(
                "Invalid format: Expected 3 fields but got " + parts.length
            );
        });
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
    private static WorkItem parseWorkItem(String[] parts, String stepName) throws IllegalArgumentException {
    
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
}