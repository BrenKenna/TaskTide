/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.TaskTideMapper;
import org.tasktide.core.TaskTideModel;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.state_summary.StateSummary;

import org.tasktide.core.supporting.Utils;


/**
 * Service to provide {@link WorkItem} interactions to backend DB
 * 
 * @author bkenna
 */
public class WorkItemService implements TaskTideMapper<WorkItem, Step>, TaskTideService<WorkItem> {
    
    // Attributes
    private final TaskTideRepository<WorkItem> repo;
    private final Utils utils;
    private final int LOCKING_WAIT_TIME;
    
    
    /**
     * Construct {@link TaskTideService} with {@link TaskTideRepository}
     * 
     * @param repo 
     */
    public WorkItemService(TaskTideRepository<WorkItem> repo) {
        this.repo = repo;
        this.LOCKING_WAIT_TIME = 4;
        this.utils = new Utils("dd/MM/yy HH:mm:ss", 4);
    }
    
    
    /**
     * Construct with configurable wait time
     * 
     * @param repo
     * @param lockingWaitTime task-tide.model.workitem.locking-wait-time
     */
    public WorkItemService(
        TaskTideRepository<WorkItem> repo,
        int lockingWaitTime
    ) {
        this.repo = repo;
        this.LOCKING_WAIT_TIME = lockingWaitTime;
        this.utils = new Utils("dd/MM/yy HH:mm:ss", lockingWaitTime);
    }
    
    
    /**
     * 
     * @param repo
     * @param lockWait
     * @param utilDate 
     */
    public WorkItemService(TaskTideRepository<WorkItem> repo, int lockWait, String utilDate) {
        this.repo = repo;
        this.LOCKING_WAIT_TIME = lockWait;
        this.utils = new Utils("dd/MM/yy HH:mm:ss", lockWait);
    }
    
    
    /**
     * Insert work item
     * 
     * @param model
     * @return {@link WorkItem}
     */
    @Override
    public synchronized WorkItem appendModel(WorkItem model) {
        return repo.insertModel(model);
    }
    
    
    /**
     * Fetch work item list by field
     * 
     * @param field
     * @param value
     * @return List-{@link WorkItem}
     */
    @Override
    public synchronized List<WorkItem> viewByField(String field, Object value) {
        return repo.findByField(field, value);
    }
    
    
    /**
     * Find {@link TaskTideModel} from backend with field and group
     *  having specified value. Step = Name, State = ToDo
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @return List-{@link WorkItem}
     */
    @Override
    public synchronized List<WorkItem> viewByFieldForGroup(String field, Object value, String group, Object groupVal) {
        return repo.findByFieldForGroup(field, value, group, groupVal);
    }
    
    
    /**
     * Find {@link WorkItem} by Id
     * 
     * @param id
     * @return {@link WorkItem}
     */
    @Override
    public synchronized WorkItem fetchById(String id) {
        Optional<WorkItem> res = repo.findById(id);
        if ( res.isPresent() ) {
            return res.get();
        }
        else {
            return null;
        }
    }
    
    
    /**
     * Find all work items
     * 
     * @return List-{@link WorkItem}
     */
    @Override
    public synchronized List<WorkItem> viewAll() {
        return repo.findAll();
    }
    
    
    /**
     * Fetch all {@link WorkItem} as {@link TaskTideModel}
     * 
     * @return List-{@link TaskTideModel}
     */
    @Override
    public synchronized List<TaskTideModel> viewAllToTaskTideModel() {
        return this.viewAll()
            .stream()
            .parallel()
            .map(elm -> (TaskTideModel<WorkItem>) elm)
            .collect(Collectors.toList());
    }
    
    
    /**
     * Drop {@link WorkItem} by Id
     * 
     * @param id
     * @return boolean
     */
    @Override
    public synchronized boolean dropById(String id) {
        return repo.deleteModel(id);
    }

    
    /**
     * Update {@link WorkItem}
     * 
     * @param model
     * @return {@link WorkItem}
     */
    @Override
    public synchronized WorkItem updateModel(WorkItem model) {
        return repo.updateModel(model);
    }

    
    /**
     * Import {@link WorkItem} list matching imported count against expected
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public synchronized boolean extendModel(List<WorkItem> toAdd) {
        return repo.extendModel(toAdd);
    }
    
    
    /**
     * Save data to backend
     * 
     * @return int
     */
    @Override
    public synchronized int save() {
        return repo.save();
    }
    
    
    /**
     * Add task to a WorkItem
     * 
     * @param workItem
     * @param task
     * @return {@link WorkItem}
     */
    public synchronized WorkItem appendTask(WorkItem workItem, ItemTask task) {
    
        // Add task to item
        if ( !workItem.addTask(task) ) {
            return null;
        }
        return repo.updateModel(workItem);
    }
    
    
    /**
     * Lock provided {@link WorkItem}
     * 
     * @param workItem
     * @return boolean 
     */
    public synchronized WorkItem lockItem(WorkItem workItem) {
    
        // Lock the provided Item
        String lockId = utils.generateToken();
        workItem.setLockId(lockId);
        workItem.setLockDate(utils.getDateUtility().getDateLong());
        return repo.updateModel(workItem);
    }
    
    
    /**
     * Mark task as done
     * 
     * @param workItem
     * @return {@link WorkItem}
     */
    public synchronized WorkItem markAsDone(WorkItem workItem) {
    
        // Set done fields
        workItem.setDoneDate(utils.getDateUtility().getDateLong());
        workItem.setTaskCounts();
        return repo.updateModel(workItem);
    }
    
    
    /**
     * Find WorkItems by state
     * 
     * @param itemState
     * @return List-{@link WorkItem}
     */
    public synchronized List<WorkItem> viewItemsByState(ItemState itemState) {
        return repo.findByField("itemState", itemState);
    }

    
    /**
     * Fetch list of work items with name
     * 
     * @param itemName
     * @return List-{@link WorkItem}
     */
    public synchronized List<WorkItem> viewItemsByName(String itemName) {
        return repo.findByField("itemName", itemName);
    }

    
    /**
     * Set task counts on for each work item
     */
    public synchronized void traceCounts() {
        repo.findAll().stream()
            .parallel()
            .forEach( elm -> elm.setTaskCounts() );
    }
    
    
    /**
     * Provide count of collection by their state
     * 
     * @param traceFirst - Update per item task counts before providing
     * @return {@link StateSummary}
     */
    public synchronized StateSummary<ItemState> fetchCountByState(boolean traceFirst) {
        
        // Initialize output
        Map<ItemState, Integer> countMap = new HashMap<>();
        
        // Update and fetch counts
        if ( traceFirst ) { traceCounts(); }
        for ( ItemState state : ItemState.values() ) {
            int recordCount = repo.findByField("itemState", state).size();
            countMap.put(state, recordCount);
        }
        
        // Return results
        return new StateSummary<>(countMap);
    }
    
    
    /**
     * Get lock wait time
     * 
     * @return int 
     */
    public synchronized int getLockWaitTime() {
        return LOCKING_WAIT_TIME;
    }

    
    /**
     * Return -{@link WorkItem} {@link TaskTideRepository}
     * 
     * @return {@link TaskTideRepository} of -{@link WorkItem}
     */
    @Override
    public synchronized TaskTideRepository<WorkItem> getRepo() {
        return this.repo;
    }
    
    
    /**
     * Fetches {@link Step} for queried {@link WorkItem}
     * 
     * @param mappingServ
     * @param model
     * @return List-{@link Step}
     */
    @Override
    public synchronized List<Step> getThroughLink(TaskTideService<Step> mappingServ, WorkItem model) {
        return mappingServ.viewByField("stepName", model.getStepName());
    }
    
    
    /**
     * Represent service as string
     * 
     * @return String
     */
    @Override
    public synchronized String toString() {
        return "WorkItemService{" + 
            "LOCKING_WAIT_TIME=" + LOCKING_WAIT_TIME +
            ",ServiceType=WorkItem" +
            ",ServiceLink=Step" +
        '}';
    }
}