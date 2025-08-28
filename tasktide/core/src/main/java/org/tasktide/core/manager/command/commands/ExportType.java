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

package org.tasktide.core.manager.command.commands;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * Enum to support exports 
 *
 * @author Brendan Kenna
 */
public enum ExportType {

    ALL {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isExportType(String query) {
            query = query.strip().replace("_", "").replace("-", "").toLowerCase();
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isExportType(ExportType query) {
            return this == query;
        }
    },

    STATE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isExportType(String query) {
            query = query.strip().replace("_", "").replace("-", "").toLowerCase();
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isExportType(ExportType query) {
            return this == query;
        }
    };


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isExportType(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isExportType(ExportType query);


    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( ExportType elm : values() ) {
            if ( elm.isExportType(query) ) {
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
        if ( query == null ) {
            return false;
        }

        for ( ExportType elm : values() ) {
            if ( elm.isExportType(query) ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Map query to enum value
     *
     * @param query
     * @return ExportType
     */
    public static ExportType get(String query) {
        int ind = indexOf(query);
        if ( ind >= 0 ) {
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
            .map( elm -> elm.name() )
        .collect(Collectors.joining(","));
    }
}