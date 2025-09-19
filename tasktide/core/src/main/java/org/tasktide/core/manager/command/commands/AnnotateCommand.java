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
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasktide.core.manager.BuilderUtility;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.supporting.JsonUtils;



/**
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
     */
    public AnnotateCommand(
        @JsonbProperty("Manager Action") ManagerAction action,
        @JsonbProperty("Manager Target") ManagerTarget target,
        @JsonbProperty("Command Spec") CommandSpec cmdSpec
    ) {
        super(action, target, cmdSpec);
    }
    
    
    /**
     * Performs annotation command
     * 
     * @return Object
     */
    @Override
    public Object runCommand() {
        
        // Fetch args
        String file = this.cmdSpec.getFilePath().get();
        String delimiter = (String) this.cmdSpec.getOptions().get().get("Delimiter");
        CustomAnnotation anno = this.fetchAnnoFromCmdSpec();
        
        return null;
    }
    
    
    /**
     * Fetches {@link CustomAnnotation} from {@link CommandSpec} 
     * 
     * @return {@link CustomAnnotation}
     */
    public CustomAnnotation fetchAnnoFromCmdSpec() {
        String query = this.cmdSpec.getQueryString().get();
        Map<String, Object> map = JsonUtils.mapFromJson(query);
        return BuilderUtility.makeAnnotation(map);
    }
    
    
    @Override
    public boolean validateCommand() {
        
        if ( this.cmdSpec.getQueryString().isPresent() ) {
            if ( this.cmdSpec.getFilePath().isEmpty() ) {
                return false;
            }
            if ( this.cmdSpec.getOptions().isEmpty() ) {
                return false;
            }
        }
        
        
        return false;
    } 
}
