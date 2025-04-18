/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.repository.ModelRepository;

import org.tasktide.core.supporting.Utils;


/**
 *
 * Service to provide WorkItem interactions to backend DB
 * 
 * @author bkenna
 */
@Dependent
public class WorkItemService {
    
    // Attributes
    private final ModelRepository<WorkItem> repo;
    private final Utils utils;
    private final int LOCKING_WAIT_TIME;
    
    /**
     * Construct with configurable wait time
     * 
     * @param repo
     * @param lockingWaitTime 
     */
    @Inject
    public WorkItemService(
        ModelRepository<WorkItem> repo,
        @ConfigProperty(name = "workitem.locking.wait.time", defaultValue = "4") int lockingWaitTime
    ) {
        this.repo = repo;
        this.LOCKING_WAIT_TIME = lockingWaitTime;
        this.utils = new Utils();
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
        return repo.updateModel(workItem);
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
        return repo.updateModel(workItem);
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
        return repo.updateModel(workItem);
    }
    
    
    /**
     * Find WorkItems by state
     * 
     * @param itemState
     * @return List-WorkItem
     */
    public List<WorkItem> findByState(ItemState itemState) {
        return repo.findByField("itemSate", itemState);
    }

    
    /**
     * Fetch list of work items with name
     * 
     * @param itemName
     * @return List-WorkItem
     */
    public List<WorkItem> findByName(String itemName) {
        return repo.findByField("itemName", itemName);
    }

    public ModelRepository<WorkItem> getRepo() {
        return repo;
    }

    public Utils getUtils() {
        return utils;
    }

    public int getLockWaitTime() {
        return LOCKING_WAIT_TIME;
    }
    
}
