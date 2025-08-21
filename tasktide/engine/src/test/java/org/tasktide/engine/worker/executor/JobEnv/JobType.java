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
public enum JobType {

    SLURM {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isJobType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isJobType(JobType query) {
            return this == query;
        }
        
        @Override
        public JobIdentifier makeJobIdentifier() {
            String jobId = System.getenv("SLURM_JOB_ID");
            return new JobIdentifier(this, jobId, 0l);
        }
    },
    
    GLITE {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isJobType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isJobType(JobType query) {
            return this == query;
        }
        
        @Override
        public JobIdentifier makeJobIdentifier() {
            String jobId = System.getenv("GLITE_WMS_JOBID");
            return new JobIdentifier(this, jobId, 0l);
        }
    },

    LSF {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isJobType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isJobType(JobType query) {
            return this == query;
        }
        
        @Override
        public JobIdentifier makeJobIdentifier() {
            String jobId = System.getenv("LSF_JOBID");
            return new JobIdentifier(this, jobId, 0l);
        }
    },
    
    AWS_BATCH {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isJobType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isJobType(JobType query) {
            return this == query;
        }
        
        @Override
        public JobIdentifier makeJobIdentifier() {
            String jobId = System.getenv("AWS_BATCH_JOB_ID");
            return new JobIdentifier(this, jobId, 0l);
        }
    },
    
    
    GCP_BATCH {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isJobType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isJobType(JobType query) {
            return this == query;
        }
        
        @Override
        public JobIdentifier makeJobIdentifier() {
            String jobId = System.getenv("BATCH_JOB_ID");
            return new JobIdentifier(this, jobId, 0l);
        }
    },
    
    AZURE_BATCH {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isJobType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isJobType(JobType query) {
            return this == query;
        }
        
        @Override
        public JobIdentifier makeJobIdentifier() {
            String jobId = System.getenv("AZ_BATCH_JOB_ID");
            return new JobIdentifier(this, jobId, 0l);
        }
    },
    
    HADOOP {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isJobType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isJobType(JobType query) {
            return this == query;
        }
        
        @Override
        public JobIdentifier makeJobIdentifier() {
            String jobId = System.getenv("APPLICATION_ID");
            return new JobIdentifier(this, jobId, 0l);
        }
    },

    
    CONTAINER {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isJobType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isJobType(JobType query) {
            return this == query;
        }
        
        @Override
        public JobIdentifier makeJobIdentifier() {
            return new JobIdentifier(this, "", 0l);
        }
    };
    
    
    /**
     * Abstract method for values make their associated JobIdentiifer
     * 
     * @return {@link JobIdentifier}
     */
    public abstract JobIdentifier makeJobIdentifier();
    

    /**
     * Abstract method check if query is enum value
     *
     * @return boolean
     */
    public abstract boolean isJobType(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isJobType(JobType query);


    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( JobType elm : values() ) {
            if ( elm.isJobType(query) ) {
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

        for ( JobType elm : values() ) {
            if ( elm.isJobType(query) ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Map query to enum value
     *
     * @param query
     * @return JobType
     */
    public static JobType get(String query) {
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