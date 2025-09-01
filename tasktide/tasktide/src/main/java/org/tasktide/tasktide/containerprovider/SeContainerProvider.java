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

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.Extension;


/**
 * CDI container provider which explicitly uses no embedded runtime
 *  compatible with Jakarta
 * 
 * @author bkenna
 */
public class SeContainerProvider implements CdiContainerProvider<SeContainer> {
 
    private SeContainerInitializer initializer;
    private SeContainer container;
    
    
    public SeContainerProvider() {
    }
    
    // 
    public SeContainerProvider(SeContainer container) {
        this.container = container;
        this.initializer = SeContainerInitializer.newInstance();
    }
    
    
    // 
    public void setContainer(SeContainer container) {
        this.container = container;
    }
    
    
    // 
    @Override
    public void initialize() {
        if ( initializer != null ) {
            SeContainerInitializer.newInstance();
        }
    }

    
    // 
    @Override
    public void start() {
        if ( initializer != null ) {
            container = initializer.initialize();
        }
    }

    
    // 
    @Override
    public void shutdown() {
        if ( container != null ) {
            container.close();
            container = null;
        }
    }

    
    // 
    @Override
    public SeContainer getContainer() {
        if ( container != null ) {
            return container;
        }
        else {
            throw new IllegalStateException("SeContainer not initialized");
        }
    }
    
    
    // 
    @Override
    public <T> T getBean(Class<T> clazz) {
        if ( container != null ) {
            return container.select(clazz).get();
        }
        else {
            throw new IllegalStateException("CDI not started");
        }
    }

    
    // 
    @Override
    @SuppressWarnings("unchecked")
    public <V> void addBeanClass(Class<V>... clazz) {
        if ( initializer != null ) {
            initializer.addBeanClasses(clazz);
        }
    }
    
    
    // 
    @Override
    @SuppressWarnings("unchecked")
    public <V> void addPackage(Class<V>... clazz) {
        if ( initializer != null ) {
            initializer.addPackages(clazz);
        }
    }
    

    // 
    @Override
    @SuppressWarnings("unchecked")
    public <V extends Extension> void addExtension(Class<V>... clazz) {
        if ( initializer != null ) {
            initializer.addExtensions(clazz);
        }
    }
}
