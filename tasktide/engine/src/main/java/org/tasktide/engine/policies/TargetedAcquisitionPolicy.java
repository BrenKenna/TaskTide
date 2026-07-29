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
import java.util.ArrayList;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.workitem.ItemState;


/**
 * Acquisition policy targeting a single collection of {@link WorkItem}
 * 
 * @author Bren
 */
public class TargetedAcquisitionPolicy extends AbstractAcquisitionPolicy {
    
    
    /**
     * Construct {@link TargetedAcquisitionPolicy}
     * 
     * @param policyType
     * @param target
     * @param state
     * @param windowSize
     * @param poolSize
     * @param iterationLimit 
     */
    TargetedAcquisitionPolicy(
        String target,
        ItemState state,
        int windowSize,
        int poolSize,
        int iterationLimit
    ) {
        super(AcquisitionPolicyMode.TARGETED, target, state, windowSize, poolSize, iterationLimit);
    }
    
    
    /**
     * Checks whether there are active tasks
     * 
     * @return boolean
     */
    @Override
    public boolean hasNext() {
        
        if ( this.iterationLimit > 1 ) {
            return this.counter < this.iterationLimit;
        }
        
        return !this.fetchWorkload().isEmpty();
    }
    
    
    /**
     * Build {@link WorkItem} from {@link TargetedAcquisitionPolicy}
     * 
     * @return List-{@link WorkItem}
     */
    @Override
    public List<WorkItem> fetchWorkload() {
    
        // Fetch by annotation
        if ( this.isCustomAnnotated() ) {
            if ( this.isTargeted() ) {
                return new ArrayList<>(TaskTideServiceManager
                    .fetchWorkItemService()
                    .getRepo()
                .findByFieldForGroupWithAnno(
                    "itemState", this.getState(),
                    "stepName", this.getTarget(),
                    this.getAnno()
                ));
            }
        }
        
        // Fetch by annotation string
        else if ( this.isStringAnnotated() ) {
            if ( this.isTargeted() ) {
                return new ArrayList<>(TaskTideServiceManager
                    .fetchWorkItemService()
                    .getRepo()
                .findByFieldForGroupWithAnno(
                    "itemState", this.getState(),
                    "stepName", this.getTarget(),
                    this.getAnnoKey(), this.getAnnoVal()
                ));
            }
        }
        
        // Fetch collection
        else {
            if ( this.isTargeted() ) {
                return new ArrayList<>(TaskTideServiceManager
                    .fetchWorkItemService()
                .viewByFieldForGroup(
                    "itemState", this.getState(),
                    "stepName", this.getTarget()
                ));
            }
        }
        
        // Otherwise empty list
        return new ArrayList<>();
    }
    
    
    /**
     * Clone active {@link TargetedAcquisitionPolicy}
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    @Override
    public TaskTideWorkloadAcquisitionPolicy clonePolicy() {
        return new AcquisitionPolicyBuilder(AcquisitionPolicyMode.TARGETED)
            .withAnno(this.getAnno())
            .withItemState(this.getState())
            .withTarget(this.getTarget())
            .withPoolSize(this.poolSize)
            .withWindowSize(this.windowSize)
        .build();
    }

    
    /**
     * Returns false
     * 
     * @return boolean
     */
    @Override
    public boolean workflowMode() {
        return false;
    }
}