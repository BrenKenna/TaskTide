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
package org.tasktide.tasktide;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.tasktide.client.ClientConfigMap;
import org.tasktide.tasktide.client.TaskTideClient;
import org.tasktide.tasktide.client.TaskTideClientUtility;
import org.tasktide.tasktide.client.TaskTideClientType;

import org.tasktide.tasktide.containerprovider.CdiContainerProvider;
import org.tasktide.tasktide.containerprovider.CdiProviders;


/**
 * Runs the desited {@link TaskTideClient}
 * 
 * @author bkenna
 */
public class TaskTide {
    
    // Attributes
    private static final Logger LOGGER = LogManager.getLogger(TaskTide.class);
    
    
    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Try run
        try {
            
            // Configure provider and argument tree
            TaskTideClientUtility.printSplash();
            LOGGER.info("Configuring the CDI Container Provider");
            CdiContainerProvider provider = TaskTideClientUtility.configureCdiInstance(CdiProviders.WELD, true);

            // Fetch config map
            LOGGER.info("Fetching TaskTide configs");
            ClientConfigMap configMap = new ClientConfigMap();
            configMap.addConfigs(provider);
            configMap.parseCommandLineArguments(args);
            if ( configMap.shouldDisplayHelp() ) {
                LOGGER.warn("Help flag detected");
                provider.shutdown();
                LOGGER.warn(JsonUtils.toJson(true, configMap.getArgTree().getVerboseHelp()));
                System.exit(0);
            }
            TaskTideClientType clientType = configMap.whichClient();
            if ( clientType == null ) {
                LOGGER.fatal("Error, exiting cannot parse provided client. Please check this value matches one of 'Manager, Engine, WebAPI'");
                provider.shutdown();
                System.exit(0);
            }

            // Fetch TaskTideServiceManager
            RepositoryType repoType = TaskTideClientUtility.fetchRepoType(configMap);
            LOGGER.info("Fetching the TaskTideServiceManager for '{}' Repository", repoType);
            TaskTideClientUtility.initServiceManager(repoType, configMap);
            LOGGER.info("ServiceManager state is now:\t'{}'", TaskTideServiceManager.isInitialized());

            // Run client
            LOGGER.info("Constructing client:\t'{}'", clientType);
            TaskTideClient client = clientType.makeClient(configMap);
            client.runClient();

            // Tear down container
            LOGGER.info("TaskTideClient completed, tearing down container");
            provider.shutdown();
            System.exit(0);
        }
        
        // Otherwise show error
        catch (Exception ex) {
            LOGGER.fatal("Exiting on fatal error:\t'{}'", ex.toString());
            ex.printStackTrace();
            System.exit(1);
        }
    }
}