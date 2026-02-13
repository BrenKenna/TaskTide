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
package org.tasktide.tasktide.client.config.dependents;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.parser.configuration.AbstractConfig;
import org.tasktide.parser.ArgumentTree;
import org.tasktide.parser.model.Argument;
import org.tasktide.parser.model.ArgumentType;


/**
 * Configurer for Jakarta NoSQL Template
 * 
 * @author bkenna
 */
public class JNoSQLConfigurer extends AbstractConfig {
    
    @ConfigProperty(name = "tasktide.core.repository.jnosql.type", defaultValue = "document") // e.g., "document", "keyvalue", "column", "graph"
    private String dbType;

    @ConfigProperty(name = "tasktide.core.repository.jnosql.provider", defaultValue = "mongo") // e.g., "mongo", "redis", "cassandra", "couchdb"
    private String provider;
    
    
    /**
     * Jakarta NoSQL Config
     * 
     */    
    @ConfigProperty(name = "tasktide.core.repository.jnosql.provider-class", defaultValue = "")
    private String nosqlProviderClass;
    
    @ConfigProperty(name = "tasktide.core.repository.jnosql.user", defaultValue = "")
    private String nosqlUser;
    
    @ConfigProperty(name = "tasktide.core.repository.jnosql.password", defaultValue = "")
    private String nosqlPassword;
    
    @ConfigProperty(name = "tasktide.core.repository.jnosql.host", defaultValue = "")
    private String nosqlHost;
    
    
    /**
     * Defaults {@link ArgumentTree} path to root
     */
    public JNoSQLConfigurer() {
        super("tasktide");
    }
    
    
    /**
     * Sets {@link ArgumentTree} path to provided
     * 
     * @param path 
     */
    public JNoSQLConfigurer(String path) {
        super(path);
    }
    
    
    /**
     * Applies configs to {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
        this.dbType();
        this.provider();
        this.nosqlProviderClass();

        this.nosqlUser();
        this.nosqlPassword();
        this.nosqlHost();
        
         // Put argument map into tree
        if ( this.getPath().isEmpty() ) {
            argTree.getTree().getRoot().setData(this.getArgumentMap());
        }
        else {
            argTree.getTree().getRoot().getData().extend(this.getArgumentMap());
        }
    }
    
    
    /**
     * Configures database type ie Document, KeyValue etc
     * 
     */
    public void dbType() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("NoSQL Database Type")
            .withDescription("Specifies the NoSQL Database Type: Document, KeyValue, Column, Graph")
            .withShortFlag("-dbt")
            .withLongFlag("--nosql-database-type")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Fetch value if present
        try {
            this.dbType = this.getConfig().getValue("tasktide.core.repository.jnosql.type", String.class);
        }
        catch (Exception ex) {
            this.dbType = "";
        }
        if (!this.dbType.isEmpty()) arg.setValue(this.dbType);
        this.getArgumentMap().putArgument(arg);
    }

    
    /**
     * Configures specific database provided ie MongoDB etc
     * 
     */
    public void provider() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("NoSQL Database Provider")
            .withDescription("Specifies the NoSQL Software Name: Mongo, CouchDB etc")
            .withShortFlag("-dbt")
            .withLongFlag("--nosql-database-provider")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Fetch value if present
        try {
            this.provider = this.getConfig().getValue("tasktide.core.repository.jnosql.provider", String.class);
        }
        catch (Exception ex) {
            this.provider = "";
        }
        if (!this.provider.isEmpty()) arg.setValue(this.provider);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures specific database provided ie MongoDB etc
     * 
     */
    public void nosqlProviderClass() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("NoSQL Provider Class")
            .withDescription("Specifies the backend NoSQL-DB Provider Class")
            .withShortFlag("-nspc")
            .withLongFlag("--nosql-provider-class")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Fetch value if present
        try {
            this.nosqlProviderClass = this.getConfig().getValue("tasktide.core.repository.jnosql.provider-class", String.class);
        }
        catch (Exception ex) {
            this.nosqlProviderClass = "";
        }
        if (!this.nosqlProviderClass.isEmpty()) arg.setValue(this.nosqlProviderClass);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository type
     * 
     */
    public void nosqlUser() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("NoSQL User")
            .withDescription("Specifies the backend NoSQL-DB Username to use")
            .withShortFlag("-nsu")
            .withLongFlag("--nosql-user")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Fetch value if present
        try {
            this.nosqlUser = this.getConfig().getValue("tasktide.core.repository.jnosql.user", String.class);
        }
        catch (Exception ex) {
            this.nosqlUser = "";
        }
        if (!this.nosqlUser.isEmpty()) arg.setValue(this.nosqlUser);
        this.getArgumentMap().putArgument(arg);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository password
     * 
     */
    public void nosqlPassword() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("NoSQL Password")
            .withDescription("Specifies the backend NoSQL-DB password to use")
            .withShortFlag("-nsp")
            .withLongFlag("--nosql-password")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Fetch value if present
        try {
            this.nosqlPassword = this.getConfig().getValue("tasktide.core.repository.jnosql.password", String.class);
        }
        catch (Exception ex) {
            this.nosqlPassword = "";
        }
        if (!this.nosqlPassword.isEmpty()) arg.setValue(this.nosqlPassword);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository host:port
     * 
     */
    public void nosqlHost() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
                .withName("NoSQL Host:Port")
                .withDescription("Specifies the backend NoSQL-DB host:port to use")
                .withShortFlag("-nsh")
                .withLongFlag("--nosql-host")
                .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Fetch value if present
        try {
            this.nosqlHost = this.getConfig().getValue("tasktide.core.repository.jnosql.host", String.class);
        }
        catch (Exception ex) {
            this.nosqlHost = "";
        }
        if (!this.nosqlHost.isEmpty()) arg.setValue(this.nosqlHost);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    
    @Override
    public void help() {}
}
