/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.tasktide.core.repository;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;

import org.testcontainers.containers.GenericContainer;

import org.tasktide.TestCaseBuilderUtility;
import org.tasktide.TestEnvironment;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;
import org.tasktide.core.supporting.JsonUtils;


/**
 * Unit tests for {@link TemplateRepository} across the
 *  {@link TaskTideModel}
 * 
 * @author bkenna
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JpaRepositoryTests {
    
    // Logger for tests
    private static final Logger logger = LogManager.getLogger(JpaRepositoryTests.class);
    
    
    // Backend repo
    @Rule
    public GenericContainer<?> mariaDB = TestEnvironment.mariaDbContainer("tasktide_database");
    
    
    // Container for fetch nosql template
    private SeContainer container;
    private EntityManager entityManager;
    
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating JPA-Repository Tests ----------------\n";
        logger.info(msg);
        container = TestEnvironment.startWeldContainer("jpa-config.properties", getClass());
        entityManager = JpaRepositoryUtility.get().fetchEntityManager();
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating JPA-Repository Tests ----------------\n";
        logger.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            logger.info("CDI container shut down");
        }
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
     * Tests query & retrieval WorkItem from JPA WorkItem Repository
     */
    @Test
    @Order(0)
    public void canQueryInsertWorkItem() {
        
        // Initialize data
        logger.info("\n\n================ Can Query JPA WorkItem Repository ================\n");
        TaskTideRepository<WorkItem> workItemRepo;
        RepositoryFactory<WorkItem> workItemRepoFactory;
        RepositoryType repoType = RepositoryType.SQL;
        List<WorkItem> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Fetch backend instance
        logger.info("Fetching JPA for repository construction");
        workItemRepoFactory = new RepositoryFactory<>("WorkItem", WorkItem.class, entityManager, repoType);
        workItemRepo = workItemRepoFactory.make();
        Map<String, String> map = workItemRepo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for WorkItemRepository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        logger.info("Inserting records");
        data.stream()
            .forEach( elm -> workItemRepo.insertModel(elm));
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<WorkItem> ref = data.get(0);
        TaskTideModel<WorkItem> result = workItemRepo.findById(ref.getId()).get();
        assertionState = result != null;
        logger.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        logger.info("\n\n================ Can Query JPA WorkItem Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests query & retrieval Step from JPA Step Repository
     * 
     */
    @Test
    @Order(1)
    public void canQueryInsertStep() {
        
        // Initialize data
        logger.info("\n\n================ Can Query JPA Step Repository ================\n");
        TaskTideRepository<Step> stepRepo;
        RepositoryFactory<Step> stepRepoFactory;
        RepositoryType repoType = RepositoryType.SQL;
        List<Step> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestStepList();
        
        // Fetch backend instance
        logger.info("Fetching JPA for repository construction");
        stepRepoFactory = new RepositoryFactory<>("Step", Step.class, entityManager, repoType);
        stepRepo = stepRepoFactory.make();
        Map<String, String> map = stepRepo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for StepRepository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        logger.info("Inserting records");
        data.stream()
            .forEach( elm -> stepRepo.insertModel(elm));
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<Step> ref = data.get(0);
        TaskTideModel<Step> result = stepRepo.findById(ref.getId()).get();
        assertionState = result != null;
        logger.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        logger.info("\n\n================ Can Query JPA Step Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests query & retrieval Workflow from JPA Workflow Repository
     * 
     */
    @Test
    @Order(1)
    public void canQueryInsertWorkflow() {
        
        // Initialize data
        logger.info("\n\n================ Can Query JPA Workflow Repository ================\n");
        TaskTideRepository<Workflow> workflowRepo;
        RepositoryFactory<Workflow> workflowRepoFactory;
        RepositoryType repoType = RepositoryType.SQL;
        List<Workflow> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestWorkflows();
        
        // Fetch backend instance
        logger.info("Fetching JPA for repository construction");
        workflowRepoFactory = new RepositoryFactory<>("Workflow", Workflow.class, entityManager, repoType);
        workflowRepo = workflowRepoFactory.make();
        Map<String, String> map = workflowRepo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for Workflow Repository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        logger.info("Inserting records");
        data.stream()
            .forEach( elm -> workflowRepo.insertModel(elm));
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<Workflow> ref = data.get(0);
        TaskTideModel<Workflow> result = workflowRepo.findById(ref.getId()).get();
        assertionState = result != null;
        logger.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        logger.info("\n\n================ Can Query JPA Workflow Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}