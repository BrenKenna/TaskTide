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

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.workitem.ItemState;


/**
 * Abstract implementation of {@link TaskTideWorkloadAcquisitionPolicy}
 *  to take the field policy building, and field getter aspects.
 *  Preserves generic {@link TaskTideModel} type to allow implementing
 *  class to focus on the specifics of building/fetching the
 *  required {@link TaskTideModel} collection
 *
 * @param <T> of {@link TaskTideModel}
 * @author Bren
 */
public abstract class AbstractAcquisitionPolicy<T extends TaskTideModel<T>> implements TaskTideWorkloadAcquisitionPolicy<T> {
    
    // Attributes for which builder
    private boolean targetted, annoString, annotation;

    // Field properties
    private String target = "";
    private ItemState state = ItemState.TODO;
    private String annoKey;
    private Object annoVal;
    private CustomAnnotation anno;
    
    
    /**
     * Building an acquisition policy with collection
     * 
     * @param target
     * @return {@link  AbstractAcquisitionPolicy}
     */
    @Override
    public AbstractAcquisitionPolicy<T> withTarget(String target) {
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
    public  AbstractAcquisitionPolicy<T> withItemState(ItemState state) {
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
    public  AbstractAcquisitionPolicy<T> withAnno(String key, Object val) {
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
    public  AbstractAcquisitionPolicy<T> withAnno(CustomAnnotation anno) {
        this.anno = anno;
        this.annotation = true;
        return this;
    }
    
    
    /**
     * Return whether targetted field has been set
     * 
     * @return booleam
     */
    @Override
    public boolean isTargetted() {
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
}