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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.services.MetricProfileService;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.job_env.metrics.MetricProfile;


/**
 * REST resource for interacting with {@link MetricProfileService}
 *
 * @author Bren
 */
@RolesAllowed("user")
@Path("/services/metric-profile")
@RequestScoped
public class MetricProfileRestResource {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(MetricProfileRestResource.class);
    private final TaskTideService<MetricProfile> metricProfileService;
    
    @Inject
    JsonWebToken jwt;
    
    /**
     * Construct with {@link MetricProfile} {@link TaskTideService}
     * 
     */
    public MetricProfileRestResource() {
        this.metricProfileService = TaskTideServiceManager.fetchMetricProfileService();
    }
    
    
    /**
     * Endpoint to create provided {@link MetricProfile}, returning
     *  whether the resource was created to the client
     * 
     * @param metricProfile
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add")
    public Response createMetricProfile(
        MetricProfile metricProfile,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Adding new MetricProfile request by user '{}' from '{}' using '{}':\n\n'{}'",
            this.jwt.getName(), ip, userAgent, metricProfile
        );
        
        // Validate input
        if ( metricProfile == null ) {
            return Response.status(400).build();
        }
        
        // Add metricProfile
        if ( metricProfileService.extendModel( List.of(metricProfile) ) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import below MetricProfile:\n\n'%s'", metricProfile.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to import provided {@link MetricProfile} collection, returning
     *  whether the resource was created to the client
     * 
     * @param metricProfiles
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add")
    public Response createMetricProfiles(
        List<MetricProfile> metricProfiles,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Import MetricProfile collection request by user '{}' from '{}' using '{}'",
            this.jwt.getName(), ip, userAgent
        );
        
        // Validate input
        if ( metricProfiles == null ) {
            return Response.status(400).build();
        }
        
        // Add metricProfile
        if ( metricProfileService.extendModel(metricProfiles) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import provided MetricProfile collection");
            return Response.status(500, msg).build();
        }
    }

    
    /**
     * Endpoint for fetching {@link MetricProfile} by Id,
     *  field having value, or field having value for a
     *  group having a value
     * 
     * @param id
     * @param field
     * @param value
     * @param grouping
     * @param groupingVal
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @GET
    @Path("/get")
    public Response readMetricProfile(
        @QueryParam("id") String id,
        @QueryParam("field") String field,
        @QueryParam("value") String value,
        @QueryParam("grouping") String grouping,
        @QueryParam("groupingValue") String groupingVal,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
        
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Get MetricProfile collection request by user '{}' from '{}' using '{}' for resource:\t'{}'",
            this.jwt.getName(), ip, userAgent, id
        );
    
        // Handle metricProfile Id
        if ( id != null ) {
            MetricProfile metricProfile = metricProfileService.fetchById(id);
            if ( metricProfile != null ) {
                return Response.ok(metricProfile).build();
            }
            String msg = String.format("No MetricProfile found for Id:\t'%s'", id);
            return Response.status(404, msg).build();
        }
        
        // Handle field and value
        else if (
            ( field != null && value != null ) &&
            ( grouping == null && groupingVal == null )
        ) {
            List<MetricProfile> results = metricProfileService.viewByField(field, value);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No MetricProfile found for Field = '%s', and Value = '%s'",
                field, value
            );
            return Response.status(404, msg).build();
        }
        
        // Handle grouping on a field
        else if (
            (field != null && value != null) &&
            ( grouping != null && groupingVal != null )
        ) {
            List<MetricProfile> results = metricProfileService
                .viewByFieldForGroup(field, value, grouping, groupingVal);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No MetricProfile found for Field = '%s', Value = '%s', Grouping = '%s', GroupingValue = '%s'",
                field, value, grouping, groupingVal
            );
            return Response.status(404, msg).build();
        }
        
        // Otherwise bad method
        else {
            String msg = "A MetricProfile.Id, or Field-Value, or Field-Value Grouping-Grouping Value must be provided";
            return Response.status(400, msg).build();
        }
    }
    
    
    /**
     * Endpoint for dropping {@link MetricProfile} by Id
     * 
     * @param id
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @DELETE
    @Path("/drop/{id}")
    public Response dropMetricProfile(
        @PathParam("id") String id,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Drop MetricProfile collection request by user '{}' from '{}' using '{}' for resource:\t'{}'",
            this.jwt.getName(), ip, userAgent, id
        );
        
        // Validate path param
        if ( id == null ) {
            return Response.status(400, "No MetricProfile.Id provided").build();
        }
        
        // Drop metricProfile
        if ( metricProfileService.dropById(id) ) {
            return Response.ok("MetricProfile deleted").build();
        }
        else {
            String msg = String.format("Server unable to verify deletion of MetricProfile:\t'%s'", id);
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint for updating {@link MetricProfile} providing new resource
     * 
     * @param metricProfile
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @PUT
    @Path("/update")
    public Response updateMetricProfile(
        MetricProfile metricProfile,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Update MetricProfile collection request by user '{}' from '{}' using '{}' for resource:\n\n'{}'",
            this.jwt.getName(), ip, userAgent, metricProfile
        );
        
        // Validate path param
        if ( metricProfile == null ) {
            return Response.status(400, "No MetricProfile.Id provided").build();
        }
        
        // Update metricProfile
        MetricProfile updated = metricProfileService.updateModel(metricProfile);
        if ( updated != null ) {
            return Response.ok(updated).build();
        }
        else {
            String msg = String.format("Server verifying update of MetricProfile:\t'%s'", metricProfile.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
}