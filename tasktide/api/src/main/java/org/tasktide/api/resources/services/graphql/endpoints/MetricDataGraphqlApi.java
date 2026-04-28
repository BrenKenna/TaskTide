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
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.api.resources.services.graphql.inputs.MetricDataInput;
import org.tasktide.api.resources.services.graphql.context.RequestContext;


/**
 * GraphQL API against {@link MetricData}
 *
 * @author Bren
 */
@GraphQLApi
@ApplicationScoped
public class MetricDataGraphqlApi {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(MetricDataGraphqlApi.class);
    private final TaskTideService<MetricData> metricDataService;
    
    @Inject
    RequestContext requestContext;

    public MetricDataGraphqlApi() {
        this.metricDataService = TaskTideServiceManager.fetchMetricDataService();
    }
    
    
    /**
     * Get {@link MetricData} from {@link MetricDataInput} query
     * 
     * @param query
     * @return {@link MetricData}
     */
    @Query("search-metric-data")
    public MetricData getMetricData(MetricDataInput query) {
        LOGGER.info(
            "GraphQL MetricDatas Query from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getUsername(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        if (!requestContext.isUserInRole("user")) {
            throw new jakarta.ws.rs.ForbiddenException();
        }
        
        if ( query.id != null ) {
            return this.metricDataService.fetchById(query.id);
        }
  
        else {
            String msg = String.format("Either metricDataId or metricData name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg);
        }
    }
    
    
    /**
     * Delete {@link MetricData} from {@link MetricDataInput}
     * 
     * @param query 
     */
    @Mutation("drop-metric-data")
    public void dropMetricData(MetricDataInput query) {
        LOGGER.info(
            "GraphQL MetricDatas Mutation from user '{}' for '{}' incoming from '{}', '{}'",
            this.requestContext.getUsername(), requestContext.getIp(), requestContext.getUserAgent()
        );
        
        
        if (!requestContext.isUserInRole("user")) {
            throw new jakarta.ws.rs.ForbiddenException();
        }
        
        
        if ( query.id == null ) {
            String msg = String.format("Either metricDataId or metricData name must be provided:\n'%s'", query);
            throw new GrapqlUncheckedException(msg); 
        }
        
        this.metricDataService.dropById(query.id);
    }
}