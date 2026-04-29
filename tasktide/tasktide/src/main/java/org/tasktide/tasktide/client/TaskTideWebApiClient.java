/*
 * Copyright 2026 Bren.
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.tasktide.api.TaskTideWebApi;

import org.tasktide.parser.ArgumentTree;
import org.tasktide.parser.model.ArgumentMap;


/**
 * Launches configured {@link TaskTideWebApi} providing a web-based interface
 *  for CRUD operations
 *
 * @author Bren
 */
public class TaskTideWebApiClient extends TaskTideClient {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(TaskTideWebApiClient.class);
    private final ArgumentMap webApiArgs;
    private final ArgumentMap globalArgs;
    
    private TaskTideWebApi webApi;
    private final Object webApiLock = new Object();
    private volatile boolean running = true;
    
    
    /**
     * Construct Web Api client
     * 
     * @param configMap
     */
    public TaskTideWebApiClient(ClientConfigMap configMap) {
        super(configMap);
        this.webApiArgs = this.getArgTree().getTree().getDataForAddress("web-api");
        this.globalArgs = this.getArgTree().getTree().getDataForAddress("");
    }
    
    
    /**
     * Constructor for unit-tests, uses null {@link ClientConfigMap},
     *   and {@link ArgumentTree}
     * 
     * @param argTree 
     */
    public TaskTideWebApiClient(ArgumentTree argTree) {
        super(null);
        this.webApiArgs = argTree.getTree().getDataForAddress("web-api");
        this.globalArgs = argTree.getTree().getDataForAddress("");
    }

    
    /**
     * Validate client arguments are configurable
     * 
     * @return boolean
     */
    @Override
    protected boolean configureClient() {
        
        // Fetch values from config map
        LOGGER.info("Fetching configurations for TaskTide-WebApi");
        String host = (String) this.webApiArgs.getArgument("Host").getValue();
        String basePath = (String) this.webApiArgs.getArgument("Base Path").getValue();
        int port = (int) this.webApiArgs.getArgument("Port").getValue();
        
        // Validate properties
        if ( host == null || basePath == null || port <= 0 ) {
            LOGGER.warn("Unable to configure client with null arugments for host, port, base-path");
            return false;
        }
        LOGGER.info(
            "Proceeding with TaskTide-WebApi configurations:\n\nHost = '{}'\nPort = '{}'\nBase Path = '{}'",
            host, port, basePath
        );
        
        // Configure web APi
        this.webApi = new TaskTideWebApi(host, port, basePath, false);
        this.webApi.configureServer();
        LOGGER.info("TaskTide-WebApi configured");
        return true;
    }

    
    /**
     * Launches {@link TaskTideWebApi}
     * 
     */
    @Override
    protected void performClientTask() {
        
        // Try start server
        LOGGER.info("Starting TaskTide-WebApi");
        boolean state = this.webApi.startWebServer();
        LOGGER.info(
            "Server listening on '{}' spinup state is '{}'",
            this.webApi.getWebUriString(), this.webApi.getState()
        );
        
        // Verify state
        if ( !state ) {
            LOGGER.warn("Unable to start TaskTide-WebApi server");
            return;
        }
        
        // Shutdown hook
        Runtime.getRuntime().addShutdownHook( new Thread( () -> {
            LOGGER.info("Shutdown signal received, shutting down TaskTide-WebApi server");
            this.webApi.stopServer();
        }));
        
        // Wait until stopped
        this.blockMain();
    }
    
    
    /**
     * Stops {@link TaskTideWebApi}
     * 
     */
    @Override
    protected void cleanUp() {
        synchronized( this.webApiLock ) {
            this.running = true;
            this.webApiLock.notifyAll();
        }
    }
    
    
    /**
     * Blocks main thread until stopped
     * 
     */
    private void blockMain() {
        synchronized ( this.webApiLock ) {
            while ( true ) {
                try {
                    this.webApiLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}