/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.configurer;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

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
    private final Config config;
    
    
    /**
     * Construct engine config template
     * 
     * @param path
     */
    public AbstractConfigurer(String path) {
        this.argMap = new ArgumentMap();
        this.path = path;
        this.argBuilder = new ArgumentBuilder();
        this.config = ConfigProvider.getConfig();
    }

    
    /**
     * Add the configured {@link ArgumentMap} to the supplied
     *  {@link ArgumentTree} under the instances path attribute
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
     * Return the instances configured {@link ArgumentMap}
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
     * Provide {@link Config}
     * 
     * @return {@link Config}
     */
    @Override
    public Config getConfig() {
        return this.config;
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
    
    
    /**
     * Fetch config, or default value
     * 
     * @param <T>
     * @param configKey
     * @param type
     * @param defaultValue
     * 
     * @return value 
     */
    public <T> T getConfigValue(String configKey, Class<T> type, T defaultValue) {
        try {
            return this.config.getValue(configKey, type);
        }
        catch (Exception ex) {
            return defaultValue;
        }
    } 
}
