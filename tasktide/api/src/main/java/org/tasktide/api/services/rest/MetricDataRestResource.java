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
package org.tasktide.api.services.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.enterprise.context.RequestScoped;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.annotation.security.RolesAllowed;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.services.MetricDataService;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.job_env.metrics.MetricData;


/**
 * REST resource for interacting with {@link MetricDataService}
 * 
 * @author Bren
 */
@RolesAllowed("user")
@Path("/services/metric-data")
@RequestScoped
public class MetricDataRestResource {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(MetricDataRestResource.class);
    private final TaskTideService<MetricData> metricDataService;
    
    @Inject
    JsonWebToken jwt;
    
    /**
     * Construct with {@link MetricData} {@link TaskTideService}
     * 
     */
    public MetricDataRestResource() {
        this.metricDataService = TaskTideServiceManager.fetchMetricDataService();
    }
    
    
    /**
     * Endpoint to create provided {@link MetricData}, returning
     *  whether the resource was created to the client
     * 
     * @param metricData
     * @param req
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add")
    public Response createMetricData(MetricData metricData, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Adding new MetricData request by user '{}' from '{}' using '{}':\n\n'{}'",
            this.jwt.getName(), ip, userAgent, metricData
        );
        
        // Validate input
        if ( metricData == null ) {
            return Response.status(400).build();
        }
        
        // Add metricData
        if ( metricDataService.extendModel( List.of(metricData) ) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import below MetricData:\n\n'%s'", metricData.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to import provided {@link MetricData} collection, returning
     *  whether the resource was created to the client
     * 
     * @param metricDatas
     * @param req
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add")
    public Response createMetricDatas(List<MetricData> metricDatas, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Importing MetricData collection request by user '{}' from '{}' using '{}'",
            this.jwt.getName(), ip, userAgent
        );
        
        // Validate input
        if ( metricDatas == null ) {
            return Response.status(400).build();
        }
        
        // Add metricData
        if ( metricDataService.extendModel(metricDatas) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import provided MetricData collection");
            return Response.status(500, msg).build();
        }
    }

    
    /**
     * Endpoint for fetching {@link MetricData} by Id,
     *  field having value, or field having value for a
     *  group having a value
     * 
     * @param id
     * @param field
     * @param value
     * @param grouping
     * @param groupingVal
     * @param req
     * 
     * @return {@link Response}
     */
    @GET
    @Path("/get")
    public Response readMetricData(
        @QueryParam("id") String id,
        @QueryParam("field") String field,
        @QueryParam("value") String value,
        @QueryParam("grouping") String grouping,
        @QueryParam("groupingValue") String groupingVal,
        @Context HttpServletRequest req
    ) {
        
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Get MetricData request by user '{}' from '{}' using '{}' for resource:\t'{}'",
            this.jwt.getName(), ip, userAgent, id
        );
    
        // Handle metricData Id
        if ( id != null ) {
            MetricData metricData = metricDataService.fetchById(id);
            if ( metricData != null ) {
                return Response.ok(metricData).build();
            }
            String msg = String.format("No MetricData found for Id:\t'%s'", id);
            return Response.status(404, msg).build();
        }
        
        // Handle field and value
        else if (
            ( field != null && value != null ) &&
            ( grouping == null && groupingVal == null )
        ) {
            List<MetricData> results = metricDataService.viewByField(field, value);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No MetricData found for Field = '%s', and Value = '%s'",
                field, value
            );
            return Response.status(404, msg).build();
        }
        
        // Handle grouping on a field
        else if (
            (field != null && value != null) &&
            ( grouping != null && groupingVal != null )
        ) {
            List<MetricData> results = metricDataService
                .viewByFieldForGroup(field, value, grouping, groupingVal);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No MetricData found for Field = '%s', Value = '%s', Grouping = '%s', GroupingValue = '%s'",
                field, value, grouping, groupingVal
            );
            return Response.status(404, msg).build();
        }
        
        // Otherwise bad method
        else {
            String msg = "A MetricData.Id, or Field-Value, or Field-Value Grouping-Grouping Value must be provided";
            return Response.status(400, msg).build();
        }
    }
    
    
    /**
     * Endpoint for dropping {@link MetricData} by Id
     * 
     * @param id
     * @param req
     * 
     * @return {@link Response}
     */
    @DELETE
    @Path("/drop/{id}")
    public Response dropMetricData(@PathParam("id") String id, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Drop MetricData request by user '{}' from '{}' using '{}' for resource:\t'{}'",
            this.jwt.getName(), ip, userAgent, id
        );
        
        // Validate path param
        if ( id == null ) {
            return Response.status(400, "No MetricData Id provided").build();
        }
        
        // Drop metricData
        if ( metricDataService.dropById(id) ) {
            return Response.ok("MetricData deleted").build();
        }
        else {
            String msg = String.format("Server unable to verify deletion of MetricData:\t'%s'", id);
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint for updating {@link MetricData} providing new resource
     * 
     * @param metricData
     * @param req
     * 
     * @return {@link Response}
     */
    @PUT
    @Path("/update")
    public Response updateMetricData(MetricData metricData, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Update MetricData request by user '{}' from '{}' using '{}'\n\n'{}'",
            this.jwt.getName(), ip, userAgent, metricData
        );
        
        // Validate path param
        if ( metricData == null ) {
            return Response.status(400, "No MetricData Id provided").build();
        }
        
        // Update metricData
        MetricData updated = metricDataService.updateModel(metricData);
        if ( updated != null ) {
            return Response.ok(updated).build();
        }
        else {
            String msg = String.format("Server verifying update of MetricData:\t'%s'", metricData.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
}