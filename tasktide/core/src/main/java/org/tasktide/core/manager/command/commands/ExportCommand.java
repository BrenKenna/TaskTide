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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.supporting.JsonUtils;
import org.tasktide.core.supporting.FileIO;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerTarget;


/**
 * Class to carry logic for exporting to json
 * 
 * @author Brendan Kenna
 */
public class ExportCommand extends AbstractCommand {
    
    private final Logger LOGGER = LogManager.getLogger(ExportCommand.class);
    
    
    /**
     * Construct export command
     * 
     * @param action
     * @param target
     * @param cmdSpec 
     */
    public ExportCommand(ManagerAction action, ManagerTarget target, CommandSpec cmdSpec) {
        super(action, target, cmdSpec);
    }

    
    /**
     * Export dataset to target file
     * 
     * @return boolean
     */
    public boolean exportToJson() {
        String targetFile = (String) this.cmdSpec.getFilePath().get();
        List<TaskTideModel> data = this.target.fetchModels();
        LOGGER.info("Retrieved '{}' records for export to:\t'{}'", data.size(), targetFile);
        return FileIO.exportJson(true, data, targetFile);
    }
    
    
    /**
     * Export to json file based on query string
     * <br>
     * '{ "Parameter": "ALL" | "STATE", "Value": "TODO" | "LOCKED" }'
     * 
     * @return boolean
     */
    public boolean exportOnQuery() {
        
        // Fetch parameters
        String targetFile = (String) this.cmdSpec.getFilePath().get();
        String queryString = (String) this.cmdSpec.getQueryString().get();
        JsonObject json = JsonUtils.stringToJson(queryString);
        
        // Fetch subject
        String subject;
        if ( this.cmdSpec.getOptionsKey("Target").isPresent() ) {
            subject = (String) this.cmdSpec.getOptionsKey("Target").get();
        }
        else {
            subject = (String) this.cmdSpec.getOptionsKey("Step Name").get();
        }
        
        // Determine type
        String param = json.getString("Parameter");
        ExportType type = ExportType.get(param);
        
        // Handle query
        List<WorkItem> data = new ArrayList<>();
        switch ( type ) {
        
            case ALL -> {
                data = TaskTideServiceManager.fetchWorkItemService().viewByField("StepName", subject);
            }
            
            case STATE -> {
                String val = json.getString("Value");
                ItemState state = ItemState.get(val);
                if ( state != null ) {
                    data = TaskTideServiceManager.fetchWorkItemService().viewByFieldForGroup("StepName", subject, "ItemState", state);
                }
            }
            
            default -> {
                String msg = String.format("Error Parameter must be one of:\t'%s'", ExportType.valuesString());
                LOGGER.error(msg);
                throw new IllegalArgumentException(msg);
            }
        }
        
        // Dump to file
        LOGGER.info("Retrieved '{}' records for export to:\t'{}'", data.size(), targetFile);
        return FileIO.exportJson(true, data, targetFile);
    }
    
    
    /**
     * Handles running the export of data collection to JSON format
     * 
     * @return boolean
     */
    @Override
    public Object runCommand() {
        switch ( this.action ) {
        
            case EXPORT -> {
                LOGGER.info("Exporting full target to json file");
                return this.exportToJson();
            }
            
            case EXPORT_QUERY -> {
                LOGGER.info("Exporting to json file based on json query string");
                return this.exportOnQuery();
            }
            
            default -> {
                ManagerAction[] actions = { ManagerAction.EXPORT, ManagerAction.EXPORT_QUERY };
                String msg = String.format("Reset command must be one of:\t'%s'", (Object[]) actions);
                throw new IllegalArgumentException(msg);
            }
        }
    }

    
    /**
     * Validates required options are defined in
     *  {@link CommandSpec}
     * 
     * @return boolean
     */
    @Override
    public boolean validateCommand() {
        switch ( this.action ) {
        
            case EXPORT -> {
                if ( this.cmdSpec.getFilePath().isEmpty() ) {
                    LOGGER.error("Error, an output file must be provided");
                    return false;
                }
                if ( !this.cmdSpec.hasOptionsKey("Target") && !this.cmdSpec.hasOptionsKey("Step Name")) {
                    LOGGER.error("Error, a subject must be provided either as a Target or Step Name");
                    return false;
                }
                return true;
            }
            
            case EXPORT_QUERY -> {
                if ( this.cmdSpec.getFilePath().isEmpty() || this.cmdSpec.getQueryString().isEmpty() ) {
                    LOGGER.error("Error, an output file and query string must be provided");
                    return false;
                }
                
                if ( !this.cmdSpec.hasOptionsKey("Target") && !this.cmdSpec.hasOptionsKey("Step Name")) {
                    LOGGER.error("Error, a subject must be provided either as a Target or Step Name");
                    return false;
                }
                
                if ( !this.cmdSpec.hasOptionsKey("Parameter") ) {
                    LOGGER.error("Export on query must have paramter");
                    return false;
                }
                
                return true;
            }
            
            default -> {
                ManagerAction[] actions = { ManagerAction.RESET_ITEM, ManagerAction.RESET_ITEMS };
                String msg = String.format("Reset command must be one of:\t'%s'", (Object[]) actions);
                throw new IllegalArgumentException(msg);
            }
        }
    }
}