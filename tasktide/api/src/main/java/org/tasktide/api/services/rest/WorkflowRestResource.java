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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.enterprise.context.RequestScoped;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import java.util.List;

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
     * @param req
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add")
    public Response createWorkflow(Workflow workflow, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Adding new workflow request from '{}', '{}':\n\n'{}'",
            ip, userAgent, workflow
        );
        
        // Validate input
        if ( workflow == null ) {
            return Response.status(400).build();
        }
        
        // Add workflow
        if ( workflowService.appendModel(workflow) != null ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import below workflow:\n\n'%s'", workflow.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to import provided {@link Workflow} collection, returning
     *  whether the resource was created to the client
     * 
     * @param workflows
     * @param req
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add")
    public Response createWorkflows(List<Workflow> workflows, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Importing workflows request from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
        
        // Validate input
        if ( workflows == null ) {
            return Response.status(400).build();
        }
        
        // Add workflow
        if ( workflowService.extendModel(workflows) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import provided workflow collection");
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to create a new empty {@link Workflow}, returning
     *  created resource to the client
     * 
     * @param workflowName
     * @param stepId
     * @param req
     * 
     * @return {@link Response}
     */
    @POST
    @Path("add")
    public Response createWorkflow(
        @QueryParam("workflowName") String workflowName,
        @QueryParam("stepId") String stepId,
        @Context HttpServletRequest req
    ) {
        
        // Log request
        List<Workflow> result;
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Adding new workflow request from '{}', '{}':\n\n'{}'",
            ip, userAgent, workflowName
        );
        
        // Validate query params
        if ( workflowName == null ) {
            return Response.status(400, "Missing workflow name").build();
        }
        
        // Handle stepId
        if ( stepId == null ) {
            TaskTideManagerUtility.configureNewWorkflow(workflowName);
        }
        else {
            Step step = TaskTideServiceManager.fetchStepService().fetchById(stepId);
            if ( step == null ) {
                String msg = String.format(
                    "Could not create '%s' step for provided step Id:\t'%s'",
                    workflowName, stepId
                );
                return Response.status(404, msg).build();
            }
            TaskTideManagerUtility.configureNewWorkflow(workflowName, step);
        }
        
        // Fetch workload if present
        result = workflowService.viewByField("WorkflowName", workflowName);
        if ( result == null ) {
            String msg = String.format(
                "Could not verify creation of workflow:\t'%s'",
                workflowName
            );
            return Response.status(500, msg).build();
        }
        
        // Otherwise return to client
        else {
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
     * @param req
     * 
     * @return {@link Response}
     */
    @GET
    @Path("/get")
    public Response readWorkflow(
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
            "Get workflow request recieved from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
    
        // Handle workflow Id
        if ( id != null ) {
            Workflow workflow = workflowService.fetchById(id);
            if ( workflow != null ) {
                return Response.ok(workflow).build();
            }
            String msg = String.format("No workflow found for Id:\t'%s'", id);
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
                "No workflow found for Field = '%s', and Value = '%s'",
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
                "No workflow found for Field = '%s', Value = '%s', Grouping = '%s', GroupingValue = '%s'",
                field, value, grouping, groupingVal
            );
            return Response.status(404, msg).build();
        }
        
        // Otherwise bad method
        else {
            String msg = "A WorkflowId, or Field-Value, or Field-Value Grouping-Grouping Value must be provided";
            return Response.status(400, msg).build();
        }
    }
    
    
    /**
     * Endpoint for adding {@link Step} to a {@link Workflow},
     *  returning updated {@link Workflow}
     * 
     * @param workflowId
     * @param stepId
     * @param req
     * 
     * @return {@link Response}
     */
    @PATCH
    @Path("/addStep")
    public Response addStep(
       @QueryParam("workflowId") String workflowId,
       @QueryParam("stepId") String stepId,
       @Context HttpServletRequest req
    ) {
        
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Get workflow request recieved from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
        
        // Validate params
        if ( workflowId == null || stepId == null ) {
            return Response.status(400, "").build();
        }
        
        // Try fetch workflow
        Workflow workflow = workflowService.fetchById(workflowId);
        if ( workflow == null ) {
            String msg = String.format("Workflow not foud:\t'%s'", workflowId);
            return Response.status(404, msg).build();
        }
        
        // Try fetch step
        Step step = TaskTideServiceManager.fetchStepService().fetchById(stepId);
        if ( step == null ) {
            String msg = String.format("Step not foud:\t'%s'", stepId);
            return Response.status(404, msg).build();
        }
        
        // Add step to workflow
        Workflow result = ( (WorkflowService) workflowService ).addStepToWorkflow(workflow, step);
        if ( result != null ) {
            return Response.ok(result).build();
        }
        else {
            String msg = String.format("Unable to verify update of workflow '%s'", workflowId);
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint for dropping {@link Workflow} by Id
     * 
     * @param id
     * @param req
     * 
     * @return {@link Response}
     */
    @DELETE
    @Path("/drop/{id}")
    public Response dropWorkflow(@PathParam("id") String id, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Get workflow request recieved from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
        
        // Validate path param
        if ( id == null ) {
            return Response.status(400, "No workflow Id provided").build();
        }
        
        // Drop workflow
        if ( workflowService.dropById(id) ) {
            return Response.ok("Workflow deleted").build();
        }
        else {
            String msg = String.format("Server unable to verify deletion of workflow:\t'%s'", id);
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint for updating {@link Workflow} providing new resource
     * 
     * @param workflow
     * @param req
     * 
     * @return {@link Response}
     */
    @PUT
    @Path("/update")
    public Response updateWorkflow(Workflow workflow, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Get workflow request recieved from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
        
        // Validate path param
        if ( workflow == null ) {
            return Response.status(400, "No workflow Id provided").build();
        }
        
        // Update workflow
        Workflow updated = workflowService.updateModel(workflow);
        if ( updated != null ) {
            return Response.ok(updated).build();
        }
        else {
            String msg = String.format("Server verifying update of workflow:\t'%s'", workflow.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
}