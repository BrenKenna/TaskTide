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
package org.tasktide.itemstore.mutex.utils;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.List;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.tasktide.itemstore.FileUtility;

import org.tasktide.itemstore.mutex.model.MutexFileType;
import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.exceptions.MutexUncheckedException;


/**
 * File utility for operations with {@link Mutex}
 *
 * @author Brendan Kenna
 */
public class MutexFilesUtils {
    
    
    // JsonB formatters
    private static final Jsonb JSON = JsonbBuilder.create();
    private static final Jsonb PRETTY_JSON = JsonbBuilder.create(
        new JsonbConfig().withFormatting(true)
    );
    
    
    /**
     * Represents provided data as JSON string
     * 
     * @param data
     * @return String
     */
    public static String toJson(Object data) {
        return PRETTY_JSON.toJson(data);
    }
    
    
    /**
     * Retrieve list of files under target directory
     *  under target
     * 
     * @param dir
     * @return Stream-Path
     */
    public static Stream<Path> fetchFiles(Path dir) {
        try {
            return Files.list(dir).sorted();
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    
    /**
     * Returns the full file path of the leader
     * 
     * @param mutex
     * @param fileType
     * @return int
     */
    public static int findPosition(Mutex mutex, MutexFileType fileType) {
        Path target = mutex.getFileForType(fileType).getParent().toAbsolutePath();
        List<Path> paths = fetchFiles(target).toList();
        int pos = -1, counter = 0;
        boolean found = false;
        
        while ( !found && counter < paths.size() ) {
            Path active = paths.get(counter);
            if ( active.equals(target) ) {
                pos = counter;
                found = true;
            }
            else {
                counter++;
            }
        }
        
        return pos;
    }
    
    
    /**
     * Find predecessor path
     * 
     * @param mutex
     * @param fileType
     * @param pos
     * @return Path
     */
    public static Path findPredecessor(Mutex mutex, MutexFileType fileType, int pos) {
        Path target = mutex.getFileForType(fileType).getParent().toAbsolutePath();
        List<Path> paths = fetchFiles(target).toList();
        if ( pos - 1 < 0 ) {
            return null;
        }
        else {
            return paths.get(pos - 1);
        }
    }

    
    /**
     * Fetch the oldest path
     * 
     * @param dir
     * 
     * @return Optional-Path
     */
    public static Optional<Path> fetchOldest(Path dir) {
        try {
            return Files.list(dir)
                .sorted()
            .findFirst();
        }
        catch (IOException ex) {
            return Optional.empty();
        }
    }
    
    
    /**
     * Enqueue lock request
     * 
     * 
     * @param mutex
     * @return boolean
     */
    public static boolean writeHostFile(Mutex mutex) {
        Path path = mutex.getHostFile().getParent().toAbsolutePath();
        if ( FileUtility.createDirectory(path) ) {
            try {
                Files.writeString(
                    mutex.getHostFile(),
                    mutex.toJsonDoc(),
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
                );
                return true;
            }
            
            catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    
    /**
     * Write the election file of {@link Mutex}
     * 
     * @param mutex
     * @return boolean
     */
    public static boolean writeElectionFile(Mutex mutex) {
        Path path = mutex.getElectionFile().getParent().toAbsolutePath();
        FileUtility.createDirectory(path);
        
        try {
            Files.writeString(
                mutex.getElectionFile(),
                mutex.toJsonDoc(),
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
            );
            return true;
        }
        
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    
    /**
     * Fetch nullable {@link Mutex} from provided file
     * 
     * @param mutexFile
     * 
     * @return Optional-{@link Mutex}
     */
    public static Optional<Mutex> readMutexFromFile(Path mutexFile) {
        try {
            return Optional.of(
                JSON.fromJson(
                    Files.readString(mutexFile),
                    Mutex.class
                )
            );
        } catch (IOException ex) {
            return Optional.empty();
        }
    }
    
    
    /**
     * Remove host file
     * 
     * @param mutex
     * @return boolean
     */
    public static boolean removeHostFile(Mutex mutex) {
        
        // Randomly stagger time before removing host file 
        try {
            Thread.sleep(MutexConstants.getRandomJitter().toMillis());
            Files.deleteIfExists(mutex.getHostFile());
            return true;
        }

        // Otherwise false
        catch (Exception ex) {
            return false;
        }
    }
    
    
    /**
     * Wait for random jitter time
     * 
     * @return boolean
     */
    public static boolean waitJitterTime() {
        try {
            TimeUnit.MILLISECONDS.sleep(MutexConstants.getRandomJitter().toMillis());
            return true;
        }
        catch (Exception ex) {
            return false;
        } 
    }
    
    
    /**
     * Wait for random jitter time
     * 
     * @param time
     * @return boolean
     */
    public static boolean waitJitterTime(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
            return true;
        }
        catch (Exception ex) {
            return false;
        } 
    }
    
    
    /**
     * Delete target flie
     * 
     * @param target
     * @return boolean
     */
    public static boolean deleteFile(Path target) {
        try {
            Files.deleteIfExists(target);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    
    /**
     * Write {@link Mutex}
     * 
     * @param mutex
     * @param targetFile
     * @return boolean
     */
    public static boolean writeMutexFile(Mutex mutex, Path targetFile) {
        try {
            Files.writeString(
                targetFile,
                mutex.toJsonDoc(),
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
            );
            return true;
        }
        
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    
    /**
     * Write mutex to required {@link MutexFileType}
     * 
     * @param mutex
     * @param fileType
     */
    public static void writeMutex(Mutex mutex, MutexFileType fileType) {
        
        // Delete file if exists
        switch ( fileType ) {
            
            case LOCK_FILE -> {
                deleteFile(mutex.getLockFile());
                writeMutexFile(mutex, mutex.getLockFile()); 
            }
            
            case HOST_FILE -> {
                deleteFile(mutex.getHostFile());
                writeMutexFile(mutex, mutex.getHostFile());
            }
            
            case ELECTION_FILE -> {
                deleteFile(mutex.getElectionFile());
                writeMutexFile(mutex, mutex.getElectionFile());
            }
            
            default -> {
                throw new MutexUncheckedException("Mutex file type must one of:\tElection, Host, Lock");
            }
        }
    }
}