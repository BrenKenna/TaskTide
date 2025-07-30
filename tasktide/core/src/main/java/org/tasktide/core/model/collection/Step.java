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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.model.state_summary.StateSummary;

import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.TaskTideModel;


/**
 * Model class for a collection of work items as metadata
 * 
 * @author bkenna
 */
@jakarta.nosql.Embeddable
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
    
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @jakarta.nosql.Column("StepState")
    @JsonbProperty("StepState")
    private TaskState stepState;
    
    @jakarta.persistence.Column(name = "StepCount")
    @jakarta.nosql.Column("StepCount")
    @JsonbProperty("StepCount")
    private int stepCount;
    
    @jakarta.persistence.Column(name = "StepsLocked")
    @jakarta.nosql.Column("StepsLocked")
    @JsonbProperty("StepsLocked")
    private int stepsLocked;
    
    @jakarta.persistence.Column(name = "StepsDone")
    @jakarta.nosql.Column("StepsDone")
    @JsonbProperty("StepsDone")
    private int stepsDone;
    
    @jakarta.persistence.Column(name = "StepsError")
    @jakarta.nosql.Column("StepsError")
    @JsonbProperty("StepsError")
    private int stepsError;
    
    @jakarta.persistence.Column(name = "StepsToDo")
    @jakarta.nosql.Column("StepsToDo")
    @JsonbProperty("StepsToDo")
    private int stepsToDo;
    
    @jakarta.persistence.Column(name = "WorkflowId")
    @jakarta.nosql.Column("WorkflowId")
    @JsonbProperty("WorkflowId")
    private String workflowId;
    
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "MemberId", referencedColumnName = "workflowId", insertable = false, updatable = false)
    @JsonbTransient
    private Workflow workflow;
    
    
    /**
     * Null value constructor
     * 
     */
    public Step(){}
    
    
    /**
     * Constructor for JSON deserialization
     * 
     * @param stepId
     * @param stepName
     * @param stepState
     * @param stepCount
     * @param stepsLocked
     * @param stepsDone 
     * @param stepsToDo
     * @param stepsError
     */
    public Step(
        String stepId,
        String stepName,
        TaskState stepState,
        int stepCount,
        int stepsLocked,
        int stepsDone,
        int stepsToDo,
        int stepsError
    ) {
        this.stepId = stepId;
        this.stepName = stepName;
        this.stepState = stepState;
        this.stepCount = stepCount;
        this.stepsLocked = stepsLocked;
        this.stepsDone = stepsDone;
        this.stepsError = stepsError;
        this.stepsToDo = stepsToDo;
        this.workflowId = workflowId;
    }
    
    
    /**
     * Constructor for JSON deserialization
     * 
     * @param stepId
     * @param stepName
     * @param stepState
     * @param stepCount
     * @param stepsLocked
     * @param stepsDone 
     * @param stepsToDo
     * @param stepsError
     * @param workflowId
     */
    @JsonbCreator
    public Step(
        @JsonbProperty("StepId") String stepId,
        @JsonbProperty("StepName") String stepName,
        @JsonbProperty("StepState") TaskState stepState,
        @JsonbProperty("StepCount") int stepCount,
        @JsonbProperty("StepsLocked") int stepsLocked,
        @JsonbProperty("StepsDone") int stepsDone,
        @JsonbProperty("StepsToDo") int stepsToDo,
        @JsonbProperty("StepsError") int stepsError,
        @JsonbProperty("WorkflowId") String workflowId
    ) {
        this.stepId = stepId;
        this.stepName = stepName;
        this.stepState = stepState;
        this.stepCount = stepCount;
        this.stepsLocked = stepsLocked;
        this.stepsDone = stepsDone;
        this.stepsError = stepsError;
        this.stepsToDo = stepsToDo;
        this.workflowId = workflowId;
    }

    
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

    
    /**
     * Get step state
     * 
     * @return {@link TaskState}
     */
    public TaskState getStepState() {
        return stepState;
    }

    
    /**
     * Set step state
     * 
     * @param stepState 
     */
    public void setStepState(TaskState stepState) {
        this.stepState = stepState;
    }

    
    /**
     * Get step count
     * 
     * @return int
     */
    public int getStepCount() {
        return stepCount;
    }

    
    /**
     * Set step count
     * 
     * @param stepCount 
     */
    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    
    /**
     * Get locked step count
     * 
     * @return int
     */
    public int getStepsLocked() {
        return stepsLocked;
    }

    
    /**
     * Set steps locked count
     * 
     * @param stepsLocked 
     */
    public void setStepsLocked(int stepsLocked) {
        this.stepsLocked = stepsLocked;
    }

    
    /**
     * Get count of steps done
     * 
     * @return int
     */
    public int getStepsDone() {
        return stepsDone;
    }

    
    /**
     * Set count of steps done
     * 
     * @param stepsDone 
     */
    public void setStepsDone(int stepsDone) {
        this.stepsDone = stepsDone;
    }

    
    /**
     * Get count of steps in error
     * 
     * @return int
     */
    public int getStepsError() {
        return stepsError;
    }

    
    /**
     * Set count of steps in error
     * 
     * @param stepsError 
     */
    public void setStepsError(int stepsError) {
        this.stepsError = stepsError;
    }

    
    /**
     * Get count of steps in to do
     * 
     * @return int
     */
    public int getStepsToDo() {
        return stepsToDo;
    }

    
    /**
     * Set count of steps in to do
     * 
     * @param stepsToDo 
     */
    public void setStepsToDo(int stepsToDo) {
        this.stepsToDo = stepsToDo;
    }

    
    @Override
    @JsonbTransient
    public String getState() {
        return this.getStepState().name();
    }
    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "Step{" +
            "stepId=" + stepId +
            ", stepName=" + stepName +
            ", stepState=" + stepState +
            ", stepCount=" + stepCount +
            ", stepsLocked=" + stepsLocked +
            ", stepsDone=" + stepsDone +
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
     * Summarize step by ItemState
     * 
     * @return {@link StateSummary}-{@link ItemState}
     */
    @JsonbTransient
    public StateSummary<ItemState> summarizeByState() {
    
        // Initialize vars
        Map<ItemState, Integer> results = new HashMap<>();
        
        // Map to results
        results.put(ItemState.LOCKED, stepsLocked);
        results.put(ItemState.DONE, stepsDone);
        results.put(ItemState.TODO, stepsToDo);
        results.put(ItemState.ERROR, this.stepsError);
        
        // Return results
        return new StateSummary<>(results);
    }
    
    
    /**
     * Set new state counts
     * 
     * @param counts 
     */
    @JsonbTransient
    public void setStateCounts(StateSummary<ItemState> counts) {
        setStepsLocked( counts.getCount(ItemState.LOCKED) );
        setStepsDone(counts.getCount(ItemState.DONE));
        setStepsToDo(counts.getCount(ItemState.TODO));
        setStepsError(counts.getCount(ItemState.ERROR));
        stepsDone = counts.getCounts().values()
                        .stream()
                        .mapToInt(Integer::intValue)
                        .sum();   
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