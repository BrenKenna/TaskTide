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
package org.tasktide.core.manager.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.manager.TaskTideServiceManager;


/**
 * Enum to suport {@link ManagerAction} over specific {@link TaskTideModel}.
 *  Exception is the {@link ManagerTask} value which goes towards {@link WorkItem}
 * 
 * @author bkenna
 */
public enum ManagerTarget {
    
    MANAGERTASK {
        @Override
        public boolean isManagerTarget(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerTarget(ManagerTarget query) {
            return this == query;
        }
        
        @Override
        public List<TaskTideModel> fetchModels() {
            return TaskTideServiceManager.fetchWorkItemService().viewAllToTaskTideModel();
        }

        @Override
        public String toString() {
            return name();
        }
    },

    WORKITEM {
        @Override
        public boolean isManagerTarget(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerTarget(ManagerTarget query) {
            return this == query;
        }
        
        @Override
        public List<TaskTideModel> fetchModels() {
            return TaskTideServiceManager.fetchWorkItemService().viewAllToTaskTideModel();
        }

        @Override
        public String toString() {
            return name();
        }
    },

    STEP {
        @Override
        public boolean isManagerTarget(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerTarget(ManagerTarget query) {
            return this == query;
        }
        
        @Override
        public List<TaskTideModel> fetchModels() {
            return TaskTideServiceManager.fetchStepService().viewAllToTaskTideModel();
        }

        @Override
        public String toString() {
            return name();
        }
    },

    WORKFLOW {
        @Override
        public boolean isManagerTarget(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerTarget(ManagerTarget query) {
            return this == query;
        }
        
        @Override
        public List<TaskTideModel> fetchModels() {
            return TaskTideServiceManager.fetchWorkflowService().viewAllToTaskTideModel();
        }
    
        @Override
        public String toString() {
            return name();
        }
    },
    
    JOB_ENVIRONMENT {
        @Override
        public boolean isManagerTarget(String query) {
            String tmp = this.toString().replace("_", query);
            query = query.strip().replace(" ", "");
            return tmp.equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerTarget(ManagerTarget query) {
            return this == query;
        }
        
        @Override
        public List<TaskTideModel> fetchModels() {
            return TaskTideServiceManager.fetchJobEnvironmentService().viewAllToTaskTideModel();
        }
    
        @Override
        public String toString() {
            return name();
        }
    },
    
    METRIC_DATA {
        @Override
        public boolean isManagerTarget(String query) {
            String tmp = this.toString().replace("_", query);
            query = query.strip().replace(" ", "");
            return tmp.equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerTarget(ManagerTarget query) {
            return this == query;
        }
        
        @Override
        public List<TaskTideModel> fetchModels() {
            return TaskTideServiceManager.fetchMetricDataService().viewAllToTaskTideModel();
        }
    
        @Override
        public String toString() {
            return name();
        }
    },
    
    METRIC_PROFILE {
        @Override
        public boolean isManagerTarget(String query) {
            String tmp = this.toString().replace("_", query);
            query = query.strip().replace(" ", "");
            return tmp.equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerTarget(ManagerTarget query) {
            return this == query;
        }
        
        @Override
        public List<TaskTideModel> fetchModels() {
            return TaskTideServiceManager.fetchMetricProfileService().viewAllToTaskTideModel();
        }
    
        @Override
        public String toString() {
            return name();
        }
    };

    
    /**
     * Check if value matches query
     * @param query
     * 
     * @return boolean 
     */
    public abstract boolean isManagerTarget(String query);
    
    
    /**
     * Check if value matches query
     * @param query
     * 
     * @return boolean 
     */
    public abstract boolean isManagerTarget(ManagerTarget query);
    
    
    /**
     * Fetch 
     * 
     * @return List-{@link TaskTideModel}
     */
    public abstract List<TaskTideModel> fetchModels();

    
    /**
     * Retrieve index of query
     * 
     * @param name
     * @return int
     */
    public static int indexOf(String name) {
        for (ManagerTarget managertarget : values() ) {
            if (managertarget.isManagerTarget(name)) {
                return managertarget.ordinal();
            }
        }
        return -1;
    }

    
    /**
     * Check whether enum has query
     * 
     * @param query
     * @return boolean
     */
    public static boolean hasQuery(String query) {
        if (query == null) {
            return false;
        }
        for (ManagerTarget managertarget : values()) {
            if (managertarget.isManagerTarget(query)) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Map query to {@link ManagerTarget}
     * 
     * @param query
     * @return {@link ManagerTarget}
     */
    public static ManagerTarget get(String query) {
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
}