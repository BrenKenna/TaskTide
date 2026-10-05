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
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.TestCaseBuilderUtility;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.itemstore_repo.ItemStoreRepositoryUtility;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.types.ItemStoreType;


/**
 * Unit tests for RocksDB backend {@link ItemStoreRepository} across the
 *  {@link TaskTideModel}
 * 
 * @author bkenna
 */
@Tag("integration-rocksdb-core")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RocksDbItemStoreRepositoryTests {
    
    // Logger for tests
    private static final Logger LOGGER = LogManager.getLogger(RocksDbItemStoreRepositoryTests.class);

    // Backend repo
    private final ItemStoreType storeType = ItemStoreType.ROCKSDB;
    private final String storeName = "TaskTideRepository/RocksDB";
    private Map<ManagerTarget, ItemStore> storeMap;
    private ItemStore itemStore;
    
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating RocksDbItemStore-Repository Tests ----------------\n";
        LOGGER.info(msg);
        ItemStoreRepositoryUtility.initialize(
            this.storeType,
            this.storeName
        );
        this.storeMap = ItemStoreRepositoryUtility
            .get()
            .fetchItemStoreMap(
                this.storeType,
                this.storeName
        );
        this.itemStore = this.storeMap.get(ManagerTarget.WORKITEM);
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating RocksDbItemStore-Repository Tests ----------------\n";
        LOGGER.info(msg);
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
     * Tests query & retrieval WorkItem from RocksDbItemStore WorkItem Repository
     */
    @Test
    @Order(0)
    public void canQueryInsertWorkItems() {
        
        // Initialize data
        LOGGER.info("\n\n================ Can Query RocksDbItemStore WorkItem Repository ================\n");
        TaskTideRepository<WorkItem> workItemRepo;
        RepositoryFactory<WorkItem> workItemRepoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<WorkItem> data;
        boolean assertionState;
        
        // Generate data for insert
        LOGGER.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Fetch backend instance
        LOGGER.info("Fetching RocksDbItemStore for repository construction");
        workItemRepoFactory = new RepositoryFactory<>("WorkItem", WorkItem.class, itemStore, repoType);
        workItemRepo = workItemRepoFactory.make();
        Map<String, String> map = workItemRepo.getRepositoryMetaData();
        LOGGER.info("\nDisplaying meta data for WorkItemRepository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        LOGGER.info("Inserting records");
        workItemRepo.extendModel(data);
        
        // Check that records can be queried
        LOGGER.info("\nVerifying records can be retrieved");
        TaskTideModel<WorkItem> ref = data.get(0);
        TaskTideModel<WorkItem> result = workItemRepo.findById(ref.getId()).get();
        assertionState = result != null;
        LOGGER.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        LOGGER.info("\n\n================ Can Query RocksDbItemStore WorkItem Repository ================\n");
        Assertions.assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests query & retrieval Step from RocksDbItemStore Step Repository
     * 
     */
    @Test
    @Order(1)
    public void canQueryInsertSingleStep() {
        
        // Initialize data
        LOGGER.info("\n\n================ Can Query RocksDbItemStore Step Repository ================\n");
        TaskTideRepository<Step> stepRepo;
        RepositoryFactory<Step> stepRepoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<Step> data;
        boolean assertionState;
        
        // Generate data for insert
        LOGGER.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestStepList();
        
        // Fetch backend instance
        LOGGER.info("Fetching RocksDbItemStore for repository construction");
        stepRepoFactory = new RepositoryFactory<>("Step", Step.class, itemStore, repoType);
        stepRepo = stepRepoFactory.make();
        Map<String, String> map = stepRepo.getRepositoryMetaData();
        LOGGER.info("\nDisplaying meta data for StepRepository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        LOGGER.info("Inserting records");
        stepRepo.insertModel(data.get(0));
        
        // Check that records can be queried
        LOGGER.info("\nVerifying records can be retrieved");
        TaskTideModel<Step> ref = data.get(0);
        TaskTideModel<Step> result = stepRepo.findById(ref.getId()).get();
        assertionState = result != null;
        LOGGER.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        LOGGER.info("\n\n================ Can Query RocksDbItemStore Step Repository ================\n");
        Assertions.assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests query & retrieval Workflow from RocksDbItemStore Workflow Repository
     * 
     */
    @Test
    @Order(2)
    public void canQueryInsertWorkflow() {
        
        // Initialize data
        LOGGER.info("\n\n================ Can Query RocksDbItemStore Workflow Repository ================\n");
        TaskTideRepository<Workflow> workflowRepo;
        RepositoryFactory<Workflow> workflowRepoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<Workflow> data;
        boolean assertionState;
        
        // Generate data for insert
        LOGGER.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestWorkflows();
        
        // Fetch backend instance
        LOGGER.info("Fetching JPA for repository construction");
        workflowRepoFactory = new RepositoryFactory<>("Workflow", Workflow.class, itemStore, repoType);
        workflowRepo = workflowRepoFactory.make();
        Map<String, String> map = workflowRepo.getRepositoryMetaData();
        LOGGER.info("\nDisplaying meta data for Workflow Repository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        LOGGER.info("Inserting records");
        workflowRepo.extendModel(data);
        
        // Check that records can be queried
        LOGGER.info("\nVerifying records can be retrieved");
        TaskTideModel<Workflow> ref = data.get(0);
        TaskTideModel<Workflow> result = workflowRepo.findById(ref.getId()).get();
        assertionState = result != null;
        LOGGER.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        LOGGER.info("\n\n================ Can Query RocksDbItemStore Workflow Repository ================\n");
        Assertions.assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
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
        LOGGER.info("\n\n================ Can Query JPA MetricData Repository ================\n");
        TaskTideRepository<MetricData> repo;
        RepositoryFactory<MetricData> repoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<MetricData> data;
        boolean assertionState;
        
        // Generate data for insert
        LOGGER.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestMetricData(),
            TestCaseBuilderUtility.makeTestMetricData(),
            TestCaseBuilderUtility.makeTestMetricData()
        );
        
        // Fetch backend instance
        LOGGER.info("Fetching ItemStore for repository construction");
        repoFactory = new RepositoryFactory<>("MetricData", MetricData.class, itemStore, repoType);
        repo = repoFactory.make();
        Map<String, String> map = repo.getRepositoryMetaData();
        LOGGER.info("\nDisplaying meta data for MetricData Repository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        LOGGER.info("Inserting records");
        repo.extendModel(data);
        
        // Check that records can be queried
        LOGGER.info("\nVerifying records can be retrieved");
        TaskTideModel<MetricData> ref = data.get(0);
        TaskTideModel<MetricData> result = repo.findById(ref.getId()).get();
        assertionState = result != null;
        LOGGER.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        LOGGER.info("\n\n================ Can Query ItemStore MetricData Repository ================\n");
        Assertions.assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
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
        LOGGER.info("\n\n================ Can Query ItemStore MetricProfile Repository ================\n");
        TaskTideRepository<MetricProfile> repo;
        RepositoryFactory<MetricProfile> repoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<MetricProfile> data;
        boolean assertionState;
        
        // Generate data for insert
        LOGGER.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestMetricProfile(),
            TestCaseBuilderUtility.makeTestMetricProfile(),
            TestCaseBuilderUtility.makeTestMetricProfile()
        );
        
        // Fetch backend instance
        LOGGER.info("Fetching ItemStore for repository construction");
        repoFactory = new RepositoryFactory<>("MetricProfile", MetricProfile.class, itemStore, repoType);
        repo = repoFactory.make();
        Map<String, String> map = repo.getRepositoryMetaData();
        LOGGER.info("\nDisplaying meta data for MetricData Repository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        LOGGER.info("Inserting records");
        repo.extendModel(data);
        
        // Check that records can be queried
        LOGGER.info("\nVerifying records can be retrieved");
        TaskTideModel<MetricProfile> ref = data.get(0);
        TaskTideModel<MetricProfile> result = repo.findById(ref.getId()).get();
        assertionState = result != null;
        LOGGER.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        LOGGER.info("\n\n================ Can Query ItemStore MetricProfile Repository ================\n");
        Assertions.assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
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
        LOGGER.info("\n\n================ Can Query ItemStore JobEnvironment Repository ================\n");
        TaskTideRepository<JobEnvironment> repo;
        RepositoryFactory<JobEnvironment> repoFactory;
        RepositoryType repoType = RepositoryType.ITEMSTORE;
        List<JobEnvironment> data;
        boolean assertionState;
        
        // Generate data for insert
        LOGGER.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestJobEnvironment(),
            TestCaseBuilderUtility.makeTestJobEnvironment(),
            TestCaseBuilderUtility.makeTestJobEnvironment()
        );
        
        // Fetch backend instance
        LOGGER.info("Fetching ItemStore for repository construction");
        repoFactory = new RepositoryFactory<>("JobEnvironment", JobEnvironment.class, itemStore, repoType);
        repo = repoFactory.make();
        Map<String, String> map = repo.getRepositoryMetaData();
        LOGGER.info("\nDisplaying meta data for MetricData Repository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        LOGGER.info("Inserting records");
        repo.extendModel(data);
        
        // Check that records can be queried
        LOGGER.info("\nVerifying records can be retrieved");
        TaskTideModel<JobEnvironment> ref = data.get(0);
        TaskTideModel<JobEnvironment> result = repo.findById(ref.getId()).get();
        assertionState = result != null;
        LOGGER.info("\nDisplayling retrieved record:\n\n{}", JsonUtils.toJson(true, result));
        
        // Evaluate
        LOGGER.info("\n\n================ Can Query ItemStore JobEnvironment Repository ================\n");
        Assertions.assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}