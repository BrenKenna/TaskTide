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


/**
 * CDI container interface to provide
 * 
 * @author bkenna
 * @param <T>
 */
public interface CdiContainerProvider<T> {
    
    
    /**
     * Allows implementing classes to define initializer
     *  before container
     */
    public void initialize();
    
    
    /**
     * Allows implementing classes to define how to start
     *  CDI container
     */
    public void start();
    
    
    /**
     * Allows implementing classes to define how to 
     *  shutdown CDI container
     */
    public void shutdown();
    
    
    /**
     * Allows implementing classes to define returning
     *  their container
     * 
     * @return T
     */
    public T getContainer();
    
    
    /**
     * Allows implementing classes to define fetching
     *  a bean from active instance
     * 
     * @param <U>
     * @param clazz
     * @return instance of U
     */
    public <U> U getBean(Class<U> clazz);
    
    
    /**
     * Allows implementing classes to define injecting
     *  classes into container
     * 
     * @param <V>
     * @param clazz 
     */
    @SuppressWarnings("unchecked")
    public <V> void addBeanClass(Class<V>... clazz);
    
    
    /**
     * Allows implementing classes to define injecting
     *  classes into container
     * 
     * @param <V>
     * @param clazz 
     */
    @SuppressWarnings("unchecked")
    public <V> void addPackage(Class<V>... clazz);
    
    
    /**
     * Allows implementing classes to define injecting
     *  classes into container
     * 
     * @param <V>
     * @param clazz 
     */
    @SuppressWarnings("unchecked")
    public <V extends Extension> void addExtension(Class<V>... clazz);
}
