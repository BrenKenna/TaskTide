/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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

    @Override
    public void shutdown() {
        if ( container != null ) {
            container.close();
            container = null;
            weld.shutdown();
            weld = null;
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
    public <V> void addPackage(Class<V>... clazz) {
        if ( weld != null ) {
            weld.addBeanClasses(clazz);
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
