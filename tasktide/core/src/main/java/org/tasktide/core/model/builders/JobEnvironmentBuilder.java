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
package org.tasktide.core.model.builders;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.job_env.JobState;
import org.tasktide.core.model.job_env.JobType;
import org.tasktide.core.model.job_env.JobEnvironment;


/**
 * {@link JobEnvironment} builder
 *
 * @author Brendan Kenna
 */
public class JobEnvironmentBuilder extends ModelBuilder<JobEnvironment> {
    
    // Attributes
    private String id, jobId, hostname, hostOS, javaVersion;
    private JobType jobType;
    private JobState jobState;
    private int jobInd;
    private CustomAnnotation anno;

    
    /**
     * Construct builder
     */
    public JobEnvironmentBuilder() {
        super();
    }
    
    
    /**
     * Build with Id field
     * 
     * @param id
     * @return {@link JobEnvironmentBuilder}
     */
    public JobEnvironmentBuilder withId(String id) {
        this.id = id;
        return this;
    }
    
    
    /**
     * Build with jobId field
     * 
     * @param jobId
     * @return {@link JobEnvironmentBuilder}
     */
    public JobEnvironmentBuilder withJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }
    
    
    /**
     * Build with hostname field
     * 
     * @param hostname
     * @return {@link JobEnvironmentBuilder}
     */
    public JobEnvironmentBuilder withHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }
    
    
    /**
     * Build with host operating system
     * 
     * @param hostOS
     * @return {@link JobEnvironmentBuilder}
     */
    public JobEnvironmentBuilder withHostOS(String hostOS) {
        this.hostOS = hostOS;
        return this;
    }
    
    
    /**
     * Build with java version
     * 
     * @param javaVersion
     * @return {@link JobEnvironmentBuilder}
     */
    public JobEnvironmentBuilder withJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
        return this;
    }
    
    
    /**
     * Build with {@link JobType}
     * 
     * @param jobType
     * @return {@link JobEnvironmentBuilder}
     */
    public JobEnvironmentBuilder withJobType(JobType jobType) {
        this.jobType = jobType;
        return this;
    }
    
    
    /**
     * Build with {@link JobState}
     * 
     * @param jobState
     * @return {@link JobEnvironmentBuilder}
     */
    public JobEnvironmentBuilder withJobState(JobState jobState) {
        this.jobState = jobState;
        return this;
    }
    
    
    /**
     * Build with job Id index
     * 
     * @param jobInd
     * @return {@link JobEnvironmentBuilder}
     */
    public JobEnvironmentBuilder withJobInd(int jobInd) {
        this.jobInd = jobInd;
        return this;
    }
    
    
    /**
     * Build with {@link CustomAnnotation}
     * 
     * @param anno
     * @return {@link JobEnvironmentBuilder}
     */
    public JobEnvironmentBuilder withCustomAnnotation(CustomAnnotation anno) {
        this.anno = anno;
        return this;
    }
    
    
    /**
     * Build job environment
     * 
     * @return {@link JobEnvironment}
     */
    @Override
    public JobEnvironment build() {
        return JobEnvironment.makeJobEnvironment(id, jobType, jobId, jobInd, hostname, hostOS, javaVersion, jobState, anno);
    }
}