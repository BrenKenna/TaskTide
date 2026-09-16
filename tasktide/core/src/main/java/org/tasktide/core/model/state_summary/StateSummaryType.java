/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.model.state_summary;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 *
 * @author Bren
 */
public enum StateSummaryType {

    ITEM_STATE_SUMMARY {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isStateSummaryType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isStateSummaryType(StateSummaryType query) {
            return this == query;
        }
    },

    TASK_STATE_SUMMARY {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isStateSummaryType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isStateSummaryType(StateSummaryType query) {
            return this == query;
        }
    };


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isStateSummaryType(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isStateSummaryType(StateSummaryType query);


    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for (StateSummaryType elm : values()) {
            if (elm.isStateSummaryType(query)) {
                return elm.ordinal();
            }
        }
        return -1;
    }


    /**
     * Check if query maps to enum value
     *
     * @param query
     * @return boolean
     */
    public static boolean hasQuery(String query) {
        if (query == null) {
            return false;
        }

        for (StateSummaryType elm : values()) {
            if (elm.isStateSummaryType(query)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Map query to enum value
     *
     * @param query
     * @return StateSummaryType
     */
    public static StateSummaryType get(String query) {
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