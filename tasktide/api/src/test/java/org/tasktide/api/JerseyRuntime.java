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

import jakarta.ws.rs.core.Application;
import jakarta.enterprise.inject.se.SeContainer;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;

import org.glassfish.jersey.jsonb.JsonBindingFeature;

/**
 * Collection of utility methods to support
 *  unit-tests against JAX-RS resources
 *  implemented by Jersey
 *
 * @author Bren
 */
public class JerseyRuntime {
    
    
    /**
     * Fetch and start {@link JerseyTest} instance
     * 
     * @param container
     * @param resources
     * @return {@link JerseyTest}
     * @throws Exception 
     */
    public static JerseyTest fetchAndRunHttpHarness(
        SeContainer container,
        Class<?>... resources
    ) throws Exception {
        JerseyTest jerseyTest = createTestHarness(container, resources);
        jerseyTest.setUp();
        return jerseyTest;
    }
    
    
    /**
     * Construct {@link JerseyTest} HTTP harness from {@link ResourceConfig}
     * 
     * @param config
     * 
     * @return {@link JerseyTest}
     */
    public static JerseyTest createTestHarness(ResourceConfig config) {

        return new JerseyTest() {
            @Override
            protected Application configure() {
                return config;
            }
        };
    }
    
    
    /**
     * Construct {@link JerseyTest} HTTP harness
     * 
     * @param container
     * @param resources
     * 
     * @return {@link JerseyTest}
     */
    public static JerseyTest createTestHarness(SeContainer container, Class<?>... resources) {
        ResourceConfig config = buildRestResourceConfig(container, resources);
        return createTestHarness(config);
    }
    
    
    public static ResourceConfig buildRestResourceConfig(SeContainer container, Class<?>... resources) {
    
        // Initialize resource config
        ResourceConfig config = new ResourceConfig();
        //config.register(JWTAuthenticationFilter.class);
        //config.register(JwtRequestFilter.class);
        
        // Fetch each resource
        for (Class<?> clazz : resources) {
            config.register(clazz);
        }

        // JSON support
        config.register(JsonBindingFeature.class);

        return config;
    }
}