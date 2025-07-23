/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.nosql_repo;

import java.util.List;

import jakarta.nosql.Template;

import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.repository.TemplateRepository;


/**
 * Workflow repository
 * 
 * @author bkenna
 */
public class TemplateWorkflowRepository extends TemplateRepository<Workflow> {
    
    /**
     * Construct WorkflowRepository with injectable template and configurable collection name
     * 
     * @param template
     * @param collectionName workflow.repo-name
     */
    public TemplateWorkflowRepository(
        Template template,
        String collectionName
    ) {
        super(template, Workflow.class, collectionName);
    }

    @Override
    public boolean extendModel(List<Workflow> toAdd) {
        long importCount = toAdd.stream()
            .filter( elm -> insertModel(elm) != null)
        .count();
        return importCount == toAdd.size();
    }
}
