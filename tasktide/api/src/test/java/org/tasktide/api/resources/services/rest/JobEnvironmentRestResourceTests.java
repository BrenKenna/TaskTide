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
import org.tasktide.api.AbstractBaseJerseyTest;

import org.tasktide.api.TestUtils;

import org.tasktide.api.auth.AuthenicationFilter;

import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.JobType;
import org.tasktide.core.supporting.JsonUtils;


/**
 * Suite of tests over {@link JobEnvironmentRestResource}
 *
 * @author Bren
 */
@Tag("system-api")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobEnvironmentRestResourceTests extends AbstractBaseJerseyTest {

    private final Logger LOGGER = LogManager.getLogger(JobEnvironmentRestResourceTests.class);
    
    private final String WORKFLOW = "Job Env Tests";
    private final String STEP = "Restful JobEnvironments";
    private final String RESOURCE_PATH = "/services/job-environment";
    
    
    public JobEnvironmentRestResourceTests() {
        super(JobEnvironmentRestResource.class);
    }
    
    @Override
    protected Application configure() {

        TestUtils.initSeContainer();
        TestUtils.createWorkflow(this.WORKFLOW);
        TestUtils.createStep(this.STEP, this.WORKFLOW);
        
        container = TestUtils.fetchConfiguredContainer();
        this.requestCtx = container.select(RequestContextController.class).get();
        
        ResourceConfig config = new ResourceConfig();
        
        this.resources = new Class<?>[] {
            JobEnvironmentRestResource.class
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
        String msg = "\n\n---------------- Initiating JobEnvironment REST Resource Tests ----------------\n";
        LOGGER.info(msg);
        TestUtils.initSeContainer();
        TestUtils.createWorkflow(this.WORKFLOW);
        TestUtils.createStep(this.STEP, this.WORKFLOW);
    }
    
    @AfterAll
    public void tearDownClass() throws Exception {
        this.tearDown();
    }


    /**
     * Tests adding {@link JobEnvironment}
     * 
     */
    @Test
    @Order(0)
    public void canAddJobEnvironment() {
    
        // Configure test
        LOGGER.info("\n\n================ JobEnvironmentRestResource Can Add JobEnvironment ================\n");
        String methodPath = RESOURCE_PATH + "/add";
        JobEnvironment jobEnvironment;
        Response resp;
        
        // Make test jobEnvironment
        jobEnvironment = TestUtils.fetchJobEnv();
        LOGGER.info("Created test jobEnvironment:\n\n'{}'", jobEnvironment.toJsonDoc());
        
        // Fetch mock token
        LOGGER.info("Firiing test JobEnvironment creation against JobEnvironmentRestResource");
        LOGGER.info("Serialized JobEnvironment:\n\n'{}'", Entity.entity(jobEnvironment, MediaType.APPLICATION_JSON));
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(jobEnvironment, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\t'{}'", resp);
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not add JobEnvironment through JobEnvironmentRestResource");
        LOGGER.info("\n\n================ JobEnvironmentRestResource Can Add JobEnvironment ================\n");
    }
    
    
    /**
     * Tests querying {@link JobEnvironment} by field
     * 
     */
    @Test
    @Order(1)
    public void canQueryJobEnvironmentByField() {
    
        // Configure test
        LOGGER.info("\n\n================ JobEnvironmentRestResource Can Query By Field ================\n");
        String methodPath = RESOURCE_PATH + "/get";
        Response resp;
        
        // Fetch mock token
        LOGGER.info("Firiing test query by field against JobEnvironmentRestResource for:\t'{}'", JobType.LOCAL);
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .queryParam("field", "JobType")
            .queryParam("value", JobType.LOCAL)
            .request()
                .header("User-Agent", "JUnit-Test")
        .get();
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\t'{}'", resp);
        
        // Evaluate test
        List<JobEnvironment> records = resp.readEntity(new GenericType<List<JobEnvironment>>() {});
        LOGGER.info("Displaying retrieved records:\n\n'{}", JsonUtils.toJson(true, records));
        Assertions.assertTrue(resp.getStatus() == 200, "Error could query JobEnvironment field through JobEnvironmentRestResource");
        LOGGER.info("\n\n================ JobEnvironmentRestResource Can Query By Field ================\n");
    }
    
    
    /**
     * Tests whether a collection of jobEnvironments can be imported
     */
    @Test
    @Order(2)
    public void canAddMultipleJobEnvironments() {
    
        // Configure test
        LOGGER.info("\n\n================ JobEnvironmentRestResource Can Add Multiple JobEnvironments ================\n");
        String methodPath = RESOURCE_PATH + "/import";
        Response resp;
        List<JobEnvironment> jobEnvironments = new ArrayList<>();
        int nJobEnvironments = 10;
        
        // Create and add jobEnvironment collection
        for ( int i = 0; i < nJobEnvironments; i++ ) {
            JobEnvironment jobEnvironment = TestUtils.fetchJobEnv();
            jobEnvironments.add(jobEnvironment);
            
        }
        
        // Fetch mock token
        LOGGER.info("Importing JobEnvironment Collection against JobEnvironmentRestResource");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(jobEnvironments, MediaType.APPLICATION_JSON));
        
        // Evaluate test
        LOGGER.info("Displaying status code:\t'{}", resp.getStatus());
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not import JobEnvironment collection through JobEnvironmentRestResource");
        LOGGER.info("\n\n================ JobEnvironmentRestResource Can Add Multiple JobEnvironments ================\n");
    }

    
    /**
     * Tests dropping jobEnvironment
     * 
     */
    @Test
    @Order(3)
    public void canDropJobEnvironment() {
    
        // Configure test
        LOGGER.info("\n\n================ JobEnvironmentRestResource Drop JobEnvironment Test ================\n");
        String methodPath = RESOURCE_PATH + "/add";
        JobEnvironment jobEnvironment;
        Response resp;
        
        // Make test jobEnvironment
        jobEnvironment = TestUtils.fetchJobEnv();
        LOGGER.info("Created test jobEnvironment:\t'{}'", jobEnvironment.getId());
        
        // Fetch mock token
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(jobEnvironment, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("JobEnvironment creation state:\t'{}'", resp.getStatus());
        
        
        // Drop jobEnvironment
        LOGGER.info("Dropping jobEnvironment test:\t'{}'", jobEnvironment.getId());
        methodPath = RESOURCE_PATH + "/drop";
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .path(jobEnvironment.getId())
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .delete();
        this.requestCtx.deactivate();
        
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not drop JobEnvironment through JobEnvironmentRestResource");
        LOGGER.info("\n\n================ JobEnvironmentRestResource Drop JobEnvironment Test ================\n");
    }

    
    /**
     * Tests updating jobEnvironment
     * 
     */
    @Test
    @Order(4)
    public void canUpdateJobEnvironment() {
    
        // Configure test
        LOGGER.info("\n\n================ JobEnvironmentRestResource Update JobEnvironment Test ================\n");
        String methodPath = RESOURCE_PATH + "/add";
        JobEnvironment jobEnvironment;
        Response resp;
        
        // Make test jobEnvironment
        jobEnvironment = TestUtils.fetchJobEnv();
        LOGGER.info("Created test jobEnvironment:\t'{}'", jobEnvironment.getId());

        // Fetch mock token
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(jobEnvironment, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("JobEnvironment creation state:\t'{}'", resp.getStatus());
        
        
        // Update jobEnvironment
        LOGGER.info("Update jobEnvironment test:\t'{}'", jobEnvironment.getId());
        jobEnvironment.setHostOS("Derp");
        methodPath = RESOURCE_PATH + "/update";
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .put(Entity.entity(jobEnvironment, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not update JobEnvironment through JobEnvironmentRestResource");
        LOGGER.info("\n\n================ JobEnvironmentRestResource Update JobEnvironment Test ================\n");
    }
}