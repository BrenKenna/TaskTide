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
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.api.services.graphql.inputs.StepInput;
import org.tasktide.api.services.graphql.context.RequestContext;


/**
 * GraphQL API against {@link Step}
 * 
 * @author Bren
 */
@GraphQLApi
@RolesAllowed("user")
@ApplicationScoped
public class StepGraphqlApi {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(StepGraphqlApi.class);
    private final TaskTideService<Step> stepService;
    
    @Inject
    RequestContext requestContext;

    public StepGraphqlApi() {
        this.stepService = TaskTideServiceManager.fetchStepService();
    }
    
    
    /**
     * Get {@link Step} from {@link StepInput} query
     * 
     * @param query
     * @return {@link Step}
     */
    @Query("search-step")
    public Step getStep(StepInput query) {
        LOGGER.info(
            "GraphQL Steps Query from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getJsonWebToken().getName(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        if ( query.stepId != null ) {
            return this.stepService.fetchById(query.stepId);
        }
        
        else if ( query.stepName != null ) {
            List<Step> steps = this.stepService.viewByField("StepName", query.stepName);
            if ( steps != null ) {
                return steps.get(0);
            }
        }
        
        else {
            String msg = String.format("Either stepId or step name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg);
        }
        return null;
    }
    
    
    /**
     * Delete {@link Step} from {@link StepInput}
     * 
     * @param query 
     */
    @Mutation("drop-step")
    public void dropStep(StepInput query) {
        LOGGER.info(
            "GraphQL Steps Mutation from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getJsonWebToken().getName(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        if ( query.stepId == null ) {
            String msg = String.format("Either stepId or step name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg); 
        }
        
        this.stepService.dropById(query.stepId);
    }
}