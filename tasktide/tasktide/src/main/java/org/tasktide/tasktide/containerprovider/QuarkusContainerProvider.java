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

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import jakarta.enterprise.inject.spi.Extension;


/**
 * CDI container provider which explicitly uses embedded {@link Arc}/Quarkus runtime
 * 
 * @author bkenna
 */
public class QuarkusContainerProvider implements CdiContainerProvider<ArcContainer> {

    ArcContainer container;
    
    @Override
    public void start() {
        if ( container == null ) {
            Arc.initialize();
            container = Arc.container();
        }
    }

    @Override
    public void shutdown() {
        if ( container != null ) {
            Arc.shutdown();
            container = null;
        }
    }

    @Override
    public ArcContainer getContainer() {
        if ( container != null ) {
            return container;
        }
        else {
            throw new IllegalStateException("Arc container not initialized");
        }
    }
    
    @Override
    public <U> U getBean(Class<U> clazz) {
        if ( container != null ) {
            return container.instance(clazz).get();
        }
        else {
            throw new IllegalStateException("Arc not yet started");
        }
    }

    @Override
    public void initialize() {}

    @Override
    @SuppressWarnings("unchecked")
    public <V> void addPackage(Class<V>... clazz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends Extension> void addExtension(Class<V>... clazz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void addBeanClass(Class<V>... clazz) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
