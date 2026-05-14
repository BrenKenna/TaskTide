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
import jakarta.nosql.Template;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.security.KeyPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.glassfish.jersey.jsonb.JsonBindingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.tasktide.api.AbstractBaseJerseyTest;

import org.tasktide.api.TestEnvironment;
import org.tasktide.api.TestUtils;

import org.tasktide.api.auth.JwtRequestFilter;

import org.tasktide.api.utils.WebApiUtils;

import org.tasktide.core.model.builders.JobEnvironmentBuilder;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.JobType;
import org.tasktide.core.repository.RepositoryType;


/**
 * Suite of tests over {@link JobEnvironmentRestResource}
 *
 * @author Bren
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JobEnvironmentRestResourceTests extends AbstractBaseJerseyTest {

    private final Logger LOGGER = LogManager.getLogger(JobEnvironmentRestResourceTests.class);
    
    private final String STEP = "Restful JobEnvironments";
    private final String RESOURCE_PATH = "/services/job-environment";
    
    private Template template;
    private KeyPair KEY_PAIR;
    
    private final JobEnvironmentBuilder jobEnvBuild;
    
    public JobEnvironmentRestResourceTests() {
        super(JobEnvironmentRestResource.class);
        this.jobEnvBuild = new JobEnvironmentBuilder();
    }
    
    @Override
    protected Application configure() {
        
        this.container = TestEnvironment.startWeldContainer("app-props.properties", getClass());
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
        config.register(JwtRequestFilter.class);
        return config;
    }
    
    
    @BeforeAll
    public void setUpClass() throws Exception {
        String msg = "\n\n---------------- Initiating JobEnvironment REST Resource Tests ----------------\n";
        LOGGER.info(msg);
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEP, "|", ",");
        
        KEY_PAIR = WebApiUtils.getKeyPair();
        System.setProperty("mp.jwt.verify.publickey", WebApiUtils.toPemPublic(KEY_PAIR.getPublic()));
        System.setProperty("mp.jwt.verify.issuer", "web-api-testing");
    }
    
    @AfterAll
    public void tearDownClass() throws Exception {
        this.tearDown();
        container.close();
    }

    
    public JobEnvironment fetchJobEnv() {
        Optional<JobEnvironment> jobEnv = JobType.fetchJobEnvironment();
        if ( jobEnv.isPresent() ) {
            return jobEnv.get();
        }
        return null;
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
        String jobEnvironmentName = "Add-JobEnvironment-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        JobEnvironment jobEnvironment;
        Response resp;
        
        // Make test jobEnvironment
        jobEnvironment = this.fetchJobEnv();
        LOGGER.info("Created test jobEnvironment:\n\n'{}'", jobEnvironment.toJsonDoc());
        
        // Fetch mock token
        LOGGER.info("Firiing test JobEnvironment creation against JobEnvironmentRestResource");
        LOGGER.info("Serialized JobEnvironment:\n\n'{}'", Entity.entity(jobEnvironment, MediaType.APPLICATION_JSON));
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(jobEnvironment, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
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
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/get";
        Response resp;
        
        // Fetch mock token
        LOGGER.info("Firiing test query by field against JobEnvironmentRestResource for:\t'{}'", STEP);
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .queryParam("field", "JobType")
            .queryParam("value", JobType.LOCAL)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
        .get();
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        List<JobEnvironment> records = resp.readEntity(new GenericType<List<JobEnvironment>>() {});
        LOGGER.info("Displaying retrieved records:\n\n'{}", records);
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
        String jobEnvironmentName = "Batch-Import-JobEnvironments";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/import";
        Response resp;
        List<JobEnvironment> jobEnvironments = new ArrayList<>();
        int nJobEnvironments = 10;
        
        // Create and add jobEnvironment collection
        for ( int i = 0; i < nJobEnvironments; i++ ) {
            JobEnvironment jobEnvironment = this.fetchJobEnv();
            jobEnvironments.add(jobEnvironment);
            
        }
        
        // Fetch mock token
        LOGGER.info("Importing JobEnvironment Collection against JobEnvironmentRestResource");
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(jobEnvironments, MediaType.APPLICATION_JSON));
        
        // Evaluate test
        LOGGER.info("Displaying status code:\n\n'{}", resp.getStatus());
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
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        JobEnvironment jobEnvironment;
        Response resp;
        
        // Make test jobEnvironment
        jobEnvironment = this.fetchJobEnv();
        LOGGER.info("Created test jobEnvironment:\t'{}'", jobEnvironment.getId());
        
        // Fetch mock token
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
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
                .header("Authorization", bearerToken)
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
        String jobEnvironmentName = "Drop-JobEnvironment-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        JobEnvironment jobEnvironment;
        Response resp;
        
        // Make test jobEnvironment
        jobEnvironment = this.fetchJobEnv();
        LOGGER.info("Created test jobEnvironment:\t'{}'", jobEnvironment.getId());

        // Fetch mock token
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
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
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .put(Entity.entity(jobEnvironment, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not update JobEnvironment through JobEnvironmentRestResource");
        LOGGER.info("\n\n================ JobEnvironmentRestResource Update JobEnvironment Test ================\n");
    }
}