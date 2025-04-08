/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.nosql.document.DocumentTemplate;
import java.util.List;

import java.util.Optional;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.ItemState;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.supporting.Utils;


/**
 *
 * 
 * @author bkenna
 */
@ApplicationScoped
public class WorkItemRepository {
    
    @Inject
    private DocumentTemplate template;
    private final Class<WorkItem> COLLECTION_CLASS = WorkItem.class;
    private final String COLLECTION = "WorkItem";
    
    private final Utils utils = new Utils();
    private final int LOCKING_WAIT_TIME = 4;
    
    
    /**
     * Fetch WorkItem by its Id
     * 
     * @param id
     * @return WorkItem
     */
    public Optional<WorkItem> findById(String id) {
        return template.find(COLLECTION_CLASS, id);
    }
    
    
    /**
     * Find WorkItems by state
     * 
     * @param state
     * @return List-WorkItem
     */
    public List<WorkItem> findByState(ItemState state) {
        return template.select(COLLECTION_CLASS)
                .where("itemName")
                .eq(state)
                .result();
    }
    
    
    /**
     * Insert WorkItem
     * 
     * @param workItem
     * @return WorkItem
     */
    public WorkItem insertItem(WorkItem workItem) {
        return template.insert(workItem);
    }
    
    
    /**
     * Add task to a WorkItem
     * 
     * @param workItem
     * @param task
     * @return WorkItem
     */
    public WorkItem addTask(WorkItem workItem, ItemTask task) {
    
        // Add task to item
        if ( !workItem.addTask(task) ) {
            return null;
        }
        return template.update(workItem);
    }
    
    
    /**
     * Update a WorkItem
     * 
     * @param workItem
     * @return 
     */
    public WorkItem updateWorkItem(WorkItem workItem) {
        return template.update(workItem);
    }
    
    
    /**
     * Delete WorkItem with Id
     * 
     * @param id
     * @return 
     */
    public boolean deleteWorkItem(String id) {
        template.delete(COLLECTION_CLASS, id);
        return findById(id).isPresent();
    }
    
    
    /**
     * Fetch list of work items with name
     * 
     * @param itemName
     * @return List-WorkItem
     */
    public List<WorkItem> findByName(String itemName) {
        return template.select(COLLECTION_CLASS)
                .where("itemName")
                .eq(itemName)
                .result();
    }
    
    
    /**
     * Generic method to find list of WorkItems by field equally value
     * 
     * @param field
     * @param value
     * @return List-WorkItem
     */
    public List<WorkItem> findByField(String field, Object value) {
        return template.select(COLLECTION_CLASS)
                .where(field)
                .eq(value)
                .result();
    }
    
    
    /**
     * Lock provided WorkItem
     * 
     * @param workItem
     * @return boolean 
     */
    public WorkItem lockItem(WorkItem workItem) {
    
        // Lock the provided Item
        String lockId = utils.generateToken();
        workItem.setLockId(lockId);
        workItem.setLockDate(utils.getDateLong());
        return template.update(workItem);
    }
    
    
    /**
     * Mark task as done
     * 
     * @param workItem
     * @return WorkItem
     */
    public WorkItem markAsDone(WorkItem workItem) {
    
        // Set done fields
        workItem.setDoneDate(utils.getDateLong());
        workItem.setTaskCounts();
        return template.update(workItem);
    }
}
