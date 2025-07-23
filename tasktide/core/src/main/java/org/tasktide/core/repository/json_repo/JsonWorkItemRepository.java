/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.json_repo;

import java.util.List;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.JsonRepository;


/**
 * JSON File I/O repository for WorkItems
 * 
 * @author bkenna
 */
public class JsonWorkItemRepository extends JsonRepository<WorkItem> {

        
    /**
     * Construct WorkItemRepository with injectable template and configurable collection name
     * 
     * @param modelCollection
     * @param collectionName workitem.repo-name
     */
    public JsonWorkItemRepository(
        List<WorkItem> modelCollection,
        String collectionName
    ) {
        super(modelCollection, WorkItem.class, collectionName);
    }
}
