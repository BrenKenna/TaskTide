/*
 * Copyright 2025 Brendan Kenna.
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
package org.tasktide.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.core.model.builders.BuilderType;
import org.tasktide.core.model.builders.MetricDataBuilder;
import org.tasktide.core.model.builders.MetricProfileBuilder;
import org.tasktide.core.model.builders.ModelBuilder;
import org.tasktide.core.model.builders.ProfileDataBuilder;

import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.JobType;

import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.model.job_env.metrics.MetricType;
import org.tasktide.core.model.job_env.metrics.ProfileData;


/**
 * Module to test building JobEnv components through
 *  {@link ModelBuilder}
 * 
 * @author Brendan Kenna
 */
@Tag("base-core")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobEnvBuilderTests {
    
    private static final Logger LOGGER = LogManager.getLogger(JobEnvBuilderTests.class);
    
    public JobEnvBuilderTests() {
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating JobEnv Builder Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating JobEnv Builder Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @BeforeEach
    public void setUp() {
        LOGGER.info("\n\n================ Starting Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("\n\n================ Terminating Active Test ================\n");
    }

    
    /**
     * Create {@link MetricData} datapoint
     * 
     * @return {@link MetricData}
     */
    public MetricData buildMetricDatapoint() {
        
        // Initialize vars
        long nowStamp = System.currentTimeMillis();
        MetricDataBuilder metricDataBuilder;
        MetricData metricData;
        
        // Build metric data point
        metricDataBuilder = (MetricDataBuilder) BuilderType.METRICDATA.makeBuilder();
        metricData = metricDataBuilder
            .withId("MetricData" + UUID.randomUUID().toString())
            .withLabel("Label")
            .withUnits("GB")
            .withAnnotation(new CustomAnnotation())
            .withMetricType(MetricType.MEMORY)
            .withTotal(0.0)
            .withAvailable(0.0)
            .withUsed(0.0)
            .withTimestamp(nowStamp)
        .build();
        
        // Return metric data point
        return metricData;
    }
    
    
    /**
     * Build data profile
     * 
     * @return {@link ProfileData}
     */
    public ProfileData buildDataProfile() {
        
        // Configure map
        ProfileDataBuilder builder = new ProfileDataBuilder();
        Map<String, MetricData> dataMap = new HashMap<>();
        dataMap.put("Metric-1", buildMetricDatapoint());
        dataMap.put("Metric-2", buildMetricDatapoint());
        dataMap.put("Metric-3", buildMetricDatapoint());
        
        // Initialize Profile
        return builder
            .withId(UUID.randomUUID().toString())
            .withMetricProfile(dataMap)
        .build();
    }
    
    
    /**
     * Tests building {@link MetricData}
     * 
     */
    @Test
    @Order(0)
    public void canBuildMetricData() {
    
        // Initialize record
        LOGGER.info("Tests building metric data");
        boolean assertionState;
        long nowStamp = System.currentTimeMillis();
        MetricDataBuilder metricDataBuilder;
        MetricData metricData;
        
        // Build metric data point
        LOGGER.info("Building metric datapoint");
        metricDataBuilder = (MetricDataBuilder) BuilderType.METRICDATA.makeBuilder();
        metricData = metricDataBuilder
            .withId("MetricData" + UUID.randomUUID().toString())
            .withLabel("Label")
            .withUnits("GB")
            .withAnnotation(new CustomAnnotation())
            .withMetricType(MetricType.MEMORY)
            .withTotal(0.0)
            .withAvailable(0.0)
            .withUsed(0.0)
            .withTimestamp(nowStamp)
        .build();
                
        // Display record for reference
        LOGGER.info("Displaying record for reference:\n'{}'", metricData.toJsonDoc());
        assertionState = metricData.getTotal() == 0.0;
        assertTrue(assertionState, "Test failed to build metric data");
        LOGGER.info("Tests building metric data");
    }
    
    
    /**
     * Tests building {@link ProfileData}
     */
    @Test
    @Order(1)
    public void canBuildProfileData() {
    
        // Initialize record
        LOGGER.info("Tests building profile of metric profile");
        boolean assertionState;
        ProfileDataBuilder builder = new ProfileDataBuilder();
        ProfileData metricProfile;
        Map<String, MetricData> dataMap;
        
        // Configure map
        LOGGER.info("Configuring data map");
        dataMap = new HashMap<>();
        dataMap.put("Metric-1", buildMetricDatapoint());
        dataMap.put("Metric-2", buildMetricDatapoint());
        dataMap.put("Metric-3", buildMetricDatapoint());
        
        // Initialize Profile
        metricProfile = builder
            .withId(UUID.randomUUID().toString())
            .withMetricProfile(dataMap)
        .build();
        
        // Display record for reference
        LOGGER.info("Displaying record for reference:\n'{}'", metricProfile.toJsonDoc());
        assertionState = metricProfile.getId() != null;
        assertTrue(assertionState, "Test failed to build metric data");
        LOGGER.info("Tests building metric profile");
    }
    
    
    /**
     * Tests building {@link MetricProfile}
     */
    @Test
    @Order(2)
    public void canBuildMetricProfile() {
        
        // Initialize record
        LOGGER.info("Tests building profile of metric profile");
        boolean assertionState;
        MetricProfileBuilder builder = new MetricProfileBuilder();
        MetricProfile metricProfile;
        ProfileData profile;
        
        // Configure map
        LOGGER.info("Configuring ProfileData");
        profile = buildDataProfile();
        
        // Initialize Profile
        metricProfile = builder
            .withId(UUID.randomUUID().toString())
            .withLabel("Metric-Profile-Label")
            .withMeanAvailable(0)
            .withMeanUsed(0)
            .withMeanTotal(0)
            .withProfile(profile)
            .withUnits("GB")
            .withType(MetricType.MEMORY)
            .withTimestamp(System.currentTimeMillis())
            .withAnnotation(new CustomAnnotation())
        .build();
        
        // Display record for reference
        LOGGER.info("Displaying record for reference:\n'{}'", metricProfile.toJsonDoc());
        assertionState = metricProfile.getType() == MetricType.MEMORY;
        assertTrue(assertionState, "Test failed to build metric data");
        LOGGER.info("Tests building metric profile");
    }
    
    
    /**
     * Tests building {@link Job Environment}
     * 
     */
    @Test
    @Order(3)
    public void canBuildJobEnvironment() {
        
        // Initialize record
        LOGGER.info("Tests building profile of Job Environment");
        boolean assertionState;
        JobEnvironment jobEnv;
        
        // Initialize Profile
        jobEnv = JobType.LOCAL.makeJobEnvironment();
        
        // Display record for reference
        LOGGER.info("Displaying record for reference:\n'{}'", jobEnv.toJsonDoc());
        assertionState = jobEnv.getType() == JobType.LOCAL;
        assertTrue(assertionState, "Test failed to build job environment");
        LOGGER.info("Tests building Job Environment");
    }
}