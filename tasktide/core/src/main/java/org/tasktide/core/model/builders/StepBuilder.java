/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.builders;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.task.TaskState;


/**
 *
 * Allow Step objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class StepBuilder extends ModelBuilder {
    
    // Attributes
    private String stepId, stepName;
    private TaskState stepState;
    private int stepCount, stepsLocked, stepsDone, stepsToDo, stepsError;
    
    
    /**
     * Construct builder
     */
    public StepBuilder() {
        super();
    }
    
    
    /**
     * Add stepId field
     * 
     * @param stepId
     * @return StepBuilder
     */
    public StepBuilder stepId(String stepId) {
        this.stepId = stepId;
        return this;
    }
    
    
    /**
     * Add step name field
     * 
     * @param stepName
     * @return StepBuilder
     */
    public StepBuilder stepName(String stepName) {
        this.stepName = stepName;
        return this;
    }
    
    
    /**
     * Add step state field
     * 
     * @param stepState
     * @return StepBuilder
     */
    public StepBuilder stepState(TaskState stepState) {
        this.stepState = stepState;
        return this;
    }
    
    
    /**
     * Add step count field
     * 
     * @param stepCount
     * @return StepBuilder
     */
    public StepBuilder stepCount(int stepCount) {
        this.stepCount = stepCount;
        return this;
    }
    
    
    /**
     * Add count of the steps locked field 
     * 
     * @param stepsLocked
     * @return StepBuilder
     */
    public StepBuilder stepsLocked(int stepsLocked) {
        this.stepsLocked = stepsLocked;
        return this;
    }
    
    
    /**
     * Add count of the steps done field
     * 
     * @param stepsDone
     * @return StepBuilder
     */
    public StepBuilder stepsDone(int stepsDone) {
        this.stepsDone = stepsDone;
        return this;
    }
    
    
    /**
     * Add count of the steps to do
     * 
     * @param stepsToDo
     * @return StepBuilder
     */
    public StepBuilder stepsToDo(int stepsToDo) {
        this.stepsToDo = stepsToDo;
        return this;
    }
    
    
    /**
     * Add count of steps in error state
     * 
     * @param stepsError
     * @return StepBuilder
     */
    public StepBuilder stepsError(int stepsError) {
        this.stepsError = stepsError;
        return this;
    }
    
    
    /**
     * Construct Step from provided fields
     * 
     * @return Step
     */
    @Override
    public Object build() {
        return new Step(stepId, stepName, stepState, stepCount, stepsLocked, stepsDone, stepsToDo, stepsError);
    }
}
