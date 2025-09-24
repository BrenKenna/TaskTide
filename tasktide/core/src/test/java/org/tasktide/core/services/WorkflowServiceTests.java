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

import org.eclipse.jnosql.mapping.core.Converters;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.document.spi.DocumentExtension;
import org.eclipse.jnosql.mapping.reflection.Reflections;
import org.eclipse.jnosql.mapping.reflection.spi.ReflectionEntityMetadataExtension;
import org.eclipse.jnosql.mapping.semistructured.EntityConverter;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.Rule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;

import org.tasktide.TestCaseBuilderUtility;
import org.tasktide.TestEnvironment;
import org.tasktide.TestUtils;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.model.collection.Workflow;

import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;
import org.tasktide.core.supporting.JsonUtils;
import org.tasktide.itemstore.ItemStore;
import org.testcontainers.containers.GenericContainer;


/**
 * Test cases for {@link TaskTideService} of {@link Workflow} for each
 *  repository type
 *
 * @author bkenna
 */
@EnableAutoWeld
@AddPackages(value = {Converters.class, Reflections.class, EntityConverter.class, Template.class, DocumentTemplate.class})
@AddExtensions( {ReflectionEntityMetadataExtension.class, DocumentExtension.class} )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkflowServiceTests {
    
    private static final Logger logger = LogManager.getLogger(WorkflowServiceTests.class);
    private SeContainer container;
    private EntityManager entityManager;
    private Template template;
    
    // Backend repos
    @Rule
    private final GenericContainer<?> couchDB = TestEnvironment.couchDbContainer("tasktide_database", false);
    
    @Rule
    private final GenericContainer<?> mariaDB = TestEnvironment.mariaDbContainer("tasktide_database");
    
    public WorkflowServiceTests() {}
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Workflow Service Tests ----------------\n";
        logger.info(msg);
        container = TestEnvironment.startWeldContainer("jpa-template.properties", getClass());
        entityManager = JpaRepositoryUtility.get().fetchEntityManager();
        template = TestEnvironment.fetchDocumentTemplate(container);
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Workflow Service Tests ----------------\n";
        logger.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            logger.info("CDI container shut down");
        }
        couchDB.stop();
        mariaDB.stop();
    }
    
    @BeforeEach
    public void setUp() {
        logger.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("\n\n================ Terminating Test ================\n");
    }

    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(0)
    public void canConstructWorkflowJsonService() {
    
        // Initialize data
        logger.info("\n\n================ Construct WorkflowService-JSON From Factory Test ================\n");
        TaskTideService<Workflow> workflowService;
        RepositoryType repoType;
        List<Workflow> backend;
        boolean assertionState;
        
        // Generate data
        logger.info("Generating data for testing");
        repoType = RepositoryType.JSON;
        backend = TestCaseBuilderUtility.makeTestWorkflows();
        
        // Setup requirements
        logger.info("Configuring Service");
        workflowService = ServiceFactory.makeWorkflowService(repoType, backend, "Workflow-Service");
        Map<String, String> map = workflowService.getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for JSON Workflow Service:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<Workflow> ref = backend.get(0);
        TaskTideModel<Workflow> result = workflowService.fetchById(ref.getId());
        logger.info("\n\nDisplaying retreieved Workflow:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        logger.info("\n\n================ Construct WorkflowService-JSON From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a workflow can be fetched 
     */
    @Test
    @Order(1)
    public void canConstructWorkflowRocksDbService() {
    
        // Initialize data
        logger.info("\n\n================ Construct WorkflowService-RocksDB From Factory Test ================\n");
        TaskTideService<Workflow> workflowService;
        RepositoryType repoType;
        ItemStore backend;
        List<Workflow> data;
        boolean assertionState ;
        
        // Generate data
        logger.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestWorkflows();
        
        // Configure requirements
        repoType = RepositoryType.ITEMSTORE;
        String collectionName = TestUtils.resolveRocksRepoPath();
        backend = TestUtils.fetchItemStore(collectionName);
        
        // Setup requirements
        logger.info("Configuring Service");
        workflowService = ServiceFactory.makeWorkflowService(repoType, backend, "Workflow-Service");
        Map<String, String> map = workflowService.getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for RocksDB Workflow Service:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Add records
        workflowService.extendModel(data);
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<Workflow> ref = data.get(0);
        TaskTideModel<Workflow> result = workflowService.fetchById(ref.getId());
        logger.info("\n\nDisplaying retreieved Workflow:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        logger.info("\n\n================ Construct WorkflowService-RocksDB From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a workflow can be fetched from template service
     */
    @Test
    @Order(2)
    public void canConstructWorkflowNoSqlService() {
    
        // Initialize data
        logger.info("\n\n================ Construct WorkflowService-Template From Factory Test ================\n");
        TaskTideService<Workflow> workflowService;
        RepositoryType repoType;
        Template backend;
        List<Workflow> data;
        boolean assertionState ;
        
        // Generate data
        logger.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestWorkflows();
        
        // Configure requirements
        repoType = RepositoryType.NOSQL;
        backend = TestUtils.fetchTemplate();
        
        // Setup requirements
        logger.info("Configuring Service");
        workflowService = ServiceFactory.makeWorkflowService(repoType, backend, "Workflow-Service");
        Map<String, String> map = workflowService.getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for NoSQL Workflow Service:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Add records
        workflowService.extendModel(data);
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<Workflow> ref = data.get(0);
        TaskTideModel<Workflow> result = workflowService.fetchById(ref.getId());
        logger.info("\n\nDisplaying retreieved Workflow:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        logger.info("\n\n================ Construct WorkflowService-Template From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a workflow can be fetched 
     */
    @Test
    @Order(3)
    public void canConstructWorkflowSqlService() {
    
        // Initialize data
        logger.info("\n\n================ Construct WorkflowService-JPA From Factory Test ================\n");
        TaskTideService<Workflow> workflowService;
        RepositoryType repoType;
        EntityManager backend;
        List<Workflow> data;
        boolean assertionState;
        
        // Generate data
        logger.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestWorkflows();
        
        // Configure requirements
        repoType = RepositoryType.SQL;
        backend = JpaRepositoryUtility.get().fetchEntityManager();
        
        // Setup requirements
        logger.info("Configuring Service");
        workflowService = ServiceFactory.makeWorkflowService(repoType, backend, "Workflow-Service");
        Map<String, String> map = workflowService.getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for JPA Step Service:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        workflowService.extendModel(data);
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<Workflow> ref = data.get(0);
        TaskTideModel<Workflow> result = workflowService.fetchById(ref.getId());
        logger.info("\n\nDisplaying retreieved Workflow:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        logger.info("\n\n================ Construct Workflow-JPA From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}