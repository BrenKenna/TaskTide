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
package org.tasktide.core.model.builders;

import org.tasktide.core.model.CustomAnnotation;


/**
 * Enumeration fo valid builder types
 * 
 * @author bkenna
 */
public enum BuilderType {
    
    PROCESS_LOG {
        @Override
        public String toString() {
            return "Process_Log";
        }

        @Override
        public boolean isType(String query) {
            return "process_log".equals(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return PROCESS_LOG == builderType;
        }

        @Override
        public ModelBuilder makeBuilder() {
            return new ProcessLogBuilder();
        }
    
    },
    
    TASK_LOGGING {
        @Override
        public String toString() {
            return "Task_Logging";
        }

        @Override
        public boolean isType(String query) {
            return "task_logging".equals(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return TASK_LOGGING == builderType;
        }
        
        @Override
        public ModelBuilder makeBuilder() {
            return new TaskLoggingBuilder();
        }
    },
    
    ITEM_TASK {
        @Override
        public String toString() {
            return "Item_Task";
        }

        @Override
        public boolean isType(String query) {
            return "item_task".equals(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return ITEM_TASK == builderType;
        }
        
        @Override
        public ModelBuilder makeBuilder() {
            return new ItemTaskBuilder();
        }
    },
    
    WORK_ITEM {
        @Override
        public String toString() {
            return "Work_Item";
        }

        @Override
        public boolean isType(String query) {
            return "work_item".equals(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return WORK_ITEM == builderType;
        }
        
        @Override
        public ModelBuilder makeBuilder() {
            return new WorkItemBuilder();
        }
    },
    
    WORKLOAD {
        @Override
        public String toString() {
            return "Workload";
        }

        @Override
        public boolean isType(String query) {
            return "workload".equals(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return WORKLOAD == builderType;
        }
        
        @Override
        public ModelBuilder makeBuilder() {
            return new WorkloadBuilder();
        }
    },
    
    STEP {
        @Override
        public String toString() {
            return "Step";
        }

        @Override
        public boolean isType(String query) {
            return "step".equals(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return STEP == builderType;
        }

        @Override
        public ModelBuilder makeBuilder() {
            return new StepBuilder();
        }
    },
    
    WORKFLOW {
        @Override
        public String toString() {
            return "Workflow";
        }

        @Override
        public boolean isType(String query) {
            return "workflow".equals(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return WORKFLOW == builderType;
        }

        @Override
        public ModelBuilder makeBuilder() {
            return new WorkflowBuilder();
        }
    },
    
    JOBENVIRONMENT {
        @Override
        public String toString() {
            return this.name();
        }

        @Override
        public boolean isType(String query) {
            return this.name().equalsIgnoreCase(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return this == builderType;
        }

        @Override
        public ModelBuilder makeBuilder() {
            return new JobEnvironmentBuilder();
        }
    },
    
    METRICDATA {
        @Override
        public String toString() {
            return this.name();
        }

        @Override
        public boolean isType(String query) {
            return this.name().equalsIgnoreCase(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return this == builderType;
        }

        @Override
        public ModelBuilder makeBuilder() {
            return new MetricDataBuilder();
        }
    },
    
    PROFILEDATA {
        @Override
        public String toString() {
            return this.name();
        }

        @Override
        public boolean isType(String query) {
            return this.name().equalsIgnoreCase(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return this == builderType;
        }

        @Override
        public ModelBuilder makeBuilder() {
            return new ProfileDataBuilder();
        }
    },
    
    METRICPROFILE {
        @Override
        public String toString() {
            return this.name();
        }

        @Override
        public boolean isType(String query) {
            return this.name().equalsIgnoreCase(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return this == builderType;
        }

        @Override
        public ModelBuilder makeBuilder() {
            return new MetricProfileBuilder();
        }
    },
    
    CUSTOMANNOTATION {
        @Override
        public String toString() {
            return this.name();
        }

        @Override
        public boolean isType(String query) {
            return this.name().equalsIgnoreCase(query.toLowerCase());
        }

        @Override
        public boolean isType(BuilderType builderType) {
            return this == builderType;
        }

        @Override
        public ModelBuilder makeBuilder() {
            return new CustomAnnotationBuilder();
        }
    };
    
    
    /**
     * Represent enum as string
     * 
     * @return String 
     */
    @Override
    public abstract String toString();
    
    
    /**
     * Compare to queried builderType string
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isType(String query);
    
    
    /**
     * Compare to queried state
     * 
     * @param builderType
     * @return boolean
     */
    public abstract boolean isType(BuilderType builderType);
    
    
    /**
     * Make builder
     * 
     * @return {@link ModelBuilder}
     */
    public abstract ModelBuilder makeBuilder();
    
    
    /**
     * Return index of queried type
     * 
     * @param query
     * @return int
     */
    public static int indexOf(String query) {
        
        // Initialize values
        int stateInd = -1;
        int limit = BuilderType.values().length;
        int counter = 0;
        
        // Search until found
        while ( counter <= limit && stateInd < 0 ) {
            BuilderType builderType = BuilderType.values()[counter];
            if ( builderType.isType(query) ) {
                stateInd = counter;
            }
            else {
                counter++;
            }
        }
        
        // Return search result
        return stateInd;
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
}