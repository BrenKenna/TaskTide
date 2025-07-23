/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.nosql_repo;

import java.util.List;

import jakarta.nosql.Template;

import org.tasktide.core.repository.TemplateRepository;
import org.tasktide.core.model.workitem.WorkItem;


/**
 *
 * WorkItem repository
 * 
 * @author bkenna
 */
public class TemplateWorkItemRepository extends TemplateRepository<WorkItem> {
    
    /**
     * Construct WorkItemRepository with injectable template and configurable collection name
     * 
     * @param template
     * @param collectionName workitem.repo-name
     */
    public TemplateWorkItemRepository(
        Template template,
        String collectionName
    ) {
        super(template, WorkItem.class, collectionName);
    }

    @Override
    public boolean extendModel(List<WorkItem> toAdd) {
        long importCount = toAdd.stream()
            .filter( elm -> insertModel(elm) != null)
        .count();
        return importCount == toAdd.size();
    }
}