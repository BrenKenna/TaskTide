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
package org.tasktide.tasktide.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentMap;


/**
 * Class to parse command-line arguments into {@link ArgumentTree}
 * 
 * @author bkenna
 */
public class CliParser {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(CliParser.class);
    private final ArgumentTree argTree;
    private final String[] argsIn;
    private final Map<String, Argument<?>> parsedArgs;
    private boolean hasHelp;
    
    
    /**
     * Construct with {@link ArgumentTree} and argument array
     * 
     * @param argTree
     * @param argsIn 
     */
    public CliParser(ArgumentTree argTree, String[] argsIn) {
        this.argTree = argTree;
        this.argsIn = argsIn;
        this.parsedArgs = new HashMap<>();
    }
    
    
    /**
     * Scan for help flag in argument tree
     * 
     * @return 
     */
    private boolean scanForHelp() {
        for ( String elm : this.argsIn ) {
            if ( elm.equalsIgnoreCase("-h") || elm.equalsIgnoreCase("--help") ) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Set whether help flag was defined in argument tree
     */
    public void setHasHelp() {
        this.hasHelp = this.scanForHelp();
    }
    
    public boolean hasHelp() {
        return this.hasHelp;
    }
    
    /**
     * Parse command-line arguments into {@link ArgumentTree}, and resulting map
     * 
     * @return Map-String, {@link Argument}
     */
    public Map<String, Argument<?>> parse() {
        parsedArgs.clear();
        
        // Check if help was reqeusted
        this.setHasHelp();
        
        // Resolve command paths
        List<String> pathTokens = argTree.resolveActionPath(argsIn);
        String actionPath = String.join(" ", pathTokens);
        
        // Parse global arguments
        ArgumentMap globalArgs = argTree.getGlobalArguments();
        if ( globalArgs != null ) {
            LOGGER.debug("Parsing global arguments:\tN = '{}'", globalArgs.getArgMap().size());
            this.parseFromMap(globalArgs);
        }
        
        // Parse command specific arguments
        LOGGER.debug("Parsing action arguments for:\t'{}'", actionPath);
        ArgumentMap actionArgs = argTree.getActionArguments(actionPath);
        if ( actionArgs != null ) {
            this.parseFromMap(actionArgs);
        }
        
        // Return parsed arguments
        return parsedArgs;
    }
    
    
    /**
     * Parse command-line argument values from {@link ArgumentMap}
     * 
     * @param map 
     */
    private void parseFromMap(ArgumentMap map) {
        for ( Argument<?> arg : map.getArgMap().values() ) {
            LOGGER.debug("Parsing argument data:\n'{}'", arg);
            int index = this.indexOf(arg);
            String raw = this.fetchRawValue(index);
            LOGGER.debug("Fetched raw value of '{}'", raw);
            if ( raw != null ) {
                arg.parseValue(raw);
                this.parsedArgs.put(arg.getName(), arg);
            }
        }
    }
    
    
    /**
     * Fetch index of argument in command-line argument array
     * 
     * @param arg
     * @return int
     */
    private int indexOf(Argument<?> arg) {
        for (int i = 0; i < this.argsIn.length; i++) {
            if (this.matchToArg(this.argsIn[i], arg)) return i;
        }
        return -1;
    }
    
    
    /**
     * Match query to the long/short argument flags
     * 
     * @param query
     * @param arg
     * 
     * @return boolean
     */
    private boolean matchToArg(String query, Argument<?> arg) {
        return query.equals(arg.getShortFlag()) || query.equals(arg.getLongFlag());
    }
    
    
    /**
     * Fetch raw string value for argument from the argument array
     * 
     * @param index
     * @return String
     */
    private String fetchRawValue(int index) {
        
        // Handle absent
        if ( index < 0 ) {
            return null;
        }
        
        // Support --key=value
        if (this.argsIn[index].contains("=")) {
            LOGGER.debug("Parsing on an eguals");
            return this.argsIn[index].split("=", 2)[1];
        }

        // Support --key value
        if (index + 1 < this.argsIn.length && !this.argsIn[index + 1].startsWith("-")) {
            LOGGER.debug("Parsing on next recod is data");
            return this.argsIn[index + 1];
        }

        // Support --keyOfInterest --nextKey
        // Treat as boolean flag
        LOGGER.debug("Parsed as boolean flag");
        return "true";
    }
}
