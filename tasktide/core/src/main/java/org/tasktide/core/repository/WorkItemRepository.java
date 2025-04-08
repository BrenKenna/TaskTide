/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.ItemState;
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
     * Construct with class & collection fields, injecting DocumentTemaplte
     */
    @Inject
    public WorkItemRepository() {
        super(WorkItem.class, "WorkItem");
    }
    
    
    /**
     * Find WorkItems by state
     * 
     * @param itemState
     * @return List-WorkItem
     */
    public List<WorkItem> findByState(ItemState itemState) {
        return findByField("itemSate", itemState);
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
        return updateModel(workItem);
    }
    
    
    /**
     * Fetch list of work items with name
     * 
     * @param itemName
     * @return List-WorkItem
     */
    public List<WorkItem> findByName(String itemName) {
        return findByField("itemName", itemName);
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
        return updateModel(workItem);
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
        return updateModel(workItem);
    }
}
