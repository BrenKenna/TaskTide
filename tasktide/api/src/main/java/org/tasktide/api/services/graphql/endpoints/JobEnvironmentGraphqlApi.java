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
package org.tasktide.api.services.graphql.endpoints;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.GraphQLApi;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.api.services.graphql.context.RequestContext;
import org.tasktide.api.services.graphql.inputs.JobEnvironmentInput;


/**
 * GraphQL API against {@link JobEnvironment}
 *
 * @author Bren
 */
@GraphQLApi
@RolesAllowed("user")
@ApplicationScoped
public class JobEnvironmentGraphqlApi {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(JobEnvironmentGraphqlApi.class);
    private final TaskTideService<JobEnvironment> jobEnvironmentService;
    
    @Inject
    RequestContext requestContext;

    public JobEnvironmentGraphqlApi() {
        this.jobEnvironmentService = TaskTideServiceManager.fetchJobEnvironmentService();
    }
    
    
    /**
     * Get {@link JobEnvironment} from {@link JobEnvironmentInput} query
     * 
     * @param query
     * @return {@link JobEnvironment}
     */
    @Query("search-job-environment")
    public JobEnvironment getJobEnvironment(JobEnvironmentInput query) {
        LOGGER.info(
            "GraphQL JobEnvironments Query from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getJsonWebToken().getName(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        if ( query.id != null ) {
            return this.jobEnvironmentService.fetchById(query.id);
        }
        
        else {
            String msg = String.format("Either jobEnvironmentId or jobEnvironment name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg);
        }
    }
    
    
    /**
     * Delete {@link JobEnvironment} from {@link JobEnvironmentInput}
     * 
     * @param query 
     */
    @Mutation("drop-job-environment")
    public void dropJobEnvironment(JobEnvironmentInput query) {
        LOGGER.info(
            "GraphQL JobEnvironments Mutation from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getJsonWebToken().getName(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        if ( query.id == null ) {
            String msg = String.format("Either jobEnvironmentId or jobEnvironment name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg); 
        }
        
        this.jobEnvironmentService.dropById(query.id);
    }
}