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
package org.tasktide.tasktide.client;

import java.util.ArrayList;
import java.util.List;

import org.tasktide.tasktide.client.config.ManagerConfig;
import org.tasktide.tasktide.client.config.EngineConfig;
import org.tasktide.tasktide.client.config.GlobalConfig;


/**
 * Enum of valid TaskTide client modules
 * 
 * @author bkenna
 */
public enum TaskTideClientType {
    
    GLOBAL {
        @Override
        public String toString() {
            return "Global";
        }

        @Override
        public boolean isClient(String s) {
            return "global".equalsIgnoreCase(s);
        }

        @Override
        public boolean isClient(TaskTideClientType other) {
            return this == other;
        }

        @Override
        public Class getConfigClass() {
            return GlobalConfig.class;
        }

        @Override
        public TaskTideClient makeClient(ClientConfigMap configMap) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
        
        @Override
        public String pathInTree() {
            return "engine";
        }
    },
    
    ENGINE {
        @Override
        public String toString() {
            return "Engine";
        }

        @Override
        public boolean isClient(String s) {
            return "engine".equalsIgnoreCase(s);
        }

        @Override
        public boolean isClient(TaskTideClientType other) {
            return this == other;
        }

        @Override
        public TaskTideClient makeClient(ClientConfigMap configMap) {
            return new TaskTideEngineClient(configMap);
        }
        
        @Override
        public Class getConfigClass() {
            return EngineConfig.class;
        }
        
        @Override
        public String pathInTree() {
            return "engine";
        }
    },
    
    MANAGER {
        @Override
        public String toString() {
            return "Manager";
        }

        @Override
        public boolean isClient(String s) {
            return "manager".equalsIgnoreCase(s);
        }

        @Override
        public boolean isClient(TaskTideClientType other) {
            return this == other;
        }
        
        @Override
        public TaskTideClient makeClient(ClientConfigMap configMap) {
            return new TaskTideManagerClient(configMap);
        }
        
        @Override
        public Class getConfigClass() {
            return ManagerConfig.class;
        }
        
        @Override
        public String pathInTree() {
            return "manager";
        }
    };

    public abstract Class getConfigClass();
    public abstract TaskTideClient makeClient(ClientConfigMap configMap);
    public abstract boolean isClient(String s);
    public abstract boolean isClient(TaskTideClientType other);
    public abstract String pathInTree();
    
    public static int indexOf(TaskTideClientType client) {
        return client.ordinal();
    }

    public static int indexOf(String name) {
        for (TaskTideClientType client : values()) {
            if (client.isClient(name)) {
                return client.ordinal();
            }
        }
        return -1;  // not found
    }

    public static boolean hasQuery(TaskTideClientType client, String query) {
        if (client == null || query == null) {
            return false;
        }
        return client.isClient(query);
    }

    public static boolean hasQuery(String query) {
        if (query == null) {
            return false;
        }
        for (TaskTideClientType client : values()) {
            if (client.isClient(query)) {
                return true;
            }
        }
        return false;
    }
    
    
    public static TaskTideClientType get(String query) {
        int ind = indexOf(query);
        if ( ind > 0 ) {
            return values()[ind];
        }
        return null;
    }
    
    
    public static TaskTideClientType get(TaskTideClientType query) {
        int ind = indexOf(query);
        if ( ind > 0 ) {
            return values()[ind];
        }
        return null;
    }
    
    
    public static List<String> treePaths() {
        List<String> output = new ArrayList<>();
        for ( TaskTideClientType elm : values() ) {
            output.add(elm.pathInTree());
        }
        return output;
    }
}