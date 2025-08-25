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
 * Valid ways {@link ManagerCommand} can import
 * 
 * @author Brendan Kenna
 */
public enum ImportType {

    DELIMITED {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isImportTypes(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isImportTypes(ImportType query) {
            return this == query;
        }
    },

    NESTED_DELIMITED {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isImportTypes(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isImportTypes(ImportType query) {
            return this == query;
        }
    },
    
    JSON_FILE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isImportTypes(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isImportTypes(ImportType query) {
            return this == query;
        }
    },
    
    
    STRING {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isImportTypes(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isImportTypes(ImportType query) {
            return this == query;
        }
    };


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isImportTypes(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isImportTypes(ImportType query);


    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( ImportType elm : values() ) {
            if ( elm.isImportTypes(query) ) {
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

        for ( ImportType elm : values() ) {
            if ( elm.isImportTypes(query) ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Map query to enum value
     *
     * @param query
     * @return ImportTypes
     */
    public static ImportType get(String query) {
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