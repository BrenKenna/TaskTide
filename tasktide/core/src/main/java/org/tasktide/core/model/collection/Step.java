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

import org.tasktide.core.model.task.TaskState;


/**
 *
 * Model class for a collection of work items as metadata
 * 
 * @author bkenna
 */
@Entity
@Dependent
public class Step {
    
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
     */
    @JsonbCreator
    public Step(
        @JsonbProperty("Step Id") String stepId,
        @JsonbProperty("Step Name") String stepName,
        @JsonbProperty("Step State") TaskState stepState,
        @JsonbProperty("Step Count") int stepCount,
        @JsonbProperty("Steps Active") int stepsLocked,
        @JsonbProperty("Steps Done") int stepsDone
    ) {
        this.stepId = stepId;
        this.stepName = stepName;
        this.stepState = stepState;
        this.stepCount = stepCount;
        this.stepsLocked = stepsLocked;
        this.stepsDone = stepsDone;
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
     * @return TaskState
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
}
