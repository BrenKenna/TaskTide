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
package org.tasktide.core.model.job_env;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Map;


/**
 * Data class for host metrics, internal builder
 *  fetches any null records, and data class has method
 *  for updating same metrics through {@link MetricsFetcher}
 * 
 * @author Brendan Kenna
 */
// @Embeddable
public class HostMetrics {
    
    // Attributes
    // @Column("Processor Count")
    @JsonbProperty("Processor Count")
    private int procCount;
    
    // @Column("Thread Count")
    @JsonbProperty("Thread Count")
    private int threadCount;
    
    // @Column("Thread Count")
    @JsonbProperty("Total Memory")
    private long totalMemory;
    
    // @Column("Free Memory")
    @JsonbProperty("Free Memory")
    private long freeMemory;
    
    // @Column("Storage Summary")
    @JsonbProperty("Storage Summary")
    private Map<String, Map<String, Long>> storage;
    
    
    /**
     * Package private construct, as these are made
     */
    private HostMetrics() {}
    
    
    /**
     * Make with provided arguments from JSON, or fetch from {@link MetricsFetcher}
     * 
     * @param procCount
     * @param threadCount
     * @param totalMemory
     * @param freeMemory
     * @param storage
     * @return HostMetrics
     */
    @JsonbCreator
    public static HostMetrics makeHostMetrics(
        @JsonbProperty("Processor Count") Integer procCount,
        @JsonbProperty("Thread Count") Integer threadCount,
        @JsonbProperty("Total Memory") Long totalMemory,
        @JsonbProperty("Free Memory") Long freeMemory,
        @JsonbProperty("Storage Summary") Map<String, Map<String, Long>> storage
    ) {
        
        // Initialize output
        HostMetrics output = new HostMetrics();
        MetricsFetcher metricsFetcher = MetricsFetcher.startUp();
        
        // Handle Processor
        if ( procCount == null ) {
            output.setProcCount(metricsFetcher.getProcessorCount());
        }
        else {
            output.setProcCount(procCount);
        }
        
        // Thread count
        if ( threadCount == null ) {
            output.setThreadCount(metricsFetcher.getThreadCount());
        }
        else {
            output.setThreadCount(threadCount);
        }
        
        // Total memory
        if ( totalMemory == null ) {
            output.setTotalMemory(metricsFetcher.getTotalMemory());
        }
        else {
            output.setTotalMemory(totalMemory);
        }
        
        // Free memory
        if ( freeMemory == null ) {
            output.setFreeMemory(metricsFetcher.getAvailableMemory());
        }
        else {
            output.setFreeMemory(freeMemory);
        }
        
        // Storage sumary
        if ( storage == null ) {
            output.setStorage(metricsFetcher.summarizeStorage());
        }
        else {
            output.setStorage(storage);
        }
        
        // Return output
        return output;
    }

    
    /**
     * Update metrics
     * 
     */
    public void updateMetrics() {
        MetricsFetcher metricsFetcher = MetricsFetcher.startUp();
        setProcCount(metricsFetcher.getProcessorCount());
        setThreadCount(metricsFetcher.getThreadCount());
        setTotalMemory(metricsFetcher.getTotalMemory());
        setFreeMemory(metricsFetcher.getAvailableMemory());
        setStorage(metricsFetcher.summarizeStorage());
    }
    
    
    /**
     * Get processor count
     * 
     * @return int
     */
    public int getProcCount() {
        return procCount;
    }

    
    /**
     * Set processor count
     * 
     * @param procCount 
     */
    public void setProcCount(int procCount) {
        this.procCount = procCount;
    }

    
    /**
     * Get thread count
     * 
     * @return int
     */
    public int getThreadCount() {
        return threadCount;
    }

    
    /**
     * Set thread count
     * 
     * @param threadCount 
     */
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    
    /**
     * Get total memory
     * 
     * @return long
     */
    public long getTotalMemory() {
        return totalMemory;
    }

    
    /**
     * Set total memory, {@link MetricsFetcher} uses GB
     * 
     * @param totalMemory 
     */
    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    
    /**
     * Set free memory, {@link MetricsFetcher} uses GB
     * 
     * @return long 
     */
    public long getFreeMemory() {
        return freeMemory;
    }

    
    /**
     * Set free memory from {@link MetricsFetcher} uses GB
     * 
     * @param freeMemory 
     */
    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    
    /**
     * Fetch storage
     * 
     * @return Storage summary
     */
    public Map<String, Map<String, Long>> getStorage() {
        return storage;
    }

    
    /**
     * Set storage
     * 
     * @param storage 
     */
    public void setStorage(Map<String, Map<String, Long>> storage) {
        this.storage = storage;
    }

    
    /**
     * Represent as json string
     * 
     * @return String
     */
    public String toJsonString() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Represent as JSON document
     * 
     * @return String
     */
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }
    
    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "HostMetrics{" +
            "procCount=" + procCount +
            ", threadCount=" + threadCount +
            ", totalMemory=" + totalMemory +
            ", freeMemory=" + freeMemory +
            ", storage=" + storage +
        '}';
    }
}