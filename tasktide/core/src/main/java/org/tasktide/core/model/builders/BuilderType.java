/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.model.builders;

/**
 *
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
            return null;
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
            return null;
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
