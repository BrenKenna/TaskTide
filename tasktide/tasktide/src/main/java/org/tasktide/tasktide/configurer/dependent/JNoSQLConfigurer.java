/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.configurer.dependent;

import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.eclipse.jnosql.mapping.column.ColumnTemplate;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.graph.GraphTemplate;
import org.eclipse.jnosql.mapping.keyvalue.KeyValueTemplate;

import org.tasktide.tasktide.configurer.AbstractConfigurer;
import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentType;

import jakarta.nosql.Template;

/**
 * Configurer for Jakarta NoSQL {@link Template}
 * 
 * @author bkenna
 */
public class JNoSQLConfigurer extends AbstractConfigurer {
    
    @Inject
    @ConfigProperty(name = "tasktide.core.repository.jnosql.type", defaultValue = "document") // e.g., "document", "keyvalue", "column", "graph"
    private String dbType;

    @Inject
    @ConfigProperty(name = "tasktide.core.repository.jnosql.provider", defaultValue = "mongo") // e.g., "mongo", "redis", "cassandra", "couchdb"
    private String provider;
    
    
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
            .withValue(this.dbType, String.class)
        .build();
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
            .withValue(this.dbType, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Method to provide template class to configure
     *  defined by tasktide.core.repository.jnosql.type
     * 
     * @return Class - Document, KeyValue, Column, Graph, null
     */
    public Class provideTemplateClass() {
        switch( this.dbType.toLowerCase() ) {
            case "document" -> {
                return DocumentTemplate.class;
            }
            
            case "keyvalue" -> {
                return KeyValueTemplate.class;
            }
            
            case "column" -> {
                return ColumnTemplate.class;
            }
            
            case "graph" -> {
                return GraphTemplate.class;
            }
            
            default -> {
                return null;
            }
        }
    }
}
