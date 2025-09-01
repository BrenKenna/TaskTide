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
package org.tasktide.tasktide.containerprovider;

import io.quarkus.arc.ArcContainer;
import jakarta.enterprise.inject.se.SeContainer;
import org.jboss.weld.environment.se.WeldContainer;


/**
 * Valid implementations of {@link CdiContainerProvider}
 * 
 * @author bkenna
 */
public enum CdiProviders {
    
    JAKARTA {
        @Override
        public CdiContainerProvider<SeContainer> createProvider() {
            return new SeContainerProvider();
        }

        @Override
        public boolean isProvider(String query) {
            return "jakarta".equalsIgnoreCase(query);
        }

        @Override
        public boolean isProvider(CdiProviders query) {
            return this == query;
        }
        
        @Override
        public String toString() {
            return "Jakarta";
        }
    },
    
    WELD {
        @Override
        public CdiContainerProvider<WeldContainer> createProvider() {
            return new WeldContainerProvider();
        }
        
        @Override
        public boolean isProvider(String query) {
            return "weld".equalsIgnoreCase(query);
        }

        @Override
        public boolean isProvider(CdiProviders query) {
            return this == query;
        }
        
        @Override
        public String toString() {
            return "Weld";
        }
    },
    
    QUARKUS {
        @Override
        public CdiContainerProvider<ArcContainer> createProvider() {
            return new QuarkusContainerProvider();
        }
        
        @Override
        public boolean isProvider(String query) {
            return "quarkus".equalsIgnoreCase(query);
        }

        @Override
        public boolean isProvider(CdiProviders query) {
            return this == query;
        }
        
        @Override
        public String toString() {
            return "Quarkus";
        }
    };
    
    
    public abstract CdiContainerProvider<?> createProvider();
    public abstract boolean isProvider(String query);
    public abstract boolean isProvider(CdiProviders query);
    
    
    public static int indexOf(CdiProviders provider) {
        return provider.ordinal();
    }

    public static int indexOf(String name) {
        for (CdiProviders provider : values()) {
            if (provider.isProvider(name)) {
                return provider.ordinal();
            }
        }
        return -1;  // not found
    }

    public static boolean hasQuery(CdiProviders provider, String query) {
        if (provider == null || query == null) {
            return false;
        }
        return provider.isProvider(query);
    }

    public static boolean hasQuery(String query) {
        if (query == null) {
            return false;
        }
        for (CdiProviders provider : values()) {
            if (provider.isProvider(query)) {
                return true;
            }
        }
        return false;
    }
}