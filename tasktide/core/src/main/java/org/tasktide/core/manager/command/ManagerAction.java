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
package org.tasktide.core.manager.command;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.tasktide.core.manager.command.commands.AbstractCommand;
import org.tasktide.core.manager.command.commands.AnnotateCommand;

import org.tasktide.core.manager.command.commands.DeleteCommand;
import org.tasktide.core.manager.command.commands.ExportCommand;
import org.tasktide.core.manager.command.commands.ImportCommand;
import org.tasktide.core.manager.command.commands.ResetCommand;
import org.tasktide.core.manager.command.commands.SummarizeCommand;


/**
 * Enum to support valid actions covered by the Manager
 * 
 * @author bkenna
 */
public enum ManagerAction {

    IMPORT {
        @Override
        public CommandType getCommandType() {
            return CommandType.BATCH_CREATE;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new ImportCommand(ManagerAction.IMPORT, target, cmdSpec, CommandType.BATCH_CREATE);
            return cmd;
        }
    },

    EXPORT {
        @Override
        public CommandType getCommandType() {
            return CommandType.EXPORT;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new ExportCommand(ManagerAction.EXPORT, target, cmdSpec, CommandType.EXPORT);
            return cmd;
        }
    },
    
    EXPORT_QUERY {
        @Override
        public CommandType getCommandType() {
            return CommandType.EXPORT;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new ExportCommand(ManagerAction.EXPORT_QUERY, target, cmdSpec, CommandType.EXPORT);
            return cmd;
        }
    },
    
    ADD {
        @Override
        public CommandType getCommandType() {
            return CommandType.CREATE;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
    
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new ImportCommand(ManagerAction.ADD, target, cmdSpec, CommandType.CREATE);
            return cmd;
        }
    },
    
    APPEND {
        @Override
        public CommandType getCommandType() {
            return CommandType.UPDATE;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new ImportCommand(ManagerAction.APPEND, target, cmdSpec, CommandType.UPDATE);
            return cmd;
        }
    },
    
    RESET_ITEM {
        @Override
        public CommandType getCommandType() {
            return CommandType.UPDATE;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            query = query.trim().replace(" ", "").replace("-", "").replace("_", "").toLowerCase();
            return "resetitem".equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new ResetCommand(ManagerAction.RESET_ITEM, target, cmdSpec, CommandType.UPDATE);
            return cmd;
        }
    },
    
    RESET_ITEMS {
        @Override
        public CommandType getCommandType() {
            return CommandType.BATCH_UPDATE;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            query = query.trim().replace(" ", "").replace("-", "").replace("_", "").toLowerCase();
            return "resetitems".equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new ResetCommand(ManagerAction.RESET_ITEMS, target, cmdSpec, CommandType.BATCH_UPDATE);
            return cmd;
        }
    },
    
    SUMMARIZE_EACH {
        @Override
        public CommandType getCommandType() {
            return CommandType.SELECT;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new SummarizeCommand(ManagerAction.SUMMARIZE_EACH, target, cmdSpec, CommandType.SELECT);
            return cmd;
        }
    },
    
    SUMMARIZE {
        @Override
        public CommandType getCommandType() {
            return CommandType.SELECT;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new SummarizeCommand(ManagerAction.SUMMARIZE, target, cmdSpec, CommandType.SELECT);
            return cmd;
        }
    },
    
    DELETE {
        @Override
        public CommandType getCommandType() {
            return CommandType.DELETE;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new DeleteCommand(ManagerAction.DELETE, target, cmdSpec, CommandType.DELETE);
            return cmd;
        }
    },
    
    DELETE_LIST {
        @Override
        public CommandType getCommandType() {
            return CommandType.BATCH_DELETE;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new DeleteCommand(ManagerAction.DELETE_LIST, target, cmdSpec, CommandType.BATCH_DELETE);
            return cmd;
        }
    },

    DELETE_ALL {
        @Override
        public CommandType getCommandType() {
            return CommandType.BATCH_DELETE;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new DeleteCommand(ManagerAction.DELETE_ALL, target, cmdSpec, CommandType.BATCH_DELETE);
            return cmd;
        }
    },
   
    ANNOTATE_JOB {
        @Override
        public CommandType getCommandType() {
            return CommandType.BATCH_UPDATE;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new AnnotateCommand(ManagerAction.ANNOTATE_JOB, target, cmdSpec, CommandType.BATCH_UPDATE);
            return cmd;
        }
    },
    
    ANNOTATION {
        @Override
        public CommandType getCommandType() {
            return CommandType.BATCH_UPDATE;
        }
        
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec) {
            AbstractCommand cmd = new AnnotateCommand(ManagerAction.ANNOTATION, target, cmdSpec, CommandType.BATCH_UPDATE);
            return cmd;
        }
    };
    
    
    /**
     * Abstract method for values to create their corresponding {@link AbstractCommand}
     * 
     * @param target
     * @param cmdSpec
     * @return {@link ManagerCommand}
     */
    public abstract ManagerCommand makeCommand(ManagerTarget target, CommandSpec cmdSpec);
    
    public abstract boolean isManagerAction(String query);
    public abstract boolean isManagerAction(ManagerAction query);
    public abstract CommandType getCommandType();


    /**
     * Fetch index of query
     * 
     * @param name
     * @return >0/-1
     */
    public static int indexOf(String name) {
        for (ManagerAction manageraction : values() ) {
            if (manageraction.isManagerAction(name)) {
                return manageraction.ordinal();
            }
        }
        return -1;
    }
    
    
    /**
     * Check if query is a valid action
     * 
     * @param query
     * @return 
     */
    public static boolean hasQuery(String query) {
        if (query == null) {
            return false;
        }
        for (ManagerAction manageraction : values()) {
            if (manageraction.isManagerAction(query)) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Get manager action matching query
     * 
     * @param query
     * @return 
     */
    public static ManagerAction get(String query) {
        int ind = indexOf(query);
        if (ind >= 0) {
            return values()[ind];
        }
        return null;
    }
    
    
    /**
     * Represent enum as string
     * 
     * @return String
     */
    public static String valuesString() {
        return Arrays.stream(values())
            .map(elm -> elm.name())
            .collect(Collectors.joining(","));
    }
}