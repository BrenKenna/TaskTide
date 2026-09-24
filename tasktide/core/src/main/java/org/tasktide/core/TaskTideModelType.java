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

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;

import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricProfile;


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
        public boolean isClassName(String query) {
            return WorkItem
                .class
                .getSimpleName()
            .equals(query);
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
        public boolean isClassName(String query) {
            return Step
                .class
                .getSimpleName()
            .equals(query);
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
        public boolean isClassName(String query) {
            return Workflow
                .class
                .getSimpleName()
            .equals(query);
        }

        @Override
        public String toString() {
            return "Workflow";
        }
    },
    
    JOB_ENVIRONMENT {
        @Override
        public boolean isModel(String query) {
            return "JobEnvironment".equals(query.toLowerCase());
        }

        @Override
        public boolean isModel(TaskTideModelType query) {
            return JOB_ENVIRONMENT == query;
        }
        
        @Override
        public boolean isClassName(String query) {
            return JobEnvironment
                .class
                .getSimpleName()
            .equals(query);
        }

        @Override
        public String toString() {
            return "JobEnvironment";
        }
    },
    
    METRIC_DATA {
        @Override
        public boolean isModel(String query) {
            return "MetricData".equals(query.toLowerCase());
        }

        @Override
        public boolean isModel(TaskTideModelType query) {
            return METRIC_DATA == query;
        }
        
        @Override
        public boolean isClassName(String query) {
            return MetricData
                .class
                .getSimpleName()
            .equals(query);
        }

        @Override
        public String toString() {
            return "MetricData";
        }
    },
    
    METRIC_PROFILE {
        @Override
        public boolean isModel(String query) {
            return "MetricProfile".equals(query.toLowerCase());
        }

        @Override
        public boolean isModel(TaskTideModelType query) {
            return METRIC_PROFILE == query;
        }
        
        @Override
        public boolean isClassName(String query) {
            return MetricProfile
                .class
                .getSimpleName()
            .equals(query);
        }

        @Override
        public String toString() {
            return "MetricProfile";
        }
    };
    
    
    /**
     * Checks whether queried string matches
     *  the simple class
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isClassName(String query);
    
    
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