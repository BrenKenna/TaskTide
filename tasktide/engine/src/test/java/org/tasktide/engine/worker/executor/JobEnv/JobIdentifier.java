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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;


/**
 *
 * @author Brendan Kenna
 */
public class JobIdentifier {
 
    // Attributes
    @JsonbProperty("Job Type")
    private final JobType type;
    
    @JsonbProperty("Job Id")
    private final String jobId;
    
    @JsonbProperty("Array Index")
    private final long arrayInd;

    
    
    /**
     * Construct
     * 
     * @param type
     * @param jobId
     * @param arrayInd 
     */
    @JsonbCreator
    public JobIdentifier(
        @JsonbProperty("Job Type") JobType type,
        @JsonbProperty("Job Id") String jobId,
        @JsonbProperty("Array Index") long arrayInd
    ) {
        this.type = type;
        this.jobId = jobId;
        this.arrayInd = arrayInd;
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
    public long getArrayInd() {
        return arrayInd;
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
     * Represet as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "JobIdentifier{" + 
            "type=" + type +
            ", jobId=" + jobId +
            ", arrayInd=" + arrayInd +
        '}';
    }
}
