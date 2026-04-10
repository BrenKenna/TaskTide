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
package org.tasktide.api.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.commands.ExportCommand;
import org.tasktide.core.manager.command.commands.ImportCommand;


/**
 * {@link ManagerCommand} REST resource
 *
 * @author Bren
 */
@Path("manager")
@RequestScoped
public class ManagerResource {
    
    // Logging
    private final Logger LOGGER = LogManager.getLogger(ManagerResource.class);
    
    
    /**
     * Endpoint for performing provided {@link ImportCommand}
     * 
     * @param cmd
     * @param req
     * @return {@link Response}
     */
    @POST
    @Path("/import")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response importTasks(ImportCommand cmd, @Context HttpServletRequest req) {
        
        // Log request
        boolean output;
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Processing import requestion from '{}', '{}':\n\n'{}'",
            ip, userAgent, cmd.toJsonDoc()
        );
        
        // Perform command
        output = (boolean) cmd.runCommand();
        
        // Log status
        LOGGER.info(
            "Import request from '{}', '{}' status:\t'{}'",
            ip, userAgent, output
        );
        
        // Handle command output
        if ( output ) {
            return Response.ok().build();
        }
        else {
            return Response.serverError().build();
        }
    }
    
    
    /**
     * Endpoint for performing the provided {@link ExportCommand}
     * 
     * @param cmd
     * @param req
     * @return boolean
     */
    @GET
    @Path("/export")
    @Produces(MediaType.APPLICATION_JSON)
    public Response exportTasks(ExportCommand cmd, @Context HttpServletRequest req) {
        
        // Log request
        boolean output;
        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");
        LOGGER.info(
            "Processing import requestion from '{}', '{}':\n\n'{}'",
            ip, userAgent, cmd.toJsonDoc()
        );
        
        // Run command
        output = (boolean) cmd.runCommand();
        
        // Log status
        LOGGER.info(
            "Import request from '{}', '{}' status:\t'{}'",
            ip, userAgent, output
        );
        
        // Handle command output
        if ( output ) {
            return Response.ok().build();
        }
        else {
            return Response.serverError().build();
        }
    }
}