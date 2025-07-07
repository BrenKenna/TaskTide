/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.parser.model;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author bkenna
 */
public class ArgumentMap {
    
    // Attributes
    private final Map<String, Argument<?>> args;
    
    
    /**
     * Initialize argument map
     * 
     */
    public ArgumentMap() {
        this.args = new HashMap<>();
    }
    
    
    /**
     * Get the argument map
     * 
     * @return Map-String, {@link Argument}
     */
    public Map<String, Argument<?>> getArgMap() {
        return this.args;
    }
    
    
    /**
     * Add the argument to argument map
     * 
     * @param arg
     * @return boolean
     */
    public boolean putArgument(Argument<?> arg) {
        return this.args.put(arg.getName(), arg) != null;
    }
    
    
    /**
     * Get the argument with key of queried string
     * 
     * @param arg
     * @return {@link Argument}
     */
    public Argument<?> getArgument(String arg) {
        return this.args.get(arg);
    }
    
    public Map<String, Argument<?>> getArgumentMap() {
        return this.args;
    }

    @Override
    public String toString() {
        return "ArgumentMap{" +
            "args=" + args +
        '}';
    }
}
