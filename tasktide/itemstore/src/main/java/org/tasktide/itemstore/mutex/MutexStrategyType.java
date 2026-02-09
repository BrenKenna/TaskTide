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
package org.tasktide.itemstore.mutex;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * Package private enum to support strategic physical locking/and
 *  release method. Allows the broader implementation elements
 *  to handled distinctly from their specifics. In addition
 *  to cleaner/digestable approach which simplifies
 *  responsibilities of testing less repetitively.
 *
 * @author Brendan Kenna
 */
public enum MutexStrategyType {

    FILE_CHANNEL {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isApplierStrategy(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isApplierStrategy(MutexStrategyType query) {
            return this == query;
        }
    },

    ELECTION {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isApplierStrategy(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isApplierStrategy(MutexStrategyType query) {
            return this == query;
        }
    };
    

    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isApplierStrategy(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isApplierStrategy(MutexStrategyType query);
    

    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( MutexStrategyType elm : values() ) {
            if ( elm.isApplierStrategy(query) ) {
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

        for ( MutexStrategyType elm : values() ) {
            if ( elm.isApplierStrategy(query) ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Map query to enum value
     *
     * @param query
     * @return MutexStrategyType
     */
    public static MutexStrategyType get(String query) {
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