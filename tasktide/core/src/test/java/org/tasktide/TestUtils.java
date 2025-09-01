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
package org.tasktide;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.nosql.Template;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.services.ServiceFactory;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.RocksDBStore;


/**
 * Various static methods to support development & use of TaskTide
 * 
 * @author bkenna
 */
public class TestUtils {
    
    private static Logger LOGGER = LogManager.getLogger(TestUtils.class);
    
    
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
     */
    public static void viewWorkItems() {
        List<WorkItem> items = TaskTideServiceManager.fetchWorkItemService().viewAll();
        LOGGER.info("Displaying WorkItems:\t'{}'", JsonUtils.toJson(true, items));
    }
    
    
    /**
     * Import test json doc via {@link ManagerCommand},
     *  requires {@link TaskTideServiceManager} to be
     *  iniialized.
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
        
        // Initialize service manager with services
        TaskTideServiceManager.initialize(repoWorkItem, repoStep, workflowServ);
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
     * Fetch a {@link RocksDBStore} with name
     * 
     * @param storeName
     * @return {@link ItemStore} of {@link RocksDBStore}
     */
    public static ItemStore fetchItemStore(String storeName) {
    
        // Resolve store name location to a Path
        Path targetPath = Paths.get(storeName);
        try {
            
            // Create path if required
            Files.createDirectories(targetPath);
            
            // Set required properites
            String dbDirectory = targetPath.toString();
            String masterDB = "master";
            String protoDB = UUID.randomUUID().toString();
            RocksDBStore itemStore = new RocksDBStore(storeName, dbDirectory, masterDB, protoDB);
            
            // Return ItemStore
            return itemStore;
        }
        catch (Exception ex) {
            return null;
        }
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
}