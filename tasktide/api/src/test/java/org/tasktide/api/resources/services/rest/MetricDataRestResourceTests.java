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

import org.tasktide.api.auth.AuthenicationFilter;
import org.tasktide.api.utils.WebApiUtils;

import org.tasktide.core.model.job_env.JobType;
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricType;
import org.tasktide.core.repository.RepositoryType;



/**
 *
 * @author Bren
 */
@Tag("integration-e2e")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetricDataRestResourceTests extends AbstractBaseJerseyTest {

    private final Logger LOGGER = LogManager.getLogger(MetricDataRestResourceTests.class);
    
    private final String STEP = "Restful MetricDatas";
    private final String RESOURCE_PATH = "/services/metric-data";
    
    private Template template;
    private KeyPair KEY_PAIR;

    
    public MetricDataRestResourceTests() {
        super(MetricDataRestResource.class);
    }
    
    @Override
    protected Application configure() {
        
        this.container = TestEnvironment.startWeldContainer("app-props.properties", getClass());
        this.requestCtx = container.select(RequestContextController.class).get();
        
        ResourceConfig config = new ResourceConfig();
        
        this.resources = new Class<?>[] {
            MetricDataRestResource.class
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
        String msg = "\n\n---------------- Initiating MetricData REST Resource Tests ----------------\n";
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
     * Tests adding {@link MetricData}
     * 
     */
    @Test
    @Order(0)
    public void canAddMetricData() {
    
        // Configure test
        LOGGER.info("\n\n================ MetricDataRestResource Can Add MetricData ================\n");
        String metricDataName = "Add-MetricData-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        MetricData metricData;
        Response resp;
        
        // Make test metricData
        metricData = TestUtils.fetchRandomMemoryMetric();
        metricData.setLabel(metricDataName);
        LOGGER.info("Created test metricData:\n\n'{}'", metricData.toJsonDoc());
        
        // Fetch mock token
        LOGGER.info("Firiing test MetricData creation against MetricDataRestResource");
        LOGGER.info("Serialized MetricData:\n\n'{}'", Entity.entity(metricData, MediaType.APPLICATION_JSON));
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(metricData, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not add MetricData through MetricDataRestResource");
        LOGGER.info("\n\n================ MetricDataRestResource Can Add MetricData ================\n");
    }
    
    
    /**
     * Tests querying {@link MetricData} by field
     * 
     */
    @Test
    @Order(1)
    public void canQueryMetricDataByField() {
    
        // Configure test
        LOGGER.info("\n\n================ MetricDataRestResource Can Query By Field ================\n");
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/get";
        Response resp;
        
        // Fetch mock token
        LOGGER.info("Firing test query by field against MetricDataRestResource for:\t'{}'", JobType.LOCAL);
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .queryParam("field", "Type")
            .queryParam("value", MetricType.CPU)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
        .get();
        this.requestCtx.deactivate();
        LOGGER.info("Displaying resource response:\n\n'{}'", resp);
        
        // Evaluate test
        List<MetricData> records = resp.readEntity(new GenericType<List<MetricData>>() {});
        LOGGER.info("Displaying retrieved records:\n\n'{}", records);
        Assertions.assertTrue(resp.getStatus() == 200, "Error could query MetricData field through MetricDataRestResource");
        LOGGER.info("\n\n================ MetricDataRestResource Can Query By Field ================\n");
    }
    
    
    /**
     * Tests whether a collection of metricDatas can be imported
     */
    @Test
    @Order(2)
    public void canImportMultipleMetricData() {
    
        // Configure test
        LOGGER.info("\n\n================ MetricDataRestResource Can Add Multiple MetricDatas ================\n");
        String metricDataName = "Batch-Import-MetricData";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/import";
        Response resp;
        List<MetricData> metricDatas = new ArrayList<>();
        int nMetricDatas = 10;
        
        // Create and add metricData collection
        for ( int i = 0; i < nMetricDatas; i++ ) {
            MetricData metricData = TestUtils.fetchRandomCpuMetric();
            metricData.setLabel(metricDataName + "-" + i);
            metricDatas.add(metricData);
            
        }
        
        // Fetch mock token
        LOGGER.info("Importing MetricData Collection against MetricDataRestResource");
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(metricDatas, MediaType.APPLICATION_JSON));
        
        // Evaluate test
        LOGGER.info("Displaying status code:\n\n'{}", resp.getStatus());
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not import MetricData collection through MetricDataRestResource");
        LOGGER.info("\n\n================ MetricDataRestResource Can Add Multiple MetricDatas ================\n");
    }

    
    /**
     * Tests dropping metricData
     * 
     */
    @Test
    @Order(3)
    public void canDropMetricData() {
    
        // Configure test
        LOGGER.info("\n\n================ MetricDataRestResource Drop MetricData Test ================\n");
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        MetricData metricData;
        Response resp;
        
        // Make test metricData
        metricData = TestUtils.fetchRandomCpuMetric();
        LOGGER.info("Created test metricData:\t'{}'", metricData.getId());
        
        // Fetch mock token
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(metricData, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("MetricData creation state:\t'{}'", resp.getStatus());
        
        
        // Drop metricData
        LOGGER.info("Dropping metricData test:\t'{}'", metricData.getId());
        methodPath = RESOURCE_PATH + "/drop";
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .path(metricData.getId())
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .delete();
        this.requestCtx.deactivate();
        
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not drop MetricData through MetricDataRestResource");
        LOGGER.info("\n\n================ MetricDataRestResource Drop MetricData Test ================\n");
    }

    
    /**
     * Tests updating metricData
     * 
     */
    @Test
    @Order(4)
    public void canUpdateMetricData() {
    
        // Configure test
        LOGGER.info("\n\n================ MetricDataRestResource Update MetricData Test ================\n");
        String metricDataName = "Update-MetricData-Test";
        String bearerToken;
        String methodPath = RESOURCE_PATH + "/add";
        MetricData metricData;
        Response resp;
        
        // Make test metricData
        metricData = TestUtils.fetchRandomCpuMetric();
        metricData.setLabel(metricDataName);
        LOGGER.info("Created test metricData:\t'{}'", metricData.getId());

        // Fetch mock token
        bearerToken = "Bearer " + WebApiUtils.token("johnDoe");
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .post(Entity.entity(metricData, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        LOGGER.info("MetricData creation state:\t'{}'", resp.getStatus());
        
        
        // Update metricData
        LOGGER.info("Update metricData test:\t'{}'", metricData.getId());
        metricData.setLabel("Some metric label");
        methodPath = RESOURCE_PATH + "/update";
        this.requestCtx.activate();
        resp = this.target(methodPath)
            .request()
                .header("Authorization", bearerToken)
                .header("User-Agent", "JUnit-Test")
                .header("X-Forwarded-For", "127.0.0.1")
        .put(Entity.entity(metricData, MediaType.APPLICATION_JSON));
        this.requestCtx.deactivate();
        
        
        // Evaluate test
        Assertions.assertTrue(resp.getStatus() == 200, "Error could not update MetricData through MetricDataRestResource");
        LOGGER.info("\n\n================ MetricDataRestResource Update MetricData Test ================\n");
    }
}