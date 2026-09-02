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
import java.util.Map;

import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.manager.TaskTideManagerUtility;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.manager.command.CommandSpec;
import static org.tasktide.core.manager.command.ManagerTarget.STEP;
import org.tasktide.core.manager.command.commands.AbstractCommand;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.supporting.FileIO;
import org.tasktide.core.supporting.JsonUtils;


/**
 * Parsing records for annotation with {@link CustomAnnotation}
 *  or Job Environment Id
 *
 * @author Brendan Kenna
 */
public class AnnotationCommandProcessor {
    
    
    /**
     * Apply {@link CustomAnnotation} to 
     * 
     * @param cmd
     * @param LOGGER
     * @param handler
     * @return int
     */
    public static int applyAnnotation(AbstractCommand cmd, Logger LOGGER, AnnotateCommandHandler handler) {
        
        // Fetch params
        String targetFile = cmd.getCmdSpec().getFilePath().get();
        String delim = (String) cmd.getCmdSpec().getOptionsKey("Delimiter").get();
        delim = TaskTideManagerUtility.handleDelim(delim);
        
        // Process line
        try {
            BufferedReader reader = FileIO.fetchBufferedReader(targetFile);
            
            String line;
            int lineNumber = 0;
            while ( (line = reader.readLine()) != null) {
                String[] parts = line.split(delim);
                if ( handler.parseRecord(parts, cmd, LOGGER) ) {
                    lineNumber++;
                }
            }
            
            return lineNumber;
        }
        catch (IOException ex) {
            LOGGER.error("Error during processing:\t'{}'", ex.getMessage());
            ex.printStackTrace();
            return -1;
        }
    }
    
    
    /**
     * Set {@link CustomAnnotation} on {@link WorkItem}/{@link ItemTask}
     * 
     * @param CMD
     * @param LOGGER
     * @return int
     */
    public static int setCustomAnnotation(AbstractCommand CMD, Logger LOGGER) {
        return applyAnnotation(CMD, LOGGER, (parts, cmd, logger) -> {
            
            // Apply annotation to WorkItem
            CustomAnnotation anno = fetchAnnoFromCmdSpec(cmd);
            boolean state = false;
            switch (parts.length) {
                case 1 -> {
                    
                    switch ( CMD.getTarget() ) {
                        case WORKITEM -> {
                            logger.info("Annotating WorkItem");
                            WorkItem item = TaskTideServiceManager
                                .fetchWorkItemService()
                                .fetchById(parts[0]);
                            if ( item != null ) {
                                item.setAnnotations(anno);
                                state = TaskTideServiceManager
                                    .fetchWorkItemService()
                                .updateModel(item) != null;
                            }
                            else {
                                logger.warn("No record found for WorkItem:\t'{}'", parts[0]);
                            }
                        }
                        
                        case STEP -> {
                            logger.info("Annotating Step");
                            Step item = TaskTideServiceManager
                                .fetchStepService()
                                .fetchById(parts[0]);
                            if ( item != null ) {
                                item.setAnnotations(anno);
                                state = TaskTideServiceManager
                                    .fetchStepService()
                                .updateModel(item) != null;
                            }
                            else {
                                logger.warn("No record found for Step:\t'{}'", parts[0]);
                            }
                        }
                        
                        case WORKFLOW -> {
                            logger.info("Annotating Workflow");
                            Workflow item = TaskTideServiceManager
                                .fetchWorkflowService()
                                .fetchById(parts[0]);
                            if ( item != null ) {
                                item.setAnnotations(anno);
                                state = TaskTideServiceManager
                                    .fetchWorkflowService()
                                .updateModel(item) != null;
                            }
                            else {
                                logger.warn("No record found for Workflow:\t'{}'", parts[0]);
                            }
                        }
                    }
                }
                case 2 -> {
                    logger.info("Annotating ItemTask");
                    WorkItem item = TaskTideServiceManager
                        .fetchWorkItemService()
                    .fetchById(parts[0]);
                    
                    if ( item != null ) {
                        ItemTask task = item.getWorkload().getById(parts[1]);
                        task.setAnnotations(anno);
                        state = TaskTideServiceManager
                            .fetchWorkItemService()
                        .updateModel(item) != null;
                    }
                    else {
                        logger.warn("No record found for ItemTask:\t'{}'", parts[0]);
                    }
                }
                default -> {
                    logger.error("Error, malformed line");
                    state = false;
                }
            }
            
            // Update record
            return state;
        });
    }
    
    
    /**
     * Annotates configured job environment Id on {@link WorkItem}/{@link ItemTask}
     * 
     * @param CMD
     * @param LOGGER
     * @return int
     */
    public static int setJobEnvironment(AbstractCommand CMD, Logger LOGGER) {
        return applyAnnotation(CMD, LOGGER, (parts, cmd, logger) -> {
            
            // Apply annotation to WorkItem
            String jobEnvId = fetchJobEnvIdFromCmdSpec(cmd);
            WorkItem item;
            switch (parts.length) {
                case 1 -> {
                    logger.info("Annotating WorkItem");
                    item = TaskTideServiceManager
                            .fetchWorkItemService()
                            .fetchById(parts[0]);
                    item.setJobEnvId(jobEnvId);
                }
                case 2 -> {
                    logger.info("Annotating ItemTask");
                    item = TaskTideServiceManager
                            .fetchWorkItemService()
                            .fetchById(parts[0]);
                    ItemTask task = item.getWorkload().getById(parts[1]);
                    task.setJobEnvId(jobEnvId);
                }
                default -> {
                    logger.error("Error, malformed line");
                    return false;
                }
            }
            
            // Update record
            return TaskTideServiceManager
                .fetchWorkItemService()
            .updateModel(item) != null;
        });
    }
    
    
    
    /**
     * Fetches {@link CustomAnnotation} from {@link CommandSpec} 
     * 
     * @param cmd
     * @return {@link CustomAnnotation}
     */
    public static CustomAnnotation fetchAnnoFromCmdSpec(AbstractCommand cmd) {
        String query = cmd.getCmdSpec().getQueryString().get();
        Map<String, Object> map = JsonUtils.mapFromJson(query);
        return BuilderUtility.makeAnnotation(map);
    }
    
    
    /**
     * Fetch JobEnvironmentId from {@link AbstractCommand}
     * 
     * @param cmd
     * @return String
     */
    public static String fetchJobEnvIdFromCmdSpec(AbstractCommand cmd) {
        String query = cmd.getCmdSpec().getQueryString().get();
        Map<String, Object> map = JsonUtils.mapFromJson(query);
        String key = (String) map.keySet().toArray()[0];
        return (String) map.values().iterator().next();
    }
}