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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentMap;
import org.tasktide.tasktide.parser.model.GenericTree;


/**
 * Implementation of {@link GenerTree} for {@link ArgumentMap}
 *
 * @author bkenna
 */
public class ArgumentTree {
    
    // Attributes
    private final GenericTree<ArgumentMap> argTree;

    
    /**
     * Construct with delimiter for tree
     * 
     * @param delim 
     */
    public ArgumentTree(String delim) {
        this.argTree = new GenericTree<>(delim);
    }
    
    
    /**
     * Return tree to expose those methods
     * 
     * @return {@link GenericTree} of {@link ArgumentMap}
     */
    public GenericTree<ArgumentMap> getTree() {
        return this.argTree;
    }
    
    
    /**
     * Get global arguments
     * 
     * @return {@link ArgumentMap}
     */
    public ArgumentMap getGlobalArguments() {
        return argTree.getRoot().getData();
    }
    
    
    /**
     * Fetch action arguments under the provided action path
     * 
     * @param actionPath
     * @return {@link ArgumentMap}
     */
    public ArgumentMap getActionArguments(String actionPath) {
        return argTree.getDataForAddress(actionPath);
    }
    
    
    /**
     * Resolve deepest command path
     * 
     * @param args
     * @return List-String/Command
     */
    public List<String> resolveActionPath(String[] args) {
        
        // Initialize required vars
        List<String> results = new ArrayList<>();
        StringBuilder path = new StringBuilder();
        
        // Scan through arguments
        for ( String argsInElement : args ) {
            
            // Handling address delimiter
            if ( !results.isEmpty() ) path.append(" ");
            path.append(argsInElement); // Add element to path
            
            // Adding if the current argsIn[] elem
            if ( argTree.findByAddress( path.toString()) != null ) {
                results.add(argsInElement);
            }
            else {
                break;
            }
        }
        
        // Return results
        return results;
    }
    
    
    /**
     * Display verbose help message global, and all action action arguments
     * 
     * @return List-String
     */
    public List<String> getVerboseHelp() {
        
        // Initialize output
        List<String> results = new ArrayList<>();

        // Global help section
        results.addAll(getGlobalHelp());
        results.add("");

        // Action help sections
        results.addAll(getActionHelp());

        // Return help data
        return results;
    }
    
    
    /**
     * Fetch the help lines for the global arguments
     * 
     * @return 
     */
    public List<String> getGlobalHelp() {
        
        // Initialize output
        List<String> results = new ArrayList<>();
        results.add("");
        results.add("Global Options:");
        results.add("");
        
        // Fetch and format global arguments
        ArgumentMap map = this.getGlobalArguments();
        if ( map != null ) {
            for( Argument<?> arg : map.getArgMap().values() ) {
                results.add(this.formatHelpLine(arg));
            }
        }
        
        // Return results
        return results;
    }
    
    
    /**
     * Get the help page for specific action
     * 
     * @param path
     * @return List-String
     */
    public List<String> getActionHelp(String path) {
        List<String> results = new ArrayList<>();
        results.add("");
        results.add("\tOptions for Action:\t" + path);
        results.add("");
        ArgumentMap map = this.getActionArguments(path);
        if (map != null) {
            for (Argument<?> arg : map.getArgMap().values()) {
                results.add(formatHelpLine(arg));
            }
        }
        return results;
    }
    
    
    public List<String> getActionHelp() {
        
        // Initialize output
        List<String> results = new ArrayList<>();
        results.add("");
        results.add("\tAction Arguments:");
        results.add("");
        
        // Traverse tree, collating help messages
        Map<String, ArgumentMap> addressMap = this.argTree.toAddressDataMap();
        for (Map.Entry<String, ArgumentMap> elm : addressMap.entrySet()) {
            
            // Distinguish key-value
            String address = elm.getKey();
            ArgumentMap map = elm.getValue();

            // Pass on empty data
            if (map == null || map.getArgMap().isEmpty()) continue;
            if (address.isEmpty() ) continue;
            
            // Otherwise add active path
            results.addAll( this.getActionHelp(address));
            results.add("\n\n");
        }
        
        // Return results
        return results;
    }
    
    
    /**
     * Format the help line
     * 
     * @param arg
     * @return String
     */
    private String formatHelpLine(Argument<?> arg) {
        return String.format(
            "\t %-10s\t%-10s/%-10s\t%s",
            arg.getName(),
            arg.getShortFlag() != null ? arg.getShortFlag() : "",
            arg.getLongFlag() != null ? arg.getLongFlag() : "",
            arg.getDescription()
        );
    }
}