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

import jakarta.json.bind.annotation.JsonbProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.CommandType;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.manager.file_handler.AnnotationCommandProcessor;

// For JavaDoc
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.manager.command.ManagerCommand;


/**
 * {@link ManagerCommand} for applying {@link CustomAnnotation}, or {@link JobEnvironment} onto
 *  {@link TaskTideModel}
 * 
 * @author Brendan Kenna
 */
public class AnnotateCommand extends AbstractCommand {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(AnnotateCommand.class);
    
    
    /**
     * Construct import command
     * 
     * @param action
     * @param target
     * @param cmdSpec
     * @param cmdType
     */
    public AnnotateCommand(
        @JsonbProperty("Manager Action") ManagerAction action,
        @JsonbProperty("Manager Target") ManagerTarget target,
        @JsonbProperty("Command Spec") CommandSpec cmdSpec,
        @JsonbProperty("Command Type") CommandType cmdType
    ) {
        super(action, target, cmdSpec, cmdType);
    }
    
    
    /**
     * Performs annotation command, throws IllegalArgumentException
     *  if not either Annotation, Annotate_Job {@link ManagerAction}
     * 
     * @return Object
     */
    @Override
    public Object runCommand() {
        
        
        // Run configured annotation
        switch ( action ) {
        
            case ANNOTATION -> {
                LOGGER.info("Applying configured annotation field to targeted collection");
                return AnnotationCommandProcessor.setCustomAnnotation(this, LOGGER);
            }
            
            case ANNOTATE_JOB -> {
                LOGGER.info("Applying JobEnvironmentId to targeted collection");
                return AnnotationCommandProcessor.setJobEnvironment(this, LOGGER);
            }
            
            default -> {
                throw new IllegalArgumentException("Error, annotation action must be one of:\tAnnotation, Annotate_Job");
            }
        }
    }

    
    /**
     * Validates whether annotation arguments are valid
     * 
     * @return boolean
     */
    @Override
    public boolean validateCommand() {
        if ( this.cmdSpec.getQueryString().isPresent() ) {
            if ( this.cmdSpec.getFilePath().isEmpty() ) {
                return false;
            }
            if ( this.cmdSpec.getOptions().isEmpty() ) {
                return false;
            }
            return true;
        }
        return false;
    } 
}
