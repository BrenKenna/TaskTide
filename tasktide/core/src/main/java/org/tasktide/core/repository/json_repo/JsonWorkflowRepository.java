/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.json_repo;

import org.tasktide.core.repository.JsonRepository;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

import org.tasktide.core.model.collection.Workflow;


/**
 * 
 * JSON File I/O repository for Workflow
 * 
 * @author bkenna
 */
public class JsonWorkflowRepository extends JsonRepository<Workflow> {
    
    /**
     * Construct WorkItemRepository with injectable template and configurable collection name
     * 
     * @param modelCollection
     * @param collectionName task-tide.repository.json.collection.step.name
     */
    @Inject
    public JsonWorkflowRepository(
        List<Workflow> modelCollection,
        @ConfigProperty(name = "task-tide.core.repository.json.collection.workflow.name", defaultValue = "Workflow") String collectionName
    ) {
        super(modelCollection, Workflow.class, collectionName);
    }
}
