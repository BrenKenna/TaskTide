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
package org.tasktide.core.model.task;

import org.tasktide.core.model.state_summary.StateSummaryType;
import org.tasktide.core.model.workitem.ItemState;


/**
 *
 * Enumeration of valid Task States: Pending, Active, Error, Complete
 * 
 * @author bkenna
 */
public enum TaskState implements StateSummaryType {
    
    PENDING {
        @Override
        public String toString() {
            return "Pending";
        }

        @Override
        public boolean isState(String query) {
            return "pending".equals( query.toLowerCase() );
        }

        @Override
        public boolean isState(TaskState taskState) {
            return PENDING == taskState;
        }
        
        @Override
        public ItemState mapToItemState() {
            return ItemState.TODO;
        }
    },
    
    ACTIVE {
        @Override
        public String toString() {
            return "Active";
        }

        @Override
        public boolean isState(String query) {
            return "active".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(TaskState taskState) {
            return ACTIVE == taskState;
        }
        
        @Override
        public ItemState mapToItemState() {
            return ItemState.LOCKED;
        }
    },
    
    ERROR {
        @Override
        public String toString() {
            return "Error";
        }

        @Override
        public boolean isState(String query) {
            return "error".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(TaskState taskState) {
            return ERROR == taskState;
        }
    
        @Override
        public ItemState mapToItemState() {
            return ItemState.ERROR;
        }
    },
    
    TIME_KEEPER {
        @Override
        public String toString() {
            return "Time Keeper";
        }

        @Override
        public boolean isState(String query) {
            query = query.toLowerCase().replace(" ", "").replace("_", "");
            return "timekeeper".equals(query);
        }

        @Override
        public boolean isState(TaskState taskState) {
            return TIME_KEEPER == taskState;
        }

        @Override
        public ItemState mapToItemState() {
            return ItemState.FOR_UNLOCK;
        }
    },
    
    COMPLETE {
        @Override
        public String toString() {
            return "Complete";
        }

        @Override
        public boolean isState(String query) {
            return "complete".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(TaskState taskState) {
            return COMPLETE == taskState;
        }
        
        @Override
        public ItemState mapToItemState() {
            return ItemState.DONE;
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
     * Compare to queried state string
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isState(String query);
    
    
    /**
     * Compare to queried state
     * 
     * @param taskState
     * @return boolean
     */
    public abstract boolean isState(TaskState taskState);
    
    
    /**
     * Map TaskState to an ItemState
     * 
     * @return {@link ItemState}
     */
    public abstract ItemState mapToItemState();
    
    
    /**
     * Return index of queried state
     * 
     * @param query
     * @return int
     */
    public static int indexOf(String query) {
        
        // Initialize values
        int stateInd = -1;
        int limit = TaskState.values().length;
        int counter = 0;
        
        // Search until found
        while ( counter <= limit && stateInd < 0 ) {
            TaskState taskState = TaskState.values()[counter];
            if ( taskState.isState(query) ) {
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
