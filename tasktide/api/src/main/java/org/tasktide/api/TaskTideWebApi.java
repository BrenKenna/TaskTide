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
package org.tasktide.api;

import java.net.URI;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.eclipse.jetty.server.Server;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jsonb.JsonBindingFeature;
import org.glassfish.jersey.server.spi.ComponentProvider;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.inject.cdi.se.CdiSeInjectionManagerFactory;



/**
 * Interface for starting embedded HTTP server
 *
 * @author Bren
 */
public class TaskTideWebApi {
    
    // Logger
    private final Logger LOGGER = LogManager.getLogger(TaskTideWebApi.class);
    
    // Attributes
    private Server server;
    private final String webUri;
    
    
    /**
     * Construct with application config
     * 
     * @param webUri 
     */
    public TaskTideWebApi(String webUri) {
        this.webUri = webUri;
    }
    
    
    /**
     * Start web server
     * 
     * @return boolean
     */
    public boolean startWebServer() {
    
        // 
        if ( this.server != null ) {
            return this.server.isStarted();
        }
        
        // Configure jersey
        ResourceConfig config = new ResourceConfig()
            .packages("org.tasktide.api")
        ;
        config.register(JsonBindingFeature.class);
        config.register(ComponentProvider.class);
        config.register(CdiSeInjectionManagerFactory.class);
        
        // Start web server
        URI uri = URI.create(webUri);
        this.server = JettyHttpContainerFactory
            .createServer(uri, config);

        return this.server.isStarting() || this.server.isRunning();
    }
    
    
    /**
     * Stop web server
     * 
     * @return boolean
     */
    public boolean stopServer() {
        if ( this.server != null ) {
            try { 
                this.server.stop();
                return this.server.isStopping() || this.server.isStopped();
            }
            catch ( Exception ex ) {
                LOGGER.error("Error stopping servier:\n\n{}", ex);
                return false;
            }
        }
        return false;
    }
}