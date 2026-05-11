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
package org.tasktide.api;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.nosql.Template;
import java.util.Random;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.BuilderUtility;

import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.builders.BuilderType;
import org.tasktide.core.model.builders.MetricDataBuilder;
import org.tasktide.core.model.builders.MetricProfileBuilder;
import org.tasktide.core.model.builders.ModelBuilderProvider;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.model.job_env.metrics.MetricType;
import org.tasktide.core.model.job_env.metrics.ProfileData;

import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.services.ServiceFactory;
import org.tasktide.core.supporting.JsonUtils;
import org.tasktide.core.supporting.Utils;


/**
 * Various static methods to support development & use of TaskTide
 * 
 * @author bkenna
 */
public class TestUtils {
    
    private static final Logger LOGGER = LogManager.getLogger(TestUtils.class);
    private static final Random RAND = new Random();
    private static final ModelBuilderProvider BUILDER_PROV = new ModelBuilderProvider();
    
    /**
     * Creates an annotation providing an early-binding
     *  pilot label, and unit-test label
     * 
     * @param pilotLabel
     * @param unitTestLabel
     * @return {@link CustomAnnotation}
     */
    public static CustomAnnotation makeAnnotation(String pilotLabel, String unitTestLabel) {
        CustomAnnotation anno = new CustomAnnotation();
        anno.add("Pilot Label", pilotLabel);
        anno.add("Unit Test", unitTestLabel);
        return anno;
    }
    
    
    /**
     * Print each work item from Id list
     * 
     * @param ids 
     */
    public static void printEach(String[] ids) {
        for ( String elm : ids ) {
            WorkItem preCmd = TaskTideServiceManager.fetchWorkItemService().fetchById(elm);
            LOGGER.info("Displaying WorkItem:\n'{}'", preCmd.toJsonDoc());
        }
    }
    
    
    /**
     * Displays all {@link Step} through Logger
     * 
     */
    public static void viewSteps() {
        List<Step> steps = TaskTideServiceManager.fetchStepService().viewAll();
        LOGGER.info("Displaying Steps:\t'{}'", JsonUtils.toJson(true, steps));
    }
    
    
    /**
     * Displays all {@link WorkItem} through Logger
     * 
     */
    public static void viewWorkItems() {
        List<WorkItem> items = TaskTideServiceManager.fetchWorkItemService().viewAll();
        LOGGER.info("Displaying WorkItems:\t'{}'", JsonUtils.toJson(true, items));
    }
    
    
    /**
     * Import test json doc via {@link ManagerCommand},
     *  requires {@link TaskTideServiceManager} to be
     *  initialized.
     * 
     * @param resourcePath
     * @param stepName
     * @param delimiter 
     */
    public static void importTestRecords(String resourcePath, String stepName, String delimiter) {
    
        // Initialize vars
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.IMPORT;
        CommandSpec cmdSpec;
        ManagerCommand cmd;
        
        // Fetch json doc
        Path path = TestUtils.fetchResourcePath(resourcePath);
        String targetFile = path.toString();
        
        // Construct command spec
        Map<String, Object> opts = new HashMap<>();
        opts.put("Delimiter", delimiter);
        opts.put("Step Name", stepName);
        cmdSpec = new CommandSpec(targetFile, null, opts);
        
        // Make and run import
        cmd = action.makeCommand(target, cmdSpec);
        LOGGER.info("Performing below import command:\n\n'{}'", cmd.toJsonDoc());
        cmd.execute();
    }
    
    
    /**
     * Import test json doc via {@link ManagerCommand},
     *  requires {@link TaskTideServiceManager} to be
     *  initialized.
     * 
     * @param resourcePath
     * @param stepName
     * @param delimiter
     * @param subDelim
     */
    public static void importTestRecords(String resourcePath, String stepName, String delimiter, String subDelim) {
    
        // Initialize vars
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.IMPORT;
        CommandSpec cmdSpec;
        ManagerCommand cmd;
        
        // Fetch json doc
        Path path = TestUtils.fetchResourcePath(resourcePath);
        String targetFile = path.toString();
        
        // Construct command spec
        Map<String, Object> opts = new HashMap<>();
        opts.put("Delimiter", delimiter);
        opts.put("Step Name", stepName);
        opts.put("Nested Delimiter", subDelim);
        cmdSpec = new CommandSpec(targetFile, null, opts);
        
        // Make and run import
        cmd = action.makeCommand(target, cmdSpec);
        LOGGER.info("Performing below import command:\n\n'{}'", cmd.toJsonDoc());
        cmd.execute();
    }
    
    
    /**
     * Initializes {@link TaskTideServiceManager} using
     *  required bakend
     * 
     * @param repoType
     * @param backend 
     */
    public static void initServiceManager(RepositoryType repoType, Object backend) {
        
        // Fetch services
        TaskTideService<Workflow> workflowServ = ServiceFactory.makeWorkflowService(repoType, backend, "Workflow");
        TaskTideService<Step> repoStep = ServiceFactory.makeStepService(repoType, backend, "Step");
        TaskTideService<WorkItem> repoWorkItem = ServiceFactory.makeWorkItemService(repoType, backend, "WorkItem");
        
        // Fetch additional
        TaskTideService<MetricData> metricServ = ServiceFactory.makeMetricDataService(repoType, backend, "MetricData");
        TaskTideService<MetricProfile> profileServ = ServiceFactory.makeMetricProfileService(repoType, backend, "MetricProfile");
        TaskTideService<JobEnvironment> jobEnvServ = ServiceFactory.makeJobEnvironmentService(repoType, backend, "JobEnvironment");
        
        // Initialize service manager with services
        TaskTideServiceManager.initialize(repoWorkItem, repoStep, workflowServ, jobEnvServ, metricServ, profileServ);
    }
    
    
    /**
     * Fetch path for provided resource, masking error
     *  from TestUtils.fetchResource 
     * 
     * @param resource
     * @return Path
     */
    public static Path fetchResourcePath(String resource) {
        try {
            return TestUtils.fetchResource(resource);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Unable to read provided resource;\t" + resource);
        }
    }
    
    
    /**
     * Fetch resolved path for a resource file
     * 
     * @param resource
     * @return Path
     * 
     * @throws URISyntaxException 
     */
    public static Path fetchResource(String resource) throws URISyntaxException {
        URL url = TestUtils.class.getClassLoader().getResource(resource);
        return Paths.get(url.toURI());
    }
    
    
    /**
     * Represent map as json string
     * 
     * @param map
     * @return String Json
     */
    public static String mapToJsonString(Map map) {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(map);
    }

    
    /**
     * Represent list as json string
     * 
     * @param list
     * @return String Json
     */
    public static String mapToJsonString(List list) {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(list);
    }
    
    
    /**
     * Represent {@link TaskTideModel TaskTideModel} list as json doc
     * 
     * @param models
     * @return String
     */
    public static String modelToJsonString(List<? extends TaskTideModel<?>> models) {
        return models.stream()
            .map(TaskTideModel::toJson)
        .collect(Collectors.joining(",\n", "{\n", "\n]"));
    }
    
    
    /**
     * Resolve a path string for test purposes
     * 
     * @return String
     */
    public static String resolveRocksRepoPath() {
        Path cwd = Paths.get( System.getProperty("user.dir") );
        Path workDir = cwd.resolve("project-test-repos").resolve("step");
        return workDir.toString();
    }
    
    
    /**
     * Fetch Jakarta NoSQL backend database from container
     * 
     * @return {@link Template}
     */
    public static Template fetchTemplate() {
        SeContainer container;
        container = SeContainerInitializer.newInstance().initialize();
        return container.select(DocumentTemplate.class).get();
    }
    
    
    /**
     * Fetch random memory metric
     * 
     * @return {@link MetricData}
     */
    public static MetricData fetchRandomMemoryMetric() {
        MetricData output;
        long total = RAND.nextLong(16, 64);
        long used = RAND.nextLong(1, 14);
        long available = total - used;
        
        MetricDataBuilder builder = (MetricDataBuilder) BUILDER_PROV.getBuilder(BuilderType.METRICDATA);
        output = builder
            .withId( "MetricData-" + Utils.getRandomUUID() )
            .withAnnotation( BuilderUtility.makeEmptyAnnotation() )
            .withLabel("Metric-Data-Label")
            .withMetricType(MetricType.CPU)
            .withTimestamp(System.currentTimeMillis())
            .withUnits("GB")
            .withTotal(total)
            .withUsed(used)
            .withAvailable(available)
        .build();
        return output;
    }
    
    
    /**
     * Fetch random Cpu metric
     * 
     * @return {@link MetricData}
     */
    public static MetricData fetchRandomCpuMetric() {
        MetricData output;
        long total = 100;
        long used = RAND.nextLong(1, 100);
        long available = total - used;
        
        MetricDataBuilder builder = (MetricDataBuilder) BUILDER_PROV.getBuilder(BuilderType.METRICDATA);
        output = builder
            .withId( "MetricData-" + Utils.getRandomUUID() )
            .withAnnotation( BuilderUtility.makeEmptyAnnotation() )
            .withLabel("Metric-Data-Label")
            .withMetricType(MetricType.CPU)
            .withTimestamp(System.currentTimeMillis())
            .withUnits("PCT")
            .withTotal(total)
            .withUsed(used)
            .withAvailable(available)
        .build();
        return output;
    }
    
    
    /**
     * Create random memory {@link MetricProfile}
     * 
     * @param dataPoints
     * 
     * @return {@link MetricProfile}
     */
    public static MetricProfile createRandomMemoryMetricProfile(int dataPoints) {
    
        MetricProfile output;
        Map<String, MetricData> profile = new HashMap<>();
        
        for ( int i = 0; i < dataPoints; i++) {
            MetricData data = fetchRandomMemoryMetric();
            profile.put("DataPoint-" + i, data);
        }
        
        MetricProfileBuilder builder = (MetricProfileBuilder) BUILDER_PROV.getBuilder(BuilderType.METRICPROFILE);
        output = builder
            .withId("MetricProfile-" + Utils.getRandomUUID())
            .withAnnotation( BuilderUtility.makeEmptyAnnotation() )
            .withProfile( new ProfileData("ProfileData-" + Utils.getRandomUUID(), profile))
            .withType(MetricType.MEMORY)
            .withUnits("GB")
            .withLabel("Random-Memory-Profile")
        .build();
        
        return output;
    }
    
    
    /**
     * Create random memory {@link MetricProfile}
     * 
     * @param dataPoints
     * 
     * @return {@link MetricProfile}
     */
    public static MetricProfile createRandomCpuMetricProfile(int dataPoints) {
    
        MetricProfile output;
        Map<String, MetricData> profile = new HashMap<>();
        
        for ( int i = 0; i < dataPoints; i++) {
            MetricData data = TestUtils.fetchRandomCpuMetric();
            profile.put("DataPoint-" + i, data);
        }
        
        MetricProfileBuilder builder = (MetricProfileBuilder) BUILDER_PROV.getBuilder(BuilderType.METRICPROFILE);
        output = builder
            .withId("MetricProfile-" + Utils.getRandomUUID())
            .withAnnotation( BuilderUtility.makeEmptyAnnotation() )
            .withProfile( new ProfileData("ProfileData-" + Utils.getRandomUUID(), profile))
            .withType(MetricType.CPU)
            .withUnits("PCT")
            .withLabel("Random-Memory-Profile")
        .build();
        
        return output;
    }
}