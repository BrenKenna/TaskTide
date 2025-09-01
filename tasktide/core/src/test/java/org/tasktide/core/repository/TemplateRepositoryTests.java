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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.enterprise.inject.se.SeContainer;

import jakarta.nosql.Template;
import java.util.List;
import java.util.Map;
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

import org.testcontainers.containers.GenericContainer;

import org.tasktide.TestEnvironment;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.supporting.JsonUtils;


/**
 * Unit tests for {@link TemplateRepository} across the
 *  {@link TaskTideModel}
 * 
 * @author bkenna
 */
@EnableAutoWeld
@AddPackages(value = {Converters.class, Reflections.class, EntityConverter.class, Template.class, DocumentTemplate.class})
@AddExtensions( {ReflectionEntityMetadataExtension.class, DocumentExtension.class} )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TemplateRepositoryTests {
    
    // Logger for tests
    private static final Logger logger = LogManager.getLogger(TemplateRepositoryTests.class);
    
    
    // Backend repo
    @Rule
    public GenericContainer<?> couchDB = TestEnvironment.couchDbContainer("tasktide_database", false);
    
    
    // Container for fetch nosql template
    private SeContainer container;
    private Template template;
    
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Template-Repository Tests ----------------\n";
        logger.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = TestEnvironment.fetchDocumentTemplate(container);
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Template-Repository Tests ----------------\n";
        logger.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            logger.info("CDI container shut down");
        }
        couchDB.stop();
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
     * Tests query & retrieval WorkItem from Template WorkItem Repository
     */
    @Test
    @Order(0)
    public void canQueryInsertWorkItem() {
        
        // Initialize data
        logger.info("\n\n================ Can Query Template WorkItem Repository ================\n");
        TaskTideRepository<WorkItem> workItemRepo;
        RepositoryFactory<WorkItem> workItemRepoFactory;
        RepositoryType repoType = RepositoryType.NOSQL;
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
        logger.info("Fetching template for repository construction");
        workItemRepoFactory = new RepositoryFactory<>("WorkItem", WorkItem.class, template, repoType);
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
        logger.info("\n\n================ Can Query Template WorkItem Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests query & retrieval Step from Template Step Repository
     * 
     */
    @Test
    @Order(1)
    public void canQueryInsertStep() {
        
        // Initialize data
        logger.info("\n\n================ Can Query Template Step Repository ================\n");
        TaskTideRepository<Step> stepRepo;
        RepositoryFactory<Step> stepRepoFactory;
        RepositoryType repoType = RepositoryType.NOSQL;
        List<Step> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestStepList();
        
        
        // Fetch backend instance
        logger.info("Fetching template for repository construction");
        stepRepoFactory = new RepositoryFactory<>("Step", Step.class, template, repoType);
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
        logger.info("\n\n================ Can Query Template Step Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Tests query & retrieval Workflow from Template Workflow Repository
     * 
     */
    @Test
    @Order(1)
    public void canQueryInsertWorkflow() {
        
        // Initialize data
        logger.info("\n\n================ Can Query Template Workflow Repository ================\n");
        TaskTideRepository<Workflow> workflowRepo;
        RepositoryFactory<Workflow> workflowRepoFactory;
        RepositoryType repoType = RepositoryType.NOSQL;
        List<Workflow> data;
        boolean assertionState;
        
        // Generate data for insert
        logger.info("Generating data for testing");
        data = TestCaseBuilderUtility.makeTestWorkflows();
        
        
        // Fetch backend instance
        logger.info("Fetching template for repository construction");
        workflowRepoFactory = new RepositoryFactory<>("Workflow", Workflow.class, template, repoType);
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
        logger.info("\n\n================ Can Query Template Workflow Repository ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}