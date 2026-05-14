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


import jakarta.nosql.Template;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.security.KeyPair;
import jakarta.enterprise.context.control.RequestContextController;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.repository.RepositoryType;

import org.tasktide.api.AbstractBaseJerseyTest;
import org.tasktide.api.TestEnvironment;
import org.tasktide.api.TestUtils;

import org.tasktide.api.auth.JwtRequestFilter;
import org.tasktide.api.resources.services.rest.WorkflowRestResource;

import org.tasktide.api.utils.WebApiUtils;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.collection.Step;


/**
 * Suite of tests for {@link WorkflowRestResource}, seeking basic functionality,
 *  and anything that can improve the core-lib
 * 
 * @author Bren
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkflowRestResourceTests extends AbstractBaseJerseyTest {
    
    private final Logger LOGGER = LogManager.getLogger(WorkflowRestResourceTests.class);
    
    private final String WORKFLOW = "Restful Workflows";
    private final String RESOURCE_PATH = "/services/workflow";
    
    private Template template;
    private KeyPair KEY_PAIR;
    
    public WorkflowRestResourceTests() {
        super(WorkflowRestResource.class);
    }
    
    @Override
    protected Application configure() {
        
        this.container = TestEnvironment.startWeldContainer("app-props.properties", getClass());
        this.requestCtx = container.select(RequestContextController.class).get();
        
        ResourceConfig config = new ResourceConfig();
        
        this.resources = new Class<?>[] {
            WorkflowRestResource.class
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
        String msg = "\n\n---------------- Initiating Workflow REST Resource Tests ----------------\n";
        LOGGER.info(msg);
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.WORKFLOW, "|", ",");
        
        KEY_PAIR = WebApiUtils.getKeyPair();
        System.setProperty("mp.jwt.verify.publickey", WebApiUtils.toPemPublic(KEY_PAIR.getPublic()));
        System.setProperty("mp.jwt.verify.issuer", "web-api-testing");
    }
    
    @AfterAll
    public void tearDownClass() throws Exception {
        this.tearDown();
        container.close();
    }


    /**
     * Tests adding {@link Workflow}
     * 
     */
    @Test
    @Order(0)
    public void canAddWorkflow() {
    
        // Configure test
        LOGGER.info("\n\n================= WorkflowRestResource Can Add Workflow =================\n");
        String workflowName = "Add-Workflow-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        Workflow workflow;
        Response resp;
        
        // Make test workflow
        workflow = BuilderUtility.buildWorkflow(workflowName);
        // workflow.getAnnotations().add("Key", "Value");
        LOGGER.info("Created test workflow:\n\n'{}'", workflow.toJsonDoc());
        
        // Fetch mock token
        LOGGER.info("Firiing test Workflow creation against WorkflowRestResource");
        LOGGER.info("Serialized Workflow:\n\n'{}'", Entity.entity(workflow, MediaType.APPLICATION_JSON));
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(workflow, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not add Workflow through WorkflowRestResource");
        Workflow result = resp.readEntity(new GenericType<Workflow>() {});
        LOGGER.info("Displaying created resource:\n\n'{}'", result.toJsonDoc());
        LOGGER.info("\n\n================ WorkflowRestResource Can Add Workflow ================\n");
    }
    
    
    /**
     * Tests querying {@link Workflow} by field
     * 
     */
    @Test
    @Order(1)
    public void canQueryWorkflowByField() {
    
        // Configure test
        LOGGER.info("\n\n=============== WorkflowRestResource Can Query By Field ===============\n");
        String bearerToken;
        String methodPath;
        Workflow workflow;
        Response resp;
        
        // Fetch mock token
        LOGGER.info("Firing test query by field against WorkflowRestResource for:\t'{}'", WORKFLOW);
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        methodPath = RESOURCE_PATH + "/get";
        resp = this.target(methodPath)
            .queryParam("field", "workflowName")
            .queryParam("value", this.WORKFLOW)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
        .get();
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        Workflow record = resp.readEntity(new GenericType<List<Workflow>>() {}).get(0);
        LOGGER.info("Displaying retrieved records:\n\n'{}", record.toJsonDoc());
        Assertions.assertTrue(resp.getStatus() == 200, "Error could query Workflow field through WorkflowRestResource");
        LOGGER.info("\n\n================ WorkflowRestResource Can Query By Field ================\n");
    }
    
    
    /**
     * Tests whether a collection of workflows can be imported
     */
    @Test
    @Order(2)
    public void canAddMultipleWorkflows() {
    
        // Configure test
        LOGGER.info("\n\n================ WorkflowRestResource Can Add Multiple Workflows ================\n");
        String workflowName = "Batch-Import-Workflows";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/import";
        Response resp;
        List<Workflow> workflows = new ArrayList<>();
        int nWorkflows = 10;
        
        // Create and add workflow collection
        for ( int i = 0; i < nWorkflows; i++ ) {
            Workflow workflow = BuilderUtility.buildWorkflow(workflowName + "-" + i);
            workflows.add(workflow);
        }
        
        // Fetch mock token
        LOGGER.info("Importing Workflow Collection against WorkflowRestResource");
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(workflows, MediaType.APPLICATION_JSON));
        
        // Evaluate test
        LOGGER.info("Displaying status code:\t'{}", resp.getStatus());
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not import Workflow collection through WorkflowRestResource");
        LOGGER.info("\n\n================ WorkflowRestResource Can Add Multiple Workflows ================\n");
    }
    
    
    /**
     * Tests creating workflow by name
     * 
     */
    @Test
    @Order(3)
    public void canCreateWorkflowByName() {
    
        // Configure test
        LOGGER.info("\n\n================ WorkflowRestResource Create Named Workflow Test ================\n");
        String workflowName = "Create-Workflow-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/create";
        Workflow workflow;
        Response resp;
        
        // Make test workflow
        workflow = BuilderUtility.buildWorkflow(workflowName);
        // workflow.getAnnotations().add("Key", "Value");
        LOGGER.info("Created test workflow:\n\n'{}'", workflow.toJsonDoc());
        
        // Fetch mock token
        LOGGER.info("Firing Workflow creation request against WorkflowRestResource");
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .queryParam("workflowName", "doggie")
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(null);
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not create named Workflow through WorkflowRestResource");
        LOGGER.info("\n\n================ WorkflowRestResource Create Named Workflow Test ================\n");
    }
    
    
    /**
     * Tests dropping workflow
     * 
     */
    @Test
    @Order(4)
    public void canDropWorkflow() {
    
        // Configure test
        LOGGER.info("\n\n================ WorkflowRestResource Drop Workflow Test ================\n");
        String workflowName = "Drop-Workflow-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        Workflow workflow;
        Response resp;
        
        // Make test workflow
        workflow = BuilderUtility.buildWorkflow(workflowName);
        LOGGER.info("Created test workflow:\t'{}'", workflow.getId());
        
        // Fetch mock token
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(workflow, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("Workflow creation state:\t'{}'", resp.getStatus());
        
        
        // Drop workflow
        LOGGER.info("Dropping workflow test:\t'{}'", workflow.getId());
        methodPath = RESOURCE_PATH + "/drop";
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .path(workflow.getId())
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .delete();
        this.requestCtx.deactivate();
        
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not drop Workflow through WorkflowRestResource");
        LOGGER.info("\n\n================ WorkflowRestResource Drop Workflow Test ================\n");
    }

    
    /**
     * Tests updating workflow
     * 
     */
    @Test
    @Order(5)
    public void canUpdateWorkflow() {
    
        // Configure test
        LOGGER.info("\n\n================ WorkflowRestResource Update Workflow Test ================\n");
        String workflowName = "Update-Workflow-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        Workflow workflow;
        Response resp;
        
        // Make test workflow
        workflow = BuilderUtility.buildWorkflow(workflowName);
        LOGGER.info("Created test workflow:\t'{}'", workflow.getId());
        
        // Fetch mock token
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(workflow, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("Workflow creation state:\t'{}'", resp.getStatus());
        
        
        // Update workflow
        LOGGER.info("Update workflow test:\t'{}'", workflow.getId());
        workflow.setWorkflowName("Updated Workflow Name");
        methodPath = RESOURCE_PATH + "/update";
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .put(Entity.entity(workflow, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not update Workflow through WorkflowRestResource");
        LOGGER.info("\n\n================ WorkflowRestResource Update Workflow Test ================\n");
    }
    
    
    /**
     * Tests updating workflow
     * 
     */
    @Test
    @Order(6)
    public void canAddStepToWorkflow() {
    
        // Configure test
        LOGGER.info("\n\n=============== WorkflowRestResource Add Step to Workflow Test ===============\n");
        String workflowName = "Add-Step-Workflow-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        Workflow workflow;
        Response resp;
        
        // Make test workflow
        workflow = BuilderUtility.buildWorkflow(workflowName);
        LOGGER.info("Created test workflow:\t'{}'", workflow.getId());
        
        // Fetch mock token
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(workflow, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("Workflow creation state:\t'{}'", resp.getStatus());
        
        // Update workflow
        LOGGER.info("Update workflow test:\t'{}'", workflow.getId());
        workflow.setWorkflowName("Updated Workflow Name");
        methodPath = RESOURCE_PATH + "/add-step";
        Step step = TaskTideServiceManager
            .fetchStepService()
            .viewByField("stepName", this.WORKFLOW)
        .get(0);
        
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .queryParam("workflowId", workflow.getId())
            .queryParam("stepId", step.getId())
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .put(Entity.entity("", MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not update Workflow through WorkflowRestResource");
        LOGGER.info("\n\n================ WorkflowRestResource Update Workflow Test ================\n");
    }
}