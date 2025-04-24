/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tasktide.core.TaskTideMapper;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.model.state_summary.StateSummary;


/**
 *
 * Service to provide {@link Step Step} interactions to backend DB ({@link TaskTideRepository}).
 * <br><br>
 * A step has a list of {@link WorkItem WorkItems} associated with it.
 * <br><br>
 * Service methods include providing Step, list of them. Getting list of WorkItems be useful, means composing repo though.
 * <br><br>
 * Implementing the {@link TaskTideMapper} allows mapping of {@link Step Step} to {@link WorkItem WorkItem}
 * 
 * @author bkenna
 */
@Dependent
public class StepService implements TaskTideMapper<Step, WorkItem>, TaskTideService<Step> {
    
    // Attributes
    private final TaskTideRepository<Step> repo;
    
    
    /**
     * Construct with repo for testing
     * 
     * @param repo 
     */
    @Inject
    public StepService(TaskTideRepository<Step> repo) {
        this.repo = repo;
    }
    
    
    /**
     * Add step to backend
     * 
     * @param model
     * @return {@link TaskTideModel TaskTideModel} of {@link Step Step}
     */
    @Override
    public Step appendModel(Step model) {
        return repo.insertModel(model);
    }

    
    /**
     * Import step list to backend, measuring imported count against expected
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public boolean extendModel(List<Step> toAdd) {
        return repo.extendModel(toAdd);
    }
    
    
    /**
     * View steps by field
     * 
     * @param field
     * @param value
     * @return List-{@link TaskTideModel TaskTideModel} of {@link Step Step}
     */
    @Override
    public List<Step> viewByField(String field, Object value) {
        return repo.findByField(field, value);
    }
    
    
    /**
     * Fetch step by id
     * 
     * @param id
     * @return {@link TaskTideModel TaskTideModel} of {@link Step Step}
     */
    @Override
    public Step fetchById(String id) {
        return repo.findById(id).get();
    }
    
    
    /**
     * View all steps
     * 
     * @return List-{@link Step Step}
     */
    @Override
    public List<Step> viewAll() {
        return repo.findAll();
    }

        
    /**
     * Fetch steps having name
     * 
     * @param stepName
     * @return List-{@link Step Step}
     */
    public List<Step> viewStepsByName(String stepName) {
        return repo.findByField("stepName", stepName);
    }
    
    
    /**
     * Drop step matching Id
     * 
     * @param id
     * @return boolean
     */
    @Override
    public boolean dropById(String id) {
        return repo.deleteModel(id);
    }
    
    
    /**
     * Update step
     * 
     * @param model
     * @return {@link Step Step}
     */
    @Override
    public Step updateModel(Step model) {
        return repo.updateModel(model);
    }
    
    
    /**
     * View task count summary for provided step
     * 
     * @param step
     * @return {@link StateSummary StateSummary}-{@link ItemState ItemState}
     */
    public StateSummary<ItemState> viewStepSummary(Step step) {
        return step.summarizeByState();
    }
    
    
    /**
     * View task count summary for all steps
     * 
     * @return Map-String, {@link StateSummary StateSummary}-{@link ItemState ItemState}
     */
    public Map<String, StateSummary<ItemState>> viewSummary() {
        
        Map<String, StateSummary<ItemState>> results = new HashMap();
        for ( Step step : repo.findAll() ) {
            results.put(step.getStepName(), step.summarizeByState());
        }
        return results;
    }
    
    
    /**
     * Update step {@link WorkItem WorkItem} {@link ItemState ItemState} counts to new values
     * 
     * @param step
     * @param newCounts
     * @return {@link Step Step}
     */
    public Step updateStepCounts(Step step, StateSummary<ItemState> newCounts) {
        step.setStateCounts(newCounts);
        return repo.updateModel(step);
    }

    
    /**
     * Map queried {@link Step Step} to {@link WorkItem WorkItem} collection
     * 
     * @param mappingRepo
     * @param model
     * @return List-{@link WorkItem WorkItem}
     */
    @Override
    public List<WorkItem> getThroughLink(TaskTideRepository<WorkItem> mappingRepo, Step model) {
        return mappingRepo.findByField("stepName", model.getStepName());
    }
    
    
    /**
     * Represent service as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "StepService{" + 
            ",ServiceType=Step" +
            ",ServiceLink=WorkItem" +
        '}';
    }

    
    /**
     * Save data to backend
     * 
     * @return int
     */
    @Override
    public int save() {
        return repo.save();
    }
}
