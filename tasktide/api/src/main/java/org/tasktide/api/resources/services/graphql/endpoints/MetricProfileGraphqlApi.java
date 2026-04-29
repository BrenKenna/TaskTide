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

import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.GraphQLApi;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.api.resources.services.graphql.inputs.MetricProfileInput;
import org.tasktide.api.resources.services.graphql.context.RequestContext;


/**
 * GraphQL API against {@link MetricProfile}
 *
 * @author Bren
 */
@GraphQLApi
@ApplicationScoped
public class MetricProfileGraphqlApi {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(MetricProfileGraphqlApi.class);
    private final TaskTideService<MetricProfile> metricProfileService;
    
    @Inject
    RequestContext requestContext;

    public MetricProfileGraphqlApi() {
        this.metricProfileService = TaskTideServiceManager.fetchMetricProfileService();
    }
    
    
    /**
     * Get {@link MetricProfile} from {@link MetricProfileInput} query
     * 
     * @param query
     * @return {@link MetricProfile}
     */
    @Query("search-metric-profile")
    public MetricProfile getMetricProfile(MetricProfileInput query) {
        LOGGER.info(
            "GraphQL MetricProfiles Query from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getUsername(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        if (!requestContext.isUserInRole("user")) {
            throw new jakarta.ws.rs.ForbiddenException();
        }
        
        if ( query.id != null ) {
            return this.metricProfileService.fetchById(query.id);
        }
  
        else {
            String msg = String.format("Either metricProfileId or metricProfile name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg);
        }
    }
    
    
    /**
     * Delete {@link MetricProfile} from {@link MetricProfileInput}
     * 
     * @param query 
     */
    @Mutation("drop-metric-profile")
    public void dropMetricProfile(MetricProfileInput query) {
        LOGGER.info(
            "GraphQL MetricProfiles Mutation from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getUsername(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        if (!requestContext.isUserInRole("user")) {
            throw new jakarta.ws.rs.ForbiddenException();
        }
        
        if ( query.id == null ) {
            String msg = String.format("Either metricProfileId or metricProfile name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg); 
        }
        
        this.metricProfileService.dropById(query.id);
    }
}