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

import jakarta.enterprise.context.control.RequestContextController;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.glassfish.jersey.jsonb.JsonBindingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.model.job_env.metrics.MetricType;


import org.tasktide.api.AbstractBaseJerseyTest;
import org.tasktide.api.TestUtils;
import org.tasktide.api.auth.AuthenicationFilter;
import org.tasktide.core.supporting.JsonUtils;


/**
 * Suite of tests against {@link MetricProfileRestResource}
 *
 * @author Bren
 */
@Tag("system-api")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetricProfileRestResourceTests extends AbstractBaseJerseyTest {

    private final Logger LOGGER = LogManager.getLogger(MetricProfileRestResourceTests.class);
    
    private final String WORKFLOW = "MetricProfile Rest Resource Tests";
    private final String STEP = "Restful MetricProfiles";
    private final String RESOURCE_PATH = "/services/metric-profile";

    private final int PROFILE_DATA_POINTS;
    
    public MetricProfileRestResourceTests() {
        super(MetricProfileRestResource.class);
        this.PROFILE_DATA_POINTS = 10;
    }
    
    @Override
    protected Application configure() {
        
        TestUtils.initSeContainer();
        TestUtils.createWorkflow(this.WORKFLOW);
        TestUtils.createStep(this.STEP, this.WORKFLOW);
        
        this.container = TestUtils.fetchConfiguredContainer();
        this.requestCtx = container.select(RequestContextController.class).get();
        
        ResourceConfig config = new ResourceConfig();
        
        this.resources = new Class<?>[] {
            MetricProfileRestResource.class
        };
        
        for (Class<?> clazz : this.resources) {
            //Object instance = container.select(clazz).get();
            config.register(clazz);
        }
        config.register(JsonBindingFeature.class);
        config.register(AuthenicationFilter.class);
        return config;
    }
    
    
    @BeforeAll
    public void setUpClass() throws Exception {
        String msg = "\n\n---------------- Initiating MetricProfile REST Resource Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public void tearDownClass() throws Exception {
        this.tearDown();
    }


    /**
     * Tests adding {@link MetricProfile}
     * 
     */
    @Test
    @Order(0)
    public void canAddMetricProfile() {
    
        // Configure test
        LOGGER.info("\n\n================ MetricProfileRestResource Can Add MetricProfile ================\n");
        String metricProfileName = "Add-MetricProfile-Test";
        String methodPath = RESOURCE_PATH + "/add";
        MetricProfile metricProfile;
        Response resp;
        
        // Make test metricProfile
        metricProfile = TestUtils.createRandomMemoryMetricProfile(this.PROFILE_DATA_POINTS);
        metricProfile.setLabel(metricProfileName);
        LOGGER.info("Created test metricProfile:\n\n'{}'", metricProfile.toJsonDoc());
        
        // Fetch mock token
        LOGGER.info("Firiing test MetricProfile creation against MetricProfileRestResource");
        LOGGER.info("Serialized MetricProfile:\n\n'{}'", Entity.entity(metricProfile, MediaType.APPLICATION_JSON));
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(metricProfile, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not add MetricProfile through MetricProfileRestResource");
        LOGGER.info("\n\n================ MetricProfileRestResource Can Add MetricProfile ================\n");
    }
    
    
    /**
     * Tests querying {@link MetricProfile} by field
     * 
     */
    @Test
    @Order(1)
    public void canQueryMetricProfileByField() {
    
        // Configure test
        LOGGER.info("\n\n================ MetricProfileRestResource Can Query By Field ================\n");
        MetricType type = MetricType.MEMORY;
        String methodPath = RESOURCE_PATH + "/get";
        Response resp;
        
        // Fetch mock token
        LOGGER.info("Firing test query by field against MetricProfileRestResource for:\t'{}'", type);
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .queryParam("field", "MetricType")
            .queryParam("value", type)
            .request()
                .header("User-Agent", "JUnit-Test")
        .get();
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\t'{}'", resp);
        
        // Evaluate test
        List<MetricProfile> records = resp.readEntity(new GenericType<List<MetricProfile>>() {});
        LOGGER.info("Displaying retrieved records:\n\n'{}", JsonUtils.toJson(true, records));
        Assertions.assertTrue(resp.getStatus() == 200, "Error could query MetricProfile field through MetricProfileRestResource");
        LOGGER.info("\n\n================ MetricProfileRestResource Can Query By Field ================\n");
    }
    
    
    /**
     * Tests whether a collection of metricProfiles can be imported
     */
    @Test
    @Order(2)
    public void canImportMultipleMetricProfile() {
    
        // Configure test
        LOGGER.info("\n\n================ MetricProfileRestResource Can Add Multiple MetricProfiles ================\n");
        String metricProfileName = "Batch-Import-MetricProfile";
        String methodPath = RESOURCE_PATH + "/import";
        Response resp;
        List<MetricProfile> metricProfiles = new ArrayList<>();
        int nMetricProfiles = 10;
        
        // Create and add metricProfile collection
        for ( int i = 0; i < nMetricProfiles; i++ ) {
            MetricProfile metricProfile = TestUtils.createRandomCpuMetricProfile(this.PROFILE_DATA_POINTS);
            metricProfile.setLabel(metricProfileName + "-" + i);
            metricProfiles.add(metricProfile);
            
        }
        
        // Fetch mock token
        LOGGER.info("Importing MetricProfile Collection against MetricProfileRestResource");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(metricProfiles, MediaType.APPLICATION_JSON));
        
        // Evaluate test
        LOGGER.info("Displaying resource response:\t'{}'", resp);
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not import MetricProfile collection through MetricProfileRestResource");
        LOGGER.info("\n\n================ MetricProfileRestResource Can Add Multiple MetricProfiles ================\n");
    }

    
    /**
     * Tests dropping metricProfile
     * 
     */
    @Test
    @Order(3)
    public void canDropMetricProfile() {
    
        // Configure test
        LOGGER.info("\n\n================ MetricProfileRestResource Drop MetricProfile Test ================\n");
        String methodPath = RESOURCE_PATH + "/add";
        MetricProfile metricProfile;
        Response resp;
        
        // Make test metricProfile
        metricProfile = TestUtils.createRandomCpuMetricProfile(this.PROFILE_DATA_POINTS);
        LOGGER.info("Created test metricProfile:\t'{}'", metricProfile.getId());
        
        // Fetch mock token
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(metricProfile, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("MetricProfile creation state:\t'{}'", resp.getStatus());
        
        
        // Drop metricProfile
        LOGGER.info("Dropping metricProfile test:\t'{}'", metricProfile.getId());
        methodPath = RESOURCE_PATH + "/drop";
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .path(metricProfile.getId())
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .delete();
        this.requestCtx.deactivate();
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not drop MetricProfile through MetricProfileRestResource");
        LOGGER.info("\n\n================ MetricProfileRestResource Drop MetricProfile Test ================\n");
    }

    
    /**
     * Tests updating metricProfile
     * 
     */
    @Test
    @Order(4)
    public void canUpdateMetricProfile() {
    
        // Configure test
        LOGGER.info("\n\n================ MetricProfileRestResource Update MetricProfile Test ================\n");
        String metricProfileName = "Update-MetricProfile-Test";
        String methodPath = RESOURCE_PATH + "/add";
        MetricProfile metricProfile;
        Response resp;
        
        // Make test metricProfile
        metricProfile = TestUtils.createRandomMemoryMetricProfile(this.PROFILE_DATA_POINTS);
        metricProfile.setLabel(metricProfileName);
        LOGGER.info("Created test metricProfile:\t'{}'", metricProfile.getId());

        // Fetch mock token
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(metricProfile, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("MetricProfile creation state:\t'{}'", resp.getStatus());
        
        
        // Update metricProfile
        LOGGER.info("Update metricProfile test:\t'{}'", metricProfile.getId());
        metricProfile.setLabel("Some metric label");
        methodPath = RESOURCE_PATH + "/update";
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .put(Entity.entity(metricProfile, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not update MetricProfile through MetricProfileRestResource");
        LOGGER.info("\n\n================ MetricProfileRestResource Update MetricProfile Test ================\n");
    }
}