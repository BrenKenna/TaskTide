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
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.model.state_summary.StateSummary;

import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.TaskTideModel;


/**
 *
 * Model class for a collection of work items as metadata
 * 
 * @author bkenna
 */
@Entity
public class Step implements TaskTideModel<Step> {
    
    @Id
    @JsonbProperty("Step Id")
    private String stepId;
    
    @Column
    @JsonbProperty("Step Name")
    private String stepName;
    
    @Column
    @JsonbProperty("Step State")
    private TaskState stepState;
    
    @Column
    @JsonbProperty("Step Count")
    private int stepCount;
    
    @Column
    @JsonbProperty("Steps Locked")
    private int stepsLocked;
    
    @Column
    @JsonbProperty("Steps Done")
    private int stepsDone;
    
    @Column
    @JsonbProperty("Steps Error")
    private int stepsError;
    
    @Column
    @JsonbProperty("Steps ToDo")
    private int stepsToDo;
    
    
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
    @JsonbCreator
    public Step(
        @JsonbProperty("Step Id") String stepId,
        @JsonbProperty("Step Name") String stepName,
        @JsonbProperty("Step State") TaskState stepState,
        @JsonbProperty("Step Count") int stepCount,
        @JsonbProperty("Steps Locked") int stepsLocked,
        @JsonbProperty("Steps Done") int stepsDone,
        @JsonbProperty("Steps ToDo") int stepsToDo,
        @JsonbProperty("Steps Error") int stepsError
    ) {
        this.stepId = stepId;
        this.stepName = stepName;
        this.stepState = stepState;
        this.stepCount = stepCount;
        this.stepsLocked = stepsLocked;
        this.stepsDone = stepsDone;
        this.stepsError = stepsError;
        this.stepsToDo = stepsToDo;
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
     * @return {@link StateSumary}-{@link ItemState}
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