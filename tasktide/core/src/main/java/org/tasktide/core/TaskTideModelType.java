/*
 * Copyright 2025 Brendan Kenna.
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


/**
 * Enum of valid model types
 * 
 * @author bkenna
 */
public enum TaskTideModelType {
    
    WORKITEM {
        @Override
        public boolean isModel(String query) {
            return "workitme".equals(query.toLowerCase());
        }

        @Override
        public boolean isModel(TaskTideModelType query) {
            return WORKITEM == query;
        }

        @Override
        public String toString() {
            return "WorkItem";
        }
    },
    
    STEP {
        @Override
        public boolean isModel(String query) {
            return "step".equals(query.toLowerCase());
        }

        @Override
        public boolean isModel(TaskTideModelType query) {
            return STEP == query;
        }

        @Override
        public String toString() {
            return "Step";
        }
    },
    
    WORKFLOW {
        @Override
        public boolean isModel(String query) {
            return "Workflow".equals(query.toLowerCase());
        }

        @Override
        public boolean isModel(TaskTideModelType query) {
            return WORKFLOW == query;
        }

        @Override
        public String toString() {
            return "Workflow";
        }
    };
    
    
    /**
     * Compare to queried model string
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isModel(String query);
    
    
    /**
     * Compare to queried to model
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isModel(TaskTideModelType query);

    
    
    /**
     * Represent value as a string
     * 
     * @return String
     */
    @Override
    public abstract String toString();
    
    
    /**
     * Return index of queried model
     * 
     * @param query
     * @return int
     */
    public static int indexOf(String query) {
        
        // Initialize values
        int modelInd = -1;
        int limit = TaskTideModelType.values().length;
        int counter = 0;
        
        // Search until found
        while ( counter <= limit && modelInd < 0 ) {
            TaskTideModelType modelType = TaskTideModelType.values()[counter];
            if ( modelType.isModel(query) ) {
                modelInd = counter;
            }
            else {
                counter++;
            }
        }
        
        // Return search result
        return modelInd;
    }
    
    
    /**
     * Check if queried type exists
     * 
     * @param query
     * @return boolean
     */
    public static boolean hasType(String query) {
        return indexOf(query) >= 0;
    }
    
    
    /**
     * Return the TaskTide model type for query or null
     * 
     * @param query
     * @return {@link TaskTideModelType}
     */
    public static TaskTideModelType getQuery(String query) {
        int index = indexOf(query);
        if ( index >= 0) {
            return TaskTideModelType.values()[index];
        }
        else {
            return null;
        }
    }
}