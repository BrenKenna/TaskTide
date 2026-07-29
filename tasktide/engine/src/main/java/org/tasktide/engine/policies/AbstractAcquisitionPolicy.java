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

    // Field properties
    private String target;
    private ItemState state = ItemState.TODO;
    private String annoKey;
    private Object annoVal;
    private CustomAnnotation anno;
    protected int windowSize, poolSize;
    protected int iterationLimit = -1, counter = 0;
    
    
    /**
     * Construct with policy type
     * 
     * @param policyType 
     */
    AbstractAcquisitionPolicy(AcquisitionPolicyMode policyType) {
        this.policyType = policyType;
        this.id = policyType.toString() + UUID.randomUUID().toString();
    }

    
    /**
     * 
     * @param policyType
     * @param target
     * @param state
     * @param windowSize
     * @param poolSize
     * @param iterationLimit 
     */
    AbstractAcquisitionPolicy(
        AcquisitionPolicyMode policyType,
        String target,
        ItemState state,
        int windowSize,
        int poolSize,
        int iterationLimit
    ) {
        this.policyType = policyType;
        this.id = policyType.toString() + UUID.randomUUID().toString();
        
        this.target = target;
        this.state = state;
        
        this.windowSize = windowSize;
        this.poolSize = poolSize;
        this.iterationLimit = iterationLimit;
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
     * Set {@link ItemState} field
     * 
     * @param state
     */
    @Override
    public void setState(ItemState state) {
        this.state = state;
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
     * Set annotation key-value
     * 
     * @param key
     * @param value 
     */
    @Override
    public void setAnnoKeyValue(String key, Object value) {
        this.annoKey = key;
        this.annoVal = value;
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
     * Set {@link CustomAnnotation} field
     * 
     */
    @Override
    public void setAnno(CustomAnnotation anno) {
        this.anno = anno;
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
     * Get iteration limit
     * 
     * @return int
     */
    @Override
    public int getIterationLimit() {
        return this.iterationLimit;
    }

    
    /**
     * Set iteration limit
     * 
     * @param iterationLimit 
     */
    @Override
    public void setIterationLimit(int iterationLimit) {
        this.iterationLimit = iterationLimit;
    }
    
    /**
     * Return whether targetted field has been set
     * 
     * @return booleam
     */
    @Override
    public boolean isTargeted() {
        return this.target != null;
    }

    
    /**
     * Return whether annotation string has been set
     * 
     * @return boolean
     */
    @Override
    public boolean isStringAnnotated() {
        return this.annoKey != null && this.annoVal != null;
    }

    
    /**
     * Return whether the annotation field has been set
     * 
     * @return boolean
     */
    @Override
    public boolean isCustomAnnotated() {
        return this.anno != null;
    }
}