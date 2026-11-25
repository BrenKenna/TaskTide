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
package org.tasktide.core.manager;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.tasktide.TestUtils;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.model.workitem.WorkItem;


// Documentation compatability
import org.tasktide.core.repository.ItemStoreRepository;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.repository.itemstore_repo.ItemStoreRepositoryUtility;
import org.tasktide.core.services.ServiceFactory;
import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.types.ItemStoreType;


/**
 * Suite of tests on {@link TaskTideServiceManager} specifically
 *  using the {@link ItemStoreRepository}
 *
 * @author Bren
 */
@Tag("integration-core")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskTideServiceManagerItemStoreTests {
    
    private final Logger LOGGER = LogManager.getLogger(TaskTideServiceManagerItemStoreTests.class);
    
    private ItemStore itemStore;
    private Map<ManagerTarget, ItemStore> storeMap;
    private final ItemStoreType storeType = ItemStoreType.SQLITE;
    private final String storeName = "TaskTide-ServiceManager-Tests/SQLite";
    private final RepositoryType repoType = RepositoryType.ITEMSTORE;

    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating ServiceManager Using ItemStore Repositories Tests ----------------\n";
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
        String msg = "\n\n---------------- Terminating ServiceManager Using ItemStore Repositories Tests ----------------\n";
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
     * Evaluates that the {@link TaskTideServiceManager} can be configured
     *  using {@link ItemStoreRepository}
     * 
     */
    @Test
    @Order(0)
    public void canInitializeServiceManager() {
        
        // Initialize
        LOGGER.info("\n\n================ Can Initialize ItemStore-Service Manager ================\n");
        TaskTideService<Workflow> workflowServ; TaskTideService<Step> stepServ;
        TaskTideService<WorkItem> workItemServ; TaskTideService<MetricData> metricServ;
        TaskTideService<MetricProfile> profileServ; TaskTideService<JobEnvironment> jobEnvServ;
        
        // Fetch services
        LOGGER.info("Configuring TaskTideModel ItemStore-Services");
        workflowServ = ServiceFactory.makeWorkflowService(
            this.repoType,
            this.storeMap.get(ManagerTarget.WORKFLOW),
            "Workflow"
        );
        stepServ = ServiceFactory.makeStepService(
            this.repoType,
            this.storeMap.get(ManagerTarget.STEP),
            "Step"
        );
        workItemServ = ServiceFactory.makeWorkItemService(
            this.repoType,
            this.storeMap.get(ManagerTarget.WORKITEM),
            "WorkItem"
        );
        
        // Fetch additional
        metricServ = ServiceFactory.makeMetricDataService(
            this.repoType,
            this.storeMap.get(ManagerTarget.METRIC_DATA),
            "MetricData"
        );
        profileServ = ServiceFactory.makeMetricProfileService(
            this.repoType,
            this.storeMap.get(ManagerTarget.METRIC_PROFILE),
            "MetricProfile"
        );
        jobEnvServ = ServiceFactory.makeJobEnvironmentService(
            this.repoType,
            this.storeMap.get(ManagerTarget.JOB_ENVIRONMENT),
            "JobEnvironment"
        );
        
        // Initialize service manager with services
        try {
            TaskTideServiceManager.initialize(
                workItemServ, stepServ, workflowServ,
                jobEnvServ, metricServ, profileServ
            );
        }
        catch ( IllegalStateException ex) {
            LOGGER.warn(
                "Modifying ItemStore-ServiceManager for provided ItemStoreMap"
            );
            TaskTideServiceManager.modify(
                workItemServ, stepServ, workflowServ,
                jobEnvServ, metricServ, profileServ
            );
        }
        
        // Evaluate test
        LOGGER.info(
            "ItemStore-ServiceManager configuration state is:\t'{}'",
            TaskTideServiceManager.isInitialized()
        );
        Assertions.assertTrue(
            TaskTideServiceManager.isInitialized(),
            "Unable to configure ItemStore-ServiceManager"
        );
        LOGGER.info("\n\n================ Can Initialize ItemStore-Service Manager ================\n");
    }
    
    
    /**
     * Evaluates that the {@link ItemStoreRepository} {@link TaskTideServiceManager}
     *  can be serialized to json
     */
    @Test
    @Order(1)
    public void canSerializeServiceManager() {
    
        // Initialize vars
        LOGGER.info("\n\n================ Can Serialize ItemStore-Service Manager ================\n");
        String json = "";
        boolean assertionState = false;
        
        // Initialize service manager
        LOGGER.info("Initializing ItemStore-ServiceManager");
        ItemStoreRepositoryUtility.get().initServiceManager();
        
        // Try seriailize service manager
        try {
            json = TaskTideServiceManager.toJson();
            if ( json != null ) {
                if ( !json.isEmpty() ) {
                    assertionState = true;
                }
            }
        }
        
        // Otherwise catch checked & unchecked exceptions
        catch ( Exception ex ) {
            LOGGER.error(
                "Error serializing ItemStore-ServiceManager:\t'{}'\n\n",
                ex.getMessage(), ex
            );
            assertionState = false;
        }
        
        // Evaluate test status
        if ( assertionState ) {
            LOGGER.info(
                "Test successful with serialized ItemStore-ServiceManager. Displaying for reference\n\n'{}'",
                json
            );
        }
        else {
            LOGGER.warn("Unable to serialize ItemStore-ServiceManager, failing test");
        }
        Assertions.assertTrue(assertionState, "Unable to serialize ItemStore-ServiceManager");
        LOGGER.info("\n\n================ Can Serialize ItemStore-Service Manager ================\n");
    }
}
