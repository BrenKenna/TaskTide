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
package org.tasktide.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tasktide.parser.model.Argument;
import org.tasktide.parser.model.ArgumentMap;


/**
 * Deprecated in favor of {@link ArgumentTree}
 * 
 * @author bkenna
 */
public class Arguments {
    
    // Attributes
    private final ArgumentMap globalArgs;
    private final Map<String, ArgumentMap> actionArgs;
    
    
    /**
     * Initialize arguments
     * 
     */
    public Arguments() {
        this.globalArgs = new ArgumentMap();
        this.actionArgs = new HashMap<>();
    }
    
    
    /**
     * Get global arguments
     * 
     * @return {@link ArgumentMap}
     */
    public ArgumentMap getGlobalArguments() {
        return this.globalArgs;
    }
    
    
    /**
     * Get global {@link Argument}
     * 
     * @param arg
     * @return {@link Argument}
     */
    public Argument<?> getGlobalArgument(String arg) {
        return this.globalArgs.getArgument(arg);
    }
    
    
    /**
     * Add a global argument
     * 
     * @param arg 
     */
    public void putGlobalArgument(Argument<?> arg) {
        this.globalArgs.putArgument(arg);
    }
    
    
    /**
     * Get the action map
     * 
     * @return Map-String, {@link ArgumentMap}
     */
    public Map<String, ArgumentMap> getActionMap() {
        return this.actionArgs;
    }
    
    
    /**
     * Get the {@link ArgumentMap} for required action
     * 
     * @param action
     * @return {@link ArgumentMap}
     */
    public ArgumentMap getActionArguments(String action) {
        return this.actionArgs.get(action);
    } 
    
    
    /**
     * Get the {@link ArgumentMap} for required action
     * 
     * @param action
     * @param arg
     * @return {@link Argument}
     */
    public Argument<?> getActionArgument(String action, String arg) {
        return this.actionArgs.get(action).getArgument(arg);
    } 
    
    
    /**
     * Add an {@link Argument} to action map
     * 
     * @param action
     * @param arg 
     */
    public void putActionArgument(String action, Argument<?> arg) {
        ArgumentMap argMap;
        if ( !this.actionArgs.containsKey(action) ) {
            argMap = new ArgumentMap();
        }
        else {
            argMap = this.actionArgs.get(action);
        }
        argMap.putArgument(arg);
        this.actionArgs.put(action, argMap);
    }
    
    
    /**
     * Get value for global argument
     * 
     * @param arg
     * @return Object
     */
    public Object getGlobalValue(String arg) {
        Argument<?> query = this.globalArgs.getArgument(arg);
        if ( query != null ) {
            return query.getValue();
        }
        return null;
    }
    
    
    /**
     * Get value for specified action argument
     * 
     * @param action
     * @param arg
     * @return Object
     */
    public Object getActionValue(String action, String arg) {
        Argument<?> query = this.getActionArgument(action, arg);
        if ( query != null) {
              return query.getValue();
         }
         return null;
    }
    
    
    /**
     * Fetch help across all arguents
     * 
     * @return List-String
     */
    public List<String> getHelp() {
        List<String> results = new ArrayList<>();
        results.addAll(this.getGlobalHelp());
        results.addAll(this.getActionHelp());
        return results;
    }
    
    
    /**
     * Fetch help from global arguments
     * 
     * @return List-String
     */
    private List<String> getGlobalHelp() {
        List<String> results = new ArrayList<>();
        for (String argId : this.globalArgs.getArgMap().keySet()) {
            Argument<?> arg = this.getGlobalArgument(argId);
            String helpLine = String.format(
                    "%s\t%s/%s\t%s",
                    arg.getName(), arg.getShortFlag(),
                    arg.getLongFlag(), arg.getDescription()
            );
            results.add(helpLine);
        }
        return results;
    }

    
    /**
     * Fetch help for an action
     * 
     * @param action
     * @return List-String
     */
    private List<String> getActionHelp(String action) {
        List<String> results = new ArrayList<>();
        ArgumentMap argMap = this.actionArgs.get(action);

        for (String argId : argMap.getArgMap().keySet()) {
            Argument<?> arg = this.getActionArgument(action, argId);
            String helpLine = String.format(
                    "%s\t%s/%s\t%s",
                    arg.getName(), arg.getShortFlag(),
                    arg.getLongFlag(), arg.getDescription()
            );
            results.add(helpLine);
        }
        return results;
    }

    
    /**
     * Fetch help across actions
     * 
     * @return List-String
     */
    private List<String> getActionHelp() {
        List<String> results = new ArrayList<>();
        for (String i : this.actionArgs.keySet()) {
            results.addAll(this.getActionHelp(i));
        }
        return results;
    }
}
