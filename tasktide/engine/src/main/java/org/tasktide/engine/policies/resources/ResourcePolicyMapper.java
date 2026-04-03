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
package org.tasktide.engine.policies.resources;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.supporting.Utils;

import org.tasktide.engine.policies.WorkItemAcquisitionPolicy;
import org.tasktide.engine.policies.AbstractAcquisitionPolicy;
import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;


/**
 * Mapper to for converting {@link AcquisitionPolicyJsonResource}
 *  to/from {@link TaskTideWorkloadAcquisitionPolicy}. So that
 *  these can be persisted, represented in logging
 *
 * @author Bren
 */
public class ResourcePolicyMapper {
    
    
    /**
     * Fetch {@link AcquisitionPolicyJsonResource} representation
     *  of {@link AbstractAcquisitionPolicy}
     * 
     * @param <T> of {@link TaskTideModel}
     * @param policy
     * @return AcquisitionPolicyJsonResource of {@link TaskTideModel}
     */
    public static
        <T extends TaskTideModel<T>> AcquisitionPolicyJsonResource<T>
    toJsonResource(AbstractAcquisitionPolicy<T> policy) {
        
        // Sets policy Id
        String policyId = Utils.getRandomUUID();
        
        // Returns policy as resouce
        return new AcquisitionPolicyJsonResource<>(
            policyId,
            policy.getClassRef().getSimpleName(),
            policy.getTarget(),
            policy.getState(),
            policy.getAnnoKey(),
            policy.getAnnoVal(),
            policy.getAnno(),
            policy.isTargetted(),
            policy.isStringAnnotated(),
            policy.isCustomAnnotated()
        );
    }
    
    
    /**
     * Fetch concrete {@link TaskTideWorkloadAcquisitionPolicy}
     *  from provided {@link AcquisitionPolicyJsonResource} and 
     *  ResourcePolicyModelType for valid {@link TaskTideModel}
     * 
     * @param <T> of {@link TaskTideModel}
     * @param resource
     * @param type
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public static
        <T extends TaskTideModel<T>> TaskTideWorkloadAcquisitionPolicy
    fromJsonResource( AcquisitionPolicyJsonResource resource, ResourcePolicyModelType type) {
        switch ( type ) {
        
            case WORKITEM -> {
                return WorkItemAcquisitionPolicy
                    .newInstance()
                    .withTarget(resource.getTarget())
                    .withItemState(resource.getState())
                    .withAnno(resource.getAnno())
                    .withAnno(resource.getAnnoKey(), resource.getAnnoVal());
            }
            
            default -> {
                return null;
            }
        }
    }
}