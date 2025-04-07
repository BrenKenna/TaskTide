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
 * Allow Workflow objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class WorkflowBuilder extends ModelBuilder {
    
    // Attributes
    private String workflowId, workflowName;
    private Map<String, Step> steps;
    
    
    /**
     * Add workflow Id field
     * 
     * @param workflowId
     * @return WorkflowBuilder
     */
    public WorkflowBuilder workflowId(String workflowId) {
        this.workflowId = workflowId;
        return this;
    }
    
    
    /**
     * Add workflow name field
     * 
     * @param workflowName
     * @return String
     */
    public WorkflowBuilder workflowName(String workflowName) {
        this.workflowName = workflowName;
        return this;
    }
    
    
    /**
     * Add workflow steps field
     * 
     * @param steps
     * @return List-Step
     */
    public WorkflowBuilder steps(Map<String, Step> steps) {
        this.steps = steps;
        return this;
    }
    
    /**
     * Add workflow steps field
     * 
     * @param steps
     * @return List-Step
     */
    public WorkflowBuilder steps(List<Step> stepList) {
        this.steps = new HashMap<>();
        for (Step step : stepList) {
            this.steps.put(step.getStepName(), step);
        }
        return this;
    }
    
    
    /**
     * Construct Workflow with the provided fields
     * 
     * @return Workflow
     */
    @Override
    public Object build() {
        return new Workflow(workflowId, workflowName, steps);
    }
}
