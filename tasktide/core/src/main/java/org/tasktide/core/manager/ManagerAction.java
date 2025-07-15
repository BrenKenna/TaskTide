/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

package org.tasktide.core.manager;


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
    };

    public abstract boolean isManagerAction(String query);
    public abstract boolean isManagerAction(ManagerAction query);


    public static int indexOf(ManagerAction manageraction) {
        return manageraction.ordinal();
    }

    public static int indexOf(String name) {
        for (ManagerAction manageraction : values() ) {
            if (manageraction.isManagerAction(name)) {
                return manageraction.ordinal();
            }
        }
        return -1;
    }

    
    public static boolean hasQuery(ManagerAction manageraction, String query) {
        if (manageraction == null || query == null) {
            return false;
        }
        return manageraction.isManagerAction(query);
    }

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

    public static ManagerAction get(String query) {
        int ind = indexOf(query);
        if (ind >= 0) {
            return values()[ind];
        }
        return null;
    }

    public static ManagerAction get(ManagerAction query) {
        int ind = indexOf(query);
        if (ind >= 0) {
            return values()[ind];
        }
        return null;
    }
}
