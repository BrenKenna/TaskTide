/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.nosql.Template;

import org.tasktide.core.model.collection.Workflow;


/**
 * Workflow repository
 * 
 * @author bkenna
 */
@ApplicationScoped
public class WorkflowRepository extends ModelRepository<Workflow> {
    
    /**
     * Construct WorkflowRepository with injectable template and configurable collection name
     * 
     * @param template
     * @param collectionName workflow.repo-name
     */
    @Inject
    public WorkflowRepository(Template template,
        @ConfigProperty(name = "workflow.repo-name", defaultValue = "Workflow") String collectionName
    ) {
        super(template, Workflow.class, collectionName);
    }
}
