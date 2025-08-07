/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.tasktide.core.repository;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.tasktide.TestCaseBuilderUtility;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.itemstore_repo.ItemStoreRepositoryUtility;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.ItemStoreType;


/**
 * Unit tests for RocksDB backend {@link ItemStoreRepository} across the
 *  {@link TaskTideModel}
 * 
 * @author bkenna
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RocksDbItemStoreRepositoryTests {
    
    // Logger for tests
    private static final Logger logger = LogManager.getLogger(RocksDbItemStoreRepositoryTests.class);

    // Backend repo
    private final ItemStoreType storeType = ItemStoreType.ROCKSDB;
    private final String storeName = "TaskTideRepository/RocksDB";
    private ItemStore itemStore;
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating RocksDbItemStore-Repository Tests ----------------\n";
        logger.info(msg);
        ItemStoreRepositoryUtility.initialize(storeType, storeName);
        itemStore = ItemStoreRepositoryUtility.get().fetchItemStore(storeName, storeType);
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating RocksDbItemStore-Repository Tests ----------------\n";
        logger.info(msg);
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
     * Tests query & retrieval WorkItem from RocksDbItemStore WorkItem Repository
     */
    @Test
    @Order(0)
    public void canQueryInsertWorkItem() {
        
        // Initialize data
        logger.info("\n\n================ Can Query RocksDbItemStore WorkItem Repository ================\n");
        TaskTideRepository<WorkItem> workItemRepo;
        RepositoryFactory<WorkItem> workItemRepoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
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
        logger.info("Fetching RocksDbItemStore for repository construction");
        workItemRepoFactory = new RepositoryFactory<>("WorkItem", WorkItem.class, itemStore, repoType);
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
        logger.info("\n\n================ Can Query RocksDbItemStore WorkItem Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests query & retrieval Step from RocksDbItemStore Step Repository
     * 
     */
    @Test
    @Order(1)
    public void canQueryInsertStep() {
        
        // Initialize data
        logger.info("\n\n================ Can Query RocksDbItemStore Step Repository ================\n");
        TaskTideRepository<Step> stepRepo;
        RepositoryFactory<Step> stepRepoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<Step> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestStepList();
        
        // Fetch backend instance
        logger.info("Fetching RocksDbItemStore for repository construction");
        stepRepoFactory = new RepositoryFactory<>("Step", Step.class, itemStore, repoType);
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
        logger.info("\n\n================ Can Query RocksDbItemStore Step Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests query & retrieval Workflow from RocksDbItemStore Workflow Repository
     * 
     */
    @Test
    @Order(1)
    public void canQueryInsertWorkflow() {
        
        // Initialize data
        logger.info("\n\n================ Can Query RocksDbItemStore Workflow Repository ================\n");
        TaskTideRepository<Workflow> workflowRepo;
        RepositoryFactory<Workflow> workflowRepoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<Workflow> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestWorkflows();
        
        // Fetch backend instance
        logger.info("Fetching JPA for repository construction");
        workflowRepoFactory = new RepositoryFactory<>("Workflow", Workflow.class, itemStore, repoType);
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
        logger.info("\n\n================ Can Query RocksDbItemStore Workflow Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}