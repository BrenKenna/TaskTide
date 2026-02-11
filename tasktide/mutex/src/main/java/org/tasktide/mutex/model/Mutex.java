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
package org.tasktide.mutex.model;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Random;
import org.tasktide.mutex.exceptions.MutexUncheckedException;


/**
 * Model class for {@link InterProcessLock}
 *
 * @author Brendan Kenna
 */
public class Mutex {
    
    // Attributes
    @JsonbProperty("Id")
    private final String id;
    
    @JsonbProperty("Timestamp")
    private long timestamp;
    
    @JsonbProperty("Lock Dir")
    private final Path lockDir;
    
    @JsonbProperty("Lock File")
    private final Path lockFile;
    
    @JsonbProperty("Host File")
    private final Path hostFile;
    
    @JsonbProperty("Election File")
    private Path electionFile;
    
    @JsonbProperty("Retry Interval")
    private Duration retryInterval;
    
    @JsonbProperty("Stale File Threshold")
    private Duration staleFileThreshold;
   
    @JsonbProperty("Start Jitter")
    private Duration startJitter;
    
    @JsonbProperty("End Jitter")
    private Duration endJitter;
    
    @JsonbProperty("Mutex State")
    private MutexState state;
    
    @JsonbTransient
    private HostLock hostLock;
    
    @JsonbTransient
    private Path confirmBallot;
    
    private final Random RAND = new Random();
    
    
    /**
     * Construct with properties
     * 
     * @param id
     * @param timestamp
     * @param lockDir
     * @param lockFile
     * @param hostFile
     * @param electionFile
     * @param retryInterval
     * @param staleFileThreshold
     * @param startJitter
     * @param endJitter
     * @param state 
     */
    @JsonbCreator
    public Mutex(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Timestamp") long timestamp,
        @JsonbProperty("Lock Directory") Path lockDir,
        @JsonbProperty("Lock File") Path lockFile,
        @JsonbProperty("Host File") Path hostFile,
        @JsonbProperty("Election File") Path electionFile,
        @JsonbProperty("Retry Interval") Duration retryInterval,
        @JsonbProperty("Stale File Threshold") Duration staleFileThreshold,
        @JsonbProperty("Start Jitter") Duration startJitter,
        @JsonbProperty("End Jitter") Duration endJitter,
        @JsonbProperty("Mutex State") MutexState state
    ) {
        this.id = id;
        this.timestamp = timestamp;
        this.lockDir = lockDir;
        this.lockFile = lockFile;
        this.hostFile = hostFile;
        this.electionFile = electionFile;
        this.state = state;
        
        this.startJitter = startJitter;
        this.endJitter = endJitter;
        
        this.staleFileThreshold = staleFileThreshold;
        this.retryInterval = retryInterval;
    }

    public Path getConfirmBallot() {
        return confirmBallot;
    }

    public void setConfirmBallot(Path confirmBallot) {
        this.confirmBallot = confirmBallot;
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
     * Get mutex timestamp
     * 
     * @return long
     */
    public long getTimestamp() {
        return timestamp;
    }

    
    /**
     * Get election file
     * 
     * @return Path
     */
    public Path getElectionFile() {
        return electionFile;
    }

    
    /**
     * Get the path for supplied {@link MutexFileType}, or
     *  unchecked exception
     * 
     * @param fileType
     * @return Path
     */
    public Path getFileForType(MutexFileType fileType) {
        
        switch ( fileType ) {
        
            case LOCK_FILE -> {
                return this.getLockFile();
            }
            
            case HOST_FILE -> {
                return this.getHostFile();
            }
            
            case ELECTION_FILE -> {
                return this.getElectionFile();
            }
            
            default -> {
                throw new MutexUncheckedException("Mutex file type must be one of:\tElection, Host, Lock");
            }
        }
    }
    
    
    /**
     * Get host lock
     * 
     * @return {@link HostLock}
     */
    public HostLock getHostLock() {
        return this.hostLock;
    }
    
    
    /**
     * Set {@link HostLock}
     * 
     * @param hostLock 
     */
    public void setHostLock(HostLock hostLock) {
        this.hostLock = hostLock;
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

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setElectionFile(Path electionFile) {
        this.electionFile = electionFile;
    }

    public void setRetryInterval(Duration retryInterval) {
        this.retryInterval = retryInterval;
    }

    public void setStaleFileThreshold(Duration staleFileThreshold) {
        this.staleFileThreshold = staleFileThreshold;
    }

    public void setStartJitter(Duration startJitter) {
        this.startJitter = startJitter;
    }

    public void setEndJitter(Duration endJitter) {
        this.endJitter = endJitter;
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