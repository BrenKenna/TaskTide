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
import java.util.Optional;
import java.util.stream.Collectors;

import org.tasktide.core.model.task.JobEnvironment;


/**
 * Enum to support strategic construction of {@link JobEnvironment}.
 *  New environments, requires add new value
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
        public String getJobId() {
            return System.getenv("SLURM_JOB_ID");
        }
        
        @Override
        public Integer getArrayIndex() {
            String ind = System.getenv("SLURM_ARRAY_TASK_ID");
            return ind != null ? Integer.valueOf(ind) : -1;
        }
        
        @Override
        public JobEnvironment makeJobEnvironment() {
            return JobEnvironment.makeJobEnvironment(this, getJobId(), getArrayIndex(), null, null, null);
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
        public String getJobId() {
            return System.getenv("GLITE_WMS_JOBID");
        }
        
        @Override
        public Integer getArrayIndex() {
            return -1;
        }
        
        @Override
        public JobEnvironment makeJobEnvironment() {
            return JobEnvironment.makeJobEnvironment(this, getJobId(), getArrayIndex(), null, null, null);
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
        public String getJobId() {
            return System.getenv("LSF_JOBID");
        }
        
        @Override
        public Integer getArrayIndex() {
            String ind = System.getenv("LSB_JOBINDEX");
            return ind != null ? Integer.valueOf(ind) : -1;
        }
        
        @Override
        public JobEnvironment makeJobEnvironment() {
            return JobEnvironment.makeJobEnvironment(this, getJobId(), getArrayIndex(), null, null, null);
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
        public String getJobId() {
            return System.getenv("AWS_BATCH_JOB_ID");
        }
        
        @Override
        public Integer getArrayIndex() {
            String ind = System.getenv("AWS_BATCH_JOB_ARRAY_INDEX");
            return ind != null ? Integer.valueOf(ind) : -1;
        }
        
        @Override
        public JobEnvironment makeJobEnvironment() {
            return JobEnvironment.makeJobEnvironment(this, getJobId(), getArrayIndex(), null, null, null);
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
        public String getJobId() {
            return System.getenv("BATCH_JOB_ID");
        }
        
        @Override
        public Integer getArrayIndex() {
            String ind = System.getenv("BATCH_TASK_INDEX");
            return ind != null ? Integer.valueOf(ind) : -1;
        }
        
        @Override
        public JobEnvironment makeJobEnvironment() {
            return JobEnvironment.makeJobEnvironment(this, getJobId(), getArrayIndex(), null, null, null);
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
        public String getJobId() {
            return System.getenv("AZ_BATCH_JOB_ID");
        }
        
        @Override
        public Integer getArrayIndex() {
            return -1;
        }
        
        @Override
        public JobEnvironment makeJobEnvironment() {
            return JobEnvironment.makeJobEnvironment(this, getJobId(), getArrayIndex(), null, null, null);
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
        public String getJobId() {
            return System.getenv("APPLICATION_ID");
        }
        
        @Override
        public Integer getArrayIndex() {
            return -1;
        }
        
        @Override
        public JobEnvironment makeJobEnvironment() {
            return JobEnvironment.makeJobEnvironment(this, getJobId(), getArrayIndex(), null, null, null);
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
        public String getJobId() {
            try {
                return EnvironmentUtil.getHostname();
            }
            catch (UnknownHostException ex) {
                return "";
            }
        }
        
        @Override
        public Integer getArrayIndex() {
            return -1;
        }
        
        @Override
        public JobEnvironment makeJobEnvironment() {
            return JobEnvironment.makeJobEnvironment(this, getJobId(), getArrayIndex(), null, null, null);
        }
    },
    
    LOCAL {
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
        public String getJobId() {
            return "LOCAL";
        }
        
        @Override
        public Integer getArrayIndex() {
            return -1;
        }
        
        @Override
        public JobEnvironment makeJobEnvironment() {
            return JobEnvironment.makeJobEnvironment(this, getJobId(), getArrayIndex(), null, null, null);
        }
    };
    
    
    /**
     * Get jobId or return null
     * 
     * @return String
     */
    public abstract String getJobId();
    
    
    /**
     * Get index of job in array
     * 
     * @return Integer
     */
    public abstract Integer getArrayIndex();
    
    
    /**
     * Abstract method for values make their associated JobEnvironment
     * 
     * @return {@link JobEnvironment}
     */
    public abstract JobEnvironment makeJobEnvironment();
    

    /**
     * Abstract method check if query is enum value
     *
     * @param query
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
    
    
    /**
     * Detect environment
     * 
     * @return {@link JobType}
     */
    public static Optional<JobType> detectEnv() {
        for ( JobType elm : JobType.values() ) {
            String jobId = elm.getJobId();
            if ( jobId != null ) {
                if ( ! jobId.isEmpty() ) {
                    return Optional.of(elm);
                }
            }
        }
        return Optional.empty();
    }
    
    
    /**
     * Fetches {@link JobEnvironment} from auto-detecting
     *  environment
     * 
     * @return {@link JobEnvironment}
     */
    public static Optional<JobEnvironment> fetchJobEnvironment() {
        Optional<JobType> optEnv = detectEnv();
        if ( optEnv.isPresent() ) {
            JobEnvironment jobEnv = optEnv.get().makeJobEnvironment();
            EnvironmentProperty.applyData(jobEnv);
            return Optional.of(jobEnv);
        }
        return Optional.empty();
    } 
}