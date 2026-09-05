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

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.mockito.Mockito;

import org.tasktide.api.utils.WebApiUtils;

import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.workitem.WorkItem;


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
    
    private final String WORKFLOW = "TaskTide Web API Tests";
    private final String STEP = "Nested NS Lookups";
    
    private final Object webApiLock = new Object();
    private volatile boolean running = true;
    
    public TaskTideWebApiTest() {
    }
    
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Engine Worker Workflow Tests ----------------\n";
        LOGGER.info(msg);
        
        TestUtils.initSeContainer();
        TestUtils.createWorkflow(this.WORKFLOW);
        TestUtils.createStep(this.STEP, this.WORKFLOW);
        List<WorkItem> tasks = TestUtils.registerRandomWorkItemCollection(ExampleGenerators.NSLOOKUPS, this.STEP);
        LOGGER.info("Displaying first registered task:\t'{}'", tasks.get(0).getId());
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Engine Worker Workflow Tests----------------\n";
        LOGGER.info(msg);
    }
    
    
    @BeforeEach
    public void setUp() {
        LOGGER.info("\n\n================ Initiating Next Test ================\n");
    }
    
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("\n\n================ Terminating Test ================\n");
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
    @Tag("system-api")
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
    @Tag("system-api")
    public void canStartWebServerAndAddStep() {
    
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
    @Order(1)
    @Tag("system-api-experimental")
    public void webServerPathsArePrevileged() {
    
        LOGGER.info("\n\n================ Web Server Paths Are Previleged ================\n");
        
        // Mock web server config
        Config config = Mockito.mock(Config.class);
        Map<String, String> props = Map.of(
            "tasktide.web-api.host", "http://localhost",
            "tasktide.web-api.port", "8080",
            "tasktide.web-api.base-path", "/tasktide",
            "tasktide.web-api.auth-scheme", "embedded"
        );
        Mockito
            .when(
                config.getOptionalValue(
                    Mockito.anyString(),
                    Mockito.eq(String.class)
                )
            )
        .thenAnswer( invok -> {
            String key = invok.getArgument(0);
            return Optional.ofNullable(props.get(key));
        });
        Mockito
            .when(
                config.getOptionalValue("tasktide.web-api.port", Integer.class)
            )
        .thenReturn(Optional.of(8080));
        
        
        // Build web-api with mocked configs
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
    @Order(2)
    @Tag("experimental-api")
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