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

import java.util.List;

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

import org.tasktide.core.TaskTideService;
import org.tasktide.core.services.JobEnvironmentService;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.job_env.JobEnvironment;


/**
 * REST resource for interacting with {@link JobEnvironmentService}
 *
 * @author Bren
 */
@RolesAllowed("user")
@Path("/services/metric-data")
@RequestScoped
public class JobEnvironmentRestResource {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(JobEnvironmentRestResource.class);
    private final TaskTideService<JobEnvironment> jobEnvironmentService;
    
    @Inject
    JsonWebToken jwt;
    
    /**
     * Construct with {@link JobEnvironment} {@link TaskTideService}
     * 
     */
    public JobEnvironmentRestResource() {
        this.jobEnvironmentService = TaskTideServiceManager.fetchJobEnvironmentService();
    }
    
    
    /**
     * Endpoint to create provided {@link JobEnvironment}, returning
     *  whether the resource was created to the client
     * 
     * @param jobEnvironment
     * @param req
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add")
    public Response createJobEnvironment(JobEnvironment jobEnvironment, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Adding new JobEnvironment request by user '{}' from '{}' using '{}':\n\n'{}'",
            this.jwt.getName(), ip, userAgent, jobEnvironment
        );
        
        // Validate input
        if ( jobEnvironment == null ) {
            return Response.status(400).build();
        }
        
        // Add jobEnvironment
        if ( jobEnvironmentService.extendModel( List.of(jobEnvironment) ) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import below JobEnvironment:\n\n'%s'", jobEnvironment.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to import provided {@link JobEnvironment} collection, returning
     *  whether the resource was created to the client
     * 
     * @param jobEnvironments
     * @param req
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add")
    public Response createJobEnvironments(List<JobEnvironment> jobEnvironments, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Importing JobEnvironment collection request by user '{}' from '{}' using '{}'",
            this.jwt.getName(), ip, userAgent
        );
        
        // Validate input
        if ( jobEnvironments == null ) {
            return Response.status(400).build();
        }
        
        // Add jobEnvironment
        if ( jobEnvironmentService.extendModel(jobEnvironments) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import provided JobEnvironment collection");
            return Response.status(500, msg).build();
        }
    }

    
    /**
     * Endpoint for fetching {@link JobEnvironment} by Id,
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
    public Response readJobEnvironment(
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
            "Get JobEnvironment request by user '{}' from '{}' using '{}' for resource:\t'{}'",
            this.jwt.getName(), ip, userAgent, id
        );
    
        // Handle jobEnvironment Id
        if ( id != null ) {
            JobEnvironment jobEnvironment = jobEnvironmentService.fetchById(id);
            if ( jobEnvironment != null ) {
                return Response.ok(jobEnvironment).build();
            }
            String msg = String.format("No JobEnvironment found for Id:\t'%s'", id);
            return Response.status(404, msg).build();
        }
        
        // Handle field and value
        else if (
            ( field != null && value != null ) &&
            ( grouping == null && groupingVal == null )
        ) {
            List<JobEnvironment> results = jobEnvironmentService.viewByField(field, value);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No JobEnvironment found for Field = '%s', and Value = '%s'",
                field, value
            );
            return Response.status(404, msg).build();
        }
        
        // Handle grouping on a field
        else if (
            (field != null && value != null) &&
            ( grouping != null && groupingVal != null )
        ) {
            List<JobEnvironment> results = jobEnvironmentService
                .viewByFieldForGroup(field, value, grouping, groupingVal);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No JobEnvironment found for Field = '%s', Value = '%s', Grouping = '%s', GroupingValue = '%s'",
                field, value, grouping, groupingVal
            );
            return Response.status(404, msg).build();
        }
        
        // Otherwise bad method
        else {
            String msg = "A JobEnvironment.Id, or Field-Value, or Field-Value Grouping-Grouping Value must be provided";
            return Response.status(400, msg).build();
        }
    }
    
    
    /**
     * Endpoint for dropping {@link JobEnvironment} by Id
     * 
     * @param id
     * @param req
     * 
     * @return {@link Response}
     */
    @DELETE
    @Path("/drop/{id}")
    public Response dropJobEnvironment(@PathParam("id") String id, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Drop JobEnvironment request by user '{}' from '{}' using '{}'`for resource:\t\t'{}'",
            this.jwt.getName(), ip, userAgent, id
        );
        
        // Validate path param
        if ( id == null ) {
            return Response.status(400, "No JobEnvironment.Id provided").build();
        }
        
        // Drop jobEnvironment
        if ( jobEnvironmentService.dropById(id) ) {
            return Response.ok("JobEnvironment deleted").build();
        }
        else {
            String msg = String.format("Server unable to verify deletion of JobEnvironment:\t'%s'", id);
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint for updating {@link JobEnvironment} providing new resource
     * 
     * @param jobEnvironment
     * @param req
     * 
     * @return {@link Response}
     */
    @PUT
    @Path("/update")
    public Response updateJobEnvironment(JobEnvironment jobEnvironment, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Update JobEnvironment request by user '{}' from '{}' using '{}'`for resource:\n\n'{}'",
            this.jwt.getName(), ip, userAgent, jobEnvironment
        );
        
        // Validate path param
        if ( jobEnvironment == null ) {
            return Response.status(400, "No JobEnvironment.Id provided").build();
        }
        
        // Update jobEnvironment
        JobEnvironment updated = jobEnvironmentService.updateModel(jobEnvironment);
        if ( updated != null ) {
            return Response.ok(updated).build();
        }
        else {
            String msg = String.format("Server verifying update of JobEnvironment:\t'%s'", jobEnvironment.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
}