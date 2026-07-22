/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.core.services;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.TaskTideMapper;

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
public class WorkItemService
    extends AbstractTaskTideService<WorkItem>
    implements TaskTideMapper<WorkItem, Step>
{
    
    // Attributes
    private final Utils utils;
    private final int LOCKING_WAIT_TIME;
    
    
    /**
     * Construct {@link TaskTideService} with {@link TaskTideRepository}
     * 
     * @param repo 
     */
    public WorkItemService(TaskTideRepository<WorkItem> repo) {
        super(repo);
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
        super(repo);
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
        super(repo);
        this.LOCKING_WAIT_TIME = lockWait;
        this.utils = new Utils(utilDate, lockWait);
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
            .forEach( 
               elm -> elm.setTaskCounts()
        );
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