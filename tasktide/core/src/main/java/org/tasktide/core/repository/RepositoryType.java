/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.repository;


/**
 * Enum of valid repository types
 * 
 * @author bkenna
 */
public enum RepositoryType {
    
    NOSQL {
        @Override
        public boolean isRepository(String query) {
            return "nosql".equals(query.toLowerCase());
        }

        @Override
        public boolean isRepository(RepositoryType query) {
            return NOSQL == query;
        }

        @Override
        public String toString() {
            return "NoSQL";
        }
    },
    
    JSON {
        @Override
        public boolean isRepository(String query) {
            return "json".equals(query.toLowerCase());
        }

        @Override
        public boolean isRepository(RepositoryType query) {
            return JSON == query;
        }

        @Override
        public String toString() {
            return "JSON";
        }
    },
    
    SQL {
        @Override
        public boolean isRepository(String query) {
            return "sql".equals(query.toLowerCase());
        }

        @Override
        public boolean isRepository(RepositoryType query) {
            return SQL == query;
        }

        @Override
        public String toString() {
            return "SQL";
        }
    };
    
    
    /**
     * Compare to queried repository string
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isRepository(String query);
    
    
    /**
     * Compare to queried to repository
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isRepository(RepositoryType query);

    
    
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
        int repoInd = -1;
        int limit = RepositoryType.values().length;
        int counter = 0;
        
        // Search until found
        while ( counter <= limit && repoInd < 0 ) {
            RepositoryType repoType = RepositoryType.values()[counter];
            if ( repoType.isRepository(query) ) {
                repoInd = counter;
            }
            else {
                counter++;
            }
        }
        
        // Return search result
        return repoInd;
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
