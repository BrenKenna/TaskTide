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


/**
 * Acquisition policy targetting a single collection of {@link WorkItem}
 * 
 * @author Bren
 */
public class TargetedAcquisitionPolicy extends AbstractAcquisitionPolicy {
    
    
    /**
     * Constructs with random UUID
     */
    TargetedAcquisitionPolicy() {
        super(AcquisitionPolicyMode.TARGETED);
    }
    
    
    /**
     * Static initializer for {@link TargetedAcquisitionPolicy}
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public static TaskTideWorkloadAcquisitionPolicy newInstance() {
        TargetedAcquisitionPolicy pol = new TargetedAcquisitionPolicy();
        return pol;
    }
    
    
    /**
     * Checks whether there are active tasks
     * 
     * @return boolean
     */
    @Override
    public boolean hasNext() {
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
                return TaskTideServiceManager
                    .fetchWorkItemService()
                    .getRepo()
                .findByFieldForGroupWithAnno(
                    "itemState", this.getState(),
                    "stepName", this.getTarget(),
                    this.getAnno()
                );
            }
        }
        
        // Fetch by annotation string
        else if ( this.isStringAnnotated() ) {
            if ( this.isTargeted() ) {
                return TaskTideServiceManager
                    .fetchWorkItemService()
                    .getRepo()
                .findByFieldForGroupWithAnno(
                    "itemState", this.getState(),
                    "stepName", this.getTarget(),
                    this.getAnnoKey(), this.getAnnoVal()
                );
            }
        }
        
        // Fetch collection
        else {
            if ( this.isTargeted() ) {
                return TaskTideServiceManager
                    .fetchWorkItemService()
                .viewByFieldForGroup(
                    "itemState", this.getState(),
                    "stepName", this.getTarget()
                );    
            }
        }
        
        // Otherwise empty list
        return new ArrayList<>();
    }
}