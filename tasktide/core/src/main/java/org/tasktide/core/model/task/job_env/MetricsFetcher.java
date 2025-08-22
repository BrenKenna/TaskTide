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
package org.tasktide.core.model.task.job_env;

import java.util.HashMap;
import java.util.Map;

import oshi.SystemInfo;
import oshi.software.os.OSFileStore;


/**
 * Fetches host metrics through 
 * 
 * @author Brendan Kenna
 */
class MetricsFetcher {
    
    // For singleton
    private static MetricsFetcher INSTANCE;
            
    // Attributes of interest
    private final SystemInfo SYS_INFO;
    
    
    /**
     * Package private constructor
     */
    private MetricsFetcher() {
        this.SYS_INFO = new SystemInfo();
    }
    
    
    /**
     * Startup instance
     * 
     */
    public static MetricsFetcher startUp() {
        if ( INSTANCE == null ) {
            INSTANCE = new MetricsFetcher();
        }
        return INSTANCE;
    }
    
    
    /**
     * Tear down instance
     * 
     */
    public static void tearDown() {
        if ( INSTANCE != null ) {
            INSTANCE = null;
        }
    }
    
    
    /**
     * Helper method for scaling to GigaBytes
     * @param data
     * 
     * @return long 
     */
    public long scaleToGigaBytes(long data) {
        return data / ( 1024 * 1024 * 1024 );
    }
    
    
    /**
     * Helper method for scaling to GigaBytes
     * @param data
     * 
     * @return long 
     */
    public long scaleToTeraBytes(long data) {
        return data / ( 1024 * 1024 * 1024 * 1024);
    }
    
    
    /**
     * Get processor count
     * 
     * @return int
     */
    public int getProcessorCount() {
        return SYS_INFO.getHardware().getProcessor().getLogicalProcessorCount();
    }
    
    
    /**
     * Fetch thread count
     * 
     * @return int
     */
    public int getThreadCount() {
        return SYS_INFO.getOperatingSystem().getThreadCount();
    }
    
    
    /**
     * Get CPU load over provided period
     * 
     * @param seconds
     * @return double
     */
    public double getCPULoad(int seconds) {
        return SYS_INFO.getHardware().getProcessor().getSystemCpuLoad(seconds * 1000L);
    }
    
    
    /**
     * Get total memory in GN
     * 
     * @return long
     */
    public long getTotalMemory() {
        return scaleToGigaBytes(SYS_INFO.getHardware().getMemory().getTotal());
    }
    
    
    /**
     * Get total memory in GN
     * 
     * @return long
     */
    public long getAvailableMemory() {
        return scaleToGigaBytes(SYS_INFO.getHardware().getMemory().getAvailable());
    }
    
    
    /**
     * Get most useful identifier for store
     * 
     * @param store
     * @return String
     */
    public String getIdentifier(OSFileStore store) {
        String storeLabel;
        if ( !store.getName().isEmpty() ) {
            storeLabel = store.getName();
        }
        
        else if ( !store.getLogicalVolume().isEmpty() ) {
            storeLabel = store.getLogicalVolume();
        }
        
        else {
            storeLabel = store.getMount();
        }
        return storeLabel;
    }
    
    
    /**
     * Summarizes total, free and used storage in bytes
     * 
     * @param store 
     * @return Map-String, Long
     */
    public Map<String, Long> summarizeVolume(OSFileStore store) {
        Map<String, Long> storeSummary = new HashMap<>();
        storeSummary.put("Total Bytes", store.getTotalSpace() / 1024);
        storeSummary.put("Free Bytes", store.getUsableSpace() / 1024);
        storeSummary.put("Used Bytes", (store.getTotalSpace() - store.getUsableSpace()) / 1024);
        return storeSummary;
    }
    
    
    /**
     * Summarize storage metrics across all stores
     * 
     * @return Map-String, Map-String, Long
     */
    public Map<String, Map<String, Long>> summarizeStorage() {
        Map<String, Map<String, Long>> output = new HashMap<>();
        for ( OSFileStore store : SYS_INFO.getOperatingSystem().getFileSystem().getFileStores() ) {
            String storeLabel = this.getIdentifier(store);
            output.put(storeLabel, this.summarizeVolume(store));
        }
        return output;
    }
}