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
package org.tasktide.tasktide.client.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.parser.ArgumentTree;
import org.tasktide.parser.model.Argument;
import org.tasktide.parser.model.ArgumentType;
import org.tasktide.parser.configuration.AbstractConfig;

import org.tasktide.api.TaskTideWebApi;


/**
 * Class to template the {@link TaskTideWebApi} module parameters
 *
 * @author Bren
 */
public class WebApiConfig extends AbstractConfig {

    
    @ConfigProperty(name = "tasktide.web-api.server.host", defaultValue = "")
    String host;
    
    @ConfigProperty(name = "tasktide.web-api.server.port", defaultValue = "")
    int port;
    
    @ConfigProperty(name = "tasktide.web-api.server.base-path", defaultValue = "")
    String basePath;
    
    
    /**
     * Defaults config path to 'tasktide web-api'
     * 
     */
    public WebApiConfig() {
        super("web-api");
    }
    
    
    /**
     * Uses supplied path for web api config
     * 
     * @param path 
     */
    public WebApiConfig(String path) {
        super(path);
    }
    
    
    /**
     * Applies the {@link TaskTideWebApi} configurations to {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
        this.help();
        this.host();
        this.port();
        this.basePath();
        
        if ( this.getPath().isEmpty() ) {
            argTree.getTree().getRoot().setData(this.getArgumentMap());
        }
        else {
            argTree.getTree().addChild(this.getPath(), this.getArgumentMap());
        }
    }

    
    
    /**
     * Configure help
     * 
     */
    @Override
    public void help() {
        Argument<Boolean> arg;
        arg = this.getArgumentBuilder()
            .withName("Help")
            .withDescription("Displays command-line documentation")
            .withShortFlag("-h")
            .withLongFlag("--help")
            .withArgType(ArgumentType.ACTION)
            .withValue(false, Boolean.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures host parameter
     * 
     */
    public void host() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Host")
            .withDescription(" ")
            .withShortFlag("-host")
            .withLongFlag("--host")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(String.class)
        .build();
        
        this.host = this.getConfigValue("tasktide.web-api.server.host", String.class, "http://localhost");
        arg.setValue(this.host);
        this.getArgumentMap().putArgument(arg);
    }

    
    /**
     * Configures basePath parameter
     * 
     */
    public void basePath() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Base Path")
            .withDescription(" ")
            .withShortFlag("-bp")
            .withLongFlag("--base-path")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(String.class)
        .build();
        
        this.basePath = this.getConfigValue("tasktide.web-api.server.base-path", String.class, "/tasktide");
        arg.setValue(this.basePath);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures port parameter
     * 
     */
    public void port() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("Port")
            .withDescription(" ")
            .withShortFlag("-port")
            .withLongFlag("--port")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(Integer.class)
        .build();
        
        this.port = this.getConfigValue("tasktide.web-api.server.port", Integer.class, 8080);
        arg.setValue(this.port);
        this.getArgumentMap().putArgument(arg);
    }
}