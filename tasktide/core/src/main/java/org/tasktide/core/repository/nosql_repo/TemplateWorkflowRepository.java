/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.nosql_repo;

import org.tasktide.core.repository.TemplateRepository;
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
public class TemplateWorkflowRepository extends TemplateRepository<Workflow> {
    
    /**
     * Construct WorkflowRepository with injectable template and configurable collection name
     * 
     * @param template
     * @param collectionName workflow.repo-name
     */
    @Inject
    public TemplateWorkflowRepository(Template template,
        @ConfigProperty(name = "task-tide.repository.nosql.collection.workflow.name", defaultValue = "Workflow") String collectionName
    ) {
        super(template, Workflow.class, collectionName);
    }
}
