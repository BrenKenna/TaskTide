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
package org.tasktide.core.model.workitem;


/**
 * Enumeration of valid WorkItem Types (Single, Nested)
 * 
 * @author bkenna
 * 
 */
public enum ItemType {
    
    SINGLE {
        @Override
        public boolean isType(String query) {
            return "single".equals(query.toLowerCase());
        }

        @Override
        public boolean isType(ItemType query) {
            return SINGLE == query;
        }

        @Override
        public String toString() {
            return "Single";
        }
    
    },
    
    NESTED {
        @Override
        public boolean isType(String query) {
            return "nested".equals(query.toLowerCase());
        }

        @Override
        public boolean isType(ItemType query) {
            return NESTED == query;
        }

        @Override
        public String toString() {
            return "Nested";
        }
    };
    
    
    /**
     * Compare item type to query string
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isType(String query);
    
    
    /**
     * Compare item type to queried type
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isType(ItemType query);
    
    
    /**
     * Represent enum value as string
     * 
     * @return String
     */
    @Override
    public abstract String toString();
    
    
    /**
     * Return index of queried type
     * 
     * @param query
     * @return int
     */
    public static int indexOf(String query) {
        
        // Initialize values
        int stateInd = -1;
        int limit = ItemType.values().length;
        int counter = 0;
        
        // Search until found
        while ( counter <= limit && stateInd < 0 ) {
            ItemType itemType = ItemType.values()[counter];
            if ( itemType.isType(query) ) {
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
