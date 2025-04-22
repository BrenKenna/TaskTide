/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.nosql_repo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.nosql.Template;

import org.tasktide.core.model.workitem.WorkItem;


/**
 *
 * WorkItem repository
 * 
 * @author bkenna
 */
@ApplicationScoped
public class TemplateWorkItemRepository extends TemplateRepository<WorkItem> {
    
    /**
     * Construct WorkItemRepository with injectable template and configurable collection name
     * 
     * @param template
     * @param collectionName workitem.repo-name
     */
    @Inject
    public TemplateWorkItemRepository(
        Template template,
        @ConfigProperty(name = "task-tide.repository.nosql.collection.workitem.name", defaultValue = "WorkItem") String collectionName
    ) {
        super(template, WorkItem.class, collectionName);
    }
}
