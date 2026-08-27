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
package org.tasktide.core.services;

import jakarta.enterprise.inject.se.SeContainer;

import jakarta.nosql.Template;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import org.junit.Rule;
//import org.testcontainers.containers.GenericContainer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.tasktide.TestCaseBuilderUtility;

import org.tasktide.TestEnvironment;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.repository.JpaRepository;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.repository.TemplateRepository;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;
import org.tasktide.core.supporting.JsonUtils;



/**
 * Tests {@link JobEnvironment} through {@link JpaRepository} and {@link TemplateRepository}
 *
 * @author Brendan Kenna
 */
@Tag("integration-model")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobEnvironmentServiceTests {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(JobEnvironmentServiceTests.class);
    private SeContainer container;
    private EntityManager entityManager;
    private Template template;
    
    
    // Backend repos
    // @Rule
    // private final GenericContainer<?> couchDB = TestEnvironment.couchDbContainer("tasktide_database", false);
    
    // @Rule
    // private final GenericContainer<?> mariaDB = TestEnvironment.mariaDbContainer("tasktide_database");
    
    
    public JobEnvironmentServiceTests() {
    }
    
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating JobEnvironment Service Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("jpa-template.properties", getClass());
        entityManager = JpaRepositoryUtility.get().fetchEntityManager();
        template = TestEnvironment.fetchDocumentTemplate(container);
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating JobEnvironment Service Tests ----------------\n";
        LOGGER.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            LOGGER.info("CDI container shut down");
        }
        // couchDB.stop();
        // mariaDB.stop();
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
     * Tests querying {@link JobEnvironment} through {@link TaskTideService}
     *  using a {@link TemplateRepository} backend
     * 
     */
    @Test
    @Order(0)
    public void canQueryThroughNoSqlJobEnvService() {
    
        // Initialize data
        LOGGER.info("\n\n================ Construct JobEnvironment Service-Template From Factory Test ================\n");
        TaskTideService<JobEnvironment> service;
        RepositoryType repoType = RepositoryType.NOSQL;
        List<JobEnvironment> data;
        boolean assertionState ;
        
        // Generate data
        LOGGER.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestJobEnvironment(),
            TestCaseBuilderUtility.makeTestJobEnvironment(),
            TestCaseBuilderUtility.makeTestJobEnvironment()
        );
        
        // Setup requirements
        LOGGER.info("Configuring Service");
        service = ServiceFactory.makeJobEnvironmentService(repoType, template, "JobEnvironment-Template");
        Map<String, String> map = service.getRepo().getRepositoryMetaData();
        LOGGER.info("Displaying meta data for NoSQL JobEnvironment Service:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        service.extendModel(data);
        
        // Check that records can be queried
        LOGGER.info("Verifying records can be retrieved");
        TaskTideModel<JobEnvironment> ref = data.get(0);
        TaskTideModel<JobEnvironment> result = service.fetchById(ref.getId());
        LOGGER.info("\n\nDisplaying retreieved JobEnvironment:\n'{}'", result.toJsonDoc());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        LOGGER.info("\n\n================ Construct JobEnvironmentService-Template From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests querying {@link JobEnvironment} through {@link TaskTideService}
     *  using a {@link TemplateRepository} backend
     * 
     */
    @Test
    @Order(1)
    public void canQueryThroughJpaJobEnvService() {
    
        // Initialize data
        LOGGER.info("\n\n================ Construct JobEnvironment Service-JpaRepository From Factory Test ================\n");
        TaskTideService<JobEnvironment> service;
        RepositoryType repoType = RepositoryType.SQL;
        List<JobEnvironment> data;
        boolean assertionState ;
        
        // Generate data
        LOGGER.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestJobEnvironment(),
            TestCaseBuilderUtility.makeTestJobEnvironment(),
            TestCaseBuilderUtility.makeTestJobEnvironment()
        );
        
        // Setup requirements
        LOGGER.info("Configuring Service");
        service = ServiceFactory.makeJobEnvironmentService(repoType, entityManager, "JobEnvironment-JpaRepository");
        Map<String, String> map = service.getRepo().getRepositoryMetaData();
        LOGGER.info("Displaying meta data for SQL JobEnvironment Service:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        service.extendModel(data);
        
        // Check that records can be queried
        LOGGER.info("Verifying records can be retrieved");
        TaskTideModel<JobEnvironment> ref = data.get(0);
        TaskTideModel<JobEnvironment> result = service.fetchById(ref.getId());
        LOGGER.info("\n\nDisplaying retreieved JobEnvironment:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        LOGGER.info("\n\n================ Construct JobEnvironmentService-JPARepository From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}