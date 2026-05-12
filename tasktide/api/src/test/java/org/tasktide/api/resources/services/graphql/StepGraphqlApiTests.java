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
package org.tasktide.api.resources.services.graphql;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.nosql.Template;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.security.KeyPair;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.api.TaskTideWebApi;
import org.tasktide.api.TestEnvironment;
import org.tasktide.api.TestUtils;
import org.tasktide.api.resources.services.graphql.inputs.StepInput;
import org.tasktide.api.utils.WebApiUtils;
import org.tasktide.core.model.task.TaskState;

import org.tasktide.core.repository.RepositoryType;


/**
 * Suite of tests against {@link TaskTideWebApi}
 *
 * @author Bren
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StepGraphqlApiTests {
    
    private final Logger LOGGER = LogManager.getLogger(StepGraphqlApiTests.class);
    
    private final String STEP = "GraphQL Steps";
    private final String RESOURCE_PATH = "/graphql";
    
    private SeContainer container;
    private Template template;
    private KeyPair KEY_PAIR;
    private Jsonb JSONB;
    
    private TaskTideWebApi webApi;
    private Client client;
    private WebTarget webTarget;
    
    
    public StepGraphqlApiTests() {
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Step REST Resource Tests ----------------\n";
        LOGGER.info(msg);
        
        // Fetch container and template
        this.container = TestEnvironment.startWeldContainer("app-props.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEP, "|", ",");
        
        // Initialize Pub-Priv key pair for web api
        KEY_PAIR = WebApiUtils.getKeyPair();
        
        // Configure web api
        Config config = ConfigProvider.getConfig();
        this.webApi = new TaskTideWebApi(config);
        this.webApi.startWebServer();
        this.JSONB = JsonbBuilder.create(
            new JsonbConfig().withFormatting(true)
        );
    }
    
    @AfterAll
    public void tearDownClass() {
        this.webApi.stopServer();
        this.container.close();
    }
    
    @BeforeEach
    public void setUp() {
        this.client = ClientBuilder.newClient();
        this.webTarget = client.target("http://localhost:8080/tasktide/graphql");
    }
    
    @AfterEach
    public void tearDown() {
        this.client.close();
    }

    
    /**
     * Parse response object into JSON string
     * 
     * @param resp
     * @return String
     */
    public String formatResponse(Response resp) {
        String body = resp.readEntity(String.class);
        Object json = this.JSONB.fromJson(body, Object.class);
        return this.JSONB.toJson(json);
    }
    
    
    /**
     * Format query string
     * 
     * @param query
     * @return String
     */
    public String formatQueryString(String query) {
        Object json = this.JSONB.fromJson(query, Object.class);
        return this.JSONB.toJson(json);
    }
    
    
    /**
     * Verifies that the {@@link TaskTideWebApi} etc can all start
     * 
     */
    @Test
    @Order(0)
    public void thisUnitTestCanBeConfigured() {
        LOGGER.info("\n\n================ Can Unit Test Be Configured ================\n");
        Assertions.assertTrue(true, "Unable to confirm unit can be configured");
        
        WebTarget webTarget = client.target(
            "http://localhost:8080/tasktide/graphql/schema.graphql"
        );
        
        String bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        Response response = webTarget.request(MediaType.TEXT_PLAIN)
            .header("Authorization", bearerToken)
            .header("User-Agent", "JUnit-Test")
            .header("X-Forwarded-For", "127.0.0.1")
            .get();

        String responseString = response.readEntity(String.class);
        LOGGER.info("Parsing schema:\n\n'{}'", responseString);
        String schema = this.formatQueryString(responseString);

        LOGGER.info("\nGraphQL Schema:\n{}", schema);
        LOGGER.info("\n\n================ Can Unit Test Be Configured ================\n");
    }
    
    
    
    @Test
    @Order(1)
    public void canSearchStep() {
    
        // Initialize test parameters
        LOGGER.info("\n\n================ Can Search Step ================\n");
        String bearerToken, query = "";
        Map<String, Object> queryConstraints;
        List<String> fields;
        StepInput step;
        Response resp;
        String result;
        boolean assertionState;
        
        // Build graphql query
        queryConstraints = Map.ofEntries(
            Map.entry("StepName", this.STEP),
            Map.entry("StepState", TaskState.PENDING)
        );
        fields = List.of("stepId", "stepName", "stepState", "stepCount", "workflowId");
        query = GraphqlQueryBuilder.buildQuery(
            "SearchStep",
            "searchStep",
            queryConstraints,
            fields
        );
        LOGGER.info("Displaying query:\n\n'{}'", this.formatQueryString(query));
        
        // Fire query
        LOGGER.info("Querying endpoint");
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");

        resp = this.webTarget
            .request(MediaType.APPLICATION_JSON)
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        result = this.formatResponse(resp);
        
        // Evaluate test state
        LOGGER.info("Logging response status:\t'{}'", resp.getStatus());
        if ( resp.getStatus() == 200 ) {
            LOGGER.info("Displaying results:\n\n'{}'", result);
            assertionState = true;
        }
        else {
            assertionState = false;
        }
        Assertions.assertTrue(assertionState, "Unable to query Step through GrpahQL endpoint");
        LOGGER.info("\n\n================ Web Server Paths Are Previleged ================\n");
    }
}