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

import java.util.UUID;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.workitem.ItemState;

import org.tasktide.engine.policies.resources.ResourcePolicyMapper;


/**
 * Abstract implementation of {@link TaskTideWorkloadAcquisitionPolicy}
 *  to take the field policy building, and field getter aspects.
 *  Preserves generic {@link TaskTideModel} type to allow implementing
 *  class to focus on the specifics of building/fetching the
 *  required {@link TaskTideModel} collection
 *
 * @author Bren
 */
public abstract class AbstractAcquisitionPolicy implements TaskTideWorkloadAcquisitionPolicy {
    
    // Attributes for which builder
    protected final String id;
    protected final AcquisitionPolicyMode policyType;
    
    private boolean targetted, annoString, annotation;

    // Field properties
    private String target = "";
    private ItemState state = ItemState.TODO;
    private String annoKey;
    private Object annoVal;
    private CustomAnnotation anno;
    protected int windowSize, poolSize;
    
    
    /**
     * Construct provided {@link AcquisitionPolicyMode}
     * 
     * @param policyType 
     */
    AbstractAcquisitionPolicy(AcquisitionPolicyMode policyType) {
        this.policyType = policyType;
        this.id = policyType.toString() + UUID.randomUUID().toString();
    }
    
    
    /**
     * Building an acquisition policy with collection
     * 
     * @param target
     * @return {@link  AbstractAcquisitionPolicy}
     */
    @Override
    public AbstractAcquisitionPolicy withTarget(String target) {
        this.target = target;
        this.targetted = true;
        return this;
    }
    
    
    /**
     * Building an acquisition policy for elements of collection
     *  matching state
     * 
     * @param state
     * @return {@link  AbstractAcquisitionPolicy}
     */
    @Override
    public AbstractAcquisitionPolicy withItemState(ItemState state) {
        this.state = state;
        return this;
    }
    
    
    /**
     * Building an acquisition policy for elements with provided
     *  annotation key-value pair
     * 
     * @param key
     * @param val
     * @return {@link  AbstractAcquisitionPolicy}
     */
    @Override
    public AbstractAcquisitionPolicy withAnno(String key, Object val) {
        this.annoKey = key;
        this.annoVal = val;
        this.annoString = true;
        return this;
    }
    
    
    /**
     * Building an acquisition policy for elements with provided
     *  annotation
     * 
     * @param anno
     * @return 
     */
    @Override
    public AbstractAcquisitionPolicy withAnno(CustomAnnotation anno) {
        this.anno = anno;
        this.annotation = true;
        return this;
    }
    
    
    /**
     * Apply window size
     * 
     * @param windowSize
     * @return 
     */
    @Override
    public AbstractAcquisitionPolicy withWindowSize(int windowSize) {
        this.windowSize = windowSize;
        return this;
    }
    
    
    /**
     * Apply pool size
     * 
     * @param poolSize
     * @return 
     */
    @Override
    public AbstractAcquisitionPolicy withPoolSize(int poolSize) {
        this.poolSize = poolSize;
        return this;
    }

    
    
    /**
     * Represent as JSON String
     * 
     * @return String
     */
    @Override
    public String toJsonString() {
        return ResourcePolicyMapper
            .toJsonResource(this)
        .toJson();
    }
    
    
    /**
     * Represent as JSON document
     * 
     * @return String
     */
    @Override
    public String toJsonDoc() {
        return ResourcePolicyMapper
            .toJsonResource(this)
        .toJsonDoc();
    }
    
    
    /**
     * Return whether targetted field has been set
     * 
     * @return booleam
     */
    @Override
    public boolean isTargeted() {
        return targetted;
    }

    
    /**
     * Return whether annotation string has been set
     * 
     * @return boolean
     */
    @Override
    public boolean isStringAnnotated() {
        return annoString;
    }

    
    /**
     * Return whether the annotation field has been set
     * 
     * @return boolean
     */
    @Override
    public boolean isCustomAnnotated() {
        return annotation;
    }

    
    /**
     * Get target field
     * 
     * @return String
     */
    @Override
    public String getTarget() {
        return target;
    }

    
    /**
     * Get {@link ItemState} field
     * 
     * @return {@link ItemState} 
     */
    @Override
    public ItemState getState() {
        return state;
    }

    
    /**
     * Get annotation key field
     * 
     * @return String
     */
    @Override
    public String getAnnoKey() {
        return annoKey;
    }

    
    /**
     * Get annotation value field
     * 
     * @return Object
     */
    @Override
    public Object getAnnoVal() {
        return annoVal;
    }
    

    /**
     * Get {@link CustomAnnotation} field
     * 
     * @return {@link CustomAnnotation}
     */
    @Override
    public CustomAnnotation getAnno() {
        return anno;
    }


    /**
     * Get window size
     * 
     * @return int
     */
    @Override
    public int getWindowSize() {
        return windowSize;
    }

    
    /**
     * Set window size
     * 
     * @param windowSize 
     */
    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }
    
    
    
    /**
     * Get window size
     * 
     * @return int
     */
    @Override
    public int getPoolSize() {
        return this.poolSize;
    }

    
    /**
     * Set window size
     * 
     * @param poolSize 
     */
    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
    
    
    /**
     * Get Id of acquisition policy
     * 
     * @return String
     */
    @Override
    public String getId() {
        return this.id;
    }
    
    
    /**
     * Get {@link AcquisitionPolicyMode}
     * 
     * @return {@link AcquisitionPolicyMode}
     */
    @Override
    public AcquisitionPolicyMode getPolicyMode() {
        return this.policyType;
    }
    
    
    /**
     * Build with light verification will add in error
     *  and logging for null target or state
     * 
     * @return AbstractAcquisitionPolicy
     */
    @Override
    public AbstractAcquisitionPolicy build() {
        if ( this.anno == null ) {
            this.anno = new CustomAnnotation();
        }
        
        if ( this.poolSize < 1 ) {
            this.poolSize = 1;
        }
        
        if ( this.windowSize < 1 ) {
            this.windowSize = 1;
        }
        
        return this;
    }
}