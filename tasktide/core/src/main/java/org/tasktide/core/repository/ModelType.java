/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.repository;


/**
 * Enum of valid model types
 * 
 * @author bkenna
 */
public enum ModelType {
    
    WORKITEM {
        @Override
        public boolean isModel(String query) {
            return "workitme".equals(query.toLowerCase());
        }

        @Override
        public boolean isModel(ModelType query) {
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
        public boolean isModel(ModelType query) {
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
        public boolean isModel(ModelType query) {
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
    public abstract boolean isModel(ModelType query);

    
    
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
        int limit = ModelType.values().length;
        int counter = 0;
        
        // Search until found
        while ( counter <= limit && modelInd < 0 ) {
            ModelType modelType = ModelType.values()[counter];
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
}
