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
package org.tasktide.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerRequestContext;

import io.smallrye.jwt.auth.principal.JWTParser;
import org.eclipse.microprofile.jwt.JsonWebToken;


/**
 *
 * @author Bren
 */
@Provider
public class JwtRequestFilter implements ContainerRequestFilter {

    @Inject
    JWTParser parser;

    @Override
    public void filter(ContainerRequestContext ctx) {

        String auth = ctx.getHeaderString("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            return;
        }

        String token = auth.substring("Bearer ".length());

        try {
            JsonWebToken jwt = parser.parse(token);

            // store for later retrieval
            ctx.setProperty("jwt", jwt);

        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT", e);
        }
    }
}