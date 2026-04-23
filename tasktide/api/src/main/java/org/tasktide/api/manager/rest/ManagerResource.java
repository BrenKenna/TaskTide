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
package org.tasktide.api.manager.rest;

import jakarta.inject.Inject;
import jakarta.enterprise.context.RequestScoped;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.commands.ResetCommand;
import org.tasktide.core.manager.command.commands.DeleteCommand;
import org.tasktide.core.manager.command.commands.ExportCommand;
import org.tasktide.core.manager.command.commands.ImportCommand;
import org.tasktide.core.manager.command.commands.SummarizeCommand;
import org.tasktide.core.manager.command.commands.AnnotateCommand;


/**
 * {@link ManagerCommand} REST resource
 *
 * @author Bren
 */
// @DeclareRoles("user") // Leaving in as a curious note on use cases
@RolesAllowed("user")
@Path("manager")
@RequestScoped
public class ManagerResource {
    
    // Logging
    private final Logger LOGGER = LogManager.getLogger(ManagerResource.class);
    
    @Inject
    JsonWebToken jwt;
    
    
    /**
     * Endpoint for performing provided {@link ImportCommand}
     * 
     * @param cmd
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @POST
    @Path("/import")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response importTasks(
        ImportCommand cmd,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
        
        // Log request
        boolean output;
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Processing import request by user '{}' from '{}' using '{}':\n\n'{}'",
            this.jwt.getName(), ip, userAgent, cmd.toJsonDoc()
        );
        
        // Perform command
        output = (boolean) cmd.runCommand();
        
        // Log status
        LOGGER.info(
            "Import request by user '{}' from '{}' '{}' status:\t'{}'",
            this.jwt.getName(), ip, userAgent, output
        );
        
        // Handle command output
        if ( output ) {
            return Response.ok().build();
        }
        else {
            return Response.status(405, "Invalid import action").build();
        }
    }
    
    
    /**
     * Endpoint for performing the provided {@link ExportCommand}
     * 
     * @param cmd
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @GET
    @Path("/export")
    @Produces(MediaType.APPLICATION_JSON)
    public Response exportTasks(
        ExportCommand cmd,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
        
        // Log request
        boolean output;
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Processing export request by user '{}' from '{}' using '{}':\n\n'{}'",
            this.jwt.getName(), ip, userAgent, cmd.toJsonDoc()
        );
        
        // Run command
        output = (boolean) cmd.runCommand();
        
        // Log status
        LOGGER.info(
            "Export request by user '{}' from '{}' '{}' status:\t'{}'",
            this.jwt.getName(), ip, userAgent, output
        );
        
        // Handle command output
        if ( output ) {
            return Response.ok().build();
        }
        else {
            return Response.status(405, "Invalid export action").build();
        }
    }
    
    
    /**
     * Endpoint for performing the provided {@link ResetCommand}
     * 
     * @param cmd
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @PATCH
    @Path("/reset")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetTasks(
        ResetCommand cmd,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        boolean output;
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Processing reset request by user '{}' from '{}' using '{}':\n\n'{}'",
            this.jwt.getName(), ip, userAgent, cmd.toJsonDoc()
        );
        
        // Run command
        output = (boolean) cmd.runCommand();
        
        // Log status
        LOGGER.info(
            "Reset request by user '{}' from '{}' '{}' status:\t'{}'",
            this.jwt.getName(), ip, userAgent, output
        );
        
        // Handle command output
        if ( output ) {
            return Response.ok().build();
        }
        else {
            return Response.status(405, "Invalid reset action").build();
        }
    }
    
    
    /**
     * Endpoint for performing the provided {@link DeleteCommand}
     * 
     * @param cmd
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @DELETE
    @Path("/delete")
    public Response deleteTasks(
        DeleteCommand cmd,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        boolean output;
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Processing delete request by user '{}' from '{}' using '{}':\n\n'{}'",
            this.jwt.getName(), ip, userAgent, cmd.toJsonDoc()
        );
        
        // Run command
        output = (boolean) cmd.runCommand();
        
        // Log status
        LOGGER.info(
            "Delete request by user '{}' from '{}' '{}' status:\t'{}'",
            this.jwt.getName(), ip, userAgent, output
        );
        
        // Handle command output
        if ( output ) {
            return Response.ok().build();
        }
        else {
            return Response.status(405, "Invalid delete action").build();
        }
    }
    
    
    /**
     * Endpoint for performing the provided {@link AnnotateCommand}.
     *  All annotations are patches
     * 
     * @param cmd
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @PATCH
    @Path("/annotate")
    public Response annotateTasks(
        AnnotateCommand cmd,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        boolean output;
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Processing annotate request by user '{}' from '{}' using '{}':\n\n'{}'",
            this.jwt.getName(), ip, userAgent, cmd.toJsonDoc()
        );
        
        // Run command
        output = (boolean) cmd.runCommand();
        
        // Log status
        LOGGER.info(
            "Annotation request by user '{}' from '{}' '{}' status:\t'{}'",
            this.jwt.getName(), ip, userAgent, output
        );
        
        // Handle command output
        if ( output ) {
            return Response.ok().build();
        }
        else {
            return Response.status(405, "Invalid annotation action").build();
        }
    }
    
    
    /**
     * Endpoint for performing required {@link SummarizeCommand}
     * 
     * @param cmd
     * @param reqHeader
     * @param uriInfo
     * @param securityContext
     * 
     * @return {@link Response}
     */
    @GET
    @Path("/summarize")
    public Response summariseTasks(
        SummarizeCommand cmd,
        @Context HttpHeaders reqHeader,
        @Context UriInfo uriInfo,
        @Context SecurityContext securityContext
    ) {
    
        // Log request
        boolean output;
        String ip = reqHeader.getHeaderString("X-Forwarded-For");
        String userAgent = reqHeader.getHeaderString("User-Agent");
        LOGGER.info(
            "Processing summary request by user '{}' from '{}' using '{}':\n\n'{}'",
            this.jwt.getName(), ip, userAgent, cmd.toJsonDoc()
        );
        
        // Run command
        output = (boolean) cmd.runCommand();
        
        // Log status
        LOGGER.info(
            "Summary request by user '{}' from '{}' '{}' status:\t'{}'",
            this.jwt.getName(), ip, userAgent, output
        );
        
        // Handle command output
        if ( output ) {
            return Response.ok().build();
        }
        else {
            return Response.status(405, "Invalid summary action").build();
        }
    }
}