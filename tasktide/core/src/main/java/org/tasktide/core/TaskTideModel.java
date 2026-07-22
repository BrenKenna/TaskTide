/*
 * Copyright 2026 Brendan Kenna.
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
package org.tasktide.core;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;


/**
 * Interface to support methods around model classes
 * 
 * @param <T> of {@link WorkItem},{@link Step},{@link Workflow}
 * @author bkenna
 */
public interface TaskTideModel<T extends TaskTideModel<T>> {
    
    
    /**
     * Represent model as JsonString
     * 
     * @return String
     */
    public String toJson();
    
    
    /**
     * Represent model as JsonDoc
     * 
     * @return String
     */
    public String toJsonDoc();
    
    
    /**
     * Get Id from model
     * 
     * @return String
     */
    public String getId();

    
    /**
     * Return current of model
     * 
     * @return String
     */
    public String getState();
    
    
    /**
     * Get the value of field
     * 
     * @param field
     * @return Object
     */
    public Object getValueFromField(String field);
    
    
    /**
     * Fetch collection model is, or is part of
     * 
     * @return String
     */
    public String getCollection();
    
    
    /**
     * Reset model state
     */
    public void resetModel();
    
    
    /**
     * Get {@link CustomAnnotation} property
     * 
     * @return {@link CustomAnnotation}
     */
    public CustomAnnotation getAnnotations();
    
    
    /**
     * Sets {@link CustomAnnotation} property
     * 
     * @param anno 
     */
    public void setAnnotations(CustomAnnotation anno);
}