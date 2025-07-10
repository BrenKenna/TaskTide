/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.tasktide.client;

import java.util.Map;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.tasktide.configurer.TaskTideConfigurer;
import org.tasktide.tasktide.parser.ArgumentTree;


/**
 * Enum of valid TaskTide client modules
 * 
 * @author bkenna
 */
public enum TaskTideClients {
    
    ENGINE {
        @Override
        public String toString() {
            return "engine";
        }

        @Override
        public boolean isClient(String s) {
            return "engine".equalsIgnoreCase(s);
        }

        @Override
        public boolean isClient(TaskTideClients other) {
            return this == other;
        }

        @Override
        public TaskTideClient makeClient(TaskTideServiceManager servManager, Map<String, TaskTideConfigurer> configMap, ArgumentTree argTree) {
            TaskTideConfigurer config = configMap.get("Engine");
            return new TaskTideEngineClient(servManager, config, argTree);
        }
    },
    
    MANAGER {
        @Override
        public String toString() {
            return "manager";
        }

        @Override
        public boolean isClient(String s) {
            return "manager".equalsIgnoreCase(s);
        }

        @Override
        public boolean isClient(TaskTideClients other) {
            return this == other;
        }
        
        @Override
        public TaskTideClient makeClient(TaskTideServiceManager servManager, Map<String, TaskTideConfigurer> configMap, ArgumentTree argTree) {
            TaskTideConfigurer config = configMap.get("Manager");
            return new TaskTideManagerClient(servManager, config, argTree);
        }
    };

    public abstract TaskTideClient makeClient(TaskTideServiceManager servManager, Map<String, TaskTideConfigurer> configMap, ArgumentTree argTree);
    public abstract boolean isClient(String s);
    public abstract boolean isClient(TaskTideClients other);
    
    public static int indexOf(TaskTideClients client) {
        return client.ordinal();
    }

    public static int indexOf(String name) {
        for (TaskTideClients client : values()) {
            if (client.isClient(name)) {
                return client.ordinal();
            }
        }
        return -1;  // not found
    }

    public static boolean hasQuery(TaskTideClients client, String query) {
        if (client == null || query == null) {
            return false;
        }
        return client.isClient(query);
    }

    public static boolean hasQuery(String query) {
        if (query == null) {
            return false;
        }
        for (TaskTideClients client : values()) {
            if (client.isClient(query)) {
                return true;
            }
        }
        return false;
    }
    
    
    
}
