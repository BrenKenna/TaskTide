/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.TaskTideMapper;
import org.tasktide.core.TaskTideModel;

import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.state_summary.StateSummary;
import org.tasktide.core.model.workitem.ItemState;



/**
 *
 * Service to provide WorkflowService interactions to backend DB
 * 
 * @author bkenna
 */
// @Dependent
public class WorkflowService implements TaskTideMapper<Workflow, Step>, TaskTideService<Workflow> {
    
    // Attributes
    private final TaskTideRepository<Workflow> repo;

    
    /**
     * Construct with repo for testing
     * 
     * @param repo 
     */
    // @Inject
    public WorkflowService(TaskTideRepository<Workflow> repo) {
        this.repo = repo;
    }

    
    /**
     * Add {@link Step Step} from {@link Workflow}
     * 
     * @param workflow
     * @param step
     * @return {@link Workflow}
     */
    public Workflow addStepToWorkflow(Workflow workflow, Step step) {
        workflow.getWorkflowSteps().put(step.getId(), step);
        return repo.updateModel(workflow);
    }
    
    
    /**
     * Drop {@link Step Step} from {@link Workflow}
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
     * Summarize {@link Step} across all {@link Workflow}
     * 
     * @return WorkflowId, StepId,{@link StateSummary}-{@link ItemState}
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
     * Fetch steps for {@link Workflow}
     * 
     * @param mappingServ
     * @param model
     * @return List-{@link Step}
     */
    @Override
    public List<Step> getThroughLink(TaskTideService<Step> mappingServ, Workflow model) {
        return new ArrayList<>(model.getWorkflowSteps().values());
    }

    
    /**
     * Add {@link Workflow}
     * 
     * @param model
     * @return {@link Workflow}
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
     * @return List-{@link Workflow}
     */
    @Override
    public List<Workflow> viewByField(String field, Object value) {
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
     * @return List-{@link Workflow}
     */
    @Override
    public List<Workflow> viewByFieldForGroup(String field, Object value, String group, Object groupVal) {
        return repo.findByFieldForGroup(field, value, group, groupVal);
    }

    
    /**
     * Fetch all {@link Workflow}
     * 
     * @return List-{@link Workflow}
     */
    @Override
    public List<Workflow> viewAll() {
        return repo.findAll();
    }

    
    /**
     * Fetch all {@link Workflow} as {@link TaskTideModel}
     * 
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<TaskTideModel> viewAllToTaskTideModel() {
        return this.viewAll()
            .stream()
            .parallel()
            .map(elm -> (TaskTideModel<Workflow>) elm)
            .collect(Collectors.toList());
    }
    
    
    /**
     * Fetch {@link Workflow Workflow} by Id
     * 
     * @param id
     * @return {@link Workflow}
     */
    @Override
    public Workflow fetchById(String id) {
        return repo.findById(id).get();
    }

    
    /**
     * Drop {@link Workflow} by Id
     * 
     * @param id
     * @return boolean
     */
    @Override
    public boolean dropById(String id) {
        return repo.deleteModel(id);
    }

    
    /**
     * Update {@link Workflow}
     * 
     * @param model
     * @return {@link Workflow}
     */
    @Override
    public Workflow updateModel(Workflow model) {
        return repo.updateModel(model);
    }

    
    /**
     * Extend {@link Workflow} matching imported count against expected
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
     * Return {@link Workflow} {@link TaskTideRepository}
     * 
     * @return {@link TaskTideRepository} of {@link Workflow}
     */
    @Override
    public TaskTideRepository<Workflow> getRepo() {
        return this.repo;
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
