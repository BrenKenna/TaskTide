/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.collection;

import jakarta.enterprise.context.Dependent;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.util.List;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * Model class for a collection of steps as a workflow/pipeline
 * 
 * @author bkenna
 */
@Entity
@Dependent
public class Workflow {
    
    @Id
    @JsonbProperty("Workflow Id")
    private String workflowId;
    
    
    @Column
    @JsonbProperty("Workflow Name")
    private String workflowName;
    
    
    @Column
    @JsonbProperty("Workflow Steps")
    private Map<String, Step> workflowSteps;
    
    
    /**
     * Null constructor
     */
    public Workflow() {
        this.workflowSteps = new HashMap<>();
    }
    
    
    /**
     * Constructor for JSON deserialization
     * 
     * @param workflowId
     * @param workflowName
     * @param workflowSteps 
     */
    @JsonbCreator
    public Workflow(
        @JsonbProperty("Workflow Id") String workflowId,
        @JsonbProperty("Workflow Name") String workflowName,
        @JsonbProperty("Workflow Steps") Map<String, Step> workflowSteps
    ) {
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.workflowSteps = workflowSteps;
    }

    
    /**
     * Get workflow Id
     * 
     * @return String
     */
    public String getWorkflowId() {
        return workflowId;
    }

    
    /**
     * Set workflow Id
     * @param workflowId 
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    
    /**
     * Get workflow name
     * 
     * @return String
     */
    public String getWorkflowName() {
        return workflowName;
    }

    
    /**
     * Set workflow name
     * 
     * @param workflowName 
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    
    /**
     * Get workflow steps
     * 
     * @return List-Step
     */
    public Map<String, Step> getWorkflowSteps() {
        return workflowSteps;
    }

    
    /**
     * Set workflow steps
     * 
     * @param workflowSteps 
     */
    public void setWorkflowSteps(Map<String, Step> workflowSteps) {
        this.workflowSteps = workflowSteps;
    }
    
    
    /**
     * Set workflow steps
     * 
     * @param workflowSteps 
     */
    public void setWorkflowSteps(List<Step> steps) {
        for(Step step : steps) {
            this.workflowSteps.put(step.getStepName(), step);
        }
    }

    
    /**
     * Represent as string
     * 
     * @return String 
     */
    @Override
    public String toString() {
        return "Workflow{" + 
            "workflowId=" + workflowId +
            ", workflowName=" + workflowName +
            ", workflowSteps=" + workflowSteps +
        '}';
    }
    
    
    /**
     * Serialize to JSON string
     * 
     * @return String
     */
    public String toJsonString() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Serialize to a human readable formatted JSON string
     * 
     * @return String
     */
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }
}
