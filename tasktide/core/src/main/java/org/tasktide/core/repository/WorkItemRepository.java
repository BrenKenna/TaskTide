/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository;

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
public class WorkItemRepository extends ModelRepository<WorkItem> {
    
    /**
     * Construct WorkItemRepository with injectable template and configurable collection name
     * 
     * @param template
     * @param collectionName workitem.repo-name
     */
    @Inject
    public WorkItemRepository(
        Template template,
        @ConfigProperty(name = "workitem.repo-name", defaultValue = "WorkItem") String collectionName
    ) {
        super(template, WorkItem.class, collectionName);
    }
}
