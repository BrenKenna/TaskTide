/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
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