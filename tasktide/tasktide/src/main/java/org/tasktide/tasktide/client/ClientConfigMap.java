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
package org.tasktide.tasktide.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.tasktide.tasktide.containerprovider.CdiContainerProvider;

import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.CliParser;
import org.tasktide.tasktide.configurer.TaskTideConfig;


/**
 * Gathers {@link TaskTideConfig} into map, entry-point
 *  for both their configuration, and application of command-line
 *  arguments.
 * 
 * @author bkenna
 */
public class ClientConfigMap {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(ClientConfigMap.class);
    private final Map<TaskTideClientType, TaskTideConfig> configMap;
    private final ArgumentTree argTree;
    private String[] argsIn;
    private CliParser parser;
    
    
    /**
     * 
     */
    public ClientConfigMap() {
        this.configMap = new HashMap<>();
        this.argTree = new ArgumentTree(" ");
    }
    
    
    /**
     * 
     * 
     * @param provider
     * @param clientType
     * @return {@link TaskTideConfig}
     */
    @SuppressWarnings("unchecked")
    private TaskTideConfig parseConfigs(CdiContainerProvider provider, TaskTideClientType clientType) {
        TaskTideConfig config;
        config = (TaskTideConfig) provider.getBean( clientType.getConfigClass());
        config.initConfig(this.argTree);
        return config;
    }
    
    
    /**
     * 
     * @param provider 
     */
    public void addConfigs(CdiContainerProvider provider) {
        for ( TaskTideClientType elm : TaskTideClientType.values() ) {
            TaskTideConfig config = this.parseConfigs(provider, elm);
            this.configMap.put(elm, config);
        }
    }    

    
    /**
     * Fetch which client to run ie Engine, Manager
     * 
     * @return {@link TaskTideClientType}
     */
    public TaskTideClientType whichClient() {
        List<String> pathTokens = argTree.resolveActionPath(argsIn);
        String actionPath = String.join(" ", pathTokens);
        
        LOGGER.debug("Evaluating client module for:\t'{}'", actionPath);
        if ( actionPath != null ) {
            return TaskTideClientType.get(actionPath);
        }
        return null;
    }
    
    
    /**
     * Return provided arguments
     * 
     * @return String[]
     */
    public String[] getArgsIn() {
        return argsIn;
    }

    
    /**
     * 
     * @param argsIn 
     */
    public void setArgsIn(String[] argsIn) {
        this.argsIn = argsIn;
    }

    
    /**
     * 
     * @return Map-{@link TaskTideClientType}, {@link TaskTideConfig}
     */
    public Map<TaskTideClientType, TaskTideConfig> getConfigMap() {
        return configMap;
    }

    
    /**
     * 
     * 
     * @param clientType
     * @return {@link TaskTideConfig}
     */
    public TaskTideConfig getConfig(TaskTideClientType clientType) {
        return this.configMap.get(clientType);
    }
    
    
    /**
     * 
     * 
     * @return {@link ArgumentTree}
     */
    public ArgumentTree getArgTree() {
        return argTree;
    }

    public CliParser getParser() {
        return parser;
    }

    public void setParser(CliParser parser) {
        this.parser = parser;
    }
    
    public void setParser(String[] args) {
        this.setArgsIn(args);
        this.parser = new CliParser(this.argTree, args);
    }
    
    
    public boolean parseCommandLineArguments(String[] args) {
        this.setParser(args);
        this.parser.parse();
        return parser.hasHelp();
    }
    
    
    public boolean shouldDisplayHelp() {
        return this.parser.hasHelp();
    }
}