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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideMapper;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.model.state_summary.StateSummary;


/**
 * Service to provide {@link Step} interactions to backend DB ({@link TaskTideRepository}).
 * <br><br>
 * A step has a list of {@link WorkItem} associated with it.
 * <br><br>
 * Service methods include providing Step, list of them. Getting list of WorkItems be useful, means composing repo though.
 * <br><br>
 * Implementing the {@link TaskTideMapper} allows mapping of {@link Step} to {@link WorkItem}
 * 
 * @author bkenna
 */
public class StepService implements TaskTideMapper<Step, WorkItem>, TaskTideService<Step> {
    
    // Attributes
    private final TaskTideRepository<Step> repo;
    
    
    /**
     * Construct with repo for testing
     * 
     * @param repo 
     */
    public StepService(TaskTideRepository<Step> repo) {
        this.repo = repo;
    }
    
    
    /**
     * Add step to backend
     * 
     * @param model
     * @return {@link TaskTideModel} of {@link Step Step}
     */
    @Override
    public synchronized Step appendModel(Step model) {
        return repo.insertModel(model);
    }

    
    /**
     * Import step list to backend, measuring imported count against expected
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public synchronized boolean extendModel(List<Step> toAdd) {
        return repo.extendModel(toAdd);
    }
    
    
    /**
     * View steps by field
     * 
     * @param field
     * @param value
     * @return List-{@link TaskTideModel} of {@link Step}
     */
    @Override
    public synchronized List<Step> viewByField(String field, Object value) {
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
     * @return List-{@link Step}
     */
    @Override
    public synchronized List<Step> viewByFieldForGroup(String field, Object value, String group, Object groupVal) {
        return repo.findByFieldForGroup(field, value, group, groupVal);
    }
    
    
    /**
     * Fetch step by id
     * 
     * @param id
     * @return {@link TaskTideModel} of {@link Step}
     */
    @Override
    public synchronized Step fetchById(String id) {
        return repo.findById(id).get();
    }
    
    
    /**
     * View all steps
     * 
     * @return List-{@link Step}
     */
    @Override
    public synchronized List<Step> viewAll() {
        return repo.findAll();
    }

    
    /**
     * Fetch all {@link Step} as {@link TaskTideModel}
     * 
     * @return List-{@link TaskTideModel}
     */
    @Override
    public synchronized List<TaskTideModel> viewAllToTaskTideModel() {
        return this.viewAll()
            .stream()
            .parallel()
            .map(elm -> (TaskTideModel<Step>) elm)
        .collect(Collectors.toList());
    }
    
        
    /**
     * Fetch steps having name
     * 
     * @param stepName
     * @return List-{@link Step}
     */
    public synchronized List<Step> viewStepsByName(String stepName) {
        return repo.findByField("stepName", stepName);
    }
    
    
    /**
     * Drop step matching Id
     * 
     * @param id
     * @return boolean
     */
    @Override
    public synchronized boolean dropById(String id) {
        return repo.deleteModel(id);
    }
    
    
    /**
     * Update step
     * 
     * @param model
     * @return {@link Step}
     */
    @Override
    public synchronized Step updateModel(Step model) {
        return repo.updateModel(model);
    }
    
    
    /**
     * View task count summary for provided step
     * 
     * @param step
     * @return {@link StateSummary}-{@link ItemState}
     */
    public synchronized StateSummary<ItemState> viewStepSummary(Step step) {
        return step.summarizeByState();
    }
    
    
    /**
     * View task count summary for all steps
     * 
     * @return Map-String, {@link StateSummary}-{@link ItemState}
     */
    public synchronized Map<String, StateSummary<ItemState>> viewSummary() {
        
        Map<String, StateSummary<ItemState>> results = new HashMap<>();
        for ( Step step : repo.findAll() ) {
            results.put(step.getStepName(), step.summarizeByState());
        }
        return results;
    }
    
    
    /**
     * Update {@link Step} {@link ItemState} counts to new values
     * 
     * @param step
     * @param newCounts
     * @return {@link Step}
     */
    public synchronized Step updateStepCounts(Step step, StateSummary<ItemState> newCounts) {
        step.setStateCounts(newCounts);
        return repo.updateModel(step);
    }

    
    /**
     * Map queried {@link Step} to {@link WorkItem} collection
     * 
     * @param mappingServ
     * @param model
     * @return List-{@link WorkItem}
     */
    @Override
    public synchronized List<WorkItem> getThroughLink(TaskTideService<WorkItem> mappingServ, Step model) {
        return mappingServ.viewByField("stepName", model.getStepName());
    }
    
    
    /**
     * Return {@link Step} {@link TaskTideRepository}
     * 
     * @return {@link TaskTideRepository} of {@link Step}
     */
    @Override
    public synchronized TaskTideRepository<Step> getRepo() {
        return this.repo;
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
     * Represent service as string
     * 
     * @return String
     */
    @Override
    public synchronized String toString() {
        return "StepService{" + 
            "ServiceType=Step" +
            ",ServiceLink=WorkItem" +
        '}';
    }
}