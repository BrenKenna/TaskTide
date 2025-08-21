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

package org.tasktide.engine.worker.executor.JobEnv;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author Brendan Kenna
 */
public enum EnvironmentProperties {

    HOST_OS {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isEnvironmentProperites(String query) {
            return "os.name".equalsIgnoreCase(query);
        }

        @Override
        public boolean isEnvironmentProperites(EnvironmentProperties query) {
            return this == query;
        }
    },

    JAVA_VERSION {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isEnvironmentProperites(String query) {
            return "java.vm.version".equalsIgnoreCase(query);
        }

        @Override
        public boolean isEnvironmentProperites(EnvironmentProperties query) {
            return this == query;
        }
    },
    
    
    OS_ARCH {
        @Override
        public boolean isEnvironmentProperites(String query) {
            return "os.arch".equalsIgnoreCase(query);
        }

        @Override
        public boolean isEnvironmentProperites(EnvironmentProperties query) {
            return this == query;
        }
    
        @Override
        public String toString() {
            return name();
        }
    },
    
    
    HOSTNAME {
        @Override
        public boolean isEnvironmentProperites(String query) {
            return "os.arch".equalsIgnoreCase(query);
        }

        @Override
        public boolean isEnvironmentProperites(EnvironmentProperties query) {
            return this == query;
        }
    
        @Override
        public String toString() {
            return name();
        }
    };


    /**
     * Abstract method check if query is enum value
     *
     * @return boolean
     */
    public abstract boolean isEnvironmentProperites(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @return boolean
     */
    public abstract boolean isEnvironmentProperites(EnvironmentProperties query);


    /**
     * Fetch the index for mapped query string
     *
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( EnvironmentProperties elm : values() ) {
            if ( elm.isEnvironmentProperites(query) ) {
                return elm.ordinal();
            }
        }
        return -1;
    }


    /**
     * Check if query maps to enum value
     *
     * @return boolean
     */
    public static boolean hasQuery(String query) {
        if ( query == null ) {
            return false;
        }

        for ( EnvironmentProperties elm : values() ) {
            if ( elm.isEnvironmentProperites(query) ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Map query to enum value
     *
     * @return EnvironmentProperites
     */
    public static EnvironmentProperties get(String query) {
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