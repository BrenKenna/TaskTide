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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.parser.ArgumentTree;

// For JavaDocs
import org.tasktide.parser.configuration.TaskTideConfig;

/**
 * Abstract for standardizing client logic for
 *  configuration, running, and clean-up
 * 
 * @author bkenna
 */
public abstract class TaskTideClient {
    
    // Shared attributes
    private final ClientConfigMap config;
    private final Logger LOGGER = LogManager.getLogger(TaskTideClient.class);
    
    
    /**
     * Construct engine client
     * 
     * @param config
     */
    public TaskTideClient(ClientConfigMap config) {
        this.config = config;
    }
    
    
    /**
     * Parses the provided command-line arguments, configure
     *  client with these parameters, performs the required client
     *  action, and runs any required clean-up
     * 
     */
    public void runClient() {
    
        // Initialize client
        if ( this.configureClient() ) {
            this.performClientTask();
        }
        
        // Otherwise display help
        else {
            LOGGER.info("Displaying help for TaskTideClient:\n'{}'", this.getArgTree().getVerboseHelp());
        }
        
        // Run any required clean-up
        this.cleanUp();
    }
    
    
    /**
     * Configures concrete {@link TaskTideConfig}
     * 
     * @return boolean
     */
    protected abstract boolean configureClient();

    
    /**
     * Performs configured action of client
     * 
     */
    protected abstract void performClientTask();
    
    
    /**
     * Performs any clean-up action(s)
     */
    protected abstract void cleanUp();

    
    /**
     * Get concrete {@link ClientConfigMap}
     * 
     * @return {@link ClientConfigMap}
     */
    public ClientConfigMap getClientConf() {
        return this.config;
    }

    
    /**
     * Get {@link ArgumentTree} shared across clients
     * 
     * @return {@link ArgumentTree}
     */
    public ArgumentTree getArgTree() {
        return this.config.getArgTree();
    }
}