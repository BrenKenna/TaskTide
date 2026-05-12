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
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jsonb.JsonBindingFeature;
import org.glassfish.jersey.servlet.ServletContainer;

import io.smallrye.graphql.entry.http.ExecutionServlet;
import jakarta.servlet.http.HttpServletRequest;

import org.tasktide.api.jwt.JwtRequestFilter;


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
    private String host, basePath;
    private int port;
    private Config config;
    private ResourceConfig resourceConfig;
    
    
    /**
     * Construct with application config
     * 
     * @param host
     * @param port
     * @param basePath 
     */
    public TaskTideWebApi(String host, int port, String basePath) {
        this.host = host;
        this.port = port;
        this.basePath = basePath;
        this.applyOverrides();
    }
    
    
    /**
     * Construct with application config
     * 
     * @param host
     * @param port
     * @param basePath 
     * @param shouldApplyOverrides 
     */
    public TaskTideWebApi(String host, int port, String basePath, boolean shouldApplyOverrides) {
        this.host = host;
        this.port = port;
        this.basePath = basePath;
        if ( shouldApplyOverrides ) {
            this.applyOverrides();
        }
    }
    
    
    /**
     * Construct with config
     * 
     * @param config 
     */
    public TaskTideWebApi(Config config) {
        this.config = config;
        this.host = config
            .getOptionalValue("tasktide.web-api.host", String.class)
        .orElse("http://localhost");
        this.port = config
            .getOptionalValue("tasktide.web-api.port", Integer.class)
        .orElse(8080);
        this.basePath = config
            .getOptionalValue("tasktide.web-api.base-path", String.class)
        .orElse("/");
    }
    
    
    /**
     * Apply overrides over host, port, base URL path
     * 
     */
    private void applyOverrides() {
        this.config = ConfigProvider.getConfig();
        this.host = config
            .getOptionalValue("tasktide.web-api.host", String.class)
        .orElse("http://localhost");
        this.port = config
            .getOptionalValue("tasktide.web-api.port", Integer.class)
        .orElse(8080);
        this.basePath = config
            .getOptionalValue("tasktide.web-api.base-path", String.class)
        .orElse("/");
    }
    
    
    /**
     * Apply {@link ResourceConfig} and
     *  {@link Config}
     * 
     */
    public void configureServer() {
    
        // Fetch config
        if ( this.config == null ) {
            this.config = ConfigProvider.getConfig();
        }
        
        // Configure jersey
        this.resourceConfig = new ResourceConfig()
            .packages("org.tasktide.api.resources")
        ;
        resourceConfig.register(JsonBindingFeature.class);
        resourceConfig.register(JwtRequestFilter.class);
        //resourceConfig.register(HttpServletRequest.class);
        // resourceConfig.register(ComponentProvider.class);
        // resourceConfig.register(CdiSeInjectionManagerFactory.class);
        
        // Set Jersey properties
        this.config.getPropertyNames().forEach(
            name -> {
                if ( name.startsWith("jersey.") ) {
                    String val = config.getConfigValue(name).getRawValue();
                    Object parsedVal = this.parseValue(val);
                    resourceConfig.property(name, parsedVal);
                }
        });
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

        // Start web server
        this.server = new Server(this.port);
        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/");
        
        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(this.resourceConfig));
        ctx.addServlet(jerseyServlet, this.basePath + "/api/*");
        
        //ServletHolder graphqlServlet = new ServletHolder(ExecutionServlet.class);
        //ctx.addServlet(graphqlServlet, this.basePath + "/graphql/*");
        
        this.server.setHandler(ctx);
        try {
            this.server.start();
            return this.server.isStarting() || this.server.isRunning();
        }
        catch ( Exception ex ) {
            return false;
        }
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
    
    
    /**
     * Get server state
     * 
     * @return 
     */
    public String getState() {
        return this.server.getState();
    }
    
    
    /**
     * Get web uri
     * 
     * @return {@link URI}
     */
    public URI getWebUri() {
        return URI.create(this.getWebUriString());
    }
    
    
    /**
     * Get web URI
     * 
     * @return String
     */
    public String getWebUriString() {
        return
            this.host + ":" +
            String.valueOf(this.port) + "/" +
        this.basePath;
    }

    
    /**
     * 
     * 
     * @return {@link Config}
     */
    public Config getConfig() {
        return config;
    }

    
    /**
     * 
     * 
     * @return {@link ResourceConfig}
     */
    public ResourceConfig getResourceConfig() {
        return resourceConfig;
    }
    

    /**
     * Parse boolean/int/long/double/string from value
     * 
     * @param value
     * @return Object
     */
    private Object parseValue(String value) {

        if (value == null) return null;

        // boolean
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }

        // integer
        if (value.matches("^-?\\d+$")) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return Long.parseLong(value); // fallback
            }
        }

        // decimal
        if (value.matches("^-?\\d+\\.\\d+$")) {
            return Double.parseDouble(value);
        }

        // otherwise leave as string
        return value;
    }
    
    
    
    public void displayResourceConfig() {
        resourceConfig.getInstances().forEach(i ->
            System.out.println("PROVIDER: " + i.getClass().getName())
        );
    }
}