/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.json_repo;

import org.tasktide.core.repository.JsonRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

import org.tasktide.core.model.workitem.WorkItem;


/**
 * JSON File I/O repository for WorkItems
 * 
 * @author bkenna
 */
@ApplicationScoped
public class JsonWorkItemRepository extends JsonRepository<WorkItem> {

        
    /**
     * Construct WorkItemRepository with injectable template and configurable collection name
     * 
     * @param modelCollection
     * @param collectionName workitem.repo-name
     */
    @Inject
    public JsonWorkItemRepository(
        List<WorkItem> modelCollection,
        @ConfigProperty(name = "task-tide.repository.json.collection.workitem.name", defaultValue = "WorkItem-Data") String collectionName
    ) {
        super(modelCollection, WorkItem.class, collectionName);
    }
}
