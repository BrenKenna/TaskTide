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

package org.tasktide.core.model.job_env.metrics;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * Represent valid {@link MetricData} instances
 * 
 * @author Brendan Kenna
 */
public enum MetricType {

    CPU {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isMetricType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isMetricType(MetricType query) {
            return this == query;
        }
    },

    MEMORY {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isMetricType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isMetricType(MetricType query) {
            return this == query;
        }
    },
    
    STORAGE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isMetricType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isMetricType(MetricType query) {
            return this == query;
        }
    },
    
    GRAPHICS {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isMetricType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isMetricType(MetricType query) {
            return this == query;
        }
    }
    ;


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isMetricType(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isMetricType(MetricType query);


    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( MetricType elm : values() ) {
            if ( elm.isMetricType(query) ) {
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

        for ( MetricType elm : values() ) {
            if ( elm.isMetricType(query) ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Map query to enum value
     *
     * @param query
     * @return MetricType
     */
    public static MetricType get(String query) {
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