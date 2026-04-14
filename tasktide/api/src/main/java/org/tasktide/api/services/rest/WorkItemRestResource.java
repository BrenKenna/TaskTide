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
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.ManagerTask;
import org.tasktide.core.services.WorkItemService;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.workitem.WorkItem;


/**
 * REST resource for interacting with {@link WorkItemService}
 *
 * @author Bren
 */
@Path("/services/workitem")
@RequestScoped
public class WorkItemRestResource {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(WorkItemRestResource.class);
    private final TaskTideService<WorkItem> workItemService;
    
    
    /**
     * Construct with {@link WorkItem} {@link TaskTideService}
     * 
     */
    public WorkItemRestResource() {
        this.workItemService = TaskTideServiceManager.fetchWorkItemService();
    }
    
    
    /**
     * Endpoint to create provided {@link WorkItem}, returning
     *  whether the resource was created to the client
     * 
     * @param workItem
     * @param req
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add")
    public Response createWorkItem(WorkItem workItem, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Adding new workItem request from '{}', '{}':\n\n'{}'",
            ip, userAgent, workItem
        );
        
        // Validate input
        if ( workItem == null ) {
            return Response.status(400).build();
        }
        
        // Add workItem
        if ( workItemService.extendModel( List.of(workItem) ) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import below workItem:\n\n'%s'", workItem.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to import provided {@link WorkItem} collection, returning
     *  whether the resource was created to the client
     * 
     * @param workItems
     * @param req
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/add")
    public Response createWorkItems(List<WorkItem> workItems, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Importing workItems request from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
        
        // Validate input
        if ( workItems == null ) {
            return Response.status(400).build();
        }
        
        // Add workItem
        if ( workItemService.extendModel(workItems) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import provided workItem collection");
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to create a new empty {@link WorkItem}, returning
     *  created resource to the client
     * 
     * @param managerTask
     * @param req
     * 
     * @return {@link Response}
     */
    @POST
    @Path("add")
    public Response createWorkItem(
        ManagerTask managerTask,
        @Context HttpServletRequest req
    ) {
        
        // Log request
        WorkItem result;
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Adding new workItem request from '{}', '{}':\n\n'{}'",
            ip, userAgent, managerTask
        );
        
        // Validate query params
        if ( managerTask == null ) {
            return Response.status(400, "Missing workItem name").build();
        }

        // Fetch workload if present
        WorkItem workItem = managerTask.asWorkItem();
        result = workItemService.appendModel(workItem);
        if ( result == null ) {
            String msg = String.format(
                "Could not verify creation of workItem:\t'%s'",
                workItem.getItemName()
            );
            return Response.status(500, msg).build();
        }
        
        // Otherwise return to client
        else {
            return Response.ok(result).build();
        }
    }
    
    
    /**
     * Endpoint for fetching {@link WorkItem} by Id,
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
    public Response readWorkItem(
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
            "Get workItem request recieved from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
    
        // Handle workItem Id
        if ( id != null ) {
            WorkItem workItem = workItemService.fetchById(id);
            if ( workItem != null ) {
                return Response.ok(workItem).build();
            }
            String msg = String.format("No workItem found for Id:\t'%s'", id);
            return Response.status(404, msg).build();
        }
        
        // Handle field and value
        else if (
            ( field != null && value != null ) &&
            ( grouping == null && groupingVal == null )
        ) {
            List<WorkItem> results = workItemService.viewByField(field, value);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No workItem found for Field = '%s', and Value = '%s'",
                field, value
            );
            return Response.status(404, msg).build();
        }
        
        // Handle grouping on a field
        else if (
            (field != null && value != null) &&
            ( grouping != null && groupingVal != null )
        ) {
            List<WorkItem> results = workItemService
                .viewByFieldForGroup(field, value, grouping, groupingVal);
            if ( results != null ) {
                if ( !results.isEmpty() ) {
                    return Response.ok(results).build();
                }
            }
            
            String msg = String.format(
                "No workItem found for Field = '%s', Value = '%s', Grouping = '%s', GroupingValue = '%s'",
                field, value, grouping, groupingVal
            );
            return Response.status(404, msg).build();
        }
        
        // Otherwise bad method
        else {
            String msg = "A WorkItemId, or Field-Value, or Field-Value Grouping-Grouping Value must be provided";
            return Response.status(400, msg).build();
        }
    }
    
    
    /**
     * Endpoint for dropping {@link WorkItem} by Id
     * 
     * @param id
     * @param req
     * 
     * @return {@link Response}
     */
    @DELETE
    @Path("/drop/{id}")
    public Response dropWorkItem(@PathParam("id") String id, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Get workItem request recieved from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
        
        // Validate path param
        if ( id == null ) {
            return Response.status(400, "No workItem Id provided").build();
        }
        
        // Drop workItem
        if ( workItemService.dropById(id) ) {
            return Response.ok("WorkItem deleted").build();
        }
        else {
            String msg = String.format("Server unable to verify deletion of workItem:\t'%s'", id);
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint for updating {@link WorkItem} providing new resource
     * 
     * @param workItem
     * @param req
     * 
     * @return {@link Response}
     */
    @PUT
    @Path("/update")
    public Response updateWorkItem(WorkItem workItem, @Context HttpServletRequest req) {
    
        // Log request
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Update workItem request recieved from '{}', '{}':\n\n'{}'",
            ip, userAgent
        );
        
        // Validate path param
        if ( workItem == null ) {
            return Response.status(400, "No workItem Id provided").build();
        }
        
        // Update workItem
        WorkItem updated = workItemService.updateModel(workItem);
        if ( updated != null ) {
            return Response.ok(updated).build();
        }
        else {
            String msg = String.format("Server verifying update of workItem:\t'%s'", workItem.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
}