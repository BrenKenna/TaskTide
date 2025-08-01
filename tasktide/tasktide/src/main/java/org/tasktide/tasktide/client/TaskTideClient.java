/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.tasktide.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.tasktide.configurer.TaskTideConfigurer;
import org.tasktide.tasktide.parser.ArgumentTree;


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
     * Configures concrete {@link TaskTideConfigurer}
     * 
     * @return boolean
     * 
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
     * Get concrete {@link TaskTideConfigurer}
     * 
     * @return {@link TaskTideConfigurer}
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