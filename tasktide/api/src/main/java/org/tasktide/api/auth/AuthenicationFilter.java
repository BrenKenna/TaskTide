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
package org.tasktide.api.auth;

import jakarta.annotation.Priority;

import jakarta.inject.Inject;

import jakarta.ws.rs.Priorities;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;


/**
 * Intercept requests for authentication properties
 *
 * @author Bren
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenicationFilter implements ContainerRequestFilter {

    @Inject
    AuthenticationScheme scheme;

    
    /**
     * Blanket abort method for container requests
     * 
     * @param ctx 
     */
    public void abort(ContainerRequestContext ctx) {
        ctx.abortWith(
            Response.status(Response.Status.UNAUTHORIZED).build()
        );
    }
    
    
    /**
     * Evaluate the authenticity of provided token under the
     *  configured {@link AuthenticationScheme}
     * 
     * @param ctx 
     */
    @Override
    public void filter(ContainerRequestContext ctx) {

        
        // Verify authorization header
        if ( !AuthUtils.checkAuthorizationHeader(ctx) ) {
            this.abort(ctx);
            return;
        }
        
        // Parse bearer token
        String token = AuthUtils.parseBearerToken(ctx);
        if (token == null) {
            abort(ctx);
            return;
        }

        // Validate authenication principal from auth token
        try {

            // Fetch verified principal for app to use
            AuthPrincipal principal =
                scheme.authenticate(token);

            // Embedd in security context of request
            SecurityContext original =
                ctx.getSecurityContext();
            ctx.setSecurityContext(
                new JwtSecurityContext(
                    principal,
                    original.isSecure(),
                    principal.getRoles()
                )
            );

        } catch (AuthenticationException ex) {
            abort(ctx);
        }
    }
}