/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
}
