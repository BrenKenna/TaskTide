/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.TaskTideMapper;

import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.state_summary.StateSummary;
import org.tasktide.core.model.workitem.ItemState;

import org.tasktide.core.supporting.Utils;


/**
 *
 * Service to provide WorkflowService interactions to backend DB
 * 
 * @author bkenna
 */
@Dependent
public class WorkflowService implements TaskTideMapper<Workflow, Step>, TaskTideService<Workflow> {
    
    // Attributes
    private final TaskTideRepository<Workflow> repo;

    
    /**
     * Construct with repo for testing
     * 
     * @param repo 
     */
    @Inject
    public WorkflowService(TaskTideRepository<Workflow> repo) {
        this.repo = repo;
    }

    
    /**
     * Add {@link Step Step} from {@link Workflow Workflow}
     * 
     * @param workflow
     * @param step
     * @return {@link Workflow Workflow}
     */
    public Workflow addStepToWorkflow(Workflow workflow, Step step) {
        workflow.getWorkflowSteps().put(step.getId(), step);
        return repo.updateModel(workflow);
    }
    
    
    /**
     * Drop {@link Step Step} from {@link Workflow Workflow}
     * 
     * @param workflow
     * @param step
     * @return boolean
     */
    public boolean dropStepFromWorkflow(Workflow workflow, Step step) {
        workflow.getWorkflowSteps().remove(step.getId());
        return repo.deleteModel(workflow.getWorkflowId());
    }
    
    
    /**
     * Summarize {@link Step Step} across all {@link Workflow Workflow}
     * 
     * @return WorkflowId, StepId,{@link StateSummary StateSummary}-{@link ItemState ItemState}
     */
    public Map<String, Map<String, StateSummary<ItemState>>> summarizeWorkflow() {
    
        // Intialize vars
        Map<String, Map<String, StateSummary<ItemState>>> results = new HashMap<>();
        
        // Fetch step summaries for each step
        for ( Workflow elm : this.viewAll() ) {
            Map<String, StateSummary<ItemState>> stepData = elm.summarizeStepStates();
            results.put(elm.getWorkflowId(), stepData);
        }
        
        // Return results
        return results;
    }
    
    
    /**
     * Fetch steps for {@link Workflow Workflow}
     * 
     * @param mappingServ
     * @param model
     * @return {@link List List}-{@link Step Step}
     */
    @Override
    public List<Step> getThroughLink(TaskTideService<Step> mappingServ, Workflow model) {
        return new ArrayList(model.getWorkflowSteps().values());
    }

    
    /**
     * Add {@link Workflow Workflow}
     * 
     * @param model
     * @return {@link Workflow Workflow}
     */
    @Override
    public Workflow appendModel(Workflow model) {
        return repo.insertModel(model);
    }

    
    /**
     * Query by field
     * 
     * @param field
     * @param value
     * @return List-{@link Workflow Workflow}
     */
    @Override
    public List<Workflow> viewByField(String field, Object value) {
        return repo.findByField(field, value);
    }

    
    /**
     * 
     * 
     * @return List-{@link Workflow Workflow}
     */
    @Override
    public List<Workflow> viewAll() {
        return repo.findAll();
    }

    
    /**
     * Fetch {@link Workflow Workflow} by Id
     * 
     * @param id
     * @return {@link Workflow Workflow}
     */
    @Override
    public Workflow fetchById(String id) {
        return repo.findById(id).get();
    }

    
    /**
     * Drop {@link Workflow Workflow} by Id
     * 
     * @param id
     * @return boolean
     */
    @Override
    public boolean dropById(String id) {
        return repo.deleteModel(id);
    }

    
    /**
     * Update {@link Workflow Workflow}
     * 
     * @param model
     * @return {@link Workflow Workflow}
     */
    @Override
    public Workflow updateModel(Workflow model) {
        return repo.updateModel(model);
    }

    
    /**
     * Extend {@link Workflow Workflow} matching imported count against expected
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public boolean extendModel(List<Workflow> toAdd) {
        return repo.extendModel(toAdd);
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
    
    
    /**
     * Represent service as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "WorkflowService{" + 
            "WorkflowType=Workflow" +
            ",ServiceLink=Step" +
        '}';
    }
}
