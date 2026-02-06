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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
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
    
    
    /**
     * Create OS process lock on target file
     * 
     * @param targetFile 
     */
    public HostLock(Path targetFile) {
        this.id = UUID.randomUUID().toString();
        this.targetFile = targetFile;
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
     * Get file lock
     * 
     * @return {@link FileLock}
     */
    public FileLock getFileLock() {
        return fileLock;
    }

    
    /**
     * Set {@link FileLock}
     * 
     * @param fileLock 
     */
    public void setFileLock(FileLock fileLock) {
        this.fileLock = fileLock;
    }


    /**
     * Get Id for host lock
     * 
     * @return String 
     */
    public String getId() {
        return id;
    }

    
    /**
     * Get target file for host lock
     * 
     * @return {@link Path}
     */
    public Path getTargetFile() {
        return targetFile;
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
        return "HostLock{" +
            "id=" + id +
            ", targetFile=" + targetFile +
            ", fileChannel=" + fileChannel +
            ", fileLock=" + fileLock +
        '}';
    }
}