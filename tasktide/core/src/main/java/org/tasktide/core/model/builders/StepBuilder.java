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


/**
 *
 * Allow {@link Step Step} objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class StepBuilder extends ModelBuilder<Step> {
    
    // Attributes
    private String stepId, stepName, workflowId;
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
            Step step = new Step(this.stepId, this.stepName);
            if ( this.anno != null ) {
                step.setAnnotations(this.anno);
            }
            return step;
        }
        return new Step(this.stepId, this.stepName, this.workflowId, this.anno);
    }
}
