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

import java.util.List;

import org.tasktide.core.TaskTideModel;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerTarget;

import org.tasktide.core.supporting.FileIO;


/**
 * Class to carry logic for exporting to json
 * 
 * @author Brendan Kenna
 */
public class ExportCommand extends AbstractCommand {
    
    
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
     * Handles running the export of data collection to JSON format
     * 
     * @return boolean
     */
    @Override
    public boolean runCommand() {
        return this.exportToJson();
    }

    
    /**
     * Validates required options are defined in
     *  {@link CommandSpec}
     * 
     * @return boolean
     */
    @Override
    public boolean validateCommand() {
        if ( !this.cmdSpec.hasOptionsKey("Target File") ) {
            return false;
        }
        
        if ( !this.cmdSpec.hasOptionsKey("Target") && !this.cmdSpec.hasOptionsKey("Step Name")) {
            return false;
        }
        
        return true;
    }
    
    
    /**
     * Export dataset to target file
     * 
     * @return boolean
     */
    public boolean exportToJson() {
        String targetFile = (String) this.cmdSpec.getOptionsKey("Target File").get();
        List<TaskTideModel> data = this.target.fetchModels();
        return FileIO.exportJson(true, data, targetFile);
    }
}