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
package org.tasktide.itemstore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Comparator;


/**
 *
 * @author Brendan Kenna
 */
public class FileUtility {
    
    // Logger
    private static final Logger LOGGER = LogManager.getLogger(FileUtility.class);
    
    
    /**
     * Get base file name for provided filepath using Paths API
     * 
     * @param filePath
     * @return String
     */
    public static String getBaseName(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        int suffixInd = fileName.lastIndexOf(".");
        return fileName.substring(0, suffixInd);
    }
    
    
    /**
     * Recursively deletes folder contents
     * 
     * @param path
     * @throws IOException 
     */
    public static void recursiveDelete(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                .sorted(Comparator.reverseOrder()) // delete children before parents
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete: " + p, e);
                    }
                });
        }
    }

    
    /**
     * Creates file
     * 
     * @param path
     * @return boolean
     */
    public static boolean makeFile(Path path) {
        
        // Create masterDB lock file
        try {
            Files.createFile(path);
            return true;
        }
        
        // Already exists
        catch (FileAlreadyExistsException e) {
            return true;
        }
        
        // Creation failed for another reason
        catch (IOException e) {
            return false;
        }
    }
    
    
    /**
     * Deletes file
     * 
     * @param path
     * @return 
     */
    public static boolean dropFile(Path path) {
        
        // Drop provided file
        try {
            Files.delete(path);
            return true;
        }
        
        // Drop failed for another reason
        catch (Exception ex) {
            LOGGER.warn(
                "Warning could not delete target file, display error:\t'{}'",
                path, ex
            );
            return false;
        }
    }
    
    
    /**
     * Puts OS file lock on path
     * 
     * @param path
     * @param fileChannel
     * @param fileLock
     * @return boolean
     * @throws IOException 
     */
    public static boolean tryLock(Path path, FileChannel fileChannel, FileLock fileLock) throws IOException {
        
        // Create masterDB lock file if non-existent
        if ( !makeFile(path) ) {return false;}
        
        // Try create a lock
        try {
            releaseLock(path, fileChannel, fileLock);
            fileChannel = new RandomAccessFile(path.toFile(), "rw").getChannel();
            fileLock = fileChannel.tryLock();
            return fileLock != null;
        }
        
        // Lock creation failed
        catch (IOException e) { throw e;}
    }
    
    
    /**
     * Releases OS file lock
     * 
     * @param path
     * @param fileChannel
     * @param fileLock
     * @return boolean
     */
    public static boolean releaseLock(Path path, FileChannel fileChannel, FileLock fileLock) {
        try {
            
            // Clear lock
            if ( fileLock != null && fileLock.isValid() ) {
                fileLock.release();
            }
            
            // Close file channel
            if ( fileChannel != null && fileChannel.isOpen() ) {
                fileChannel.close();
            }
            
            // Remove lock
            Files.delete(path);
            return true;
            
        } catch (IOException ex) {
            return false;
        }
    }

    
    /**
     * Checks whether provided path is writable
     * 
     * @param path
     * @return boolean
     */
    public static boolean checkIfWritable(Path path) {
        return Files.isWritable(path);
    }
    
    
    
    /**
     * Create directory if not already existing,
     *  masking any error under boolean
     * 
     * @param path
     * 
     * @return boolean 
     */
    public static boolean createDirectory(Path path) {
        if ( Files.exists(path) ) {
            return true;
        }
        
        try {
            Files.createDirectories(path);
            return true;
        }
        
        catch (IOException ex) {
            return false;
        }
    }
}
