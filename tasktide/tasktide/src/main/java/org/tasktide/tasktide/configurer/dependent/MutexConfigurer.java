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
package org.tasktide.tasktide.configurer.dependent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.tasktide.configurer.AbstractConfig;
import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentType;


/**
 * Configure properties for the {@link MutexOrchestrator}
 *
 * @author Brendan Kenna
 */
public class MutexConfigurer extends AbstractConfig {
    
    
    /**
     * Time constants
     */
    private final Random RAND = new Random();
    
    @ConfigProperty(name = "tasktide.mutex.retryInterval", defaultValue = "30L")
    private long retryInterval;
    
    @ConfigProperty(name = "tasktide.mutex.startJitter", defaultValue = "250L")
    private long startJitter;
    
    @ConfigProperty(name = "tasktide.mutex.endJitter", defaultValue = "250L")
    private long endJitter;
    
    @ConfigProperty(name = "tasktide.mutex.staleFileThreshold", defaultValue = "500L")
    private long staleFileThreshold;
    
    @ConfigProperty(name = "tasktide.mutex.minRandomDuration", defaultValue = "10L")
    private long minRandomLong;
    
    @ConfigProperty(name = "tasktide.mutex.maxRandomDuration", defaultValue = "500L")
    private long maxRandomLong;
    
    
    /**
     * Path constants
     */
    @ConfigProperty(name = "tasktide.mutex.lockDir", defaultValue = "")
    private Path lockDir;
    
    @ConfigProperty(name = "tasktide.mutex.lockFile", defaultValue = "")
    private Path lockFile;
    
    @ConfigProperty(name = "tasktide.mutex.hostDir", defaultValue = "")
    private Path hostDir;
    
    @ConfigProperty(name = "tasktide.mutex.hostFile", defaultValue = "")
    private Path hostFile;
    
    @ConfigProperty(name = "tasktide.mutex.electionDir", defaultValue = "")
    private Path electionDir;
    
    @ConfigProperty(name = "tasktide.mutex.electionFile", defaultValue = "")
    private Path electionFile;
    
    
    /**
     * Defaults {@link ArgumentTree} path to root
     */
    public MutexConfigurer() {
        super("tasktide");
    }
    
    
    /**
     * Sets {@link ArgumentTree} path to provided
     * 
     * @param path 
     */
    public MutexConfigurer(String path) {
        super(path);
    }
    

    /**
     * Initialize configuration of {@link MutexConfigurer}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
        
        // Configure duration properties
        this.retryInterval();
        this.startJitter();
        this.endJitter();
        this.staleFileThreshold();
        this.minRandomDuration();
        this.maxRandomDuration();
        
        // Validate any min-max pairs
        if ( this.startJitter > this.endJitter ) {
            long tmp = this.startJitter;
            this.startJitter = this.endJitter;
            this.endJitter = tmp;
        }
        if ( this.minRandomLong > this.maxRandomLong ) {
            long tmp = this.minRandomLong;
            this.minRandomLong = this.maxRandomLong;
            this.maxRandomLong = tmp;
        }
        
        // Configure path properties
        this.lockDir();
        this.lockFile();
        this.electionDir();
        this.electionFile();
        this.hostDir();
        this.hostFile();
    }
    
    
    /**
     * Sets retry interval
     */
    public void retryInterval() {
        
        // Initialize vars
        Argument<Long> arg;
        
        // Fetch value if present
        try {
            this.retryInterval = this.getConfig().getValue("tasktide.mutex.retryInterval", Long.class);
        }
        catch (Exception ex) {
            this.retryInterval = RAND.nextLong(550L, 700L);
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Mutex retry interval")
            .withDescription("Configures retry interval for TaskTide-Mutex")
            .withLongFlag("--retry-interval")
            .withShortFlag("-rt")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Long.class);
        
        // Handle setting value
        arg.setValue(this.retryInterval);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets startJitter
     */
    public void startJitter() {
        
        // Initialize vars
        Argument<Long> arg;
        
        // Fetch value if present
        try {
            this.startJitter = this.getConfig().getValue("tasktide.mutex.startJitter", Long.class);
        }
        catch (Exception ex) {
            this.startJitter = RAND.nextLong(10L, 300L);
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Mutex start jitter")
            .withDescription("Configures minimum milliseconds wait time")
            .withLongFlag("--start-jitter")
            .withShortFlag("-sj")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Long.class);
        
        // Handle setting value
        arg.setValue(this.startJitter);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets endJitter
     */
    public void endJitter() {
        
        // Initialize vars
        Argument<Long> arg;
        
        // Fetch value if present
        try {
            this.endJitter = this.getConfig().getValue("tasktide.mutex.endJitter", Long.class);
        }
        catch (Exception ex) {
            this.endJitter = RAND.nextLong(301L, 500L);
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Mutex end jitter")
            .withDescription("Configures maximum milliseconds wait time")
            .withLongFlag("--end-jitter")
            .withShortFlag("-ej")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Long.class);
        
        // Handle setting value
        arg.setValue(this.endJitter);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets staleFileThreshold
     */
    public void staleFileThreshold() {
        
        // Initialize vars
        Argument<Long> arg;
        
        // Fetch value if present
        try {
            this.staleFileThreshold = this.getConfig().getValue("tasktide.mutex.staleFileThreshold", Long.class);
        }
        catch (Exception ex) {
            this.staleFileThreshold = RAND.nextLong(800L, 1500L);
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Stale File Threshold")
            .withDescription("Configures stale file threshold: 0-1500ms")
            .withLongFlag("--stale-file-threshold")
            .withShortFlag("-sft")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Long.class);
        
        // Handle setting value
        arg.setValue(this.staleFileThreshold);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets staleFileThreshold
     */
    public void minRandomDuration() {
        
        // Initialize vars
        Argument<Long> arg;
        
        // Fetch value if present
        try {
            this.minRandomLong = this.getConfig().getValue("tasktide.mutex.minRandomLong", Long.class);
        }
        catch (Exception ex) {
            this.minRandomLong = RAND.nextLong(10L, 300L);
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Minimum Random Long")
            .withDescription("Configures value for minimum random long: 10-3000ms")
            .withLongFlag("--min-random-long")
            .withShortFlag("-minrl")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Long.class);
        
        // Handle setting value
        arg.setValue(this.minRandomLong);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets staleFileThreshold
     */
    public void maxRandomDuration() {
        
        // Initialize vars
        Argument<Long> arg;
        
        // Fetch value if present
        try {
            this.maxRandomLong = this.getConfig().getValue("tasktide.mutex.maxRandomLong", Long.class);
        }
        catch (Exception ex) {
            this.maxRandomLong = RAND.nextLong(301L, 500L);
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Minimum Random Long")
            .withDescription("Configures value for minimum random long: 301-500ms")
            .withLongFlag("--max-random-long")
            .withShortFlag("-maxrl")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Long.class);
        
        // Handle setting value
        arg.setValue(this.maxRandomLong);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets locking directory
     */
    public void lockDir() {
        
        // Initialize vars
        String tmp;
        Argument<Path> arg;
        
        // Fetch value if present
        try {
            tmp = this.getConfig().getValue("tasktide.mutex.lockDir", String.class);
        }
        catch (Exception ex) {
            tmp = "ItemStore-Mutex";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Locking Directory")
            .withDescription("Configures central locking directory")
            .withLongFlag("--lockDir")
            .withShortFlag("-ld")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Path.class);
        
        // Handle setting value
        this.lockDir = Paths.get(tmp);
        arg.setValue(this.lockDir);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets lock file
     */
    public void lockFile() {
        
        // Initialize vars
        String tmp;
        Argument<Path> arg;
        
        // Fetch value if present
        try {
            tmp = this.getConfig().getValue("tasktide.mutex.lockFile", String.class);
        }
        catch (Exception ex) {
            tmp = "lock.lock";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Locking File")
            .withDescription("Configures central locking file")
            .withLongFlag("--lockFile")
            .withShortFlag("-lf")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Path.class);
        
        // Handle setting value
        this.lockFile = Paths.get(tmp);
        arg.setValue(this.lockFile);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets lock file
     */
    public void hostDir() {
        
        // Initialize vars
        String tmp;
        Argument<Path> arg;
        
        // Fetch value if present
        try {
            tmp = this.getConfig().getValue("tasktide.mutex.hostDir", String.class);
        }
        catch (Exception ex) {
            tmp = "hostDir";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Host Directory")
            .withDescription("Configures central host directory")
            .withLongFlag("--hostDir")
            .withShortFlag("-hd")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Path.class);
        
        // Handle setting value
        this.hostDir = Paths.get(tmp);
        arg.setValue(this.hostDir);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets lock file
     */
    public void hostFile() {
        
        // Initialize vars
        String tmp;
        Argument<Path> arg;
        
        // Fetch value if present
        try {
            tmp = this.getConfig().getValue("tasktide.mutex.hostFile", String.class);
        }
        catch (Exception ex) {
            tmp = "hostFile";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Host File")
            .withDescription("Configures central host file")
            .withLongFlag("--hostFile")
            .withShortFlag("-hf")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Path.class);
        
        // Handle setting value
        this.hostFile = Paths.get(tmp);
        arg.setValue(this.hostFile);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets lock file
     */
    public void electionDir() {
        
        // Initialize vars
        String tmp;
        Argument<Path> arg;
        
        // Fetch value if present
        try {
            tmp = this.getConfig().getValue("tasktide.mutex.electionDir", String.class);
        }
        catch (Exception ex) {
            tmp = "electionDir";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Election Directory")
            .withDescription("Configures central election directory")
            .withLongFlag("--electionDir")
            .withShortFlag("-ed")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Path.class);
        
        // Handle setting value
        this.electionDir = Paths.get(tmp);
        arg.setValue(this.electionDir);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets lock file
     */
    public void electionFile() {
        
        // Initialize vars
        String tmp;
        Argument<Path> arg;
        
        // Fetch value if present
        try {
            tmp = this.getConfig().getValue("tasktide.mutex.electionFile", String.class);
        }
        catch (Exception ex) {
            tmp = "electionFile.lock";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Election File")
            .withDescription("Configures election file")
            .withLongFlag("--electionFile")
            .withShortFlag("-ef")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Path.class);
        
        // Handle setting value
        this.electionFile = Paths.get(tmp);
        arg.setValue(this.electionFile);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Not used because this is component of global setting
     */
    @Override
    public void help() {}
}