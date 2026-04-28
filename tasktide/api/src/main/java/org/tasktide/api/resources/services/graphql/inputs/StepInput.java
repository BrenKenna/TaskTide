/*
 * Copyright 2026 Bren.
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
package org.tasktide.api.resources.services.graphql.inputs;

import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.Description;

import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.builders.StepBuilder;

import org.tasktide.core.supporting.JsonUtils;


/**
 * Data model for GraphQL for {@link Step}
 *
 * @author Bren
 */
@Input
@Description("GraphQL Data Model for TaskTide-Step")
public class StepInput {
    
    private static final StepBuilder stepBuilder = new StepBuilder();

    public String stepId, stepName, workflowId;
    
    @Description("Last defined TaskState of Step")
    public TaskState stepState;
    public int stepCount, stepsLocked, stepsDone, stepsToDo, stepsError;
    
    @Description("Annotation as JSON String")
    public String anno;
    
    
    /**
     * Represent as {@link Step}
     * 
     * @return {@link Step}
     */
    public Step asStep() {
        return stepBuilder
            .withStepId(this.stepId)
            .withStepName(this.stepName)
            .withWorkflowId(workflowId)
            .withStepState(this.stepState)
            .withStepCount(this.stepCount)
            .withStepsLocked(this.stepsLocked)
            .withStepsDone(this.stepsDone)
            .withStepsToDo(this.stepsToDo)
            .withStepsError(this.stepsError)
            .withAnnotation(this.parseAnnotation())
        .build();
    }
    
    
    /**
     * Parse the JSON string annotation field
     *  to {@link CustomAnnotation}
     * 
     * @return {@link CustomAnnotation}
     */
    public CustomAnnotation parseAnnotation() {
        return JsonUtils.fromJson(anno, CustomAnnotation.class);
    }
}