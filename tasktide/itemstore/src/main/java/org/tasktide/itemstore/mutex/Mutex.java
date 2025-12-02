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
package org.tasktide.itemstore.mutex;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Random;


/**
 * Model class for {@link InterProcessLock}
 *
 * @author Brendan Kenna
 */
public class Mutex {
    
    // Attributes
    @JsonbProperty("Id")
    private final String id;
    
    @JsonbProperty("Lock Dir")
    private final Path lockDir;
    
    @JsonbProperty("Lock File")
    private final Path lockFile;
    
    @JsonbProperty("Host File")
    private final Path hostFile;
    
    @JsonbProperty("Retry Interval")
    private final Duration retryInterval;
    
    @JsonbProperty("Stale File Threshold")
    private final Duration staleFileThreshold;
   
    @JsonbProperty("Stale File Threshold")
    private final Duration startJitter;
    
    @JsonbProperty("End Jitter")
    private final Duration endJitter;
    
    @JsonbProperty("Mutex State")
    private MutexState state;
    
    private final Random RAND = new Random();
    
    
    /**
     * Construct with properties
     * 
     * @param id
     * @param lockDir
     * @param lockFile
     * @param hostFile
     * @param retryInterval
     * @param staleFileThreshold
     * @param startJitter
     * @param endJitter
     * @param state 
     */
    @JsonbCreator
    public Mutex(
        String id,
        Path lockDir,
        Path lockFile,
        Path hostFile,
        Duration retryInterval,
        Duration staleFileThreshold,
        Duration startJitter,
        Duration endJitter,
        MutexState state
    ) {
        this.id = id;
        this.lockDir = lockDir;
        this.lockFile = lockFile;
        this.hostFile = hostFile;
        this.state = state;
        
        this.startJitter = startJitter;
        this.endJitter = endJitter;
        
        this.staleFileThreshold = staleFileThreshold;
        this.retryInterval = retryInterval;
    }

    
    /**
     * Get locking directory
     * 
     * @return Path
     */
    public Path getLockDir() {
        return lockDir;
    }

    
    /**
     * Get lock central file
     * 
     * @return Path
     */
    public Path getLockFile() {
        return lockFile;
    }

    
    /**
     * Get host lock file
     * 
     * @return Path
     */
    public Path getHostFile() {
        return hostFile;
    }

    
    /**
     * Retry interval between lock attempts
     * 
     * @return {@link Duration}
     */
    public Duration getRetryInterval() {
        return retryInterval;
    }

    
    /**
     * Stale file threshold
     * 
     * @return {@link Duration}
     */
    public Duration getStaleFileThreshold() {
        return staleFileThreshold;
    }

    
    /**
     * Get start jitter
     * 
     * @return {@link Duration}
     */
    public Duration getStartJitter() {
        return startJitter;
    }

    
    /**
     * Get a random millisecond duration to
     *  to stagger process calls
     * 
     * @return {@link Duration}
     */
    public Duration getRandomJitter() {
        long min = this.startJitter.toMillis();
        long max = this.endJitter.toMillis();
        return Duration.ofMillis(RAND.nextLong(min, max));
    }
    
    
    /**
     * Get end jitter
     * 
     * @return {@link Duration}
     */
    public Duration getEndJitter() {
        return endJitter;
    }

    
    /**
     * Get current {@link MutexState}
     * 
     * @return {@link MutexState}
     */
    public MutexState getState() {
        return state;
    }
    
    
    /**
     * Set new {@link MutexState}
     * 
     * @param state 
     */
    public void setState(MutexState state) {
        this.state = state;
    }

    
    /**
     * Fetch Id
     * 
     * @return String
     */
    public String getId() {
        return id;
    }

    
    /**
     * Represent as JSON document
     * 
     * @return String
     */
    public String toJson() {
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
        return "Mutex{" +
            "id=" + id +
            ", lockDir=" + lockDir +
            ", lockFile=" + lockFile +
            ", hostFile=" + hostFile +
            ", retryInterval=" + retryInterval +
            ", staleFileThreshold=" + staleFileThreshold +
            ", startJitter=" + startJitter +
            ", endJitter=" + endJitter +
            ", state=" + state +
        '}';
    }
}