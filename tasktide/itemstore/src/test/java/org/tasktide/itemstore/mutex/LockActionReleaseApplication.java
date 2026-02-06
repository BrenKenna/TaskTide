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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.file.StandardOpenOption;

import java.util.Random;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;
import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;
import org.tasktide.itemstore.mutex.utils.MutexLabellingUtils;
import org.tasktide.itemstore.mutex.MutexOrchestrator;


/**
 * Application for testing {@link MutexOrchestrator},
 *  race condition on provided file requires central
 *  lock to acquired, unit of work performed, then
 *  lock released for the next process
 *
 * @author Brendan Kenna
 */
public class LockActionReleaseApplication {

    // Random number generator
    private static final Logger LOGGER = LogManager.getLogger(LockActionReleaseApplication.class);
    private static final Random RAND = new Random();
    private static final Timestamps RESULTS = new Timestamps();
    
    private static void wireRealMethodsWithLogging(
        MutexElection nfs,
        MutexElection fileChannel,
        Logger logger
    ) throws MutexCheckedException {

        doAnswer(invoc -> {
            invoc.callRealMethod();
            logger.info("NFS Lock acquire -> {}", MutexOrchestrator.fetchActive());
            RESULTS.setPostLock(System.currentTimeMillis());
            return null;
        }).when(nfs).acquire(any(Mutex.class));

        doAnswer(invoc -> {
            RESULTS.setEnd(System.currentTimeMillis());
            invoc.callRealMethod();
            logger.info("NFS Lock release -> {}", MutexOrchestrator.fetchActive());
            return null;
        }).when(nfs).release(any(Mutex.class));
    }
    
    
    /**
     * Application method with first argument element being the central
     *  file to write to
     * 
     * @param args
     * 
     * @throws MutexCheckedException
     * @throws IOException 
     */
    public static void main(String[] args) throws MutexCheckedException, IOException {

        // Initialize variables
        long duration, id;
        String record;
        Path dataFile;

        // Configure args
        LOGGER.info("Configuring arguments for testing");
        dataFile = Paths.get(args[0]);
        id = RAND.nextLong();
        
        // Configure constants
        MutexTestUtils.configurePaths();
        MutexLabellingUtils.configure();
        OrchestratorSpies spies = OrchestratorSpies.configure();
        wireRealMethodsWithLogging(
            spies.getNfsMutex(),
            spies.getFileChannelMutex(),
            LOGGER
        );
        
        // Acquire lock
        LOGGER.info("Acquring lock");
        duration = RAND.nextLong(1000, 5000);
        RESULTS.setStart(System.currentTimeMillis());
        MutexOrchestrator.acquireLock();
        LOGGER.info(
            "Lock acquired displaying for reference:\n\n'{}'",
            MutexOrchestrator.fetchActive().toJsonDoc()
        );
        
        // Allow time to pass
        LOGGER.info("Letting time elapse");
        MutexFilesUtils.waitJitterTime(duration);

        // Release lock
        LOGGER.info("Time elapsed, releasing lock");
        MutexOrchestrator.releaseLock();
        
        // Formate record
        LOGGER.info("Lock released, formatting record for test");
        record = String.format(
            "%d\t%d\t%d\t%d\t%d",
            id, RESULTS.getStart(), RESULTS.getPostLock(), RESULTS.getEnd(), duration
        );
        LOGGER.info("Displaying test record for reference:\n\n'{}'", record);
        
        // Write record
        LOGGER.info("Writing record");
        Files.writeString(
            dataFile,
            record,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
        LOGGER.info("Lock released, test complete");
    }
}