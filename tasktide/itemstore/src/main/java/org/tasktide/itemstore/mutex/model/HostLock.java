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
package org.tasktide.itemstore.mutex.model;

import org.tasktide.itemstore.mutex.utils.MutexConstants;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;

import java.util.UUID;


/**
 * Model class to hold host file lock data
 *
 * @author Brendan Kenna
 */
public class HostLock {
    
    // Attributes
    private final String id;
    private final Path targetFile;
    private FileChannel fileChannel;
    private FileLock fileLock;
    private MutexState state;
    
    
    /**
     * Create OS process lock on target file
     * 
     * @param targetFile 
     */
    public HostLock(Path targetFile) {
        this.id = UUID.randomUUID().toString();
        this.targetFile = targetFile;
        this.state = MutexState.INITIALIZATION;
    }
    
    
    /**
     * Set {@link FileLock} on 
     * 
     * @return boolean
     */
    public synchronized boolean setLock() {
        this.setState(MutexState.WAITING);
        try {
            this.setFileChannel();
            this.setFileLock();
            this.setState(MutexState.HOST_LOCKED);
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    
    /**
     * Release host lock
     * 
     * @return boolean
     */
    public synchronized boolean releaseLock() {
        MutexConstants.waitOverJitter();
        try {
            
            // Release lock
            this.setState(MutexState.RELEASED);
            if ( this.fileLock != null && this.fileLock.isValid() ) {
                this.fileLock.release();
            }
            
            // Close file channel
            if ( this.fileChannel != null && this.fileChannel.isOpen() ) {
                this.fileChannel.close();
            }
            
            // Return closure state
            return true;
        }
        
        catch (IOException ex) {
            return false;
        }
    }

    
    /**
     * Get file channel
     * 
     * @return {@link FileChannel}
     */
    public FileChannel getFileChannel() {
        return fileChannel;
    }

    
    /**
     * Set file channel
     * 
     * @param fileChannel 
     */
    public void setFileChannel(FileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }
    
    
    /**
     * Open r/w file channel
     * 
     * @throws IOException 
     */
    public void setFileChannel() throws IOException {
        this.fileChannel = new RandomAccessFile(this.getTargetFile().toFile(), "rw").getChannel();
    }
    

    /**
     * Get file lock
     * 
     * @return {@link FileLock}
     */
    public FileLock getFileLock() {
        return fileLock;
    }

    public void setFileLock(FileLock fileLock) {
        this.fileLock = fileLock;
    }
    
    
    public void setFileLock() throws IOException {
        this.fileLock = fileChannel.tryLock();
    }
    

    public MutexState getState() {
        return state;
    }

    public void setState(MutexState state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public Path getTargetFile() {
        return targetFile;
    }

    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "HostLock{" +
            "id=" + id +
            ", targetFile=" + targetFile +
            ", fileChannel=" + fileChannel +
            ", fileLock=" + fileLock +
            ", state=" + state +
        '}';
    }
}
