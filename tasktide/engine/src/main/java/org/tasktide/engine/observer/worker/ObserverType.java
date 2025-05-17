/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.engine.observer.worker;


/**
 * Enum to support logic handling optional/critical {@link TaskTideWorkerObserver}s
 * 
 * @author bkenna
 */
public enum ObserverType {
    
    OPTIONAL {
        @Override
        public boolean isType(ObserverType query) {
            return OPTIONAL == query;
        }

        @Override
        public boolean isType(String query) {
            return "optional".equals(query.toLowerCase());
        }

        @Override
        public boolean isOptional() {
            return true;
        }

        @Override
        public String toString() {
            return "Optional";
        }
    },
    
    CRITICAL {
        @Override
        public boolean isType(ObserverType query) {
            return CRITICAL == query;
        }

        @Override
        public boolean isType(String query) {
            return "critical".equals(query.toLowerCase());
        }

        @Override
        public boolean isOptional() {
            return false;
        }

        @Override
        public String toString() {
            return "Critical";
        }
    };
    
    
    /**
     * Check is observer types are the same
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isType(ObserverType query);
    
    
    /**
     * Check whether queried string matches current type
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isType(String query);
    
    
    /**
     * Check is enum is optional
     * 
     * @return boolean
     */
    public abstract boolean isOptional();
    
    
    /**
     * Represent enum as string
     * 
     * @return String
     */
    @Override
    public abstract String toString();
}
