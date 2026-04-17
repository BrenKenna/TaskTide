/*
 * Copyright 2026 Bren.
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
package org.tasktide.api.services.graphql.inputs;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.Input;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.builders.JobEnvironmentBuilder;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.JobState;

import org.tasktide.core.model.job_env.JobType;
import org.tasktide.core.supporting.JsonUtils;


/**
 * Data model for GraphQL for {@link JobEnvironment}
 *
 * @author Bren
 */
@Input
@Description("GraphQL Data Model for TaskTide-JobEnvironment")
public class JobEnvironmentInput {

    private final JobEnvironmentBuilder jobEnvBuilder = new JobEnvironmentBuilder();
    
    public String id, jobId,
        hostname, hostOS, javaVersion;
    
    @Description("Job array index if relevant")
    public int arrayInd;
    
    @Description("JobType of Job Environment:\tSLURM,Hadoop etc")
    public JobType jobType;
    
    @Description("Job environment state")
    public JobState jobState;
    
    @Description("Annotation field")
    public String anno;
    
    
    /**
     * Parse the JSON string annotation field
     *  to {@link CustomAnnotation}
     * 
     * @return {@link CustomAnnotation}
     */
    public CustomAnnotation parseAnnotation() {
        return JsonUtils.fromJson(anno, CustomAnnotation.class);
    }
    
    
    /**
     * Represent as {@link JobEnvironment}
     * 
     * @return {@link JobEnvironment}
     */
    public JobEnvironment asJobEnv() {
        return this.jobEnvBuilder
            .withId(id)
            .withJobType(jobType)
            .withJobId(jobId)
            .withJobInd(arrayInd)
            .withHostOS(hostOS)
            .withJavaVersion(javaVersion)
            .withJobState(jobState)
            .withCustomAnnotation(this.parseAnnotation())
        .build();
    }
}