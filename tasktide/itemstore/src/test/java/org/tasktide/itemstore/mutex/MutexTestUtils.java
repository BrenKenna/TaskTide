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

import java.nio.file.Path;
import java.nio.file.Paths;

import org.tasktide.itemstore.mutex.utils.MutexConstants;
import org.tasktide.itemstore.mutex.utils.MutexLabellingUtils;


/**
 *
 * @author Brendan Kenna
 */
public class MutexTestUtils {
    
    
    /**
     * Configure paths
     * 
     */
    public static void configurePaths() {
    
        // Configure duration constants
        String nodeProcId = MutexLabellingUtils.getNodeProcId();
        MutexConstants.initializeDurations();
        
        // Configure paths and directories
        Path cwd = Paths.get("").toAbsolutePath();
        Path targetPath = cwd.resolve("ItemStore-Mutex");
        Path hostLockDir = targetPath.resolve("Host-Lock");
        Path electionDir = targetPath.resolve("Queue");
        
        // Configure files
        Path lockingFile = targetPath.resolve("lock-file.lock");
        Path electionFile = electionDir.resolve(
            System.currentTimeMillis() +
            nodeProcId +
            ".lock"
        );
        
        // Initialize paths
        MutexConstants.initializePaths(targetPath, lockingFile, electionDir, electionFile, hostLockDir);
    }
}