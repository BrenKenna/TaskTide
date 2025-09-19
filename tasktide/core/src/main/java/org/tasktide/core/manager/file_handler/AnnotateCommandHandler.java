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

import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.commands.AbstractCommand;
import org.tasktide.core.model.CustomAnnotation;


/**
 *
 * @author Brendan Kenna
 */
@FunctionalInterface
public interface AnnotateCommandHandler {
    
    
    /**
     * Parsing files from {@link ManagerCommand} requiring
     *  iterations over them
     * 
     * @param parts
     * @param cmd
     * @param LOGGER 
     * @return {@link CustomAnnotation}
     */
    public CustomAnnotation parseRecord(String[] parts, AbstractCommand cmd, Logger LOGGER);
}
