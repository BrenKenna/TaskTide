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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.tasktide.engine.policies.workflow.WorkflowStrategyType;


/**
 * Enum to support operations of valid {@link AbstractAcquisitionPolicy}
 *
 * @author Bren
 */
public enum AcquisitionPolicyMode {

    WORKFLOW {
        @Override
        public String toString() {
            return "Workflow";
        }

        @Override
        public boolean isAcquisitionPolicyMode(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isAcquisitionPolicyMode(AcquisitionPolicyMode query) {
            return this == query;
        }
    },

    TARGETED {
        @Override
        public String toString() {
            return "Targeted";
        }

        @Override
        public boolean isAcquisitionPolicyMode(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isAcquisitionPolicyMode(AcquisitionPolicyMode query) {
            return this == query;
        }
    };


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isAcquisitionPolicyMode(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isAcquisitionPolicyMode(AcquisitionPolicyMode query);


    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for (AcquisitionPolicyMode elm : values()) {
            if (elm.isAcquisitionPolicyMode(query)) {
                return elm.ordinal();
            }
        }
        return -1;
    }


    /**
     * Check if query maps to enum value
     *
     * @param query
     * @return boolean
     */
    public static boolean hasQuery(String query) {
        if (query == null) {
            return false;
        }

        for (AcquisitionPolicyMode elm : values()) {
            if (elm.isAcquisitionPolicyMode(query)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Map query to enum value
     *
     * @param query
     * @return AcquisitionPolicyMode
     */
    public static AcquisitionPolicyMode get(String query) {
        int ind = indexOf(query);
        if (ind >= 0) {
            return values()[ind];
        }
        return null;
    }


    /**
     * Represent enum as string
     *
     * @return String
     */
    public static String valuesString() {
        return Arrays.stream(values())
            .map(elm -> elm.name())
        .collect(Collectors.joining(","));
    }
    
    
    
    /**
     * Initialize {@link AbstractAcquisitionPolicy} for configured
     *  target
     * 
     * @param target
     * @param opts
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public static TaskTideWorkloadAcquisitionPolicy initBuilder(String target, Map<String, Object> opts) {
        
        // Pass if target is null
        if ( target == null ) {
            return null;
        }
        
        // Pass if target is an empty string
        if ( target.isEmpty() ) {
            return null;
        }
        
        // Parse stripped string
        target = target.replaceAll("[,;]$", "").strip();
        
        // Evaluate as a workflow acquisition policy
        if ( target.contains(",") || target.contains(";") ) {
            target = target.replace(";", ",");
            List<String> steps = List.of(target.split(","));
            String stratString = (String) opts.getOrDefault("Workflow Strategy", WorkflowStrategyType.SEQUENTIAL.name());
            WorkflowStrategyType workflowStrategy = WorkflowStrategyType.get(stratString);
            return WorkflowAcquisitionPolicy.newInstance(steps, workflowStrategy);
        }
        
        // Evaluate as targeted acquisition policy
        else {
            return TargetedAcquisitionPolicy.newInstance();
        }
    }
}