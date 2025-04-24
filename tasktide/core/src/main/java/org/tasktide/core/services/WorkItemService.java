/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import org.tasktide.core.model.state_summary.StateSummary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.TaskTideMapper;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.collection.Step;

import org.tasktide.core.supporting.Utils;


/**
 *
 * Service to provide WorkItem interactions to backend DB
 * 
 * @author bkenna
 */
@Dependent
public class WorkItemService implements TaskTideMapper<WorkItem, Step>, TaskTideService {
    
    // Attributes
    private final TaskTideRepository<WorkItem> repo;
    private final Utils utils;
    private final int LOCKING_WAIT_TIME;
    
    
    /**
     * Construct with configurable wait time
     * 
     * @param repo
     * @param lockingWaitTime task-tide.model.workitem.locking-wait-time
     */
    @Inject
    public WorkItemService(
        TaskTideRepository<WorkItem> repo,
        @ConfigProperty(name = "task-tide.model.workitem.locking-wait-time", defaultValue = "4") int lockingWaitTime
    ) {
        this.repo = repo;
        this.LOCKING_WAIT_TIME = lockingWaitTime;
        this.utils = new Utils();
    }
   
    
    /**
     * Insert work item
     * 
     * @param workItem
     * @return WorkItem
     */
    public WorkItem appendWorkItem(WorkItem workItem) {
        return repo.insertModel(workItem);
    }
    
    
    /**
     * Fetch work item list by field
     * 
     * @param field
     * @param value
     * @return List-WorkItem
     */
    public List<WorkItem> viewItemsByField(String field, Object value) {
        return repo.findByField(field, value);
    }
    
    
    /**
     * Find all work items
     * 
     * @return List-WorkItem
     */
    public List<WorkItem> viewItems() {
        return repo.findAll();
    }
            
    
    /**
     * Add task to a WorkItem
     * 
     * @param workItem
     * @param task
     * @return WorkItem
     */
    public WorkItem appendTask(WorkItem workItem, ItemTask task) {
    
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
    public List<WorkItem> viewItemsByState(ItemState itemState) {
        return repo.findByField("itemState", itemState);
    }

    
    /**
     * Fetch list of work items with name
     * 
     * @param itemName
     * @return List-WorkItem
     */
    public List<WorkItem> viewItemsByName(String itemName) {
        return repo.findByField("itemName", itemName);
    }

    
    /**
     * Set task counts on for each work item
     */
    public void traceCounts() {
        repo.findAll().stream()
            .parallel()
            .forEach( elm -> elm.setTaskCounts() );
    }
    
    
    /**
     * Provide count of collection by their state
     * 
     * @param traceFirst - Update per item task counts before providing
     * @return StateSummary
     */
    public StateSummary fetchCountByState(boolean traceFirst) {
        
        // Initialize output
        Map<ItemState, Integer> countMap = new HashMap<>();
        
        // Update and fetch counts
        if ( traceFirst ) { traceCounts(); }
        for ( ItemState state : ItemState.values() ) {
            int recordCount = repo.findByField("itemState", state).size();
            countMap.put(state, recordCount);
        }
        
        // Return results
        return new StateSummary(countMap);
    }
    
    
    /**
     * Get lock wait time
     * 
     * @return int 
     */
    public int getLockWaitTime() {
        return LOCKING_WAIT_TIME;
    }

    
    /**
     * Represent service as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "WorkItemService{" + 
            "LOCKING_WAIT_TIME=" + LOCKING_WAIT_TIME +
            ",ServiceType=WorkItem" +
        '}';
    }

    
    /**
     * Fetches {@link Step Step} for queried {@link WorkItem WorkItem}
     * 
     * @param mappingRepo
     * @param model
     * @return List-{@link Step Step}
     */
    @Override
    public List<Step> getThroughLink(TaskTideRepository<Step> mappingRepo, WorkItem model) {
        return mappingRepo.findByField("stepName", model.getStepName());
    }
}