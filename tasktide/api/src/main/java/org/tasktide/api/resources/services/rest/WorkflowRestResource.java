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
package org.tasktide.api.resources.services.rest;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import org.tasktide.api.utils.WebApiUtils;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.services.WorkflowService;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.manager.TaskTideManagerUtility;

import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.collection.Step;


/**
 * REST resource for interacting with {@link WorkflowService}
 *
 * @author Bren
 */
@Path("/services/workflow")
@RequestScoped
public class WorkflowRestResource {
 
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(WorkflowRestResource.class);
    private final TaskTideService<Workflow> workflowService;

    
    /**
     * Construct with {@link Workflow} {@link TaskTideService}
     * 
     */
    public WorkflowRestResource() {
        this.workflowService = TaskTideServiceManager.fetchWorkflowService();
    }
    
    
    /**
     * Endpoint to create provided {@link Workflow}, returning
     *  whether the resource was created to the client
     * 
     * @param workflow
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createWorkflow(
        Workflow workflow,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Adding new Workflow request by user '{}' from '{}' using '{}':\n\n'{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent, workflow
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // Validate input
        if ( workflow == null ) {
            return Response.status(400).build();
        }
        
        // Add workflow
        if ( workflowService.appendModel(workflow) != null ) {
            return Response.ok(workflow).build();
        }
        else {
            String msg = String.format("Unable to import below Workflow:\n\n'%s'", workflow.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to import provided {@link Workflow} collection, returning
     *  whether the resource was created to the client
     * 
     * @param workflows
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/import")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createWorkflows(
        List<Workflow> workflows,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Importing Workflow collection by user '{}' from '{}' using '{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // Validate input
        if ( workflows == null ) {
            return Response.status(400).build();
        }
        
        // Add workflow
        if ( workflowService.extendModel(workflows) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import provided Workflow collection");
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to create a new empty {@link Workflow}, returning
     *  created resource to the client
     * 
     * @param workflowName
     * @param stepId
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createWorkflow(
        @QueryParam("workflowName") String workflowName,
        @QueryParam("stepId") String stepId,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
        
        // Log request
        List<Workflow> result;
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Adding new Workflow request by user '{}' from '{}' using '{}' for resource:\t'{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent, workflowName
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // Validate query params
        if ( workflowName == null ) {
            return Response.status(400, "Missing Worfklow name").build();
        }
        
        // Handle stepId
        TaskTideManagerUtility.updateStepWorkflowIds();
        if ( stepId == null ) {
            LOGGER.info("Creating workflow:\t'{}'", workflowName);
            TaskTideManagerUtility.configureNewWorkflow(workflowName);
        }
        else {
            LOGGER.info("Creating workflow '{}' for step:\t'{}'", stepId);
            Step step = TaskTideServiceManager.fetchStepService().fetchById(stepId);
            if ( step == null ) {
                String msg = String.format(
                    "Could not create '%s' Step for provided Step.Id:\t'%s'",
                    workflowName, stepId
                );
                LOGGER.warn(msg);
                return Response.status(404, msg).build();
            }
            TaskTideManagerUtility.configureNewWorkflow(workflowName, step);
        }
        
        // Fetch workload if present
        LOGGER.info("Verifying creation of workflow:\t'{}'", workflowName);
        result = workflowService.viewByField("workflowName", workflowName);
        if ( result == null ) {
            String msg = String.format(
                "Could not verify creation of Workflow:\t'%s'",
                workflowName
            );
            LOGGER.warn(msg);
            return Response.status(500, msg).build();
        }
        
        // Otherwise return to client
        else {
            LOGGER.info("Returning created resource to client");
            return Response.ok(result.get(0)).build();
        }
    }
    
    
    /**
     * Endpoint for fetching {@link Workflow} by Id,
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response readWorkflow(
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
            "Get Workflow request by user '{}' from '{}' using '{}' for resource:\t'{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent, id
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // Handle workflow Id
        if ( id != null ) {
            Workflow workflow = workflowService.fetchById(id);
            if ( workflow != null ) {
                return Response.ok(workflow).build();
            }
            String msg = String.format("No Workflow found for Id:\t'%s'", id);
            return Response.status(404, msg).build();
        }
        
        // Handle field and value
        else if (
            ( field != null && value != null ) &&
            ( grouping == null && groupingVal == null )
        ) {
            List<Workflow> results = workflowService.viewByField(field, value);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No Workflow found for Field = '%s', and Value = '%s'",
                field, value
            );
            return Response.status(404, msg).build();
        }
        
        // Handle grouping on a field
        else if (
            (field != null && value != null) &&
            ( grouping != null && groupingVal != null )
        ) {
            List<Workflow> results = workflowService
                .viewByFieldForGroup(field, value, grouping, groupingVal);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No Workflow found for Field = '%s', Value = '%s', Grouping = '%s', GroupingValue = '%s'",
                field, value, grouping, groupingVal
            );
            return Response.status(404, msg).build();
        }
        
        // Otherwise bad method
        else {
            String msg = "A Workflow.Id, or Field-Value, or Field-Value Grouping-Grouping Value must be provided";
            return Response.status(400, msg).build();
        }
    }
    
    
    /**
     * Endpoint for adding {@link Step} to a {@link Workflow},
     *  returning updated {@link Workflow}
     * 
     * @param workflowId
     * @param stepId
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @PUT
    @Path("/add-step")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStep(
       @QueryParam("workflowId") String workflowId,
       @QueryParam("stepId") String stepId,
       @Context HttpHeaders reqHeader,
       @Context UriInfo uriInfo,
       @Context SecurityContext securityContext
    ) {
        
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Add step to Workflow request by user '{}' from '{}' using '{}':\n\nStep = '{}'\nWorkflow = '{}'\n",
            securityContext.getUserPrincipal().getName(), ip, userAgent, stepId, workflowId
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // Validate params
        LOGGER.info("Validating query parameters");
        if ( workflowId == null || stepId == null ) {
            return Response.status(400, "").build();
        }
        
        // Try fetch workflow
        LOGGER.info("Fetching workflow");
        Workflow workflow = workflowService.fetchById(workflowId);
        if ( workflow == null ) {
            String msg = String.format("Workflow not foud:\t'%s'", workflowId);
            return Response.status(404, msg).build();
        }
        
        // Try fetch step
        LOGGER.info("Fetching step");
        Step step = TaskTideServiceManager.fetchStepService().fetchById(stepId);
        if ( step == null ) {
            String msg = String.format("Step not foud:\t'%s'", stepId);
            return Response.status(404, msg).build();
        }
        
        // Clear old workflow reference
        LOGGER.info("Clearing step reference on old workflow:\n\nStepId='{}'\nWorkflowId='{}'\n", step.getId(), step.getWorkflowId());
        String oldWorkflowId = step.getWorkflowId();
        if ( oldWorkflowId != null ) {
           LOGGER.info("Fetching old workflow for Id:\t'{}'", oldWorkflowId);
           Workflow oldWorkflow = this.workflowService.fetchById(oldWorkflowId);
           LOGGER.info("Workflow retrieved");
           if ( oldWorkflow != null ) {
                oldWorkflow.getWorkflowSteps().remove(step.getId());
                LOGGER.info("Updating old workflow");
                this.workflowService.updateModel(oldWorkflow);
            }
           LOGGER.warn("No record workflow Id detected previously assigned to step:\t'{}',{}'", step.getId(), oldWorkflowId);
        }
        else {
            LOGGER.warn("No previous workflow allocation detected for:\t'{}'", step.getId());
        }
        
        
        // Add step to workflow
        LOGGER.info("Adding step to workflow");
        Workflow result = ( (WorkflowService) workflowService ).addStepToWorkflow(workflow, step);
        step.setWorkflowId(workflowId);
        TaskTideServiceManager
            .fetchStepService()
        .updateModel(step);
        
        if ( result != null ) {
            return Response.ok(result).build();
        }
        else {
            String msg = String.format("Unable to verify update of Workflow '%s'", workflowId);
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint for dropping {@link Workflow} by Id
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
    public Response dropWorkflow(
        @PathParam("id") String id,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Drop Workflow request by user '{}' from '{}' using '{}' for resource:\t'{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent, id
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // Validate path param
        if ( id == null ) {
            return Response.status(400, "No Workflow.Id provided").build();
        }
        
        // Drop workflow
        if ( workflowService.dropById(id) ) {
            return Response.ok("Workflow deleted").build();
        }
        else {
            String msg = String.format("Server unable to verify deletion of Workflow:\t'%s'", id);
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint for updating {@link Workflow} providing new resource
     * 
     * @param workflow
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateWorkflow(
        Workflow workflow,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Update Workflow request by user '{}' from '{}' using '{}':\n\n'{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent, workflow
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // Validate path param
        if ( workflow == null ) {
            return Response.status(400, "No Workflow.Id provided").build();
        }
        
        // Update workflow
        Workflow updated = workflowService.updateModel(workflow);
        if ( updated != null ) {
            return Response.ok(updated).build();
        }
        else {
            String msg = String.format("Server verifying update of Workflow:\t'%s'", workflow.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
}