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
package org.tasktide.api.resources.services.graphql.endpoints;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.security.RolesAllowed;

import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.GraphQLApi;

import java.util.List;

import org.tasktide.api.resources.services.graphql.context.RequestContext;
import org.tasktide.api.resources.services.graphql.inputs.WorkflowInput;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.collection.Workflow;


/**
 * GraphQL API against {@link Workflow}
 *
 * @author Bren
 */
@GraphQLApi
@ApplicationScoped
public class WorkflowGraphqlApi {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(WorkflowGraphqlApi.class);
    private final TaskTideService<Workflow> workflowService;
    
    @Inject
    RequestContext requestContext;

    public WorkflowGraphqlApi() {
        this.workflowService = TaskTideServiceManager.fetchWorkflowService();
    }
    
    
    /**
     * Get {@link Workflow} from {@link WorkflowInput} query
     * 
     * @param query
     * @return {@link Workflow}
     */
    @Query("search-workflow")
    public Workflow getWorkflow(WorkflowInput query) {
        LOGGER.info(
            "GraphQL Workflow Query from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getUsername(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        if (!requestContext.isUserInRole("user")) {
            throw new jakarta.ws.rs.ForbiddenException();
        }
        
        if ( query.workflowId != null ) {
            return this.workflowService.fetchById(query.workflowId);
        }
        
        else if ( query.workflowName != null ) {
            List<Workflow> workflows = this.workflowService.viewByField("WorkflowName", query.workflowName);
            if ( workflows != null ) {
                return workflows.get(0);
            }
        }
        
        else {
            String msg = String.format("Either workflowId or workflow name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg);
        }
        return null;
    }
    
    
    /**
     * Delete {@link Workflow} from {@link WorkflowInput}
     * 
     * @param query 
     */
    @Mutation("drop-workflow")
    public void dropWorkflow(WorkflowInput query) {
        LOGGER.info(
            "GraphQL Workflow Mutation from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getUsername(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        if (!requestContext.isUserInRole("user")) {
            throw new jakarta.ws.rs.ForbiddenException();
        }
        
        if ( query.workflowId == null ) {
            String msg = String.format("Either workflowId or workflow name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg); 
        }
        
        this.workflowService.dropById(query.workflowId);
    }
}