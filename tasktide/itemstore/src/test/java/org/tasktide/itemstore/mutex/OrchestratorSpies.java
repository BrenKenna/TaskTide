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
package org.tasktide.itemstore.mutex;

import static org.mockito.Mockito.spy;
import org.tasktide.itemstore.mutex.exceptions.MutexUncheckedException;


/**
 * Class to support unit testing of {@link MutexOrhcestrator}
 *  unit tests through Mockito
 *
 * @author Brendan Kenna
 */
class OrchestratorSpies {
    
    // Attributes
    private static OrchestratorSpies INSTANCE;
    private final MutexElection
        NFS_MUTEX, FILE_CHANNEL_MUTEX;
    
    
    /**
     * Private constructor for static instance
     * 
     * @param nfsMutex
     * @param fileChannelMutex 
     */
    private OrchestratorSpies(MutexElection nfsMutex, MutexElection fileChannelMutex) {
        this.NFS_MUTEX = nfsMutex;
        this.FILE_CHANNEL_MUTEX = fileChannelMutex;
        MutexOrchestrator.configureForTestCases(this.NFS_MUTEX, this.FILE_CHANNEL_MUTEX);
    }
    
    
    /**
     * Get {@link OrchestratorSpies} instance if initialized,
     * 
     * @throws MutexUncheckedException
     * @return {@link OrchestratorSpies}
     */
    public static synchronized OrchestratorSpies getInstance() {
        if ( INSTANCE == null ) {
            throw new MutexUncheckedException("Instance must be initialized");
        }
        return INSTANCE;
    }
    
    
    /**
     * Initializes instance with provided {@link MutexElection} instances
     *  or previously configured instance
     * 
     * @param nfsMutex
     * @param fileChannelMutex
     * 
     * @return {@link OrchestratorSpies} 
     */
    public static synchronized OrchestratorSpies getInstance(
        MutexElection nfsMutex,
        MutexElection fileChannelMutex
    ) {
        if ( INSTANCE == null ) {
            INSTANCE = new OrchestratorSpies(nfsMutex, fileChannelMutex);
        }
        return INSTANCE;
    }
    
    
    /**
     * Configure instance with default mutexes
     * 
     * @return {@link OrchestratorSpies}
     */
    public static synchronized OrchestratorSpies configure() {
        if ( INSTANCE == null ) {
            MutexElection nfsMutes = spy( new NfsMutex() );
            MutexElection fileChannelMutex = spy( new FileChannelMutex() );
            INSTANCE = new OrchestratorSpies(nfsMutes, fileChannelMutex);
        }
        return INSTANCE;
    }
    
    
    /**
     * Fetch {@link NfsMutex}
     * 
     * @return {@link NfsMutex}
     */
    public MutexElection getNfsMutex() {
        return this.NFS_MUTEX;
    }
    
    
    /**
     * Fetch {@link NfsMutex}
     * 
     * @return {@link FileChannelMutex}
     */
    public MutexElection getFileChannelMutex() {
        return this.FILE_CHANNEL_MUTEX;
    }
}