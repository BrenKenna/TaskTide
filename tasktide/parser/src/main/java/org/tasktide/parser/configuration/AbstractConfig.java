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
package org.tasktide.parser.configuration;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import org.tasktide.parser.ArgumentTree;
import org.tasktide.parser.CliParser;
import org.tasktide.parser.generic_tree.GenericTreeNode;

import org.tasktide.parser.model.ArgumentBuilder;
import org.tasktide.parser.model.ArgumentMap;


/**
 * Abstract class deals with the broader parts of config, {@link ArgumentMap}, path etc
 * 
 * @author bkenna
 */
public abstract class AbstractConfig implements TaskTideConfig {
    
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
    public AbstractConfig(String path) {
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
     * Extend the provided path on the {@link ArgumentTree}
     *  if present with {@link ArgumentMap}. Otherwise add path
     * 
     * @param argTree
     * @param path
     * @param map 
     */
    @Override
    public void extendPath(ArgumentTree argTree, String path, ArgumentMap map) {
        
        GenericTreeNode<ArgumentMap> node;
        node = argTree.getTree().findByAddress(this.getPath());

        if ( node.getData() != null ) {
            node.getData().extend(this.getArgumentMap());
        }
        else {
            argTree.getTree().addChild(this.getPath(), this.getArgumentMap());
        }
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