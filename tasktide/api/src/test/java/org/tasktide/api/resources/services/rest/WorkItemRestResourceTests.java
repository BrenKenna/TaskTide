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
import org.tasktide.api.TestEnvironment;
import org.tasktide.api.TestUtils;

import org.tasktide.core.manager.BuilderUtility;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;
import org.tasktide.core.repository.RepositoryType;

import org.tasktide.api.auth.AuthenicationFilter;

import org.tasktide.api.utils.WebApiUtils;


/**
 * Suite of tests for {@link WorkItemRestResource}, seeking basic functionality,
 *  and anything that can improve the core-lib
 *
 * @author Bren
 */
@Tag("integration-e2e")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkItemRestResourceTests extends AbstractBaseJerseyTest {
    
    private final Logger LOGGER = LogManager.getLogger(WorkItemRestResourceTests.class);
    
    private final String STEP = "Restful WorkItems";
    private final String RESOURCE_PATH = "/services/workitem";
    
    private Template template;
    private KeyPair KEY_PAIR;
    
    public WorkItemRestResourceTests() {
        super(WorkItemRestResource.class);
    }
    
    @Override
    protected Application configure() {
        
        this.container = TestEnvironment.startWeldContainer("app-props.properties", getClass());
        this.requestCtx = container.select(RequestContextController.class).get();
        
        ResourceConfig config = new ResourceConfig();
        
        this.resources = new Class<?>[] {
            WorkItemRestResource.class
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
        String msg = "\n\n---------------- Initiating WorkItem REST Resource Tests ----------------\n";
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


    /**
     * Tests adding {@link WorkItem}
     * 
     */
    @Test
    @Order(0)
    public void canAddWorkItem() {
    
        // Configure test
        LOGGER.info("\n\n================ WorkItemRestResource Can Add WorkItem ================\n");
        String workItemName = "Add-WorkItem-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        WorkItem workItem;
        Response resp;
        
        // Make test workItem
        Workload workload = BuilderUtility.buildEmptyWorkload();
        workItem = BuilderUtility.buildWorkItem(workItemName, workload, workItemName);
        // workItem.getAnnotations().add("Key", "Value");
        LOGGER.info("Created test workItem:\n\n'{}'", workItem.toJsonDoc());
        
        // Fetch mock token
        LOGGER.info("Firiing test WorkItem creation against WorkItemRestResource");
        LOGGER.info("Serialized WorkItem:\n\n'{}'", Entity.entity(workItem, MediaType.APPLICATION_JSON));
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(workItem, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not add WorkItem through WorkItemRestResource");
        LOGGER.info("\n\n================ WorkItemRestResource Can Add WorkItem ================\n");
    }
    
    
    /**
     * Tests querying {@link WorkItem} by field
     * 
     */
    @Test
    @Order(1)
    public void canQueryWorkItemByField() {
    
        // Configure test
        LOGGER.info("\n\n================ WorkItemRestResource Can Query By Field ================\n");
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/get";
        Response resp;
        
        // Fetch mock token
        LOGGER.info("Firiing test query by field against WorkItemRestResource for:\t'{}'", STEP);
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .queryParam("field", "itemName")
            .queryParam("value", "NameServerLookups_2")
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
        .get();
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        List<WorkItem> records = resp.readEntity(new GenericType<List<WorkItem>>() {});
        LOGGER.info("Displaying retrieved records:\n\n'{}", records);
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not query WorkItem field through WorkItemRestResource");
        LOGGER.info("\n\n================ WorkItemRestResource Can Query By Field ================\n");
    }
    
    
    /**
     * Tests whether a collection of workItems can be imported
     */
    @Test
    @Order(2)
    public void canAddMultipleWorkItems() {
    
        // Configure test
        LOGGER.info("\n\n================ WorkItemRestResource Can Add Multiple WorkItems ================\n");
        String workItemName = "Batch-Import-WorkItems";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add-workItems";
        Response resp;
        List<WorkItem> workItems = new ArrayList<>();
        int nWorkItems = 10;
        
        // Create and add workItem collection
        for ( int i = 0; i < nWorkItems; i++ ) {
            Workload workload = BuilderUtility.buildEmptyWorkload();
            WorkItem workItem = BuilderUtility.buildWorkItem(workItemName + "-" + i, workload, workItemName);
            workItems.add(workItem);
        }
        
        // Fetch mock token
        LOGGER.info("Importing WorkItem Collection against WorkItemRestResource");
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(workItems, MediaType.APPLICATION_JSON));
        
        // Evaluate test
        LOGGER.info("Displaying status code:\t'{}", resp.getStatus());
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not import WorkItem collection through WorkItemRestResource");
        LOGGER.info("\n\n================ WorkItemRestResource Can Add Multiple WorkItems ================\n");
    }
    
    
    /**
     * Tests creating workItem by name
     * 
     */
    @Test
    @Order(3)
    public void canCreateWorkItemByName() {
    
        // Configure test
        LOGGER.info("\n\n================ WorkItemRestResource Create Named WorkItem Test ================\n");
        String workItemName = "Create-WorkItem-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/create-workItem?workItemName=doggie";
        WorkItem workItem;
        Response resp;
        
        // Make test workItem
        Workload workload = BuilderUtility.buildEmptyWorkload();
        workItem = BuilderUtility.buildWorkItem(workItemName, workload, workItemName);
        // workItem.getAnnotations().add("Key", "Value");
        LOGGER.info("Created test workItem:\n\n'{}'", workItem.toJsonDoc());
        
        // Fetch mock token
        LOGGER.info("Firing WorkItem creation request against WorkItemRestResource");
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(null);
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not create named WorkItem through WorkItemRestResource");
        LOGGER.info("\n\n================ WorkItemRestResource Create Named WorkItem Test ================\n");
    }
    
    
    /**
     * Tests dropping workItem
     * 
     */
    @Test
    @Order(4)
    public void canDropWorkItem() {
    
        // Configure test
        LOGGER.info("\n\n================ WorkItemRestResource Drop WorkItem Test ================\n");
        String workItemName = "Drop-WorkItem-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add-workItem";
        WorkItem workItem;
        Response resp;
        
        // Make test workItem
        Workload workload = BuilderUtility.buildEmptyWorkload();
        workItem = BuilderUtility.buildWorkItem(workItemName, workload, workItemName);
        LOGGER.info("Created test workItem:\t'{}'", workItem.getId());
        
        // Fetch mock token
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(workItem, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("WorkItem creation state:\t'{}'", resp.getStatus());
        
        
        // Drop workItem
        LOGGER.info("Dropping workItem test:\t'{}'", workItem.getId());
        methodPath = RESOURCE_PATH + "/drop";
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .path(workItem.getId())
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .delete();
        this.requestCtx.deactivate();
        
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not drop WorkItem through WorkItemRestResource");
        LOGGER.info("\n\n================ WorkItemRestResource Drop WorkItem Test ================\n");
    }

    
    /**
     * Tests updating workItem
     * 
     */
    @Test
    @Order(5)
    public void canUpdateWorkItem() {
    
        // Configure test
        LOGGER.info("\n\n================ WorkItemRestResource Update WorkItem Test ================\n");
        String workItemName = "Update-WorkItem-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add-workItem";
        WorkItem workItem;
        Response resp;
        
        // Make test workItem
        Workload workload = BuilderUtility.buildEmptyWorkload();
        workItem = BuilderUtility.buildWorkItem(workItemName, workload, workItemName);
        LOGGER.info("Created test workItem:\t'{}'", workItem.getId());
        
        // Fetch mock token
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(workItem, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("WorkItem creation state:\t'{}'", resp.getStatus());
        
        
        // Update workItem
        LOGGER.info("Update workItem test:\t'{}'", workItem.getId());
        workItem.setItemName(workItemName);
        methodPath = RESOURCE_PATH + "/update";
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .put(Entity.entity(workItem, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not update WorkItem through WorkItemRestResource");
        LOGGER.info("\n\n================ WorkItemRestResource Update WorkItem Test ================\n");
    }
    
    
    /**
     * Tests updating workItem
     * 
     */
    @Test
    @Order(6)
    public void canAddTaskToWorkItem() {
    
        // Configure test
        LOGGER.info("\n\n================ WorkItemRestResource Add Task to WorkItem Test ================\n");
        String workItemName = "Add-Task-WorkItem-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        WorkItem workItem;
        ItemTask task;
        Response resp;
        
        // Make test workItem
        Workload workload = BuilderUtility.buildEmptyWorkload();
        workItem = BuilderUtility.buildWorkItem(workItemName, workload, workItemName);
        LOGGER.info("Created test workItem:\t'{}'", workItem.getId());
        
        // Fetch mock token
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(workItem, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("WorkItem creation state:\t'{}'", resp.getStatus());
        
        
        // Update workItem
        LOGGER.info("Update workItem test:\t'{}'", workItem.getId());
        task = BuilderUtility.buildItemTask("New Task", "nslookup nui.ie");
        task.setWorkItemId(workItem.getId());
        task.setJobEnvId("NA");
        LOGGER.info("Serialized ItemTask:\n\n'{}'", Entity.entity(task, MediaType.APPLICATION_JSON));
        methodPath = RESOURCE_PATH + "/add-task";
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .queryParam("itemId", workItem.getId())
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .put(Entity.entity(task, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        
        // Evaluate test
        String msg = String.format("Error could not update WorkItem through WorkItemRestResource status '%d'", resp.getStatus());
        Assertions.assertTrue(resp.getStatus() == 200, msg);
        LOGGER.info("\n\n================ WorkItemRestResource Add Task to WorkItem Test ================\n");
    }

}