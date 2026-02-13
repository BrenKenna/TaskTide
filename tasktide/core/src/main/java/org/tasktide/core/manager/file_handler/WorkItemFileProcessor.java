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

import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.TaskTideManagerUtility;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.task.ItemTask;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;

import org.tasktide.core.supporting.FileIO;

// For JavaDocs
import org.tasktide.core.manager.command.ManagerCommand;


/**
 * Processes provided collection of data using a given {@link WorkItemLineHandler}.
 *  Purpose of this class was to reduce repetitive boilerplate, and simplify the
 *  design and testabilty of {@link ManagerCommand} for dropping/resetting
 *  tasks, because the target files are more or less structured the same.
 *  Hence using a Strategic (this class) Template (functional interface {@link WorkItemLineHandler})
 *  pattern to do this.
 * 
 * @author Brendan Kenna
 */
public class WorkItemFileProcessor {
    
    
    /**
     * Standardized processing of {@link WorkItem} record lines 
     * 
     * @param targetFile
     * @param delimiter
     * @param handler
     * @param LOGGER
     * 
     * @return int
     */
    public static int processFile(String targetFile, String delimiter, WorkItemLineHandler handler, Logger LOGGER) {
    
        // Acknowledge delimiter
        delimiter = TaskTideManagerUtility.handleDelim(delimiter);
        LOGGER.info("Processing items with delimiter '{}' in file '{}'", delimiter, targetFile);
        
        // Try read provided resource
        try ( BufferedReader reader = FileIO.fetchBufferedReader(targetFile) ) {
            
            // Intialize required variables
            TaskTideService<WorkItem> serv = TaskTideServiceManager.fetchWorkItemService();
            int counter = 0;
            String line;
            
            // Processing loop
            while ( (line = reader.readLine()) != null ) {
                String parts[] =
                    delimiter.isEmpty()
                    ? new String[] { line.strip() }
                    : line.split(delimiter)
                ;
                handler.handleLine(parts, serv, LOGGER);
                counter++;
            }
            
            // Return counter
            return counter;
        }
        
        // Log error
        catch ( IOException ex ) {
            LOGGER.error("Unable to process target file:\t'{}'", targetFile, ex);
            return -1;
        }
    }
    
    
    /**
     * Reset all or targeted {@link ItemTask} in {@link Workload}
     *  of {@link WorkItem}
     * 
     * @param file
     * @param delimiter
     * @param logger
     * 
     * @return int
     */
    public static int resetItems(String file, String delimiter, Logger logger) {
        return processFile(file, delimiter, (parts, serv, log) -> {
            
            if ( parts.length < 1 || parts.length > 2 ) {
                logger.error("Error passing on malformed line:\t'{}'", (Object[]) parts);
                return;
            }
            
            WorkItem item = serv.fetchById(parts[0].strip());
            if ( item == null ) {
                logger.error("Error passing on line with no record matching:\t'{}'", parts[0]);
                return;
            }
            
            switch (parts.length) {
                case 1 -> {
                    String i = parts[0].strip();
                    item.resetModel();
                    serv.updateModel(item);
                }
                    
                case 2 -> {
                    String workItemId = parts[0].strip();
                    String itemTaskId = parts[1].strip();
                    if (itemTaskId != null && !itemTaskId.isBlank()) {
                        log.info("Resetting task '{}' in '{}'", itemTaskId, workItemId);
                        item.resetTask(itemTaskId);
                    } else {
                        log.info("Resetting all tasks in '{}'", workItemId);
                        item.resetModel();
                    }   serv.updateModel(item);
                }

                default -> {
                    String val = Arrays.toString(parts);
                    log.warn("Skipping malformed line: '{}'", val);
                }
            }
        }, logger);
    }
    
    
    /**
     * Delete all or targeted {@link ItemTask} in {@link Workload}
     *  of {@link WorkItem}
     * 
     * @param file
     * @param delimiter
     * @param logger
     * 
     * @return int
     */
    public static int deleteTasks(String file, String delimiter, Logger logger) {
        return processFile(file, delimiter, (parts, serv, log) -> {
            
            if ( parts.length < 1 || parts.length > 2 ) {
                return;
            }
            
            WorkItem item = serv.fetchById(parts[0].strip());
            if ( item == null ) {
                return;
            }
            
            if (parts.length == 1) {
                item = serv.fetchById(parts[0].strip());
                item.getWorkload().setTaskMap(new HashMap<>());
                item.setTaskCounts();
                serv.updateModel(item);
            } else if (parts.length == 2) {
                String workItemId = parts[0].strip();
                String itemTaskId = parts[1].strip();
                item = serv.fetchById(workItemId);

                if (itemTaskId != null && !itemTaskId.isBlank()) {
                    log.info("Resetting task '{}' in '{}'", itemTaskId, workItemId);
                    item.getWorkload().dropTaskById(itemTaskId);
                } else {
                    log.info("Resetting all tasks in '{}'", workItemId);
                    item.getWorkload().setTaskMap(new HashMap<>());
                }
                item.setTaskCounts();
                serv.updateModel(item);
            } else {
                log.warn("Skipping malformed line: '{}'", Arrays.toString(parts));
            }
        }, logger);
    }
}