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

import java.util.List;
import java.util.ArrayList;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.builders.WorkflowBuilder;
import org.tasktide.core.model.collection.Step;

import org.tasktide.core.supporting.JsonUtils;


/**
 * Data model for GraphQL for {@link Workflow}
 * 
 * @author Bren
 */
@Input
@Description("GraphQL Data Model for TaskTide-Workflow")
public class WorkflowInput {
    
    private final WorkflowBuilder workflowBuilder = new WorkflowBuilder();
    
    public String workflowId, workflowName, stepName;
    public List<StepInput> workflowSteps;
    
    @Description("Workflow annotations as JSON String")
    public String anno;
    
    
    /**
     * Represent as {@link Workflow}
     * 
     * @return {@link Workflow}
     */
    public Workflow asWorkflow() {
        return this.workflowBuilder
            .withId(workflowId)
            .withWorkflowName(workflowName)
            .withSteps( this.parseSteps() )
            .withAnnotation( this.parseAnnotation() )
        .build();
    }
    
    
    /**
     * Parse {@link StepInput} to
     *  {@link Step} collection
     * 
     * @return List-{@link Step}
     */
    public List<Step> parseSteps() {
        List<Step> steps = new ArrayList<>();
        for ( StepInput step: workflowSteps ) {
            steps.add( step.asStep() );
        }
        return steps;
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