/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.tasktide.tasktide;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.repository.RepositoryType;

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
            Config config = ConfigProvider.getConfig(); // Boots SmallRye before Weld
            CdiContainerProvider provider = TaskTideClientUtility.configureCdiInstance(CdiProviders.WELD);

            // Fetch config map
            LOGGER.info("Fetching TaskTide configs");
            ClientConfigMap configMap = new ClientConfigMap();
            configMap.addConfigs(provider);
            configMap.setArgsIn(args);
            TaskTideClientType clientType = configMap.whichClient();

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