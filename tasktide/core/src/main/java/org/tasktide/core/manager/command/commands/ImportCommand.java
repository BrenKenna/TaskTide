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

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerTarget;


/**
 * {@link ManagerCommand} for importing tasks from JSON
 *  query string, or file
 *
 * @author Brendan Kenna
 */
public class ImportCommand extends AbstractCommand{
    
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
        
        return true;
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
     */
    public boolean importFile() {
        return true;
    }

    
    /**
     * Imports data from json string
     * 
     * @return boolean
     */
    public boolean importFromString() {
        return true;
    }
    
    
    /**
     * Get whether import from file (true), or
     *  or import from string (false) is configured.
     * 
     * @return boolean
     */
    public ImportType getImportType() {
        return this.importType;
    }
}
