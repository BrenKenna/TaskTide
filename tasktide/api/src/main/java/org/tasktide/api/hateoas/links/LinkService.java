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
package org.tasktide.api.hateoas.links;

import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;

import java.net.URI;


/**
 * Handles creating links
 *
 * @author Bren
 */
@ApplicationScoped
public class LinkService {

    
    /**
     * Build {@link URI}
     * 
     * @param req
     * @param uriInfo
     * @param path
     * 
     * @return String
     */
    public String buildUri(
        ContainerRequestContext req,
        UriInfo uriInfo,
        String path
    ) {
        
        // Build URI
        URI base = this.resolveBaseUri(req, uriInfo);
        return UriBuilder
            .fromUri(base)
            .path(path)
        .build().toString();
    }
    
    
    /**
     * Parse {@link ContainerRequestContext} and {@link UriInfo}
     *  for building proxy-aware aware {@link URI}
     * 
     * @param req
     * @param uriInfo
     * 
     * @return {@link URI}
     */
    private URI resolveBaseUri(ContainerRequestContext req, UriInfo uriInfo) {
    
        String proto = this.header(req, "X-Frowarded-Proto", uriInfo.getBaseUri().getScheme());
        String host = this.header(req, "X-Frowarded-Host", uriInfo.getBaseUri().getHost());
        int port = uriInfo.getBaseUri().getPort();
        
        return UriBuilder
            .fromUri(uriInfo.getBaseUri())
            .scheme(proto)
            .host(host)
            .port(port)
        .build();
    }
    
    
    /**
     * Parse requested header from {@link ContainerRequestContext}
     *  or use fallback value
     * 
     * @param req
     * @param name
     * @param fallback
     * 
     * @return String
     */
    private String header(ContainerRequestContext req, String name, String fallback) {
        String value = req.getHeaderString(name);
        return value != null ? value : fallback;
    }
}