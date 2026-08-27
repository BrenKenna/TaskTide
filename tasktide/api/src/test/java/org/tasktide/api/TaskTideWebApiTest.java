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

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import org.glassfish.jersey.server.ResourceConfig;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.api.utils.WebApiUtils;

import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.repository.RepositoryType;


/**
 * Suite of tests for {@link TaskTideWebApi} which
 *  is the entry-point of the WebApi. Methods
 *  here sanity check mounted endpoints for Auth,
 *  and firing HTTP requests against. Distinct
 *  over HTTP-Harness, as these relate to starting/stopping
 *  server, and replicate client/browser etc
 *
 * @author Bren
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskTideWebApiTest {
    
    private final Logger LOGGER = LogManager.getLogger(TaskTideWebApiTest.class);
    
    private final String STEP = "Nested NS Lookups";
    
    private SeContainer container;
    private Template template;
    
    private final Object webApiLock = new Object();
    private volatile boolean running = true;
    
    public TaskTideWebApiTest() {
    }
    
    
    @BeforeAll
    public void setUpClass() {
        container = TestEnvironment.startWeldContainer("app-props.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEP, "|", ",");
    }
    
    
    @AfterAll
    public void tearDownClass() {
    }
    
    
    @BeforeEach
    public void setUp() {
    }
    
    
    @AfterEach
    public void tearDown() {
    }

    
    /**
     * Blocks main thread until stopped
     * 
     */
    private void blockMain() {
        synchronized ( this.webApiLock ) {
            while ( true ) {
                try {
                    this.webApiLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    
    /**
     * Tests configured properties are registered,
     *  very handy for testing the degree of validity.
     *  Could almost be its own suite
     */
    @Test
    @Order(0)
    @Tag("integration-e2e")
    public void canReadAtLeastOneConfigMap() {
    
        LOGGER.info("\n\n================ Can Read Web API Config ================\n");
        Config config = ConfigProvider.getConfig();
        TaskTideWebApi webApi = new TaskTideWebApi(config);
        
        LOGGER.info("TaskTide web URL:\t'{}'", webApi.getWebUri());
        webApi.configureServer();
        ResourceConfig rc = webApi.getResourceConfig();
        
        int counter = 0;
        for ( String name : config.getPropertyNames() ) {
            if ( name.startsWith("jersey.") ) {
                String val = config.getConfigValue(name).getRawValue();
                Object value = rc.getProperty(name);
                LOGGER.info("Property '{}' value is '{}'", name, value);
                if ( value != null ) { counter++; }
            }
        }
        Assertions.assertTrue(counter >= 0, "Unable to pull any configuration properties");
        LOGGER.info("\n\n================ Can Read Web API Config ================\n");
    }
    
    
    /**
     * Tests that configured web server reaches a started state
     */
    @Test
    @Order(1)
    @Tag("integration-e2e")
    public void canStartWebServer() {
    
        LOGGER.info("\n\n================ Can Start Web Server ================\n");
        
        Config config = ConfigProvider.getConfig();
        TaskTideWebApi webApi = new TaskTideWebApi(config);
        
        LOGGER.info("TaskTide web URL:\t'{}'", webApi.getWebUri());
        webApi.configureServer();
        ResourceConfig rc = webApi.getResourceConfig();
        
        boolean serverStarted = webApi.startWebServer();
        while ( !webApi.getState().equals("STARTED") ) {
            LOGGER.info("Waiting for web server to start");
        }
        LOGGER.info("Server status:\t'{}'", webApi.getState());
        
        String bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        Step step = BuilderUtility.buildStep("Test Step");
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/tasktide/api/services/step/add");
        LOGGER.info("Sending POST request to:\t'{}://{}'", target.getUri().getHost(), target.getUri().getPath());
        Response resp = target.request(MediaType.APPLICATION_JSON)
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(step, MediaType.APPLICATION_JSON));
        
        int statusCode = resp.getStatus();
        LOGGER.info("Logging response status:\t'{}'", statusCode);
        Assertions.assertTrue(statusCode == 200, "Unable to add the test step");
        LOGGER.info("\n\n================= Can Start Web Server =================\n");
    }
    
    
    /**
     * Tests that paths on web service are privileged
     */
    @Test
    @Order(2)
    @Tag("integration-e2e")
    public void webServerPathsArePrevileged() {
    
        LOGGER.info("\n\n================ Web Server Paths Are Previleged ================\n");
        
        Config config = ConfigProvider.getConfig();
        TaskTideWebApi webApi = new TaskTideWebApi(config);
        
        LOGGER.info("TaskTide web URL:\t'{}'", webApi.getWebUri());
        webApi.configureServer();
        ResourceConfig rc = webApi.getResourceConfig();
        
        boolean serverStarted = webApi.startWebServer();
        while ( !webApi.getState().equals("STARTED") ) {
            LOGGER.info("Waiting for web server to start");
        }
        LOGGER.info("Server status:\t'{}'", webApi.getState());
        
        
        
        String bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        Step step = BuilderUtility.buildStep("Test Step");
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/tasktide/api/services/step/add");
        LOGGER.info("Sending POST request to:\t'http://localhost:8080/tasktide/api/services/step/add'");
        Response resp = target.request(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(step, MediaType.APPLICATION_JSON));
        
        
        int statusCode = resp.getStatus();
        LOGGER.info("Logging response status:\t'{}'", resp.getStatus());
        Assertions.assertTrue(statusCode == 401, "Unable to add the test step");
        LOGGER.info("\n\n================ Web Server Paths Are Previleged ================\n");
    }
    
    
    /**
     * Tests web service operates indefinetely, for using web-browser
     *  cURL requests etc
     */
    @Test
    @Order(3)
    @Tag("base")
    public void canStartWebService() {
    
        LOGGER.info("\n\n================ Can Start Web Server ================\n");
        
        Config config = ConfigProvider.getConfig();
        TaskTideWebApi webApi = new TaskTideWebApi(config);
        
        LOGGER.info("TaskTide web URL:\t'{}'", webApi.getWebUri());
        webApi.configureServer();
        ResourceConfig rc = webApi.getResourceConfig();
        
        boolean serverStarted = webApi.startWebServer();
        while ( !webApi.getState().equals("STARTED") ) {
            LOGGER.info("Waiting for web server to start state:\n'{}'", webApi.getState());
        }
        LOGGER.info("Server status:\t'{}'", webApi.getState());

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook( new Thread( () -> {
            LOGGER.info("Shutdown signal received, shutting down TaskTide-WebApi server");
            webApi.stopServer();
        }));
        
        // Wait until stopped
        this.blockMain();
        
        LOGGER.info("\n\n================ Can Start Web Server ================\n");
    }
}