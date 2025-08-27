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
package org.tasktide.core.supporting;

import jakarta.json.bind.Jsonb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;


/**
 * Collection of useful File I/O methods
 * 
 * @author Brendan Kenna
 */
public class FileIO {
    
    
    /**
     * Fetch buffered reader for provided file
     * 
     * @param resourcePath
     * @return {@link BufferedReader}
     * 
     * @throws IOException 
     */
    public static BufferedReader fetchBufferedReader(String resourcePath) throws IOException {
        Path path = Paths.get(resourcePath);
        return Files.newBufferedReader(path);
    }
    
    
    /**
     * Fetch buffered writer for provided file
     * 
     * @param resourcePath
     * @return {@link BufferedWriter}
     * 
     * @throws IOException 
     */
    public static BufferedWriter fetchBufferedWriter(String resourcePath) throws IOException {
        Path path = Paths.get(resourcePath);
        return Files.newBufferedWriter(path);
    }
    
    
    /**
     * Fetch file writer for provided file
     * 
     * @param filePath
     * @return {@link FileWriter}
     * 
     * @throws IOException 
     */
    public static FileWriter fetchFileWriter(String filePath) throws IOException {
        return new FileWriter(filePath);
    }
 
    
    /**
     * Fetch file reader for provided file
     * 
     * @param filePath
     * @return {@link FileReader}
     * 
     * @throws IOException 
     */
    public static FileReader fetchFileReader(String filePath) throws IOException {
        return new FileReader(filePath);
    }
    
    
    /**
     * Export provided dataset to the target output file, flagging
     *  whether to indent resulting JSON doc
     * 
     * @param <T>
     * @param prettyPrint
     * @param data
     * @param outFile
     * @return boolean
     */
    public static <T> boolean exportJson(boolean prettyPrint, T data, String outFile) {
        try ( FileWriter writer = fetchFileWriter(outFile) ) {
            Jsonb json = JsonUtils.getJsonb(prettyPrint);
            json.toJson(data, writer);
            writer.close();
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    
    /**
     * Reads input file to a collection of provided reference classes
     * 
     * @param <T>
     * @param outFile
     * @param refClass
     * @return List-T
     */
    public static <T> List<T> importJson(String outFile, Class<T> refClass) {
        try ( FileReader reader = fetchFileReader(outFile) ) {
            Jsonb jsonb = JsonUtils.getJsonb(true);
            List<T> output = Arrays.asList(jsonb.fromJson(reader, refClass));
            reader.close();
            return output;
        }
        
        catch (IOException ex) {
            return null;
        }
    }
}