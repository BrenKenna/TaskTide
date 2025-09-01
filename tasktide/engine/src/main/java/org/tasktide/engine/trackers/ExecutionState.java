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
package org.tasktide.engine.trackers;


/**
 * Enum to support worker execution states
 * 
 * @author bkenna
 */
public enum ExecutionState {
    
    QUEUED {
        @Override
        public boolean isState(String query) {
            return "queued".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(ExecutionState query) {
            return QUEUED == query;
        }

        @Override
        public String toString() {
            return "Queued";
        }
    },
    
    PREPARE {
        @Override
        public boolean isState(String query) {
            return "prepare".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(ExecutionState query) {
            return PREPARE == query;
        }

        @Override
        public String toString() {
           return "Prepare";
        }
    },
    
    LOCKED {
        @Override
        public boolean isState(String query) {
            return "locked".equals( query.toLowerCase() );
        }

        @Override
        public boolean isState(ExecutionState query) {
            return LOCKED == query;
        }

        @Override
        public String toString() {
            return "Locked";
        }
    },
    
    RUNNING {
        @Override
        public boolean isState(String query) {
            return "running".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(ExecutionState query) {
            return RUNNING == query;
        }

        @Override
        public String toString() {
            return "Running";
        }
    },
    
    COMPLETED {
        @Override
        public boolean isState(String query) {
            return "completed".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(ExecutionState query) {
            return COMPLETED == query;
        }

        @Override
        public String toString() {
            return "Completed";
        }
    },
    
    FAILED {
        @Override
        public boolean isState(String query) {
            return "failed".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(ExecutionState query) {
            return FAILED == query;
        }

        @Override
        public String toString() {
            return "Failed";
        }
    },
    
    ABORTED {
        @Override
        public boolean isState(String query) {
            return "aborted".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(ExecutionState query) {
            return ABORTED == query;
        }

        @Override
        public String toString() {
            return "Aborted";
        }
    },
    
    SKIPPED {
        @Override
        public boolean isState(String query) {
            return "skipped".equals(query.toLowerCase());
        }

        @Override
        public boolean isState(ExecutionState query) {
            return SKIPPED == query;
        }

        @Override
        public String toString() {
            return "Skipped";
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
    public abstract boolean isState(ExecutionState query);
    
    
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
        int limit = ExecutionState.values().length;
        int counter = 0;
        
        // Search until found
        while ( counter <= limit && stateInd < 0 ) {
            ExecutionState itemState = ExecutionState.values()[counter];
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
