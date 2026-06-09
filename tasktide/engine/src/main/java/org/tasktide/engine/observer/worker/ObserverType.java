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
package org.tasktide.engine.observer.worker;

import org.tasktide.engine.observer.WorkerObserver;


/**
 * Enum to support logic handling optional/critical {@link WorkerObserver}
 * 
 * @author bkenna
 */
public enum ObserverType {
    
    OPTIONAL {
        @Override
        public boolean isType(ObserverType query) {
            return OPTIONAL == query;
        }

        @Override
        public boolean isType(String query) {
            return "optional".equals(query.toLowerCase());
        }

        @Override
        public boolean isOptional() {
            return true;
        }

        @Override
        public String toString() {
            return "Optional";
        }
    },
    
    CRITICAL {
        @Override
        public boolean isType(ObserverType query) {
            return CRITICAL == query;
        }

        @Override
        public boolean isType(String query) {
            return "critical".equals(query.toLowerCase());
        }

        @Override
        public boolean isOptional() {
            return false;
        }

        @Override
        public String toString() {
            return "Critical";
        }
    };
    
    
    /**
     * Check is observer types are the same
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isType(ObserverType query);
    
    
    /**
     * Check whether queried string matches current type
     * 
     * @param query
     * @return boolean
     */
    public abstract boolean isType(String query);
    
    
    /**
     * Check is enum is optional
     * 
     * @return boolean
     */
    public abstract boolean isOptional();
    
    
    /**
     * Represent enum as string
     * 
     * @return String
     */
    @Override
    public abstract String toString();
}
