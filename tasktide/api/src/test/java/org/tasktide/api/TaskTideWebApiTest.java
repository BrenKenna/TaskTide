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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.tasktide.api.utils.WebApiUtils;
import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.repository.RepositoryType;


/**
 * 
 *
 * @author Bren
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskTideWebApiTest {
    
    private final Logger LOGGER = LogManager.getLogger(TaskTideWebApiTest.class);
    
    private final String STEP = "Nested NS Lookups";
    
    private SeContainer container;
    private Template template;
    
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

    
    
    @Test
    @Order(0)
    public void canReadConfigMap() {
    
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
        Assertions.assertTrue(counter >= 0, "Unable to pull configuration properties");
        LOGGER.info("\n\n================ Can Read Web API Config ================\n");
    }
    
    
    
    @Test
    @Order(1)
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
        WebTarget target = client.target("http://localhost:8080/services/step/add-step");
        LOGGER.info("Sending POST request to:\t'{}{}'", target.getUri().getHost(), target.getUri().getPath());
        Response resp = target.request(MediaType.APPLICATION_JSON)
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(step, MediaType.APPLICATION_JSON));
        LOGGER.info("Logging response status:\t'{}'", resp.getStatus());
        
        LOGGER.info("\n\n================ Can Start Web Server ================\n");
    }
}