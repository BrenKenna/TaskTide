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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;


/**
 *
 * Allow {@link Workflow Workflow} objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class WorkflowBuilder extends ModelBuilder {
    
    // Attributes
    private String workflowId, workflowName;
    private Map<String, Step> steps;
    private CustomAnnotation anno;
    
    
    /**
     * Add {@link Workflow} Id field
     * 
     * @param workflowId
     * @return {@link WorkflowBuilder}
     */
    public WorkflowBuilder withId(String workflowId) {
        this.workflowId = workflowId;
        return this;
    }
    
    
    /**
     * Add {@link Workflow} name field
     * 
     * @param workflowName
     * @return String
     */
    public WorkflowBuilder withWorkflowName(String workflowName) {
        this.workflowName = workflowName;
        return this;
    }
    
    
    /**
     * Add {@link Workflow} steps field
     * 
     * @param steps
     * @return {@link WorkflowBuilder}
     */
    public WorkflowBuilder withSteps(Map<String, Step> steps) {
        this.steps = steps;
        return this;
    }
    
    
    /**
     * Add {@link Workflow} steps field
     * 
     * @param stepList
     * @return {@link WorkflowBuilder}
     */
    public WorkflowBuilder withSteps(List<Step> stepList) {
        this.steps = new HashMap<>();
        for (Step step : stepList) {
            this.steps.put(step.getStepName(), step);
        }
        return this;
    }
    
    
    /**
     * Adds {@link CustomAnnotation}
     * 
     * @param anno
     * @return {@ilnk WorkflowBuilder}
     */
    public WorkflowBuilder withCustomAnnotation(CustomAnnotation anno) {
        this.anno = anno;
        return this;
    }
    
    
    /**
     * Construct {@link Workflow} with the provided fields
     * 
     * @return {@link Workflow}
     */
    @Override
    public Workflow build() {
        return new Workflow(workflowId, workflowName, steps, anno);
    }
}
