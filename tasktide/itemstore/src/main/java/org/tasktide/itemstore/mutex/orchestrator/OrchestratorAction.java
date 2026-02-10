/*
 * Copyright 2026 Brendan Kenna.
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

package org.tasktide.itemstore.mutex.orchestrator;


import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author Brendan Kenna
 */
public enum OrchestratorAction {

    LOCK {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isOrchestratorAction(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isOrchestratorAction(OrchestratorAction query) {
            return this == query;
        }
    },

    RELEASE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isOrchestratorAction(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isOrchestratorAction(OrchestratorAction query) {
            return this == query;
        }
    };


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isOrchestratorAction(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isOrchestratorAction(OrchestratorAction query);


    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( OrchestratorAction elm : values() ) {
            if ( elm.isOrchestratorAction(query) ) {
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

        for ( OrchestratorAction elm : values() ) {
            if ( elm.isOrchestratorAction(query) ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Map query to enum value
     *
     * @param query
     * @return OrchestratorAction
     */
    public static OrchestratorAction get(String query) {
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