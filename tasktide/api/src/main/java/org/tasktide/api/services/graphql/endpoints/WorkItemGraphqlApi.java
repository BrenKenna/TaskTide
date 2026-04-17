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

import java.util.List;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.api.services.graphql.context.RequestContext;
import org.tasktide.api.services.graphql.inputs.WorkItemInput;


/**
 *
 * @author Bren
 */
@GraphQLApi
@RolesAllowed("users")
@ApplicationScoped
public class WorkItemGraphqlApi {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(WorkItemGraphqlApi.class);
    private final TaskTideService<WorkItem> workItemService;
    
    @Inject
    RequestContext requestContext;

    public WorkItemGraphqlApi() {
        this.workItemService = TaskTideServiceManager.fetchWorkItemService();
    }
    
    
    /**
     * Get {@link WorkItem} from {@link WorkItemInput} query
     * 
     * @param query
     * @return {@link WorkItem}
     */
    @Query("search-workitem")
    public WorkItem getWorkItem(WorkItemInput query) {
        LOGGER.info(
            "GraphQL WorkItem Query from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getJsonWebToken().getName(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        if ( query.workItemId != null ) {
            return this.workItemService.fetchById(query.workItemId);
        }
        
        else if ( query.workItemName != null ) {
            List<WorkItem> workItems = this.workItemService.viewByField("WorkItemName", query.workItemName);
            if ( workItems != null ) {
                return workItems.get(0);
            }
        }
        
        else {
            String msg = String.format("Either workItemId or workItem name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg);
        }
        return null;
    }
    
    
    /**
     * Delete {@link WorkItem} from {@link WorkItemInput}
     * 
     * @param query 
     */
    @Mutation("drop-workitem")
    public void dropWorkItem(WorkItemInput query) {
        LOGGER.info(
            "GraphQL WorkItem Mutation from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getJsonWebToken().getName(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        if ( query.workItemId == null ) {
            String msg = String.format("Either workItemId or workItem name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg); 
        }
        
        this.workItemService.dropById(query.workItemId);
    }
}