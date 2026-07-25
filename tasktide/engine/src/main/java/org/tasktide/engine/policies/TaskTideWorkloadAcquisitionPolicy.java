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

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.workitem.ItemState;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Interface for defining policies for consuming workloads.
 * <br>
 * All policies resolve to a {@link WorkItem}, some may only
 *  target a specific step/task collection, or target a set
 *  of steps for a workflow
 * <br>
 * Leaving the definition of the target up to the user,
 *  provides a much broader and generic approach to DAG
 *  scheduling then explicitly taking this on
 *
 * @author Bren
 */
public interface TaskTideWorkloadAcquisitionPolicy {
    
    
    /**
     * Fetch/build {@link TaskTideModel} workload from {@link TaskTideServiceManager}
     *  from built query
     * 
     * @return List-{@link WorkItem}
     */
    public List<WorkItem> fetchWorkload();
    
    
    /**
     * Checks whether there are active tasks
     * 
     * @return boolean
     */
    public boolean hasNext();
    
    
    /**
     * Build query with {@link CustomAnnotation} for early task
     *  binding semantics with Pilot Label for example
     * 
     * @param anno
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public TaskTideWorkloadAcquisitionPolicy withAnno(CustomAnnotation anno);
    
    
    /**
     * Build query with {@link CustomAnnotation} for early task
     *  binding semantics with Pilot Label for example
     * 
     * @param key
     * @param val
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public TaskTideWorkloadAcquisitionPolicy withAnno(String key, Object val);
    
    
    /**
     * Build with {@link ItemState}
     * 
     * @param state
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public TaskTideWorkloadAcquisitionPolicy withItemState(ItemState state);
    
    
    /**
     * Building an acquisition policy with collection
     * 
     * @param target
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public TaskTideWorkloadAcquisitionPolicy withTarget(String target);
    
    
    /**
     * Build acquisition policy with provided window size
     * 
     * @param windowSize
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public TaskTideWorkloadAcquisitionPolicy withWindowSize(int windowSize);
    
    
    /**
     * Build acquisition policy with provided pool size
     * 
     * @param poolSize
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public TaskTideWorkloadAcquisitionPolicy withPoolSize(int poolSize);
    
    
    /**
     * Build acquisition policy
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public TaskTideWorkloadAcquisitionPolicy build();
    
    
    /**
     * Represent as JSON string
     * 
     * @return String
     */
    public String toJsonString();
    
    
    /**
     * Represent as JSON document
     * 
     * @return String
     */
    public String toJsonDoc();
    
    
    /**
     * Return whether targetted field has been set
     * 
     * @return booleam
     */
    public boolean isTargeted();

    
    /**
     * Return whether annotation string has been set
     * 
     * @return boolean
     */
    public boolean isStringAnnotated();

    
    /**
     * Return whether the annotation field has been set
     * 
     * @return boolean
     */
    public boolean isCustomAnnotated();

    
    /**
     * Get target field
     * 
     * @return String
     */
    public String getTarget();

    
    /**
     * Get {@link ItemState} field
     * 
     * @return {@link ItemState} 
     */
    public ItemState getState();

    
    /**
     * Get annotation key field
     * 
     * @return String
     */
    public String getAnnoKey();

    
    /**
     * Get annotation value field
     * 
     * @return Object
     */
    public Object getAnnoVal();
    

    /**
     * Get {@link CustomAnnotation} field
     * 
     * @return {@link CustomAnnotation}
     */
    public CustomAnnotation getAnno();
    
    
    /**
     * Get window size
     * 
     * @return int
     */
    public int getWindowSize();
    
    
    /**
     * Get pool size
     * 
     * @return int
     */
    public int getPoolSize();
    
    
    /**
     * Get Id
     * 
     * @return String
     */
    public String getId();
    
    
    /**
     * Get {@link AcquisitionPolicyMode}
     * 
     * @return {@link AcquisitionPolicyMode}
     */
    public AcquisitionPolicyMode getPolicyMode();
}