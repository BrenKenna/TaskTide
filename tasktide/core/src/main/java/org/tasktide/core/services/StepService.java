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
 * @author bkenna
 */
@Dependent
public class StepService implements TaskTideMapper<Step, WorkItem>, TaskTideService {
    
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
     * View all steps
     * 
     * @return List-{@link Step Step}
     */
    public List<Step> viewSteps() {
        return repo.findAll();
    }
    
    
    /**
     * Fetch steps with field matching value
     * 
     * @param field
     * @param value
     * @return List-{@link Step Step}
     */
    public List<Step> viewStepsByField(String field, Object value) {
        return repo.findByField(field, value);
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
     * Fetch step by Id
     * 
     * @param stepId
     * @return {@link Step Step}
     */
    public Step fetchStep(String stepId) {
        return repo.findById(stepId).get();
    }
    
    
    /**
     * Append provided step
     * 
     * @param step
     * @return {@link Step Step}
     */
    public Step appendStep(Step step) {
        return repo.insertModel(step);
    }
    
    
    /**
     * Drop step matching Id
     * 
     * @param id
     * @return boolean
     */
    public boolean dropStep(String id) {
        return repo.deleteModel(id);
    }
    
    
    /**
     * Update step
     * 
     * @param step
     * @return {@link Step Step}
     */
    public Step updateStep(Step step) {
        return repo.updateModel(step);
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
}
