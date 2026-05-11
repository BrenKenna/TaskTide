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
package org.tasktide.core.model.collection;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

import jakarta.nosql.Convert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.CustomAnnotation;
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
    
    @jakarta.persistence.Transient
    @JsonbProperty("WorkflowSteps")
    private Map<String, Step> workflowSteps;
    
    @JsonbTransient
    @jakarta.persistence.Transient
    @jakarta.nosql.Column("StepIds")
    private List<String> stepIds;
    
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Step> stepList = new ArrayList<>();

    
    // Custom annotations
    @jakarta.nosql.Column("Annotations")
    @jakarta.persistence.Column(name = "Annotations")
    @JsonbProperty("Annotations")
    private CustomAnnotation anno;
    
    
    /**
     * Null constructor
     */
    public Workflow() {
        this.workflowSteps = new HashMap<>();
        this.anno = new CustomAnnotation();
    }
    
    
    /**
     * Constructor for JSON (de)serialization
     * 
     * @param workflowId
     * @param workflowName
     * @param workflowSteps
     * @param anno
     */
    @JsonbCreator
    public Workflow(
        @JsonbProperty("WorkflowId") String workflowId,
        @JsonbProperty("WorkflowName") String workflowName,
        @JsonbProperty("WorkflowSteps") Map<String, Step> workflowSteps,
        @JsonbProperty("Custom Annotation") CustomAnnotation anno
    ) {
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.workflowSteps = workflowSteps;
        this.anno = anno;
    }

    
    /**
     * Jakarta-NoSQL compatibble constructor?
     * 
     * @param workflowId
     * @param workflowName
     * @param stepIds
     * @param anno 
     */
    public Workflow(String workflowId, String workflowName, List<String> stepIds, CustomAnnotation anno) {
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.workflowSteps = WorkflowStepConverter.convertToEntityAttribute(stepIds);
        this.anno = anno;
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
     * Get annotations
     * 
     * @return {@link CustomAnnotation}
     */
    @Override
    public CustomAnnotation getAnnotations() {
        return this.anno;
    }
    
    
    /**
     * Set provided {@link CustomAnnotation}
     * 
     * @param anno 
     */
    @Override
    public void setAnnotations(CustomAnnotation anno) {
        this.anno = anno;
    }
    
    
    /**
     * Reset workload
     */
    @Override
    public void resetModel() {
        for ( Step elm : this.workflowSteps.values() ) {
            elm.resetModel();
        }
    }
    
    
    /**
     * Return workflow id
     * 
     * @return 
     */
    @Override
    public String getCollection() {
        return this.workflowName;
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
        
        if (this.workflowSteps == null || this.workflowSteps.isEmpty()) {
            this.hydrateSteps();
        }
        
        return this.workflowSteps;
    }

    
    /**
     * Set workflow steps
     * 
     * @param workflowSteps 
     */
    public void setWorkflowSteps(Map<String, Step> workflowSteps) {
        this.workflowSteps = workflowSteps;
    }
    
    
    public List<String> getStepIds() {
        if ( this.stepIds == null && this.workflowSteps != null ) {
            this.stepIds = WorkflowStepConverter.convertToDatabaseColumn(this.workflowSteps);
        }
        return this.stepIds;
    }
    
    
    public void setStepIds(List<String> stepIds) {
        this.stepIds = stepIds;
    }
    
    
    public void hydrateSteps() {
        if ( this.stepIds == null ) {
            this.workflowSteps = new HashMap<>();
            return;
        }
        
        if ( this.workflowSteps != null && !this.workflowSteps.isEmpty() ) {
            return; // already hydrated
        }
        
        this.workflowSteps = WorkflowStepConverter.convertToEntityAttribute(stepIds);
    }
    
    
    public void deHydrateSteps() {
        if ( this.workflowSteps == null || this.workflowSteps.isEmpty() ) {
             this.stepIds = new ArrayList<>();
        }
        
        else {
            this.stepIds = WorkflowStepConverter.convertToDatabaseColumn(workflowSteps);
        }
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
    @Override
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