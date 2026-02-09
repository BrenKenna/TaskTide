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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import org.tasktide.itemstore.mutex.MutexStrategyType;
import org.tasktide.itemstore.mutex.model.HostLock;
import org.tasktide.itemstore.mutex.model.HostLockFactory;
import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.model.MutexFileType;
import org.tasktide.itemstore.mutex.model.MutexState;
import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;

/**
 *
 * @author Brendan Kenna
 */
public class FileChannelStrategy extends MutexStrategy {

    /**
     * Constructs strategy for implementing
     *  {@link FileChannelMutex}
     *  
     */
    public FileChannelStrategy() {
        super(MutexStrategyType.FILE_CHANNEL);
    }

    
    @Override
    public synchronized boolean apply(Mutex mutex) {
        
        // Pass if already active
        if (mutex.getHostLock() != null) {
            return false;
        }

        // Fetch file
        MutexFilesUtils.waitJitterTime();
        Path targetFile = mutex.getFileForType(MutexFileType.HOST_FILE);
        HostLock hostLock = HostLockFactory.create(targetFile);
        mutex.setHostLock(hostLock);

        // Fetch channel to file and lock
        try {

            // Acquire lock
            FileChannel fileChannel = new RandomAccessFile(targetFile.toFile(), "rw").getChannel();
            FileLock fileLock = fileChannel.lock();

            // Set property
            hostLock.setFileChannel(fileChannel);
            hostLock.setFileLock(fileLock);

            // Return success flag
            mutex.setState(MutexState.LOCKED);
            MutexFilesUtils.waitJitterTime();
            return true;
        }
        
        catch (IOException ex) {
            return false;
        }
    }

    @Override
    public synchronized boolean release(Mutex mutex) {
        
        // Any sanity check
        Path targetFile = mutex.getFileForType(MutexFileType.HOST_FILE);

        // Fetch path and lock
        HostLock hostLock = mutex.getHostLock();
        try {

            // Release lock on file channel
            if (hostLock.getFileLock() != null && hostLock.getFileLock().isValid()) {
                hostLock.getFileLock().release();
            }

            // Close file channel
            if (hostLock.getFileChannel() != null && hostLock.getFileChannel().isOpen()) {
                hostLock.getFileChannel().close();
            }

            // Return closure state
            MutexFilesUtils.waitJitterTime();
            return true;
        }
        
        catch (IOException ex) {
            return false;
        }
    }

    @Override
    public boolean cleanUp(Mutex mutex) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}