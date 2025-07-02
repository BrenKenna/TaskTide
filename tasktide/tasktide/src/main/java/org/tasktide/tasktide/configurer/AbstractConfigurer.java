/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.configurer;

import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.CliParser;

import org.tasktide.tasktide.parser.model.ArgumentBuilder;
import org.tasktide.tasktide.parser.model.ArgumentMap;


/**
 * Abstract class deals with the broader parts of config, {@link ArgumentMap}, path etc
 * 
 * @author bkenna
 */
public abstract class AbstractConfigurer implements TaskTideConfigurer {
    
    // Attributes
    private final ArgumentMap argMap;
    private final String path;
    private final ArgumentBuilder argBuilder;
    
    
    /**
     * Construct engine config template
     * 
     * @param path
     */
    public AbstractConfigurer(String path) {
        this.argMap = new ArgumentMap();
        this.path = path;
        this.argBuilder = new ArgumentBuilder();
    }

    
    /**
     * Add the {@link TaskTideEngine} configuration to the supplied
     *  {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void addToTree(ArgumentTree argTree) {
        argTree.getTree().addChild(path, argMap);
    }
    
    
    /**
     * Return path to the engine arguments
     * 
     * @return String
     */
    @Override
    public String getPath() {
        return this.path;
    }
    
    
    /**
     * Return the {@link TaskTideEngine} argument map
     * 
     * @return {@link ArgumentMap}
     */
    @Override
    public ArgumentMap getArgumentMap() {
        return this.argMap;
    }
    
    
    /**
     * Provide {@link ArgumentBuilder}
     * 
     * @return {@link ArgumentBuilder}
     */
    @Override
    public ArgumentBuilder getArgumentBuilder() {
        return this.argBuilder;
    }
    
    
    /**
     * Parse command-line argument values into {@link ArgumentTree}
     * 
     * @param argsIn
     * @param argTree
     * 
     * @return boolean
     */
    @Override
    public boolean parseCommandLineArguments(String[] argsIn, ArgumentTree argTree) {
        CliParser output = new CliParser(argTree, argsIn);
        return !output.parse().isEmpty();
    }
}
