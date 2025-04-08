/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.producers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.nosql.document.DocumentTemplate;

import org.eclipse.microprofile.config.inject.ConfigProperty;


/**
 * 
 * Class to produce DocumentTemplate from application config
 * 
 * @author bkenna
 */
@ApplicationScoped
public abstract class DocumentTemplateProducer {
    

    protected final String dbUrl, dbUserName, dbPass;
    
    
    @Inject
    public DocumentTemplateProducer(
            @ConfigProperty(name = "db.url") String dbUrl,
            @ConfigProperty(name = "db.username") String dbUserName,
            @ConfigProperty(name = "db.password") String dbPass
    ) {
        this.dbUrl = dbUrl;
        this.dbUserName = dbUserName;
        this.dbPass = dbPass;
    }
    
    
    @Produces
    public abstract DocumentTemplate createDocumentTemplate();
}
