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
import jakarta.nosql.Column;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.model.state_summary.StateSummary;

import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.CustomAnnotation;


/**
 * Model class for a collection of work items as metadata
 * 
 * @author bkenna
 */
@jakarta.nosql.Entity
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "Step")
public class Step implements TaskTideModel<Step> {
    
    @jakarta.nosql.Id
    @jakarta.persistence.Id
    @JsonbProperty("StepId")
    private String stepId;
    
    @jakarta.persistence.Column(name = "StepName")
    @jakarta.nosql.Column("StepName")
    @JsonbProperty("StepName")
    private String stepName;
    
    @jakarta.persistence.Column(name = "WorkflowId")
    @jakarta.nosql.Column("WorkflowId")
    @JsonbProperty("WorkflowId")
    private String workflowId;
    
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "MemberId", referencedColumnName = "workflowId", insertable = false, updatable = false)
    @JsonbTransient
    private Workflow workflow;
    
    // Custom annotations
    @jakarta.persistence.Column(name = "Step Annotation")
    @jakarta.nosql.Column("Step Annotation")
    @JsonbProperty("Step Annotation")
    private CustomAnnotation anno;
    
    
    /**
     * Null value constructor
     * 
     */
    public Step() {
        this.anno = new CustomAnnotation();
    }
    
    
    /**
     * Constructor for JSON deserialization
     * 
     * @param stepId
     * @param stepName
     * 
     */
    public Step(
        String stepId,
        String stepName
    ) {
        this.stepId = stepId;
        this.stepName = stepName;
        this.anno = new CustomAnnotation();
    }
    
    
    /**
     * Constructor for JSON deserialization
     * 
     * @param stepId
     * @param stepName
     * @param workflowId
     * @param anno
     * 
     */
    @JsonbCreator
    public Step(
        @JsonbProperty("StepId") String stepId,
        @JsonbProperty("StepName") String stepName,
        @JsonbProperty("WorkflowId") String workflowId,
        @JsonbProperty("Step Annotation") CustomAnnotation anno
    ) {
        this.stepId = stepId;
        this.stepName = stepName;
        this.workflowId = workflowId;
        this.anno = anno;
    }

    
    /**
     * Get annotations
     * 
     * @return {@link CustomAnnotation}
     */
    @Override
    @JsonbProperty("Step Annotation")
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
     * Reset model
     */
    @Override
    public void resetModel() {}
    
    
    /**
     * Get step Id
     * 
     * @return String
     */
    public String getStepId() {
        return stepId;
    }

    
    /**
     * Set step Id
     * 
     * @param stepId 
     */
    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    
    /**
     * Get parent workflow Id
     * 
     * @return String
     */
    public String getWorkflowId() {
        return this.workflowId;
    }

    
    /**
     * Set parent workflow Id
     * 
     * @param workflowId 
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
    
    
    /**
     * Get parent workflow Id
     * 
     * @return String
     */
    public Workflow getWorkflow() {
        return this.workflow;
    }

    
    /**
     * Set workflow
     * 
     * @param workflow
     */
    public void setWorkflowId(Workflow workflow) {
        this.workflow = workflow;
    }
    
    
    /**
     * Return workflow id
     * 
     * @return 
     */
    @Override
    public String getCollection() {
        return this.workflowId;
    }
    
    
    /**
     * Get step name
     * 
     * @return String
     */
    public String getStepName() {
        return stepName;
    }

    
    /**
     * Set step name
     * 
     * @param stepName 
     */
    public void setStepName(String stepName) {
        this.stepName = stepName;
    }


    
    @Override
    @JsonbTransient
    public String getState() {
        return "";
    }
    
    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "Step{" +
            "stepId=" + this.stepId +
            ", stepName=" + this.stepName +
            ", workflowId=" + this.workflowId
        + "}";
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

    
    /**
     * {@link TaskTideModel} interface method to represent as JsonDoc
     * 
     * @return String
     */
    @Override
    public String toJson() {
        return this.toJsonDoc();
    }

    
    /**
     * {@link TaskTideModel} interface method to return Id
     * 
     * @return String
     */
    @Override
    @JsonbTransient
    public String getId() {
        return getStepId();
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