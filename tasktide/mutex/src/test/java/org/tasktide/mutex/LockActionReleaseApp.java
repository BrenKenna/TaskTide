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
package org.tasktide.mutex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.file.StandardOpenOption;

import org.tasktide.mutex.exceptions.MutexCheckedException;

import org.tasktide.mutex.orchestrator.MutexOrchestrator;

import org.tasktide.mutex.utils.MutexFilesUtils;
import org.tasktide.mutex.utils.MutexLabellingUtils;


/**
 * Application for testing {@link MutexOrchestrator},
 *  race condition on provided file requires central
 *  lock to acquired, unit of work performed, then
 *  lock released for the next process
 *
 * @author Brendan Kenna
 */
public class LockActionReleaseApp {

    // Random number generator
    private static final Logger LOGGER = LogManager.getLogger(LockActionReleaseApp.class);
    private static final Timestamps RESULTS = new Timestamps();
    
    public static int fetchLastInt(Path file) throws IOException {
        int last = -1;
        for (String data : Files.readAllLines(file)) {
            try {
                last = Integer.parseInt(data.trim());
            } catch (NumberFormatException ex) {
                LOGGER.warn("Skipping invalid line: '{}'", data);
            }
        }
        return last;
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
        Path dataFile;

        // Configure args
        LOGGER.info("APP-Configuring arguments for testing");
        dataFile = Paths.get(args[0]);
        
        // Configure constants
        MutexTestUtils.configurePaths();
        MutexLabellingUtils.configure();
        MutexOrchestrator.configure();
        
        // Acquire lock
        LOGGER.info("APP-Acquring lock");
        MutexFilesUtils.waitJitterTime();
        try {
            MutexOrchestrator.tryAcquireUntilSuccess();
            
            if (!Files.exists(dataFile)) {
                LOGGER.info("APP-Writing '0' to data file:\t'{}'", dataFile);
                Files.writeString(dataFile, "0", StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            }
            else {
                int val = fetchLastInt(dataFile)+ 1;
                LOGGER.info("APP-Writing '{}' to data file:\t'{}'", val, dataFile);
                Files.writeString(dataFile, "\n" + String.valueOf(val), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }

            // Release lock
            LOGGER.info("APP-Record written, releasing lock");
            MutexOrchestrator.releaseLock();
            LOGGER.info("APP-Lock released, test complete");
        }
        catch ( Exception ex ) {
            LOGGER.error("Unable to acquire lock exiting:\n\n'{}'", ex);
        }
    }
}