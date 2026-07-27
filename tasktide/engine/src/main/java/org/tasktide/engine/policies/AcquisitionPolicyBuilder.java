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
package org.tasktide.engine.policies;

import java.util.List;
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.engine.exceptions.TaskTideEngineUncheckedException;
import org.tasktide.engine.policies.workflow.WorkflowStrategyMode;
import org.tasktide.engine.policies.workflow.WorkflowStrategyType;


/**
 *
 * @author Bren
 */
public class AcquisitionPolicyBuilder {

    
    // Attributes
    private AcquisitionPolicyMode mode;
    private CustomAnnotation anno;
    private String annoKey;
    private Object annoVal;
    private String target;
    private ItemState state;
    private int
        windowSize,
        poolSize,
    iterationLimit;
    private WorkflowStrategyType stratType;
    private WorkflowStrategyMode stratMode;

    
    /**
     * Initialize builder with {@link AcquisitionPolicyMode}
     * 
     * @param mode 
     */
    AcquisitionPolicyBuilder(AcquisitionPolicyMode mode) {
        this.mode = mode;
    }
    
    
    /**
     * Configures {@link AcquisitionPolicyMode}
     * 
     * @param mode
     * 
     * @return {@link AcquisitionPolicyBuilder}
     */
    public AcquisitionPolicyBuilder withPolicyMode(AcquisitionPolicyMode mode) {
        this.mode = mode;
        return this;
    } 
    
    
    /**
     * Building an acquisition policy with collection
     * 
     * @param target
     * 
     * @return {@link AcquisitionPolicyBuilder}
     */
    public AcquisitionPolicyBuilder withTarget(String target) {
        this.target = target;
        return this;
    }
    
    
    /**
     * Building an acquisition policy for elements of collection
     *  matching state
     * 
     * @param state
     * 
     * @return {@link AcquisitionPolicyBuilder}
     */
    public AcquisitionPolicyBuilder withItemState(ItemState state) {
        this.state = state;
        return this;
    }
    
    
    /**
     * Building an acquisition policy for elements with provided
     *  annotation key-value pair
     * 
     * @param key
     * @param val
     * 
     * @return {@link AcquisitionPolicyBuilder}
     */
    public AcquisitionPolicyBuilder withAnno(String key, Object val) {
        this.annoKey = key;
        this.annoVal = val;
        return this;
    }
    
    
    /**
     * Building an acquisition policy for elements with provided
     *  annotation
     * 
     * @param anno
     * 
     * @return {@link AcquisitionPolicyBuilder}
     */
    public AcquisitionPolicyBuilder withAnno(CustomAnnotation anno) {
        this.anno = anno;
        return this;
    }
    
    
    /**
     * Apply window size
     * 
     * @param windowSize
     * 
     * @return {@link AcquisitionPolicyBuilder}
     */
    public AcquisitionPolicyBuilder withWindowSize(int windowSize) {
        this.windowSize = windowSize;
        return this;
    }
    
    
    /**
     * Apply pool size
     * 
     * @param poolSize
     * 
     * @return {@link AcquisitionPolicyBuilder}
     */
    public AcquisitionPolicyBuilder withPoolSize(int poolSize) {
        this.poolSize = poolSize;
        return this;
    }
    
    
    /**
     * Apply iteration limit
     * 
     * @param iterationLimit
     * @return {@link AcquisitionPolicyBuilder}
     */
    public AcquisitionPolicyBuilder withIterationLimit(int iterationLimit) {
        this.iterationLimit = iterationLimit;
        return this;
    }
    
    
    /**
     * Apply {@link WorkflowStrategyType}
     * 
     * @param stratType
     * @return {@link AcquisitionPolicyBuilder}
     */
    public AcquisitionPolicyBuilder withStrategyType(WorkflowStrategyType stratType) {
        this.stratType = stratType;
        return this;
    }
    
    
    /**
     * Apply {@link WorkflowStrategyMode}
     * 
     * @param stratMode
     * @return {@link AcquisitionPolicyBuilder}
     */
    public AcquisitionPolicyBuilder withStrategyMode(WorkflowStrategyMode stratMode) {
        this.stratMode = stratMode;
        return this;
    }
    
    
    /**
     * Build with light verification will add in error
     *  and logging for null target or state
     * 
     * @return AbstractAcquisitionPolicy
     * @throws TaskTideEngineUncheckedException
     */
    public AbstractAcquisitionPolicy build() {
        
        // Initialize output
        AbstractAcquisitionPolicy policy;
        
        // Handle pool size
        if ( this.poolSize < 1 ) {
            this.poolSize = 1;
        }
        
        // Handle window size
        if ( this.windowSize < 1 ) {
            this.windowSize = 1;
        }
        
        // Handle iteration limit
        if ( this.iterationLimit < 1) {
            this.iterationLimit = -1;
        }
        
        // Build targeted acquisition policy
        if ( this.mode.isAcquisitionPolicyMode(AcquisitionPolicyMode.TARGETED) ) {
            policy = new TargetedAcquisitionPolicy(target, state, windowSize, poolSize, iterationLimit);
        }
        
        // Build workflow acquisition policy
        else if ( this.mode.isAcquisitionPolicyMode(AcquisitionPolicyMode.WORKFLOW) ) {
            
            String parsed = target.replaceAll("[,;]$", "").strip();
            target = parsed.replace(";", ",");
            List<String> steps = List.of(target.split(","));
            
            policy = new WorkflowAcquisitionPolicy(
                steps,
                this.stratType,
                this.stratMode,
                this.poolSize,
                this.windowSize,
                this.iterationLimit
            );
        }
        
        // Otherwise unchecked exception
        else {
            throw new TaskTideEngineUncheckedException("AcquisitionPolicyMode must be configured");
        }
        
        if ( this.anno != null ) {
            policy.setAnno(anno);
        }
        if ( this.annoKey != null && this.annoVal != null ) {
            policy.setAnnoKeyValue(annoKey, annoVal);
        }
        
        // Return policy
        return policy;
    }
}