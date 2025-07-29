/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.itemstore;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Collection of static method to support the testing of the {@link ItemStore}
 *  interface and implementing classes.
 *  
 * @author bkenna
 */
public class ItemStoreTestUtils {
    
    // Attributes
    private static final JsonbConfig JSONB_CONFIG = new JsonbConfig().withFormatting(true);
    private static final Jsonb JSONB_COMPACT = JsonbBuilder.create();
    private static final Jsonb JSONB_PRETTY = JsonbBuilder.create(JSONB_CONFIG);
    private static final Random RANDOM = new Random();
    private static final Logger logger = LogManager.getLogger(ItemStoreTestUtils.class);
    
    
    /**
     * Serialize object of type to compact JSON string
     * 
     * @param <T>
     * @param obj
     * @return String
     */
    public static <T> String toCompactJson(T obj) {
        return JSONB_COMPACT.toJson(obj);
    }
    
    
    /**
     * Serialize object to indented JSON string
     * 
     * @param <T>
     * @param obj
     * @return String
     */
    public static <T> String toJson(T obj) {
        return JSONB_PRETTY.toJson(obj);
    }
    
    
    /**
     * Deserialize object from JSON string
     * 
     * @param <T>
     * @param obj
     * @param modelClass
     * @return T
     */
    public static <T> T fromJson(String obj, Class<T> modelClass) {
        return JSONB_COMPACT.fromJson(obj, modelClass);
    }
    
    
    /**
     * Set working {@link Path} for {@link RocksDBStore} under the supplied store
     *  name for the supplied directory
     * 
     * @param flag
     * @param directory
     * @param storeName
     * @return {@link Path}
     */
    public static Path setWorkingDirectory(String flag, String directory, String storeName) {
        try {
            Path cwd = Paths.get(directory);
            Path workDir = cwd.resolve(flag).resolve(storeName);
            Files.createDirectories(workDir);
            return workDir;
        }
        catch ( IOException ex) {
            return null;
        } 
    }
    
    
    /**
     * Set working {@link Path} directory for RocksDB under supplied store name
     *  on current directory
     * 
     * @param flag
     * @param storeName
     * @return {@link Path}
     */
    public static Path setWorkingDirectory(String flag, String storeName) {
        try {
            Path cwd = Paths.get( System.getProperty("user.dir") );
            Path workDir = cwd.resolve(flag).resolve(storeName);
            Files.createDirectories(workDir);
            return workDir;
        }
        catch ( IOException ex) {
            return null;
        } 
    }
    
    
    /**
     * Make {@link RocksDBStore} under the target {@link Path}
     * 
     * @param storeName
     * @param workDir
     * @return {@link RocksDBStore}
     */
    public static RocksDBStore makeRocksDB(String storeName, Path workDir) {
        String dbDirectory = workDir.toString();
        String masterDB = "master";
        String protoDB = UUID.randomUUID().toString();
        try {
            RocksDBStore itemStore = new RocksDBStore(storeName, dbDirectory, masterDB, protoDB);
            return itemStore;
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    
    /**
     * Fetch process builder for {@link CrossJvmFileLocker}
     * 
     * @return {@link ProcessBuilder} 
     */
    public static ProcessBuilder crossJvmFileLockerProcess() {
    
        // Initialize required variables
        String classPath;
        ProcessBuilder output;
        
        // Configure process
        classPath = System.getProperty("java.class.path");
        output = new ProcessBuilder(
      "java",
      "-cp", classPath,
      "org.tasktide.itemstore.stores.CrossJvmFileLocker"
        );
        
        // Return process builder
        return output;
    }
    
    
    /**
     * Run process from builder
     * 
     * @param procBuilder
     * @param flag
     * @return {@link Process}
     */
    public static Process runProcess(ProcessBuilder procBuilder, String flag) {
    
        // Try running process, and return it
        try {
            Process process = procBuilder.start();
            TimeUnit.MILLISECONDS.sleep(RANDOM.nextInt(200, 500));
            return process;
        }
        
        // Otherwise log error, and null
        catch ( IOException | InterruptedException ex ) {
            logger.error("Unable to run process:\t'{}'", flag);
            return null;
        }
    }
    
    
    /**
     * Run the {@link CrossJvmFileLocker} {@link Process} from {@link ProcessBuilder} required
     *  number of times serially
     * 
     * @param proc
     * @param nProcesses
     * 
     * @return List-{@link Process}
     */
    public static List<Process> runProcesses(ProcessBuilder proc, int nProcesses) {
    
        // Initialize required vars
        List<Process> output = new ArrayList<>();
        
        // Run processes
        // proc.inheritIO();
        for ( int i = 0; i < nProcesses; i++ ) {
            Process process = runProcess(proc, "Process-" + i);
            if ( process != null ) {
                output.add(process);
            }
        }
        
        // Return output
        return output;
    }
    
    
    /**
     * Run the {@link CrossJvmFileLocker} {@link Process} from {@link ProcessBuilder} required
     *  number of times in parallel
     * 
     * @param baseProc
     * @param nProcesses
     * @return List-{@link Process}
     */
    public static List<Process> runProcessesPara(ProcessBuilder baseProc, int nProcesses) {
        List<Process> output = new ArrayList<>();

        for (int i = 0; i < nProcesses; i++) {
            try {
                // Clone the base ProcessBuilder to avoid shared mutable state
                ProcessBuilder proc = new ProcessBuilder(baseProc.command());
                // Optionally set environment, directory, etc. if needed here

                Process process = proc.start();  // Start process asynchronously
                output.add(process);
                System.out.println("Started process-" + i);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to start process-" + i);
            }
        }
        return output;
    }
    
    
    /**
     * Wait until all process are complete
     * 
     * @param processList
     * @return boolean
     */
    public static boolean waitUntilDone(List<Process> processList) {
        try {
            logger.info("Waiting proceses to complete");
            for ( Process proc : processList ) {
                logger.info("Waiting on process:\t'{}'", proc.pid());
                proc.waitFor();
            }
            return true;
        }
        catch (InterruptedException ex) {
            logger.warn("Interupt exception encountered during processing:\n", ex);
            return false;
        }
    }

    
    /**
     * Summarize processes through logger. Returning false if any failed
     * 
     * @param processList 
     * @return int
     */
    public static int summarizeProcesses(List<Process> processList) {
        int nPassing = 0;
        for ( Process proc : processList ) {
            
            // Fetch stdout & stderr 
            String stdout = readStream(proc.getInputStream());
            String stderr = readStream(proc.getErrorStream());
            
            // Display process
            logger.info(
           "\nDisplaying process data:\nProcess ID:\t'{}'\nExit Value:\t'{}'\nStdout:\n\n'{}'\n\nStderr:\n\n",
              proc.pid(), proc.exitValue(), stdout, stderr
            );
            
            // Set flag if failed
            if ( proc.exitValue() == 0) {
                nPassing++;
            }
        }
        return nPassing;
    }
    
    
    /**
     * Read stream into a string
     * 
     * @param inputStream
     * @return String
     */
    public static String readStream(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
        catch ( IOException ex ) {
            return "";
        }
    }
}
