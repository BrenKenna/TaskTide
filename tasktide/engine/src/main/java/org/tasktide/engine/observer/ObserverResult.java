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
package org.tasktide.engine.observer;

import org.tasktide.engine.observer.worker.ObserverType;


/**
 * Class to support handling of failed {@link WorkerObserver} action
 * 
 * @author bkenna
 */
public class ObserverResult {
 
    // Attributes
    private final boolean success;
    private final String failedObserver;
    private final ObserverType type;
    private final boolean canIgnore;

    
    /**
     * Construct with success flag, and observer
     * 
     * @param success
     * @param failedObserver 
     * @param type 
     */
    public ObserverResult(boolean success, String failedObserver, ObserverType type) {
        this.success = success;
        this.failedObserver = failedObserver;
        this.type = type;
        this.canIgnore = false;
    }

    
    /**
     * Construct with ignore flag
     * 
     * @param success
     * @param failedObserver
     * @param type
     * @param canIgnore 
     */
    public ObserverResult(boolean success, String failedObserver, ObserverType type, boolean canIgnore) {
        this.success = success;
        this.failedObserver = failedObserver;
        this.type = type;
        this.canIgnore = canIgnore;
    }
    
    
    /**
     * Check observer state
     * 
     * @return boolean
     */
    public boolean isSuccess() {
        return success;
    }

    
    /**
     * Return failed observer
     * 
     * @return String
     */
    public String getFailedObserver() {
        return failedObserver;
    }

    
    /**
     * Return {@link ObserverType}
     * 
     * @return {@link ObserverType} 
     */
    public ObserverType getType() {
        return type;
    }
    
    
    /**
     * Return whether failure can be ignored
     * 
     * @return boolean
     */
    public boolean canIgnore() {
        return this.canIgnore;
    }
    
    
    /**
     * Create successful observer result
     * 
     * @return ObserverResult 
     */
    public static ObserverResult success() {
        return new ObserverResult(true, null, null);
    }

    
    /**
     * Create failed observer result
     * 
     * @param observer
     * @return ObserverResult
     */
    public static ObserverResult failure(WorkerObserver observer) {
        return new ObserverResult(false, observer.getClass().getSimpleName(), observer.getType());
    }
    
    
    /**
     * Create failed observer result, flagging if can ignore
     * 
     * @param observer
     * @param canIgnore
     * @return ObserverResult
     */
    public static ObserverResult failure(WorkerObserver observer, boolean canIgnore) {
        return new ObserverResult(false, observer.getClass().getSimpleName(), observer.getType(), canIgnore);
    }
}