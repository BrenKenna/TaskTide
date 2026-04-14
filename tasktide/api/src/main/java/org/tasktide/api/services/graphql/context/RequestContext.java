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
package org.tasktide.api.services.graphql.context;

import jakarta.inject.Inject;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;


/**
 * Injectable request context parsable by GraphQL resource
 *
 * @author Bren
 */
@RequestScoped
public class RequestContext {

    @Inject
    HttpServletRequest request;
    
    @Inject
    JsonWebToken jwt;
    
    private final String requestId = UUID.randomUUID().toString();
    
    
    /**
     * Get JSON web token
     * 
     * @return {@link JsonWebToken}
     */
    public JsonWebToken getJsonWebToken() {
        return this.jwt;
    }
    
    
    /**
     * Fetch HTTP request
     * 
     * @return 
     */
    public HttpServletRequest getRequest() {
        return this.request;
    }
    
    
    /**
     * Get interally defined request Id
     * 
     * @return String
     */
    public String getRequestId() {
        return requestId;
    }
    
    
    /**
     * Get origin IP address
     * 
     * @return String
     */
    public String getIp() {
        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0];
        }

        return request.getRemoteAddr();
    }
    
    
    /**
     * Get user agent
     * 
     * @return String
     */
    public String getUserAgent() {
        return request.getHeader("User-Agent");
    }

    
    /**
     * Get HTTP method
     * 
     * @return String
     */
    public String getMethod() {
        return request.getMethod();
    }

    
    /**
     * Get URI path
     * 
     * @return String
     */
    public String getPath() {
        return request.getRequestURI();
    }
}