/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

package org.tasktide.core.manager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;


/**
 * Enum to suport {@link ManagerAction} over specific {@link TaskTideModel}
 * 
 * @author bkenna
 */
public enum ManagerTarget {

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
        public List<TaskTideModel> fetchModels(TaskTideServiceManager servManager) {
            return servManager.getWorkItemService().viewAllToTaskTideModel();
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
        public List<TaskTideModel> fetchModels(TaskTideServiceManager servManager) {
            return servManager.getStepService().viewAllToTaskTideModel();
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
        public List<TaskTideModel> fetchModels(TaskTideServiceManager servManager) {
            return servManager.getWorkflowService().viewAllToTaskTideModel();
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
     * @param servManager
     * @return 
     */
    public abstract List<TaskTideModel> fetchModels(TaskTideServiceManager servManager);

    
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