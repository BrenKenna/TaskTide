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
package org.tasktide.core.model.job_env;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

import java.lang.reflect.Field;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.CustomAnnotation;
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
@jakarta.nosql.Entity("JobEnvironment")
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "JobEnvironment")
public class JobEnvironment implements TaskTideModel<JobEnvironment> {
    
    // Environment identifier
    @jakarta.nosql.Id
    @jakarta.persistence.Id
    @JsonbProperty("Id")
    private String id;
    
    // Type of Job - SLURM, gLITE etc
    @jakarta.nosql.Column("JobType")
    @jakarta.persistence.Column(name = "JobType")
    @JsonbProperty("Job Type")
    private JobType type;
    
    // Job Id under JobType
    @jakarta.nosql.Column("JobId")
    @jakarta.persistence.Column(name = "JobId")
    @JsonbProperty("Job Id")
    private String jobId;
    
    // Index in array if present
    @jakarta.nosql.Column("ArrayIndex")
    @jakarta.persistence.Column(name = "ArrayIndex")
    @JsonbProperty("Array Index")
    private int arrayInd;

    // Host job was running on
    @jakarta.nosql.Column("Hostname")
    @jakarta.persistence.Column(name = "Hostname")
    @JsonbProperty("Hostname")
    private String hostname;
    
    // Operating system of host
    @jakarta.nosql.Column("HostOs")
    @jakarta.persistence.Column(name = "HostOs")
    @JsonbProperty("Host OS")
    private String hostOS;
    
    // Version of Java running on host
    @jakarta.nosql.Column("JavaVersion")
    @jakarta.persistence.Column(name = "JavaVersion")
    @JsonbProperty("Java Version")
    private String javaVersion;
    
    // State of the job
    @jakarta.nosql.Column("JobState")
    @jakarta.persistence.Column(name = "JobState")
    @JsonbProperty("Job State")
    private JobState jobState;
    
    // Custom annotations
    @jakarta.nosql.Column("CustomAnnotation")
    @jakarta.persistence.Column(name = "CustomAnnotation")
    @JsonbProperty("Custom Annotation")
    private CustomAnnotation anno;
    
    
    /**
     * For Jakarta compatibility
     */
    public JobEnvironment() {
        this.anno = new CustomAnnotation();
    }
    
    
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
        this.anno = new CustomAnnotation();
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
     * @param jobState
     */
    public JobEnvironment(
        String id,
        JobType type,
        String jobId,
        String hostname,
        String hostOS,
        String javaVersion,
        int arrayInd,
        JobState jobState
    ) {
        this.id = id;
        this.type = type;
        this.jobId = jobId;
        this.hostname = hostname;
        this.hostOS = hostOS;
        this.javaVersion = javaVersion;
        this.arrayInd = arrayInd;
        this.anno = new CustomAnnotation();
        this.jobState = jobState;
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
     * @param jobState
     * @param anno
     */
    public JobEnvironment(
        String id,
        JobType type,
        String jobId,
        String hostname,
        String hostOS,
        String javaVersion,
        int arrayInd,
        JobState jobState,
        CustomAnnotation anno
    ) {
        this.id = id;
        this.type = type;
        this.jobId = jobId;
        this.hostname = hostname;
        this.hostOS = hostOS;
        this.javaVersion = javaVersion;
        this.arrayInd = arrayInd;
        this.jobState = jobState;
        this.anno = anno;
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
     * @param jobState
     * @param anno
     * 
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
        @JsonbProperty("Java Version") String javaVersion,
        @JsonbProperty("Job State") JobState jobState,
        @JsonbProperty("Custom Annotation") CustomAnnotation anno
    ) {
        
        // Define basic if hostname etc is not defined
        if ( hostname == null && hostOS == null && javaVersion == null ) {
            return new JobEnvironment(id, type, jobId, arrayInd);
        }
        
        // Otherwise use
        else {
            return new JobEnvironment(id, type, jobId, hostname, hostOS, javaVersion, arrayInd, jobState, anno);
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
    @Override
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
     * Get annotations
     * 
     * @return {@link CustomAnnotation}
     */
    @Override
    public CustomAnnotation getAnnotations() {
        return this.anno;
    }
    
    
    /**
     * Set annotation provided
     * 
     * @param anno 
     */
    @Override
    public void setAnnotations(CustomAnnotation anno) {
        this.anno = anno;
    }
    
    
    /**
     * Represent as JSON-B string
     * 
     * @return String
     */
    @Override
    public String toJson() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Represent as JSON-B Document
     * 
     * @return String
     */
    @Override
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }
    

    /**
     * Gets current {@link JobState}
     * 
     * @return String 
     */
    @JsonbTransient
    @Override
    public String getState() {
        return this.jobState.name();
    }

    
    /**
     * Gets current {@link JobState}
     * 
     * @return {@link JobState} 
     */
    public JobState getJobState() {
        return this.jobState;
    }
    
    
    /**
     * Sets a new {@link JobState}
     * @param jobState 
     */
    public void setJobState(JobState jobState) {
        this.jobState = jobState;
    }
    
    
    /**
     * Returns the {@link JobType}
     * 
     * @return String
     */
    @Override
    public String getCollection() {
        return this.type.name();
    }

    
    /**
     * Not implemented
     */
    @Override
    public void resetModel() {
        
    }

    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "JobEnvironment{" +
            "id=" + id +
            ", type=" + type +
            ", jobId=" + jobId +
            ", arrayInd=" + arrayInd +
            ", hostname=" + hostname +
            ", hostOS=" + hostOS +
            ", javaVersion=" + javaVersion +
        '}';
    }
    
    
    /**
     * Fetches value for queried field
     * 
     * @param field
     * @return Object
     */
    @Override
    public Object getValueFromField(String field) {
        try {
            // Use reflection to get the declared field from this class
            Field declaredField = this.getClass().getDeclaredField(field);
            declaredField.setAccessible(true); // In case the field is private
            Object fieldValue = declaredField.get(this);

            return fieldValue;

        }
        catch (Exception ex) {
            // Optional: Log or rethrow if needed
            return null;
        }
    }
}