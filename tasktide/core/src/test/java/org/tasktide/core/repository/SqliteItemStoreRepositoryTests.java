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
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.itemstore_repo.ItemStoreRepositoryUtility;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.ItemStoreType;


/**
 * Unit tests for SQLite backed {@link ItemStoreRepository} across the
 *  {@link TaskTideModel}
 * 
 * @author bkenna
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqliteItemStoreRepositoryTests {
    
    // Logger for tests
    private static final Logger logger = LogManager.getLogger(SqliteItemStoreRepositoryTests.class);

    // Backend repo
    private final ItemStoreType storeType = ItemStoreType.SQLITE;
    private final String storeName = "TaskTideRepository/SQLite";
    private ItemStore itemStore;
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating SQLiteItemStore-Repository Tests ----------------\n";
        logger.info(msg);
        ItemStoreRepositoryUtility.initialize(storeType, storeName);
        itemStore = ItemStoreRepositoryUtility.get().fetchItemStore(storeName, storeType);
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating SQLiteItemStore-Repository Tests ----------------\n";
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
     * Tests query & retrieval WorkItem from SQLiteItemStore WorkItem Repository
     */
    @Test
    @Order(0)
    public void canQueryInsertWorkItem() {
        
        // Initialize data
        logger.info("\n\n================ Can Query SQLiteItemStore WorkItem Repository ================\n");
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
        logger.info("Fetching SQLiteItemStore for repository construction");
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
        logger.info("\n\n================ Can Query SQLiteItemStore WorkItem Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests query & retrieval Step from SQLiteItemStore Step Repository
     * 
     */
    @Test
    @Order(1)
    public void canQueryInsertStep() {
        
        // Initialize data
        logger.info("\n\n================ Can Query SQLiteItemStore Step Repository ================\n");
        TaskTideRepository<Step> stepRepo;
        RepositoryFactory<Step> stepRepoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<Step> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestStepList();
        
        // Fetch backend instance
        logger.info("Fetching SQLiteItemStore for repository construction");
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
        logger.info("\n\n================ Can Query SQLiteItemStore Step Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests query & retrieval Workflow from SQLiteItemStore Workflow Repository
     * 
     */
    @Test
    @Order(2)
    public void canQueryInsertWorkflow() {
        
        // Initialize data
        logger.info("\n\n================ Can Query SQLiteItemStore Workflow Repository ================\n");
        TaskTideRepository<Workflow> workflowRepo;
        RepositoryFactory<Workflow> workflowRepoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<Workflow> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestWorkflows();
        
        // Fetch backend instance
        logger.info("Fetching SQLiteItemStore for repository construction");
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
        logger.info("\n\n================ Can Query SQLiteItemStore Workflow Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests inserting and querying a set of {@link MetricData}
     *  from {@link ItemStoreRepository}
     * 
     */
    @Test
    @Order(3)
    public void canQueryInsertedMetricData() {
    
        // Initialize data
        logger.info("\n\n================ Can Query JPA MetricData Repository ================\n");
        TaskTideRepository<MetricData> repo;
        RepositoryFactory<MetricData> repoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<MetricData> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestMetricData(),
            TestCaseBuilderUtility.makeTestMetricData(),
            TestCaseBuilderUtility.makeTestMetricData()
        );
        
        // Fetch backend instance
        logger.info("Fetching ItemStore for repository construction");
        repoFactory = new RepositoryFactory<>("MetricData", MetricData.class, itemStore, repoType);
        repo = repoFactory.make();
        Map<String, String> map = repo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for MetricData Repository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        logger.info("Inserting records");
        repo.extendModel(data);
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<MetricData> ref = data.get(0);
        TaskTideModel<MetricData> result = repo.findById(ref.getId()).get();
        assertionState = result != null;
        logger.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        logger.info("\n\n================ Can Query ItemStore MetricData Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests inserting and querying a set of {@link MetricProfile}
     *  from {@link ItemStoreRepository}
     * 
     */
    @Test
    @Order(4)
    public void canQueryInsertedMetricProfile() {
    
        // Initialize data
        logger.info("\n\n================ Can Query ItemStore MetricProfile Repository ================\n");
        TaskTideRepository<MetricProfile> repo;
        RepositoryFactory<MetricProfile> repoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<MetricProfile> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestMetricProfile(),
            TestCaseBuilderUtility.makeTestMetricProfile(),
            TestCaseBuilderUtility.makeTestMetricProfile()
        );
        
        // Fetch backend instance
        logger.info("Fetching ItemStore for repository construction");
        repoFactory = new RepositoryFactory<>("MetricProfile", MetricProfile.class, itemStore, repoType);
        repo = repoFactory.make();
        Map<String, String> map = repo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for MetricData Repository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        logger.info("Inserting records");
        repo.extendModel(data);
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<MetricProfile> ref = data.get(0);
        TaskTideModel<MetricProfile> result = repo.findById(ref.getId()).get();
        assertionState = result != null;
        logger.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        logger.info("\n\n================ Can Query ItemStore MetricProfile Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests inserting and querying a set of {@link JobEnvironment}
     *  from {@link ItemStoreRepository}
     * 
     */
    @Test
    @Order(5)
    public void canQueryInsertedJobEnvironment() {
    
        // Initialize data
        logger.info("\n\n================ Can Query ItemStore JobEnvironment Repository ================\n");
        TaskTideRepository<JobEnvironment> repo;
        RepositoryFactory<JobEnvironment> repoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<JobEnvironment> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestJobEnvironment(),
            TestCaseBuilderUtility.makeTestJobEnvironment(),
            TestCaseBuilderUtility.makeTestJobEnvironment()
        );
        
        // Fetch backend instance
        logger.info("Fetching ItemStore for repository construction");
        repoFactory = new RepositoryFactory<>("JobEnvironment", JobEnvironment.class, itemStore, repoType);
        repo = repoFactory.make();
        Map<String, String> map = repo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for MetricData Repository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        logger.info("Inserting records");
        repo.extendModel(data);
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<JobEnvironment> ref = data.get(0);
        TaskTideModel<JobEnvironment> result = repo.findById(ref.getId()).get();
        assertionState = result != null;
        logger.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        logger.info("\n\n================ Can Query ItemStore JobEnvironment Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}