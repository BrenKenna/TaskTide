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

package org.tasktide.core.model.task.job_env;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.tasktide.core.model.task.JobEnvironment;


/**
 * Enum which fetches host data, and apply to {@link JobEnvironment}
 * 
 * @author Brendan Kenna
 */
enum EnvironmentProperty {

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
        public boolean isEnvironmentProperites(EnvironmentProperty query) {
            return this == query;
        }
        
        @Override
        public String getProperty() {
            return System.getProperties().getProperty("os.name");
        }
        
        @Override
        public void addToEnvironment(JobEnvironment jobEnv) {
            String prop = getProperty() + "-" + EnvironmentProperty.OS_ARCH.getProperty();
            jobEnv.setHostname(prop);
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
        public boolean isEnvironmentProperites(EnvironmentProperty query) {
            return this == query;
        }
        
        @Override
        public String getProperty() {
            return System.getProperties().getProperty("java.vm.version");
        }
        
        @Override
        public void addToEnvironment(JobEnvironment jobEnv) {
            String prop = getProperty();
            jobEnv.setHostname(prop);
        }
    },
    
    OS_ARCH {
        @Override
        public boolean isEnvironmentProperites(String query) {
            return "os.arch".equalsIgnoreCase(query);
        }

        @Override
        public boolean isEnvironmentProperites(EnvironmentProperty query) {
            return this == query;
        }
    
        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public String getProperty() {
            return System.getProperties().getProperty("os.arch");
        }
        
        @Override
        public void addToEnvironment(JobEnvironment jobEnv) {
            String prop = EnvironmentProperty.HOST_OS.getProperty() + "-" + getProperty();
            jobEnv.setHostname(prop);
        }
    },
    
    HOSTNAME {
        @Override
        public boolean isEnvironmentProperites(String query) {
            return "os.arch".equalsIgnoreCase(query);
        }

        @Override
        public boolean isEnvironmentProperites(EnvironmentProperty query) {
            return this == query;
        }
    
        @Override
        public String toString() {
            return name();
        }
        
        @Override
        public String getProperty() {
            try {
                return EnvironmentUtil.getHostname();
            }
            catch (UnknownHostException ex) {
                return "";
            }
        }
        
        @Override
        public void addToEnvironment(JobEnvironment jobEnv) {
            String prop = getProperty();
            jobEnv.setHostname(prop);
        }
    };


    /**
     * Abstract method which values implement for
     *  fetching associated property
     * 
     * @return String
     */
    public abstract String getProperty();
    
    
    /**
     * Add properties to {@link JobEnvironment}
     * 
     * @param jobEnv 
     */
    public abstract void addToEnvironment(JobEnvironment jobEnv);
    
    
    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isEnvironmentProperites(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isEnvironmentProperites(EnvironmentProperty query);


    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( EnvironmentProperty elm : values() ) {
            if ( elm.isEnvironmentProperites(query) ) {
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

        for ( EnvironmentProperty elm : values() ) {
            if ( elm.isEnvironmentProperites(query) ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Map query to enum value
     *
     * @param query
     * @return EnvironmentProperites
     */
    public static EnvironmentProperty get(String query) {
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
     * Applies host data scoped by enum to {@link JobEnvironment}
     * 
     * @param jobEnv 
     */
    public static void applyData(JobEnvironment jobEnv) {
        for ( EnvironmentProperty elm : EnvironmentProperty.values() ) {
            elm.addToEnvironment(jobEnv);
        }
    }
}