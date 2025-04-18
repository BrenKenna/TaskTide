/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.repository.qualifiers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jnosql.communication.Settings;

import org.eclipse.jnosql.databases.mongodb.communication.MongoDBDocumentConfiguration;
import org.eclipse.jnosql.databases.mongodb.communication.MongoDBDocumentManagerFactory;
import org.eclipse.jnosql.databases.mongodb.communication.MongoDBDocumentManager;
import org.eclipse.jnosql.databases.mongodb.mapping.MongoDBTemplate;

import org.eclipse.jnosql.databases.couchdb.communication.CouchDBDocumentConfiguration;
import org.eclipse.jnosql.databases.couchdb.communication.CouchDBDocumentManager;
import org.eclipse.jnosql.databases.couchdb.communication.CouchDBDocumentManagerFactory;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 *
 * Enum to hold valid StorageTypes
 * 
 * @author bkenna
 */
public enum StorageType {
    
    MONGODB {
        @Override
        public DocumentTemplate createTemplate(String database) {
            MongoDBDocumentConfiguration config = new MongoDBDocumentConfiguration();
            MongoDBDocumentManagerFactory fact = config.apply( this.makeSettings() );
            MongoDBDocumentManager manager = fact.apply(database);
            DocumentTemplate template = null;
            return template;
        }

        @Override
        public Settings makeSettings() {
            
            // Inject the MicroProfile Config
            Config config = ConfigProvider.getConfig();
            Map<String, Object> mongoConfig = new HashMap<>();

            // Extract relevant config values based on the prefix
            mongoConfig.put(
                   "jnosql.document.database", 
                  config.getValue("jnosql.document.database",
                   String.class)
            );
            mongoConfig.put(
                    "jnosql.mongodb.url",
                  config.getValue("jnosql.provider.host",
                   String.class)
            );
            mongoConfig.put(
                    "jnosql.mongodb.user",
                    config.getOptionalValue("jnosql.mongodb.user", String.class).orElse("")  // Use empty if not provided
            );
            mongoConfig.put(
                    "jnosql.mongodb.password",
                   config.getOptionalValue("jnosql.provider.password",
                    String.class).orElse("")
            );
            mongoConfig.put(
                    "jnosql.document.provider",
                   config.getOptionalValue("jnosql.document.provider",
                    String.class).orElse("")
            );
            return Settings.builder()
                    .putAll(mongoConfig)
                    .build();
        }
        
        
    },
    
    COUCHBASE {
        @Override
        public DocumentTemplate createTemplate(String database) {
            return null;
        }
        
        @Override
        public Settings makeSettings() {
            return null;
        }
    },
    
    ARANGODB {
        @Override
        public DocumentTemplate createTemplate(String database) {
            return null;
        }
        
        @Override
        public Settings makeSettings() {
            return null;
        }
    },
    
    COUCHDB {
        @Override
        public DocumentTemplate createTemplate(String database) {
            CouchDBDocumentConfiguration config = new CouchDBDocumentConfiguration();
            CouchDBDocumentManagerFactory fact = config.apply(null);
            CouchDBDocumentManager manager = fact.apply(database);
            return null;
        }
        
        @Override
        public Settings makeSettings() {
            return null;
        }
    };
    
    
    /**
     * Create DocumentTemplate based on enum value
     * 
     * @param Database
     * @return DocumentTemplate
     */
    public abstract DocumentTemplate createTemplate(String database);
    
    
    /**
     * Parse settings from micro-profile config
     */
    public abstract Settings makeSettings();
}
