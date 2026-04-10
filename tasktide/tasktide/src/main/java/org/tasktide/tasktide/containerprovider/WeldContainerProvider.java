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

import jakarta.enterprise.inject.spi.Extension;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;


/**
 * CDI container provider which explicitly uses embedded {@link Weld} runtime
 * 
 * @author bkenna
 */
public class WeldContainerProvider implements CdiContainerProvider<WeldContainer> {
    
    private WeldContainer container;
    private Weld weld;

    
    @Override
    public void initialize() {
        if ( weld == null ) {
            weld = new Weld();
        }
    }
    
    
    @Override
    public void start() {
        if ( weld != null ) {
            container = weld.initialize();
        }
    }
    
    
    public void close() {
        if ( container != null ) {
            weld.shutdown();
            if ( container.isRunning() ) {
                container.close();
            }
            container = null;
            weld = null;
        }
    }

    
    @Override
    public void shutdown() {
        if ( container != null ) {
            this.close();
        }
    }

    
    @Override
    public WeldContainer getContainer() {
        if ( container != null ) {
            return container;
        }
        else {
            throw new IllegalStateException("Weld/SeContainer not initialized");
        }
    }
    
    
    @Override
    @SuppressWarnings("unchecked")
    public <U> U getBean(Class<U> clazz) {
        if ( container != null ) {
            return container.select(clazz).get();
        }
        else {
            throw new IllegalStateException("Weld/SeContainer not initialized");
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <V> void addBeanClass(Class<V>... clazz) {
        if ( weld != null ) {
            weld.addBeanClasses(clazz);
        }
    }
    

    @Override
    @SuppressWarnings("unchecked")
    public <V> void addPackage(Class<V>... clazz) {
        if ( weld != null ) {
            weld.addPackages(clazz);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends Extension> void addExtension(Class<V>... clazz) {
        if ( weld != null ) {
            weld.addExtensions(clazz);
        }
    }
}
