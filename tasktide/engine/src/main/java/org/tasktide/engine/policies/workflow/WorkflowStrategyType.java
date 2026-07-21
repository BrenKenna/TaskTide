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
        public WorkflowAcquisitionStrategy makeStrategy() {
            return new SequentialWorkflowStrategy();
        }
    },

    ROUND_ROBIN {
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
        public WorkflowAcquisitionStrategy makeStrategy() {
            return new RoundRobinWorkflowStrategy();
        }
    };
    
    
    /**
     * {@link WorkflowAcquisitionStrategy} factory method
     * 
     * @return {@link WorkflowAcquisitionStrategy}
     */
    public abstract WorkflowAcquisitionStrategy makeStrategy();


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
     * @return {@link WorkflowAcquisitionStrategy}
     */
    public static WorkflowAcquisitionStrategy getStrategy(boolean shouldCycle) {
        if ( shouldCycle ) {
            return new RoundRobinWorkflowStrategy();
        }
        
        else {
            return new SequentialWorkflowStrategy();
        }
    }
}