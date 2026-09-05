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
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
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
import org.tasktide.core.manager.ManagerTask;
import org.tasktide.core.services.WorkItemService;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.task.ItemTask;

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
    public Response createWorkItem(
        WorkItem workItem,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Adding new WorkItem request by user '{}' from '{}' using '{}':\n\n'{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent, workItem
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // Validate input
        if ( workItem == null ) {
            return Response.status(400).build();
        }
        
        // Add workItem
        if ( workItemService.extendModel( List.of(workItem) ) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import below WorkItem:\n\n'%s'", workItem.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to import provided {@link WorkItem} collection, returning
     *  whether the resource was created to the client
     * 
     * @param workItems
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
    public Response createWorkItems(
        List<WorkItem> workItems,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Importing WorkItem collection request by user '{}' from '{}' using '{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // Validate input
        if ( workItems == null ) {
            return Response.status(400).build();
        }
        
        // Add workItem
        if ( workItemService.extendModel(workItems) ) {
            return Response.ok().build();
        }
        else {
            String msg = String.format("Unable to import provided WorkItem collection");
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint to create a new empty {@link WorkItem}, returning
     *  created resource to the client
     * 
     * @param managerTask
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createWorkItem(
        ManagerTask managerTask,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
        
        // Log request
        WorkItem result;
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Adding new WorkItem request by user '{}' from '{}' using '{}':\n\n'{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent, managerTask
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // Validate query params
        if ( managerTask == null ) {
            return Response.status(400, "Missing WorkItem name").build();
        }

        // Fetch workload if present
        WorkItem workItem = managerTask.asWorkItem();
        result = workItemService.appendModel(workItem);
        if ( result == null ) {
            String msg = String.format(
                "Could not verify creation of WorkItem:\t'%s'",
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
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readWorkItem(
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
            "Get WorkItem request by user '{}' from '{}' using '{}' for resource:\t'{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent, id
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    
        // Handle workItem Id
        if ( id != null ) {
            WorkItem workItem = workItemService.fetchById(id);
            if ( workItem != null ) {
                return Response.ok(workItem).build();
            }
            String msg = String.format("No WorkItem found for Id:\t'%s'", id);
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
                "No WorkItem found for Field = '%s', and Value = '%s'",
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
                "No WorkItem found for Field = '%s', Value = '%s', Grouping = '%s', GroupingValue = '%s'",
                field, value, grouping, groupingVal
            );
            return Response.status(404, msg).build();
        }
        
        // Otherwise bad method
        else {
            String msg = "A WorkItem.Id, or Field-Value, or Field-Value Grouping-Grouping Value must be provided";
            return Response.status(400, msg).build();
        }
    }
    
    
    /**
     * Endpoint for dropping {@link WorkItem} by Id
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
    public Response dropWorkItem(
        @PathParam("id") String id,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Drop WorkItem request by user '{}' from '{}' using '{}' for resource:\t'{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent, id
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // Validate path param
        if ( id == null ) {
            return Response.status(400, "No WorkItem.Id provided").build();
        }
        
        // Drop workItem
        if ( workItemService.dropById(id) ) {
            return Response.ok("WorkItem deleted").build();
        }
        else {
            String msg = String.format("Server unable to verify deletion of WorkItem:\t'%s'", id);
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint for updating {@link WorkItem} providing new resource
     * 
     * @param workItem
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
    public Response updateWorkItem(
        WorkItem workItem,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Update WorkItem request by user '{}' from '{}' using '{}' for resource:\n\n'{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent, workItem
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
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
            String msg = String.format("Server verifying update of WorkItem:\t'%s'", workItem.toJsonDoc());
            return Response.status(500, msg).build();
        }
    }
    
    
    /**
     * Endpoint for adding {@link ItemTask} to a {@link WorkItem},
     * 
     * @param itemTask
     * @param itemId
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @PUT
    @Path("/add-task")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTaskToWorkItem(
       ItemTask itemTask,
       @QueryParam("itemId") String itemId,
       @Context HttpHeaders reqHeader,
       @Context UriInfo uriInfo,
       @Context SecurityContext securityContext
    ) {
        
        // Log request
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Add ItemTask to WorkItem request by user '{}' from '{}' using '{}':\t'{}'",
            securityContext.getUserPrincipal().getName(), ip, userAgent, itemId
        );
        
        // Role can be configured
        if ( !WebApiUtils.checkSecurityContext(securityContext, "user") ) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        // Validate params
        LOGGER.info("Evaluating provided parameters");
        if ( itemTask == null || itemId == null ) {
            LOGGER.info("Invalid arguments for appending task to WorkItem:\t'{}'", itemId);
            LOGGER.info("Displaying ItemTask:\n\n'{}'", itemTask.toJsonDoc());
            return Response.status(400, "Invalid arguments for appending task to WorkItem").build();
        }
        
        // Try fetch work item
        LOGGER.info("Fetching workItem for:\t'{}'", itemId);
        WorkItem workItem = this.workItemService.fetchById(itemId);
        if ( workItem == null ) {
            String msg = String.format("WorkItem not foud:\t'%s'", itemId);
            return Response.status(404, msg).build();
        }
        
        // Add task to work item
        LOGGER.info("Adding task:\t'{}'", itemTask.getId());
        if ( workItem.addTask(itemTask) ) {
            workItem = this.workItemService.updateModel(workItem);
            if ( workItem != null ) {
                LOGGER.info("WorkItem updated:\t'{}'", itemId);
                return Response.ok(workItem).build();
            }
            else {
                LOGGER.info("Unable to verify update of WorkItem:\t'{}'", itemId);
                String msg = String.format("Unable to verify update of WorkItem '%s'", itemId);
                return Response.status(500, msg).build();
            }
        }
        else {
            LOGGER.info("Unable to append task to WorkItem:\t'{}'", itemId);
            String msg = String.format("Unable to verify update of WorkItem '%s'", itemId);
            return Response.status(500, msg).build();
        }
    }
}