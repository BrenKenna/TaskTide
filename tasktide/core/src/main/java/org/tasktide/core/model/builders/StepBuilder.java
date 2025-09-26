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
package org.tasktide.core.model.builders;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.task.TaskState;


/**
 *
 * Allow {@link Step Step} objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class StepBuilder extends ModelBuilder<Step> {
    
    // Attributes
    private String stepId, stepName, workflowId;
    private TaskState stepState;
    private int stepCount, stepsLocked, stepsDone, stepsToDo, stepsError;
    private CustomAnnotation anno;
    
    
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
     * @return {@link StepBuilder}
     */
    public StepBuilder withStepId(String stepId) {
        this.stepId = stepId;
        return this;
    }
    
    
    /**
     * Add step name field
     * 
     * @param stepName
     * @return {@link StepBuilder}
     */
    public StepBuilder withStepName(String stepName) {
        this.stepName = stepName;
        return this;
    }
    
    
    /**
     * Add workflowId field
     * 
     * @param workflowId
     * @return {@link StepBuilder}
     */
    public StepBuilder withWorkflowId(String workflowId) {
        this.workflowId = workflowId;
        return this;
    }
    
    
    /**
     * Add step state field
     * 
     * @param stepState
     * @return {@link StepBuilder}
     */
    public StepBuilder withStepState(TaskState stepState) {
        this.stepState = stepState;
        return this;
    }
    
    
    /**
     * Add step count field
     * 
     * @param stepCount
     * @return {@link StepBuilder}
     */
    public StepBuilder withStepCount(int stepCount) {
        this.stepCount = stepCount;
        return this;
    }
    
    
    /**
     * Add count of the steps locked field 
     * 
     * @param stepsLocked
     * @return {@link StepBuilder}
     */
    public StepBuilder withStepsLocked(int stepsLocked) {
        this.stepsLocked = stepsLocked;
        return this;
    }
    
    
    /**
     * Add count of the steps done field
     * 
     * @param stepsDone
     * @return {@link StepBuilder}
     */
    public StepBuilder withStepsDone(int stepsDone) {
        this.stepsDone = stepsDone;
        return this;
    }
    
    
    /**
     * Add count of the steps to do
     * 
     * @param stepsToDo
     * @return {@link StepBuilder}
     */
    public StepBuilder withStepsToDo(int stepsToDo) {
        this.stepsToDo = stepsToDo;
        return this;
    }
    
    
    /**
     * Add count of steps in error state
     * 
     * @param stepsError
     * @return {@link StepBuilder}
     */
    public StepBuilder withStepsError(int stepsError) {
        this.stepsError = stepsError;
        return this;
    }
    
    
    /**
     * Adds {@link CustomAnnotation}
     * 
     * @param anno
     * @return {@ilnk StepBuilder}
     */
    public StepBuilder withAnnotation(CustomAnnotation anno) {
        this.anno = anno;
        return this;
    }
    
    
    /**
     * Construct {@link Step} from provided fields
     * 
     * @return {@link Step}
     */
    @Override
    public Step build() {
        if ( workflowId == null ) {
            return new Step(stepId, stepName, stepState, stepCount, stepsLocked, stepsDone, stepsToDo, stepsError);
        }
        return new Step(stepId, stepName, stepState, stepCount, stepsLocked, stepsDone, stepsToDo, stepsError, workflowId, anno);
    }
}
