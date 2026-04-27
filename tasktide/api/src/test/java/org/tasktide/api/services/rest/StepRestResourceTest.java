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
package org.tasktide.api.services.rest;

import jakarta.enterprise.context.control.RequestContextController;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import jakarta.nosql.Template;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.security.KeyPair;

import java.util.List;
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

import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.model.collection.Step;

import org.tasktide.api.TestUtils;
import org.tasktide.api.TestEnvironment;
import org.tasktide.api.WebApiTestUtils;


/**
 * Suite of tests against {@link StepRestResource}
 *
 * @author Bren
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StepRestResourceTest extends AbstractBaseJerseyTest {
    
    private final Logger LOGGER = LogManager.getLogger(StepRestResourceTest.class);
    
    private final String STEP = "Restful Steps";
    private final String RESOURCE_PATH = "/services/step";
    
    private Template template;
    private KeyPair KEY_PAIR;
    
    public StepRestResourceTest() {
        super(StepRestResource.class);
    }
    
    @Override
    protected Application configure() {
        
        this.container = TestEnvironment.startWeldContainer("app-props.properties", getClass());
        this.requestCtx = container.select(RequestContextController.class).get();
        
        ResourceConfig config = new ResourceConfig();
        
        this.resources = new Class<?>[] {
            StepRestResource.class
        };
        
        for (Class<?> clazz : this.resources) {
            //Object instance = container.select(clazz).get();
            config.register(clazz);
        }
        config.register(JsonBindingFeature.class);
        return config;
    }
    
    
    @BeforeAll
    public void setUpClass() throws Exception {
        String msg = "\n\n---------------- Initiating Step REST Resource Tests ----------------\n";
        LOGGER.info(msg);
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEP, "|", ",");
        
        KEY_PAIR = WebApiTestUtils.getKeyPair();
        System.setProperty("mp.jwt.verify.publickey", WebApiTestUtils.toPemPublic(KEY_PAIR.getPublic()));
        System.setProperty("mp.jwt.verify.issuer", "web-api-testing");
    }
    
    @AfterAll
    public void tearDownClass() throws Exception {
        this.tearDown();
        container.close();
    }


    /**
     * Tests adding {@link Step}
     * 
     */
    @Test
    @Order(0)
    public void canAddStep() {
    
        // Configure test
        LOGGER.info("\n\n================ StepRestResource Can Add Step ================\n");
        String stepName = "Test-Step";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add-step";
        Step step;
        Response resp;
        
        // Make test step
        step = BuilderUtility.buildStep(stepName);
        // step.getAnnotations().add("Key", "Value");
        LOGGER.info("Created test step:\n\n'{}'", step.toJsonDoc());
        
        // Fetch mock token
        LOGGER.info("Firiing test Step creation against StepRestResource");
        LOGGER.info("Serialized Step:\n\n'{}'", Entity.entity(step, MediaType.APPLICATION_JSON));
        bearerToken = "Bearer " + WebApiTestUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(step, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not add Step through StepRestResource");
        LOGGER.info("\n\n================ StepRestResource Can Add Step ================\n");
    }
    
    
    /**
     * Tests querying {@link Step} by field
     * 
     */
    @Test
    @Order(1)
    public void canQueryStepByField() {
    
        // Configure test
        LOGGER.info("\n\n================ StepRestResource Can Query By Field ================\n");
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/get";
        Response resp;
        
        // Fetch mock token
        LOGGER.info("Firiing test query by field against StepRestResource for:\t'{}'", STEP);
        bearerToken = "Bearer " + WebApiTestUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .queryParam("field", "stepName")
            .queryParam("value", STEP)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
        .get();
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        List<Step> records = resp.readEntity(new GenericType<List<Step>>() {});
        LOGGER.info("Displaying retrieved records:\n\n'{}", records);
        Assertions.assertTrue(resp.getStatus() == 200, "Error could query Step field through StepRestResource");
        LOGGER.info("\n\n================ StepRestResource Can Query By Field ================\n");
    }
}