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
import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.FileUtility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;


/**
 * File utility for operations with {@link Mutex}
 *
 * @author Brendan Kenna
 */
public class MutexFilesUtilis {
    
    
    // JsonB formatters
    private static final Jsonb JSON = JsonbBuilder.create();
    private static final Jsonb PRETTY_JSON = JsonbBuilder.create(
        new JsonbConfig().withFormatting(true)
    );
    
    
    
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
                    mutex.getId(),
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
                );
                return true;
            }
            
            catch (IOException ex) {
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
                mutex.toJson(),
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
            );
            return true;
        }
        
        catch (IOException ex) {
            return false;
        }
    }
    
    
    /**
     * Fetch nullable {@link Mutex} for provided file
     * 
     * @param electionFile
     * 
     * @return Optional-{@link Mutex}
     */
    public static Optional<Mutex> readElectionFile(Path electionFile) {
        try {
            return Optional.of(
                JSON.fromJson(
                    Files.readString(electionFile),
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
}