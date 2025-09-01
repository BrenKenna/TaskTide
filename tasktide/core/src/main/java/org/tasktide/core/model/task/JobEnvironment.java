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
package org.tasktide.core.model.task;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import jakarta.nosql.Entity;
import jakarta.nosql.Column;
import jakarta.nosql.Id;

import org.tasktide.core.model.task.job_env.JobType;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Data class to hold collection of host meta-data running job
 *  such as where Hostname/Environment etc, job Id, OS, and.
 * <br><br>
 * A single JobEnviroment can relate to multiple {@link WorkItem},
 *  but a single {@link WorkItem} does not relate to multiple JobEnvs.
 *
 * @author Brendan Kenna
 */
@Entity
public class JobEnvironment {
    
    // Attributes
    @Id
    @JsonbProperty("Id")
    private String id;
    
    @Column("Job Type")
    @JsonbProperty("Job Type")
    private JobType type;
    
    @Column("Job Id")
    @JsonbProperty("Job Id")
    private String jobId;
    
    @Column("Array Index")
    @JsonbProperty("Array Index")
    private int arrayInd;

    @Column("Hostname")
    @JsonbProperty("Hostname")
    private String hostname;
    
    @Column("Host OS")
    @JsonbProperty("Host OS")
    private String hostOS;
    
    @Column("Jave Version")
    @JsonbProperty("Java Version")
    private String javaVersion;
    
    
    /**
     * For Jakarta compatibility
     */
    public JobEnvironment() {}
    
    
    /**
     * Construct
     * 
     * @param id
     * @param type
     * @param jobId
     * @param arrayInd 
     */
    public JobEnvironment(String id, JobType type, String jobId, int arrayInd) {
        this.id = id;
        this.type = type;
        this.jobId = jobId;
        this.arrayInd = arrayInd;
    }

    
    /**
     * Construct with all arguments
     * 
     * @param id
     * @param type
     * @param jobId
     * @param hostname
     * @param hostOS
     * @param javaVersion
     * @param arrayInd 
     */
    public JobEnvironment(String id, JobType type, String jobId, String hostname, String hostOS, String javaVersion, int arrayInd) {
        this.id = id;
        this.type = type;
        this.jobId = jobId;
        this.hostname = hostname;
        this.hostOS = hostOS;
        this.javaVersion = javaVersion;
        this.arrayInd = arrayInd;
    }
    
    
    /**
     * Static method supporting json deserialization through
     *  which makeJobEnvironment is created
     * 
     * @param id
     * @param type
     * @param jobId
     * @param arrayInd
     * @param hostname
     * @param hostOS
     * @param javaVersion
     * @return {@link JobEnvironment}
     */
    @JsonbCreator
    public static JobEnvironment makeJobEnvironment(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Job Type") JobType type,
        @JsonbProperty("Job Id") String jobId,
        @JsonbProperty("Array Index") int arrayInd,
        @JsonbProperty("Hostname") String hostname,
        @JsonbProperty("Host OS") String hostOS,
        @JsonbProperty("Java Version") String javaVersion
    ) {
        
        // Define basic if hostname etc is not defined
        if ( hostname == null && hostOS == null && javaVersion == null ) {
            return new JobEnvironment(id, type, jobId, arrayInd);
        }
        
        // Otherwise use
        else {
            return new JobEnvironment(id, type, jobId, hostname, hostOS, javaVersion, arrayInd);
        }
    }
    
    
    /**
     * Get job type
     * 
     * @return {@link JobType}
     */
    public JobType getType() {
        return type;
    }

    
    /**
     * Get job Id
     * 
     * @return String
     */
    public String getJobId() {
        return jobId;
    }

    
    /**
     * Get index in job array
     * 
     * @return long
     */
    public int getArrayInd() {
        return arrayInd;
    }

    
    /**
     * Get hostname, defined from inet address
     * 
     * @return String
     */
    public String getHostname() {
        return hostname;
    }

    
    /**
     * Get hostname from inet address
     * 
     * @param hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    
    /**
     * Get host os, defined from properties os.name+os.version
     * 
     * @return String
     */
    public String getHostOS() {
        return hostOS;
    }

    
    /**
     * Set host os, defined from properties os.name+os.version
     * 
     * @param hostOS 
     */
    public void setHostOS(String hostOS) {
        this.hostOS = hostOS;
    }

    
    /**
     * Get java version
     * 
     * @return String
     */
    public String getJavaVersion() {
        return javaVersion;
    }

    
    /**
     * Set java version
     * 
     * @param javaVersion 
     */
    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }
    
    
    /**
     * Get Id for job environment
     * 
     * @return String
     */
    public String getId() {
        return this.id;
    }
    
    
    /**
     * Set Id
     * 
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }

    
    /**
     * Set {@link JobType}
     * 
     * @param type 
     */
    public void setType(JobType type) {
        this.type = type;
    }

    
    /**
     * Sets job Id
     * 
     * @param jobId 
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    
    /**
     * Sets job arrray index
     * 
     * @param arrayInd 
     */
    public void setArrayInd(int arrayInd) {
        this.arrayInd = arrayInd;
    }
    
    
    /**
     * Represent as json string
     * 
     * @return string
     */
    public String toJsonString() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Represent as json document
     * 
     * @return String
     */
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }

    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "JobIdentifier{" +
            "id=" + id +
            "type=" + type +
            ", jobId=" + jobId +
            ", arrayInd=" + arrayInd +
            ", hostname=" + hostname +
            ", hostOS=" + hostOS +
            ", javaVersion=" + javaVersion +
        '}';
    }
}