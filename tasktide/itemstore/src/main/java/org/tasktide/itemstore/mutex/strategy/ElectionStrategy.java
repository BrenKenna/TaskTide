/*
 * Copyright 2026 Brendan Kenna.
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
package org.tasktide.itemstore.mutex.strategy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.itemstore.FileUtility;

import org.tasktide.itemstore.mutex.MutexStrategyType;

import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.model.MutexFileType;
import org.tasktide.itemstore.mutex.model.MutexState;
import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;

import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;
import org.tasktide.itemstore.mutex.exceptions.MutexUncheckedException;


/**
 * Splits NFS {@link ElectionStrategy} orientated locking
 *  into phases:<br><br>
 * 
 * 1). Initialization.<br>
 * 2). Wait for leadership.<br>
 * 3). Examine iteration:<br>
 *      - Current position.<br>
 *      - Queue stagnation.<br>
 *      - Leader time-to-live.<br>
 * 4). Release lock.<br>
 *
 * @author Brendan Kenna
 */
public class ElectionStrategy extends MutexStrategy {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(ElectionStrategy.class);
    
    
    /**
     * Construct {@link ElectionStrategy}
     * 
     */
    public ElectionStrategy() {
        super(MutexStrategyType.ELECTION);
    }
    
    
    /**
     * Apply {@link Mutex}
     * 
     * @param mutex
     * @return boolean
     */
    @Override
    public synchronized boolean apply(Mutex mutex) {
        
        // Initialize mutex
        this.initMutex(mutex);
        
        // Wait for leadership
        this.waitUntilLeader(mutex);
        
        // Write mutex to lock file
        LOGGER.debug("Active leader with Id:\t'{}'", mutex.getId());
        mutex.setState(MutexState.HOST_LOCKED);
        LOGGER.debug("State set, writing mutex for:\t'{}'", mutex.getId());
        MutexFilesUtils.writeMutex(mutex, MutexFileType.ELECTION_FILE);
        LOGGER.debug("Mutex written for:\t'{}'", mutex.getId());
        return MutexFilesUtils.writeHostFile(mutex);
    }
    
    
    /**
     * Remove election files
     * 
     * @param mutex
     * @return boolean
     */
    @Override
    public synchronized boolean cleanUp(Mutex mutex) {
        LOGGER.debug("Cleaning up host file:\t'{}'", mutex.getId());
        FileUtility.dropFile(mutex.getHostFile());
        LOGGER.debug("Cleaning up confirm ballot file:\t'{}'", mutex.getId());
        FileUtility.dropFile(mutex.getConfirmBallot());
        LOGGER.debug("Cleaning up election file:\t'{}'", mutex.getId());
        return FileUtility.dropFile(mutex.getElectionFile());
    }
    
    
    /**
     * Release provided {@link Mutex}
     * 
     * @param mutex
     * @return boolean
     */
    @Override
    public synchronized boolean release(Mutex mutex) {
        MutexFilesUtils.deleteFile(mutex.getHostFile());
        MutexFilesUtils.deleteFile(mutex.getConfirmBallot());
        return MutexFilesUtils.deleteFile(mutex.getElectionFile());
    }
    
    
    /**
     * Initialize {@link Mutex}
     * 
     * @param mutex 
     */
    public void initMutex(Mutex mutex) {
    
        // Set state as initialization
        MutexFilesUtils.waitJitterTime();
        LOGGER.debug("Initializing mutex:\t'{}'", mutex.getId());
        mutex.setState(MutexState.INITIALIZATION);
        FileUtility.makeFile(mutex.getElectionFile());
        MutexFilesUtils.writeMutex(mutex, MutexFileType.ELECTION_FILE);

        // Fetch position and active leader
        LOGGER.debug("Mutex initialized, inferring queue position");
        inferLeader( MutexFileType.ELECTION_FILE ).orElseThrow(
            () -> new MutexUncheckedException("Error no election files found")
        );
        mutex.setState(MutexState.WAITING);
        MutexFilesUtils.writeMutex(mutex, MutexFileType.ELECTION_FILE);
    }
    
    
    /**
     * Examines current position in queue has changed.
     * <br><br>
     * 1). PositionCheckValues.PROGRESSED    = No action<br>
     * 2). PositionCheckValues.UNCHANGED     = Increment streak<br>
     * 3). PositionCheckValues.LIMIT_REACHED = Recast ballot<br>
     * 
     * @param state
     * @param mutex
     * @param limit
     * 
     * @return {@link PositionCheckValues}
     */
    public PositionCheckValues examineQueueStagnation(ElectionState state, Mutex mutex, int limit) {
    
        // Exmaine position unchanging
        if ( state.getLastPos() == state.getPos() ) {
            // LOGGER.debug("Incrementing streak now to '{}' for mutex:\t'{}'", state.getStreak(), mutex.getId());
            if ( state.getStreak() == limit ) {
                return PositionCheckValues.LIMIT_REACHED;
            }
            return PositionCheckValues.UNCHANGED;
        }
        return PositionCheckValues.PROGRESSED;
    }
    
    
    /**
     * Evaluate provided leader TTL
     * 
     * @param leader
     * 
     * @return boolean
     */
    public boolean evaluateLeaderTtl(Path leader) {
        
        // Examine whether leader has gone stale
        boolean leaderState;
        try {
            leaderState = MutexFilesUtils.evaluateLeaderTimeToLive(leader);
            
            if ( leaderState ) {
                boolean clearStaleLeader = MutexFilesUtils.clearStaleLeader(leader);
                LOGGER.warn(
                    "Removing below stale leader status:\t'{}'\n\n'{}'",
                    clearStaleLeader,
                    leader
                );
                return false;
            }

            else {
                LOGGER.info("Leader passed TTL check-in:\t'{}'", leader);
                return true;
            }
        }
        
        catch ( Exception ex ) {
            LOGGER.warn(
                "Error verifiying leader TTL:\t'{}'\n'{}'",
                leader,
                ex
            );
            return false;
        }
    }
    
    
    /**
     * Evaluate iteration
     * 
     * @param state
     * @param mutex
     * @param limit
     * 
     * @return {@link LoopDecision}
     */
    public LoopDecision evaluateIteration(ElectionState state, Mutex mutex, int limit) {
    
        // Initialize vars
        PositionCheckValues posCheck;
        state.setPos( inferPosition(mutex) );
        if ( state.getPos() >= 0 ) {
        
            // Become leader
            if ( state.getPos() == 0 ) {
                return LoopDecision.ACQUIRED;
            }
            
            // Fetch leader
            try {
                state.setLeader( MutexFilesUtils.getLeader() );
            }
            catch (MutexCheckedException ex) {
                LOGGER.error("Unable to fetch current leader");
                return LoopDecision.CONTINUE;
            }
            
            // Evaluate queue stagnation
            posCheck = examineQueueStagnation(state, mutex, limit);
            switch ( posCheck ) {
                case PROGRESSED -> {
                    state.setLastPos( state.getPos() );
                }
                
                case UNCHANGED -> {
                    state.setStreak( state.getStreak() + 1 );
                }
                
                case LIMIT_REACHED -> {
                    state.reset();
                    return LoopDecision.RECAST_BALLOT;
                }
            }
            
            // Examine predecessor
            state.setPredecessor(
                MutexFilesUtils.findPredecessor(
                    mutex,
                    MutexFileType.ELECTION_FILE,
                    state.getPos()
                )
            );
            
            // Evaluate stale leader
            if ( state.getPredecessor() != null ) {
                boolean predMiss = !Files.exists( state.getPredecessor() );
                if ( !predMiss && state.getPos() <= 3 ) {
                    if ( this.evaluateLeaderTtl( state.getLeader() ) ) {
                        predMiss = true;
                    }
                }
                
                if ( predMiss ) {
                    return LoopDecision.RESET_PREDECESSOR;
                }
            }
            
            // Flag next iteration
            return LoopDecision.CONTINUE;
        }
        
        // Fallback case
        /*
        if ( 
            state.getPredecessor() != null &&
            !Files.exists(state.getPredecessor())
        ) {
            return LoopDecision.ACQUIRED;
        }
        */
        
        // Flag next iteration
        return LoopDecision.CONTINUE;
    }
    
    
    /**
     * Wait until leadership is acquired
     * 
     * @param mutex 
     */
    public void waitUntilLeader(Mutex mutex) {
    
        // Initialize
        boolean acquired = false;
        ElectionState state;
        
        // Wait for leadership
        state = new ElectionState();
        while(!acquired) {
            
            // Wait before evaluating
            MutexFilesUtils.waitJitterTime();
            LoopDecision loopDecision = evaluateIteration(state, mutex, 10);
            
            // Handle results
            switch ( loopDecision ) {
            
                case ACQUIRED -> {
                    LOGGER.info("Leadership acquired:\t'{}'", mutex.getId());
                }
                
                case RECAST_BALLOT -> {
                    LOGGER.info("Recasting ballot:\t'{}'", mutex.getId());
                    MutexFilesUtils.recastBallot(mutex, true);
                }
                
                case RESET_PREDECESSOR -> {
                    LOGGER.info("Resetting predecessor:\t'{}'", mutex.getId());
                    state.setPredecessor(null);
                }
                
                case CONTINUE -> {
                    //LOGGER.debug("Waiting for leadership for mutex:\t'{}'", mutex.getId());
                }
            }
            
            // Verify leadership: Cover any FS visibility quirks etc
            if ( loopDecision.isLoopDecision(LoopDecision.ACQUIRED) ) {
                LOGGER.info("Verifying leadership:\t'{}'", mutex.getId());
                MutexFilesUtils.waitJitterTime();
                if ( this.inferPosition(mutex) == 0 ) {
                    LOGGER.info("Leadership verified:\t'{}'", mutex.getId());
                    acquired = true;
                }
                else {
                    LOGGER.warn("Unable to verifiy leadership:\t'{}'", mutex.getId());
                    acquired = false;
                }
            }
        }
    }
}