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

package org.tasktide.core.manager.command;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * Valid ways {@link ManagerCommand} can interact with {@link TaskTideRepository}
 * 
 * @author Brendan Kenna
 */
public enum CommandType {
    
    EXPORT {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isCommandType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isCommandType(CommandType query) {
            return this == query;
        }
    },
    
    SELECT {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isCommandType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isCommandType(CommandType query) {
            return this == query;
        }
    },

    CREATE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isCommandType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isCommandType(CommandType query) {
            return this == query;
        }
    },
    
    BATCH_CREATE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isCommandType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isCommandType(CommandType query) {
            return this == query;
        }
    },
    
    UPDATE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isCommandType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isCommandType(CommandType query) {
            return this == query;
        }
    },
    
    BATCH_UPDATE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isCommandType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isCommandType(CommandType query) {
            return this == query;
        }
    },
    
    DELETE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isCommandType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isCommandType(CommandType query) {
            return this == query;
        }
    },
    
    BATCH_DELETE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isCommandType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isCommandType(CommandType query) {
            return this == query;
        }
    };


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isCommandType(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isCommandType(CommandType query);


    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( CommandType elm : values() ) {
            if ( elm.isCommandType(query) ) {
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

        for ( CommandType elm : values() ) {
            if ( elm.isCommandType(query) ) {
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
    public static CommandType get(String query) {
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