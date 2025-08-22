/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

package org.tasktide.core.manager;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * Enum to support valid actions covered by the Manager
 * 
 * @author bkenna
 */
public enum ManagerAction {

    IMPORT {

        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
    },

    EXPORT {
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
    },
    
    ADD {

        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
    
    },
    
    APPEND {
    
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
    },
    
    RESET_ITEM {
        @Override
        public boolean isManagerAction(String query) {
            query = query.trim().replace(" ", "").replace("-", "").replace("_", "").toLowerCase();
            return "resetitem".equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
    },
    
    RESET_ITEMS {
        @Override
        public boolean isManagerAction(String query) {
            query = query.trim().replace(" ", "").replace("-", "").replace("_", "").toLowerCase();
            return "resetitems".equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
    },
    
    
    SUMMARIZE_EACH {
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
    },
    
    SUMMARIZE {
        @Override
        public boolean isManagerAction(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isManagerAction(ManagerAction query) {
            return this == query;
        }

        @Override
        public String toString() {
            return name();
        }
    };

    public abstract boolean isManagerAction(String query);
    public abstract boolean isManagerAction(ManagerAction query);


    /**
     * Fetch index of query
     * 
     * @param name
     * @return >0/-1
     */
    public static int indexOf(String name) {
        for (ManagerAction manageraction : values() ) {
            if (manageraction.isManagerAction(name)) {
                return manageraction.ordinal();
            }
        }
        return -1;
    }
    
    
    /**
     * Check if query is a valid action
     * 
     * @param query
     * @return 
     */
    public static boolean hasQuery(String query) {
        if (query == null) {
            return false;
        }
        for (ManagerAction manageraction : values()) {
            if (manageraction.isManagerAction(query)) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Get manager action matching query
     * 
     * @param query
     * @return 
     */
    public static ManagerAction get(String query) {
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