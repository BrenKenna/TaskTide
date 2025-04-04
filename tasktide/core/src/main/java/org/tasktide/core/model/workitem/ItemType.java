/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.model.workitem;


/**
 * 
 * Enumeration of valid WorkItem Types (Single, Nested)
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
