/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.collection;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.state_summary.StateSummary;
import org.tasktide.core.model.workitem.ItemState;


/**
 * Model class for a collection of steps as a workflow/pipeline
 * 
 * @author bkenna
 */
@jakarta.nosql.Entity
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "Workflow")
public class Workflow implements TaskTideModel<Workflow> {
    
    @jakarta.nosql.Id
    @jakarta.persistence.Id
    @JsonbProperty("WorkflowId")
    private String workflowId;
    
    @jakarta.nosql.Column("WorkflowName")
    @jakarta.persistence.Column(name = "WorkflowName")
    @JsonbProperty("WorkflowName")
    private String workflowName;
    
    @jakarta.nosql.Column("WorkflowSteps")
    @jakarta.persistence.Transient
    @JsonbProperty("WorkflowSteps")
    private Map<String, Step> workflowSteps;
    
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Step> stepList = new ArrayList<>();
    
    
    /**
     * Null constructor
     */
    public Workflow() {
        this.workflowSteps = new HashMap<>();
    }
    
    
    /**
     * Constructor for JSON (de)serialization
     * 
     * @param workflowId
     * @param workflowName
     * @param workflowSteps 
     */
    @JsonbCreator
    public Workflow(
        @JsonbProperty("WorkflowId") String workflowId,
        @JsonbProperty("WorkflowName") String workflowName,
        @JsonbProperty("WorkflowSteps") Map<String, Step> workflowSteps
    ) {
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.workflowSteps = workflowSteps;
    }

    
    /**
     * JPA PostLoad method for populating step map from stepList
     * 
     */
    @jakarta.persistence.PostLoad
    public void populateStateMap() {
        workflowSteps = new HashMap<>();
        for ( Step elm : stepList ) {
            workflowSteps.put(elm.getStepName(), elm);
        }
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
     * @return Map-String, {@link Step}
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
     * @param steps 
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
    
    
    @Override
    public String getState() {
        return "";
    }
    
    /**
     * Summarize {@link Workflow} {@link Step} collection
     * 
     * @return Map-String,{@link StateSummary}-{@link ItemState}
     */
    @JsonbTransient
    public Map<String, StateSummary<ItemState>> summarizeStepStates() {
        Map<String, StateSummary<ItemState>> results = new HashMap<>();
        for ( Entry<String, Step> elm : workflowSteps.entrySet() ) {
               results.put( elm.getKey(), elm.getValue().summarizeByState() );
        }
        return results;
    }
            
    
    /**
     * TaskTideModel interface method to represent as JsonDoc
     * 
     * @return String
     */
    @Override
    public String toJson() {
        return this.toJsonDoc();
    }

    
    /**
     * TaskTideModel interface method to return Id
     * 
     * @return String
     */
    @Override
    public String getId() {
        return workflowId;
    }

    
    /**
     * {@link TaskTideModel} interface get the value from the required field
     * 
     * @param field
     * @return Object
     */
    @Override
    public Object getValueFromField(String field) {
        try {
            // Use reflection to get the declared field from this class
            Field declaredField = this.getClass().getDeclaredField(field);
            declaredField.setAccessible(true); // In case the field is private
            Object fieldValue = declaredField.get(this);

            return fieldValue;

        }
        catch (Exception ex) {
            // Optional: Log or rethrow if needed
            return null;
        }
    }
}
