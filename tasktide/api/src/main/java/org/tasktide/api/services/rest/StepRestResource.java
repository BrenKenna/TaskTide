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

import jakarta.annotation.security.RolesAllowed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
// import org.eclipse.microprofile.jwt.JsonWebToken;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.services.StepService;
import org.tasktide.core.manager.TaskTideManagerUtility;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.collection.Step;

import org.tasktide.api.TaskTideRestApi;


/**
 * REST resource for interacting with {@link StepService}
 *
 * @author Bren
 */
@RequestScoped
@RolesAllowed("user")
@Path("/services/step")
public class StepRestResource extends TaskTideRestApi {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(StepRestResource.class);
    private final TaskTideService<Step> stepService;
    
    // @Inject
    //JsonWebToken jwt;
    
    /**
     * Construct with {@link Step} {@link TaskTideService}
     * 
     */
    public StepRestResource() {
        LOGGER.info("Step resource created");
        this.stepService = TaskTideServiceManager.fetchStepService();
    }
    
    
    /**
     * Endpoint to create provided {@link Step}, returning
     *  whether the resource was created to the client
     * 
     * @param step
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add-step")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createStep(
        Step step,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
        
        // Validate input
        if ( step == null ) {
            return Response.status(405).build();
        }
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Adding new Step request by user '{}' from '{}' using '{}':\n\n'{}'",
            securityContext.getUserPrincipal(), ip, userAgent, step.toJsonDoc()
        );

        
        // Add step
        if ( stepService.appendModel(step) != null ) {
            return Response.ok(step).build();
        }
        else {
            String msg = String.format("Unable to import below Step:\n\n'%s'", step.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to import provided {@link Step} collection, returning
     *  whether the resource was created to the client
     * 
     * @param steps
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add-steps")
    public Response createSteps(
        List<Step> steps,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Importing Step collection request from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
        
        // Validate input
        if ( steps == null ) {
            return Response.status(400).build();
        }
        
        // Add step
        if ( stepService.extendModel(steps) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import provided Step collection");
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to create a new empty {@link Step}, returning
     *  created resource to the client
     * 
     * @param stepName
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/create-step")
    public Response createStep(
        @QueryParam("stepName") String stepName,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
        
        // Log request
        List<Step> result;
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Adding new Step request from '{}', '{}':\n\n'{}'",
            ip, userAgent, stepName
        );
        
        // Validate query params
        if ( stepName == null ) {
            return Response.status(400, "Missing Step name").build();
        }

        // Fetch workload if present
        TaskTideManagerUtility.configureNewStep(stepName);
        result = stepService.viewByField("StepName", stepName);
        if ( result == null ) {
            String msg = String.format(
                "Could not verify creation of Step:\t'%s'",
                stepName
            );
            return Response.status(500, msg).build();
        }
        
        // Otherwise return to client
        else {
            return Response.ok(result.get(0)).build();
        }
    }
    
    
    /**
     * Endpoint for fetching {@link Step} by Id,
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
    public Response readStep(
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
            "Get Step request recieved from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
    
        // Handle step Id
        if ( id != null ) {
            Step step = stepService.fetchById(id);
            if ( step != null ) {
                return Response.ok(step).build();
            }
            String msg = String.format("No Step found for Id:\t'%s'", id);
            return Response.status(404, msg).build();
        }
        
        // Handle field and value
        else if (
            ( field != null && value != null ) &&
            ( grouping == null && groupingVal == null )
        ) {
            List<Step> results = stepService.viewByField(field, value);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No Step found for Field = '%s', and Value = '%s'",
                field, value
            );
            return Response.status(404, msg).build();
        }
        
        // Handle grouping on a field
        else if (
            (field != null && value != null) &&
            ( grouping != null && groupingVal != null )
        ) {
            List<Step> results = stepService
                .viewByFieldForGroup(field, value, grouping, groupingVal);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No Step found for Field = '%s', Value = '%s', Grouping = '%s', GroupingValue = '%s'",
                field, value, grouping, groupingVal
            );
            return Response.status(404, msg).build();
        }
        
        // Otherwise bad method
        else {
            String msg = "A Step.Id, or Field-Value, or Field-Value Grouping-Grouping Value must be provided";
            return Response.status(400, msg).build();
        }
    }
    
    
    /**
     * Endpoint for dropping {@link Step} by Id
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
    public Response dropStep(
        @PathParam("id") String id,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Get Step request recieved from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
        
        // Validate path param
        if ( id == null ) {
            return Response.status(400, "No Step.Id provided").build();
        }
        
        // Drop step
        if ( stepService.dropById(id) ) {
            return Response.ok("Step deleted").build();
        }
        else {
            String msg = String.format("Server unable to verify deletion of Step:\t'%s'", id);
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint for updating {@link Step} providing new resource
     * 
     * @param step
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @PUT
    @Path("/update")
    public Response updateStep(
        Step step,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Update Step request recieved from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
        
        // Validate path param
        if ( step == null ) {
            return Response.status(400, "No Step.Id provided").build();
        }
        
        // Update step
        Step updated = stepService.updateModel(step);
        if ( updated != null ) {
            return Response.ok(updated).build();
        }
        else {
            String msg = String.format("Server verifying update of Step:\t'%s'", step.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
}