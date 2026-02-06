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

package org.tasktide.itemstore.mutex.model;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.tasktide.itemstore.mutex.utils.MutexConstants;
import org.tasktide.itemstore.mutex.exceptions.MutexUncheckedException;


/**
 * Enum to support operation over valid file types
 *
 * @author Brendan Kenna
 */
public enum MutexFileType {

    ELECTION_FILE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isMutexFileType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isMutexFileType(MutexFileType query) {
            return this == query;
        }
        
        @Override
        public Path fetchPathForFile() {
            return MutexConstants.getElectionFile();
        }
        
        @Override
        public Path fetchPathForDir() {
            return MutexConstants.getElectionDir();
        }
    },
    
    CONFIRM_FILE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isMutexFileType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isMutexFileType(MutexFileType query) {
            return this == query;
        }
        
        @Override
        public Path fetchPathForFile() {
            return MutexConstants.getElectionFile();
        }
        
        @Override
        public Path fetchPathForDir() {
            return MutexConstants.getElectionDir();
        }
    },

    HOST_FILE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isMutexFileType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isMutexFileType(MutexFileType query) {
            return this == query;
        }
        
        @Override
        public Path fetchPathForFile() {
            return MutexConstants.getHostFile();
        }
        
        @Override
        public Path fetchPathForDir() {
            return MutexConstants.getHostDir();
        }
    },
    
    LOCK_FILE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isMutexFileType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isMutexFileType(MutexFileType query) {
            return this == query;
        }
        
        @Override
        public Path fetchPathForFile() {
            return MutexConstants.getLockFile();
        }
        
        @Override
        public Path fetchPathForDir() {
            return MutexConstants.getLockDir();
        }
    };


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isMutexFileType(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isMutexFileType(MutexFileType query);


    /**
     * Fetch path for file
     * 
     * @return Path
     */
    public abstract Path fetchPathForFile();
    
    
    /**
     * Fetch path for directory
     * 
     * @return Path
     */
    public abstract Path fetchPathForDir();
    
    
    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( MutexFileType elm : values() ) {
            if ( elm.isMutexFileType(query) ) {
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

        for ( MutexFileType elm : values() ) {
            if ( elm.isMutexFileType(query) ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Map query to enum value
     *
     * @param query
     * @return {@link MutexFileType}
     */
    public static MutexFileType get(String query) {
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
    
    
    /**
     * Fetch {@link MutexFileType} from {@link MutexConstants}
     * 
     * @param fileType
     * @return Path
     */
    public static Path fetchFileFromConstants(MutexFileType fileType) {
        switch ( fileType ) {
        
            case ELECTION_FILE -> {
                return MutexConstants.getElectionFile();
            }
            
            case HOST_FILE -> {
                return MutexConstants.getHostFile();
            }
            
            case LOCK_FILE -> {
                return MutexConstants.getLockFile();
            }
            
            default -> {
                throw new MutexUncheckedException(
                    "MutexFileType must be one of:\t"
                    + MutexFileType.valuesString()
                );
            }
        }
    }
    
    
    /**
     * Fetch {@link MutexFileType} from {@link MutexConstants}
     * 
     * @param fileType
     * @return Path
     */
    public static Path fetchDirectoryFromConstants(MutexFileType fileType) {
        switch ( fileType ) {
        
            case ELECTION_FILE -> {
                return MutexConstants.getElectionDir();
            }
            
            case HOST_FILE -> {
                return MutexConstants.getHostFile();
            }
            
            case LOCK_FILE -> {
                return MutexConstants.getLockFile();
            }
            
            default -> {
                throw new MutexUncheckedException(
                    "MutexFileType must be one of:\t"
                    + MutexFileType.valuesString()
                );
            }
        }
    }
}