/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.builders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;


/**
 *
 * Allow {@link Workflow Workflow} objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class WorkflowBuilder extends ModelBuilder {
    
    // Attributes
    private String workflowId, workflowName;
    private Map<String, Step> steps;
    
    
    /**
     * Add {@link Workflow Workflow} Id field
     * 
     * @param workflowId
     * @return WorkflowBuilder
     */
    public WorkflowBuilder id(String workflowId) {
        this.workflowId = workflowId;
        return this;
    }
    
    
    /**
     * Add {@link Workflow Workflow} name field
     * 
     * @param workflowName
     * @return String
     */
    public WorkflowBuilder workflowName(String workflowName) {
        this.workflowName = workflowName;
        return this;
    }
    
    
    /**
     * Add {@link Workflow Workflow} steps field
     * 
     * @param steps
     * @return WorkflowBuilder
     */
    public WorkflowBuilder steps(Map<String, Step> steps) {
        this.steps = steps;
        return this;
    }
    
    /**
     * Add {@link Workflow Workflow} steps field
     * 
     * @param stepList
     * @return WorkflowBuilder
     */
    public WorkflowBuilder steps(List<Step> stepList) {
        this.steps = new HashMap<>();
        for (Step step : stepList) {
            this.steps.put(step.getStepName(), step);
        }
        return this;
    }
    
    
    /**
     * Construct {@link Workflow Workflow} with the provided fields
     * 
     * @return {@link Workflow Workflow}
     */
    @Override
    public Workflow build() {
        return new Workflow(workflowId, workflowName, steps);
    }
}
