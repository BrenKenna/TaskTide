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
package org.tasktide.engine.policies.workflow;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * Enum to support operations over valid {@link WorkflowAcquisitionStrategy}
 *  instances, and is the interface for initializing their strategic building
 *
 * @author Bren
 */
public enum WorkflowStrategyType {

    SEQUENTIAL {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isWorkflowStrategyType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isWorkflowStrategyType(WorkflowStrategyType query) {
            return this == query;
        }
        
        @Override
        public WorkflowStrategyBuilder initializeStrategyBuilder() {
            return new WorkflowStrategyBuilder(this);
        }
    },

    ROUND_ROBIN {
        @Override
        public String toString() {
            return "Round Robin";
        }

        @Override
        public boolean isWorkflowStrategyType(String query) {
            query = query.toLowerCase()
               .replace("_", " ")
               .replace("roundrobin", "round robin")
            ;
            return "Round Robin".equalsIgnoreCase(query);
        }

        @Override
        public boolean isWorkflowStrategyType(WorkflowStrategyType query) {
            return this == query;
        }
        
        @Override
        public WorkflowStrategyBuilder initializeStrategyBuilder() {
            return new WorkflowStrategyBuilder(this);
        }
    };
    
    
    /**
     * {@link WorkflowAcquisitionStrategy} factory method
     * 
     * @return {@link WorkflowStrategyBuilder}
     */
    public abstract WorkflowStrategyBuilder initializeStrategyBuilder();



    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isWorkflowStrategyType(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isWorkflowStrategyType(WorkflowStrategyType query);


    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for (WorkflowStrategyType elm : values()) {
            if (elm.isWorkflowStrategyType(query)) {
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

        for (WorkflowStrategyType elm : values()) {
            if (elm.isWorkflowStrategyType(query)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Map query to enum value
     *
     * @param query
     * @return WorkflowStrategyType
     */
    public static WorkflowStrategyType get(String query) {
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
     * Make either {@link RoundRobinWorkflowStrategy}, or
     *  {@link SequentialWorkflowStrategy} depending on
     *  whether the engine should cycle through steps
     * 
     * @param shouldCycle
     * @return {@link WorkflowStrategyBuilder}
     */
    public static WorkflowStrategyBuilder getStrategy(boolean shouldCycle) {
        
        // Initialize round robin scanner if strategy if cycling is required
        if ( shouldCycle ) {
            return new WorkflowStrategyBuilder(WorkflowStrategyType.ROUND_ROBIN)
                .withStrategyMode(WorkflowStrategyMode.SCANNER);
        }
        
        // Otherwise use sequential exhaustion
        else {
            return new WorkflowStrategyBuilder(WorkflowStrategyType.SEQUENTIAL)
                .withStrategyMode(WorkflowStrategyMode.EXHAUST);
        }
    }
}