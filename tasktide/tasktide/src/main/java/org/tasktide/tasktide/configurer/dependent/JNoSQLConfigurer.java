/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.configurer.dependent;

import jakarta.inject.Inject;
import org.eclipse.jnosql.mapping.column.ColumnTemplate;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.graph.GraphTemplate;
import org.eclipse.jnosql.mapping.keyvalue.KeyValueTemplate;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.tasktide.tasktide.configurer.AbstractConfigurer;
import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentType;

/**
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
    
    public JNoSQLConfigurer() {
        super("tasktide");
    }
    
    public JNoSQLConfigurer(String path) {
        super(path);
    }
    
    @Override
    public void initConfig(ArgumentTree argTree) {
        this.dbType();
        this.provider();
    }
    
    
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
