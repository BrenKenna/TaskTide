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
import jakarta.enterprise.context.control.RequestContextController;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jsonb.JsonBindingFeature;

import org.tasktide.api.services.rest.StepRestResource;


/**
 * 
 *
 * @author Bren
 */
public class AbstractBaseJerseyTest extends JerseyTest {

    protected SeContainer container;
    protected RequestContextController requestCtx;
    
    private Class<?>[] resources;
    
    protected AbstractBaseJerseyTest(Class<?>... resources) {
        this.resources = resources;
    }
    
    
    @Override
    protected Application configure() {
        
        this.container = TestEnvironment.startWeldContainer("app-props.properties", getClass());
        this.requestCtx = container.select(RequestContextController.class).get();
        
        ResourceConfig config = new ResourceConfig();
        
        resources = new Class<?>[] {
            StepRestResource.class
        };
        
        for (Class<?> clazz : resources) {
            //Object instance = container.select(clazz).get();
            config.register(clazz);
        }
        config.register(JsonBindingFeature.class);
        return config;
    }
    
    
    public boolean activateCtx() {
        return this.requestCtx.activate();
    }
    
    
    public void deactivateCtx() {
        this.requestCtx.deactivate();
    }
}