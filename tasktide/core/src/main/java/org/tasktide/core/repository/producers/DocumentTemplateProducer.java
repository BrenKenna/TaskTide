/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.producers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.jnosql.mapping.document.DocumentTemplate;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.tasktide.core.repository.qualifiers.StorageType;

/**
 * Abstract class to produce DocumentTemplate from application config
 * 
 * @author bkenna
 */
@ApplicationScoped
public abstract class DocumentTemplateProducer implements DatabaseConfig {
    

    // Attributes for db connection
    private final StorageType dbType;
    private final String dbUrl, dbUserName, dbPass, db;
    
    
    /**
     * Construct producer from application properties
     * 
     * @param dbUrl
     * @param dbUserName
     * @param dbPass 
     */
    @Inject
    public DocumentTemplateProducer(
            @ConfigProperty(name = "db.type") StorageType dbType,
            @ConfigProperty(name = "db.url") String dbUrl,
            @ConfigProperty(name = "db.username") String dbUserName,
            @ConfigProperty(name = "db.password") String dbPass,
            @ConfigProperty(name = "db.database") String db
    ) {
        this.dbType = dbType;
        this.dbUrl = dbUrl;
        this.dbUserName = dbUserName;
        this.dbPass = dbPass;
        this.db = db;
    }
    
    
    /**
     * Config to produce DocumentTemplate from config
     * 
     * @return DocumentTemplate
     */
    public DocumentTemplate createDocumentTemplate() {
        return dbType.createTemplate(db);
    }
}
