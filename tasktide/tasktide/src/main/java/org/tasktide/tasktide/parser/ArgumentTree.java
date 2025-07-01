/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.parser;

import java.util.ArrayList;
import java.util.List;
import org.tasktide.tasktide.parser.model.ArgumentMap;
import org.tasktide.tasktide.parser.model.GenericTree;


/**
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
}
