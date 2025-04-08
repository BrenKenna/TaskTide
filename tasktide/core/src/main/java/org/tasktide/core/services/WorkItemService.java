/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * Service to provide WorkItem interactions to backend DB
 * 
 * @author bkenna
 */
@ApplicationScoped
public class WorkItemService {
    
    // Attributes
    @Inject
    private DocumentTemplate template;
    private final String COLLECTION = "WorkItem";
    private final Class COLLECTION_CLASS = WorkItem.class;
    private static final Logger logger = LogManager.getLogger(WorkItemService.class);
    private final Utils utils = new Utils();
    private final int LOCKING_WAIT_TIME = 4;
    
    
    /**
     * Get the WorkItem with provided Id
     * 
     * @param id
     * @return WorkItem
     */
    public WorkItem getById(String id) {
        
        // Fetch by Id
        logger.info("\nAttempting to find WorkItem with id: {}\n", id);
        Optional<WorkItem> workItem = template.find(this.COLLECTION_CLASS, id);
        
        // Return result
        if ( workItem.isPresent() ) {
            logger.info("\nSuccessfully found WorkItem with id: {}\n", id);
            return workItem.get();
        }
        else {
            logger.warn("\nUnable to find WorkItem with id: {}\n", id);
            return null;
        }
    }
    
    /**
     * Insert the provided work item
     * 
     * @param workItem
     * @return WorkItem
     */
    public WorkItem insertWorkItem(WorkItem workItem) {
        
        // Insert record
        logger.info("\nAttempting to insert WorkItem with name: {}\n", workItem.getItemName());
        WorkItem inserted = template.insert(workItem);
        logger.debug("\nInserted WorkItem: {}", inserted.toJsonString());
        
        // Log state
        if ( workItem.equals(inserted) ) {
            logger.info("\nSuccessful insertion of WorkItem with name: {}\n", inserted.getItemName());
        }
        else {
            logger.warn("\nWarning inserted WorkItem \"{}\" not the same as input\n", inserted.getItemName());
        }
        
        // Return inserted item
        return inserted;
    }
    
    
    /**
     * Add task to work item
     * 
     * @param workItem
     * @param task
     * @return WorkItem
     */
    public WorkItem insertTask(WorkItem workItem, ItemTask task) {
        
        // Append to task to items workload
        logger.info("\nAttempting to add task \"{}\" to item \"{}\"\n", task.getTaskName(), workItem.getItemName());
        if ( !workItem.addTask(task) ) {
            
            // Log warning of tasks existence
            logger.warn("\nWarning, work item already contains task \"{}\"", task.getTaskName());
            return null;
        }
        
        // Push change
        WorkItem updated = template.update(workItem);
        
        // Log state
        if ( workItem.getWorkload().getWorkload().size() < updated.getWorkload().getWorkload().size() ) {
            logger.info("\nSuccessful update of WorkItem with name: {}\n", updated.getItemName());
        }
        else {
            logger.warn("\nWarning inserted WorkItem \"{}\" not the same as input\n", updated.getItemName());
        }
        
        // Return updated
        return updated;
    }
    
    
    /**
     * Drop work item from collection
     * 
     * @param workItem
     * @return boolean
     */
    public boolean dropWorkItem(WorkItem workItem) {
        
        // Drop provided work item
        logger.info("\nAttempting to drop work item with name: {}\n", workItem.getItemName());
        Optional<WorkItem> output;
        template.delete(this.COLLECTION_CLASS, workItem.getId());
        
        // Try fetch record
        output = template.find(this.COLLECTION_CLASS, workItem.getId());
        if ( output.isPresent() ) {
            logger.warn("\nWarning deleted work item persists in collection \"{}\"\n", output.get().getItemName());
        }
        else {
            logger.info("\nSuccessfully deleted work item from collection \"{}\"\n", workItem.getItemName());
        }
        return output.isPresent();
    }
    
    
    /**
     * Update provided work item
     * 
     * @param workItem
     * @return boolean
     */
    public boolean updateWorkItem(WorkItem workItem) {
    
        // Update provided work item
        logger.info("\nAttempting to update work item with name: {}\n", workItem.getItemName());
        WorkItem updated = template.update(workItem);
        
        // Log state
        if ( workItem.equals(updated) ) {
            logger.info("\nSuccessful update of WorkItem with name: {}\n", updated.getItemName());
            return true;
        }
        else {
            logger.warn("\nWarning updated WorkItem \"{}\" not the same as input\n", updated.getItemName());
            return false;
        }
    }
    
    
    /**
     * Lock provided item
     * 
     * @param workItem
     * @return boolean
     */
    public boolean lockItem(WorkItem workItem) {
    
        // Update provided work item
        logger.info("\nAttempting to lock work item with name: {}\n", workItem.getItemName());
        String lockId = utils.generateToken();
        workItem.setLockId(lockId);
        workItem.setLockDate(utils.getDateLong());
        template.update(workItem);
        
        // Check lock
        utils.waitSeconds(this.LOCKING_WAIT_TIME);
        WorkItem toVerify = this.getById(workItem.getId());
        if ( toVerify == null ) {
            return false;
        }

        // Proceed to lockId verification
        if ( lockId.equals(toVerify.getLockId()) ) {
            logger.info("\nSuccessfully locked WorkItem named \"{}\" at \"{}\"\n", toVerify.getItemName(), toVerify.getLockDate());;
            return true;
        }
        else {
            logger.warn("\nWarning updated WorkItem lockId \"{}\" not the same as input\n", toVerify.getItemName());
            return false;
        }
    }
    
    
    /**
     * Fetch list of work items by provided state
     * 
     * @param state
     * @return List-WorkItem
     */
    public List<WorkItem> getByState(ItemState state) {
        
        // Fetch by state
        logger.info("\nFetching WorkItems with state: {}\n", state);
        
        // Run query
        List<WorkItem> results = template.select(COLLECTION_CLASS)
                .where("itemState")
                .eq(state.toString())
                .result();
        
        // Handle result
        if ( results.isEmpty() ) {
            logger.warn("\nUnable to fetch any WorkItems with state: {}\n", state);
        }
        else {
            logger.info("\nFound N = \"{}\" WorkItems with state: {}\n", results.size(), state);
        }
        
        // Return results
        return results;
    }
}
