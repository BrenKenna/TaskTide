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

// import org.testcontainers.containers.GenericContainer;
// import org.junit.Rule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.TestCaseBuilderUtility;
import org.tasktide.TestEnvironment;
import org.tasktide.TestUtils;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;
import org.tasktide.core.supporting.JsonUtils;
import org.tasktide.itemstore.ItemStore;


/**
 * Test cases for {@link TaskTideService} of {@link WorkItem} for each
 *  repository type
 *
 * @author bkenna
 */
@Tag("unit-repo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkItemServiceTests {
    
    private static final Logger logger = LogManager.getLogger(WorkItemServiceTests.class);
    private SeContainer container;
    private EntityManager entityManager;
    private Template template;
    
    // Backend repos
    //@Rule
    //private final GenericContainer<?> couchDB = TestEnvironment.couchDbContainer("tasktide_database", false);
    
    // @Rule
    // private final GenericContainer<?> mariaDB = TestEnvironment.mariaDbContainer("tasktide_database");
    
    
    public WorkItemServiceTests() {}
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating WorkItem Service Tests ----------------\n";
        logger.info(msg);
        container = TestEnvironment.startWeldContainer("jpa-template.properties", getClass());
        entityManager = JpaRepositoryUtility.get().fetchEntityManager();
        template = TestEnvironment.fetchDocumentTemplate(container);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating WorkItem Service Tests ----------------\n";
        logger.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            logger.info("CDI container shut down");
        }
        //couchDB.stop();
        // mariaDB.stop();
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
    public void canConstructWorkItemJsonService() {
    
        // Initialize data
        logger.info("\n\n================ Construct WorkItemService-JSON From Factory Test ================\n");
        TaskTideService<WorkItem> workItemService;
        RepositoryType repoType;
        List<WorkItem> backend;
        boolean assertionState;
        
        // Generate data
        logger.info("Generating data for testing");
        repoType = RepositoryType.JSON;
        backend = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Setup requirements
        logger.info("Configuring Service");
        workItemService = ServiceFactory.makeWorkItemService(repoType, backend, "WorkItem-Service");
        Map<String, String> map = workItemService.getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for JSON WorkItem Service:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<WorkItem> ref = backend.get(0);
        TaskTideModel<WorkItem> result = workItemService.fetchById(ref.getId());
        logger.info("\n\nDisplaying retreieved WorkItem:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        logger.info("\n\n================ Construct WorkItemService-JSON From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(1)
    public void canConstructWorkItemRocksDbService() {
    
        // Initialize data
        logger.info("\n\n================ Construct WorkItemService-RocksDB From Factory Test ================\n");
        TaskTideService<WorkItem> workItemService;
        RepositoryType repoType;
        ItemStore backend;
        List<WorkItem> data;
        boolean assertionState ;
        
        // Generate data
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Configure requirements
        repoType = RepositoryType.ITEMSTORE;
        String collectionName = TestUtils.resolveRocksRepoPath();
        backend = TestUtils.fetchItemStore(collectionName);
        
        // Setup requirements
        logger.info("Configuring Service");
        workItemService = ServiceFactory.makeWorkItemService(repoType, backend, "WorkItem-Service");
        Map<String, String> map = workItemService.getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for RocksDB-WorkItem Service:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Add records
        workItemService.extendModel(data);
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<WorkItem> ref = data.get(0);
        TaskTideModel<WorkItem> result = workItemService.fetchById(ref.getId());
        logger.info("\n\nDisplaying retreieved WorkItem:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        logger.info("\n\n================ Construct WorkItemService-RocksDB From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(2)
    public void canConstructWorkItemNoSqlService() {
    
        // Initialize data
        logger.info("\n\n================ Construct WorkItemService-Template From Factory Test ================\n");
        TaskTideService<WorkItem> workItemService;
        RepositoryType repoType;
        Template backend;
        List<WorkItem> data;
        boolean assertionState ;
        
        // Generate data
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Configure requirements
        repoType = RepositoryType.NOSQL;
        backend = TestUtils.fetchTemplate();
        
        // Setup requirements
        logger.info("Configuring Service");
        workItemService = ServiceFactory.makeWorkItemService(repoType, backend, "WorkItem-Service");
        Map<String, String> map = workItemService.getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for Template WorkItem Service:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Add records
        workItemService.extendModel(data);
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        logger.info("\n\nDisplaying first record for reference:\n'{}'", JsonUtils.toJson(true, data.get(0)));
        TaskTideModel<WorkItem> ref = data.get(0);
        TaskTideModel<WorkItem> result = workItemService.fetchById(ref.getId());
        logger.info("\n\nDisplaying retreieved WorkItem:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        logger.info("\n\n================ Construct WorkItemService-Template From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(3)
    public void canConstructWorkItemSqlService() {
    
        // Initialize data
        logger.info("\n\n================ Construct WorkItemService-JPA From Factory Test ================\n");
        TaskTideService<WorkItem> workItemService;
        RepositoryType repoType;
        EntityManager backend;
        List<WorkItem> data;
        boolean assertionState ;
        
        // Generate data
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Configure requirements
        repoType = RepositoryType.SQL;
        backend = JpaRepositoryUtility.get().fetchEntityManager();
        
        // Setup requirements
        logger.info("Configuring Service");
        workItemService = ServiceFactory.makeWorkItemService(repoType, backend, "WorkItem-Service");
        Map<String, String> map = workItemService.getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for JPA WorkItem Service:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        workItemService.extendModel(data);
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<WorkItem> ref = data.get(0);
        TaskTideModel<WorkItem> result = workItemService.fetchById(ref.getId());
        logger.info("\n\nDisplaying retreieved WorkItem:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        logger.info("\n\n================ Construct WorkItemService-JPA From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}