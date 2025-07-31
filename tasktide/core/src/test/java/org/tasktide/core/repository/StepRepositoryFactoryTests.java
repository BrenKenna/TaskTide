/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.repository;

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;

import org.tasktide.TestCaseBuilderUtility;
import org.tasktide.TestUtils;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;
import org.tasktide.core.supporting.JsonUtils;
import org.tasktide.itemstore.ItemStore;


/**
 * Test module for {@link RepositoryFactory} for {@link Step}
 *  across the {@link RepositoryType}
 * 
 * @author bkenna
 */
@EnableAutoWeld
@AddPackages(value = {Converters.class, EntityConverter.class, Template.class, DocumentTemplate.class})
@AddPackages(value = {Tunes.class, Reflections.class})
@AddExtensions( {ReflectionEntityMetadataExtension.class, DocumentExtension.class} )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StepRepositoryFactoryTests {
    
    private static final Logger logger = LogManager.getLogger(StepRepositoryFactoryTests.class);
    
    public StepRepositoryFactoryTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating Repository Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Repository Tests ----------------\n";
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
     * Test that a work item can be fetched 
     */
    @Test
    @Order(0)
    public void canConstructStepJsonRepository() {
    
        // Initialize data
        logger.info("\n\n================ Construct JSON Repositories From Factory Test ================\n");
        TaskTideRepository<Step> stepRepo;
        RepositoryFactory<Step> stepRepoFactory;
        RepositoryType repoType;
        List<Step> backend;
        Step record;
        boolean assertionState;
        
        // Generate data
        logger.info("Generating data for testing");
        repoType = RepositoryType.JSON;
        backend = List.of(
            TestCaseBuilderUtility.makeTestStep(),
            TestCaseBuilderUtility.makeTestStep(),
            TestCaseBuilderUtility.makeTestStep()
        );
        
        // Setup requirements
        logger.info("Configuring repository");
        stepRepoFactory = new RepositoryFactory<>("Test-Json-Step", Step.class, backend, repoType);
        stepRepo = stepRepoFactory.make();
        Map<String, String> map = stepRepo.getRepositoryMetaData();
        logger.info("Displaying meta data for Step Repository:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<Step> ref = backend.get(0);
        assertionState = stepRepo.findById(ref.getId()).get() != null;
        
        // Log test state
        logger.info("\n\n================ Construct JSON Repositories From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(1)
    public void canConstructStepRocksDbRepository() {
    
        // Initialize data
        logger.info("\n\n================ Construct RocksDB Repositories From Factory Test ================\n");
        TaskTideRepository<Step> stepRepo;
        RepositoryFactory<Step> stepRepoFactory;
        RepositoryType repoType;
        ItemStore backend;
        List<Step> data;
        boolean assertionState ;
        
        // Generate data
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestStep(),
            TestCaseBuilderUtility.makeTestStep(),
            TestCaseBuilderUtility.makeTestStep()
        );
        
        // Configure requirements
        repoType = RepositoryType.ITEMSTORE;
        String collectionName = TestUtils.resolveRocksRepoPath();
        backend = TestUtils.fetchItemStore(collectionName);
        
        // Configure repository
        logger.info("\nConfiguring repository");
        stepRepoFactory = new RepositoryFactory<>("Test-Rocks-Step", Step.class, backend, repoType);
        stepRepo = stepRepoFactory.make();
        Map<String, String> map = stepRepo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for StepRepository:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Add records
        data.stream()
            .forEach( elm -> stepRepo.insertModel(elm));
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<Step> ref = data.get(0);
        System.out.println("\nDisplaying reference step:\n" + ref.toJson());
        assertionState = !stepRepo.findById(ref.getId()).isEmpty();
        logger.info("\nDisplayling all records:\n\n{}", TestUtils.modelToJsonString(stepRepo.findAll()));
        logger.info("\nVerifying method calls:\n\n'{}'", stepRepo.findAll().get(0).summarizeByState().toJsonDoc());
        
        // Log test state
        logger.info("\n\n================ Construct RocksDB Repositories From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(2)
    public void canConstructStepNoSqlRepository() {
    
        // Initialize data
        logger.info("\n\n================ Construct NoSQL Repositories From Factory Test ================\n");
        TaskTideRepository<Step> stepRepo;
        RepositoryFactory<Step> stepRepoFactory;
        RepositoryType repoType;
        Template backend;
        List<Step> data;
        Step record;
        boolean assertionState = false;
        
        // Generate data
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestStep(),
            TestCaseBuilderUtility.makeTestStep(),
            TestCaseBuilderUtility.makeTestStep()
        );
        
        // Fetch backend instance
        repoType = RepositoryType.NOSQL;
        backend = TestUtils.fetchTemplate();
        logger.info("Backend template:\t'{}'", backend);
        
        // Configure repository
        logger.info("\nConfiguring repository");
        stepRepoFactory = new RepositoryFactory<>("Test-Template-Step", Step.class, backend, repoType);
        stepRepo = stepRepoFactory.make();
        Map<String, String> map = stepRepo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for StepRepository:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Add records
        data.stream()
            .forEach( elm -> stepRepo.insertModel(elm));
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<Step> ref = data.get(0);
        assertionState = !stepRepo.findById(ref.getId()).isEmpty();
        List<Step> steps = stepRepo.findAll();
        logger.info("\nDisplayling all records:\n\n{}", TestUtils.modelToJsonString(steps));
        
        // Log test state
        logger.info("\n\n================ Construct NoSQL Repositories From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(3)
    public void canConstructStepSqlRepository() {
    
        // Initialize data
        logger.info("\n\n================ Construct JPA step Repository From Factory Test ================\n");
        TaskTideRepository<Step> stepRepo;
        RepositoryFactory<Step> stepRepoFactory;
        RepositoryType repoType;
        EntityManager backend;
        List<Step> data;
        boolean assertionState;
        
        // Generate data
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestStep(),
            TestCaseBuilderUtility.makeTestStep(),
            TestCaseBuilderUtility.makeTestStep()
        );
        
        // Fetch backend instance
        repoType = RepositoryType.SQL;
        backend = JpaRepositoryUtility.get().fetchEntityManager();
        logger.info("Backend Entity:\t'{}'", backend);
        
        // Configure repository
        logger.info("\nConfiguring repository");
        stepRepoFactory = new RepositoryFactory<>("Test-JPA-Step", Step.class, backend, repoType);
        stepRepo = stepRepoFactory.make();
        Map<String, String> map = stepRepo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for StepRepository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records
        logger.info("Displaying step:\n{}", data.get(0).toJsonDoc());
        data.stream()
            .forEach( elm -> stepRepo.insertModel(elm));
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<Step> ref, res;
        ref = data.get(0);
        res = stepRepo.findById(ref.getId()).get();
        assertionState = res != null;
        logger.info("\nDisplayling all records:\n\n{}", JsonUtils.toJson(true, res));
        
        // Log test state
        logger.info("\n\n================ Construct JPA Step Repository From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}