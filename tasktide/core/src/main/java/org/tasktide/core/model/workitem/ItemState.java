/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.model.workitem;

import org.tasktide.core.model.state_summary.StateSummaryType;
import org.tasktide.core.model.task.TaskState;


/**
 * 
 * Enumeration of valid item states of ToDo, Locked, Done, Error
 * 
 * @author bkenna
 */
public enum ItemState implements StateSummaryType {
    
    TODO {
        @Override
        public boolean isState(String query) {
            return "todo".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(ItemState query) {
            return TODO == query;
        }

        @Override
        public String toString() {
            return "ToDo";
        }

        @Override
        public TaskState mapToTaskState() {
            return TaskState.PENDING;
        }
    },
    
    LOCKED {
        @Override
        public boolean isState(String query) {
            return "locked".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(ItemState query) {
            return LOCKED == query;
        }

        @Override
        public String toString() {
            return "Locked";
        }
        
        @Override
        public TaskState mapToTaskState() {
            return TaskState.ACTIVE;
        }
    },
    
    DONE {
        @Override
        public boolean isState(String query) {
            return "done".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(ItemState query) {
            return DONE == query;
        }

        @Override
        public String toString() {
            return "Done";
        }
    
        @Override
        public TaskState mapToTaskState() {
            return TaskState.COMPLETE;
        }
    },
    
    ERROR {
        @Override
        public boolean isState(String query) {
            return "error".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(ItemState query) {
            return ERROR == query;
        }

        @Override
        public String toString() {
            return "Error";
        }
        
        @Override
        public TaskState mapToTaskState() {
            return TaskState.ERROR;
        }
    },
    
    FOR_UNLOCK {
        @Override
        public boolean isState(String query) {
            query = query.toLowerCase().replace(" ", "").replace("_", "");
            return "forunlock".equals(query);
        }

        @Override
        public boolean isState(ItemState query) {
            return FOR_UNLOCK == query;
        }

        @Override
        public TaskState mapToTaskState() {
            return TaskState.TIME_KEEPER;
        }

        @Override
        public String toString() {
            return "For Unlock";
        }
    };
    
    
    /**
     * Compare to queried state string
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isState(String query);
    
    
    /**
     * Compare to queried to state
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isState(ItemState query);
    
    
    /**
     * Map ItemState to a TaskState
     * 
     * @param task
     * @return TaskState
     */
    public abstract TaskState mapToTaskState();
    
    
    /**
     * Represent value as a string
     * 
     * @return String
     */
    @Override
    public abstract String toString();
    
    
    /**
     * Return index of queried state
     * 
     * @param query
     * @return int
     */
    public static int indexOf(String query) {
        
        // Initialize values
        int stateInd = -1;
        int limit = ItemState.values().length;
        int counter = 0;
        
        // Search until found
        while ( counter <= limit && stateInd < 0 ) {
            ItemState itemState = ItemState.values()[counter];
            if ( itemState.isState(query) ) {
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
