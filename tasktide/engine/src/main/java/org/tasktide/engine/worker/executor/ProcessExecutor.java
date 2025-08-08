/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.executor;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.task.ProcessLog;
import org.tasktide.core.model.task.TaskLogging;

import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.supporting.DateUtility;
import org.tasktide.engine.worker.executor.streamhandler.StreamHandler;


/**
 * 
 * Class responsible for spawning OS process for the command of {@ItemTask}
 * 
 * @author bkenna
 */
public class ProcessExecutor {
    
    // Attributes
    private final String id;
    private final Path logDir;
    private final Logger logger = LogManager.getLogger(ProcessExecutor.class);
    private final DateUtility dateUtils;
    private final StreamHandler streamHandler;
    
    
    /**
     * Construct with specific constants
     * 
     * @param dateFormat
     * @param expiration 
     */
    public ProcessExecutor(
       @ConfigProperty(name = "task-tide.core.utils.date-format", defaultValue = "dd/MM/yy HH:mm:ss") String dateFormat,
       @ConfigProperty(name = "task-tide.core.utils.expiration", defaultValue = "2") int expiration
    ) {
        this.id = UUID.randomUUID().toString();
        this.logDir = Paths.get("logs", this.id);
        this.dateUtils = new DateUtility(dateFormat, expiration);
        this.streamHandler = new StreamHandler(
            this.logDir.resolve("stdout.log").toFile(),
            this.logDir.resolve("stderr.log").toFile()
        );
    }
    
    
    /**
     * Default constructor
     */
    public ProcessExecutor() {
        this.id = UUID.randomUUID().toString();
        this.logDir = Paths.get("logs", this.id);
        this.dateUtils = new DateUtility("dd/MM/yy HH:mm:ss", 2);
        this.streamHandler = new StreamHandler(
            this.logDir.resolve("stdout.log").toFile(),
            this.logDir.resolve("stderr.log").toFile()
        );
    }

    
    /**
     * Fetch stdout log file
     * 
     * @return
     * @throws IOException 
     */
    public Path fetchStdoutLog() throws IOException {

        // Create log directory
        Files.createDirectories(this.logDir);
        return this.logDir.resolve("stdout.log");
    }
    
    
    /**
     * Fetch stdout log file
     * 
     * @return
     * @throws IOException 
     */
    public Path fetchStderrLog() throws IOException {

        // Create log directory
        Files.createDirectories(this.logDir);
        return this.logDir.resolve("stderr.log");
    }
    
    
    /**
     * Executes the provided script through Java Lang ProcessBuilder
     * 
     * @param script
     * @return Process
     * @throws IOException
     * @throws InterruptedException 
     */
    public Process executeScript(String script) throws IOException, InterruptedException {
        
        // Initialize vars
        Process proc;
        File stdout, stderr;
        ProcessBuilder procBuild;
        
        // Fetch output logs
        stdout = this.fetchStdoutLog().toFile();
        stderr = this.fetchStderrLog().toFile();
        
        // Build process
        procBuild = new ProcessBuilder(script);
        procBuild.redirectError(stderr);
        procBuild.redirectOutput(stdout);
        
        // Start and wait for completion
        proc = procBuild.start();
        proc.waitFor();
        return proc;
    }
    
    
    /**
     * Run provided command returning {@link TaskLogging} 
     * 
     * @param command
     * @return {@link TaskLogging}
     * <br><br>
     * @throws IOException
     * @throws InterruptedException 
     */
    public TaskLogging execute(String command) throws IOException, InterruptedException {
        
        // Intialize vars
        logger.debug("Beginning execution of task:\t" + command);
        long startTime, doneTime;
        Process process;
        ProcessLog procLog;
        TaskLogging result;
        
        // Run process
        startTime = dateUtils.getDateLong();
        process = Runtime.getRuntime().exec(command);
        doneTime = dateUtils.getDateLong();
        logger.debug("Execution complete for task:\t" + command);
        
        // Build process log from logs
        logger.debug("Building ProcessLog for task:\t" + command);
        procLog = buildProcessLog(process);
        logger.debug("Displaying ProcessLog:\n" + procLog.toJsonDoc());
        result = buildTaskLogging(process, procLog, startTime, doneTime);
        logger.debug("Displaying TaskLogging:\n" + result.toJsonDoc());

        // Handle exit code: perhaps log
        if ( result.getExitCode() == 0 ) {
            logger.info("Successful execution of task:\t" + command);
        }
        else {
            logger.error("Error executing task:\t" + command);
            logger.error("Displaying failed TaskLogging:\n" + result.toJsonDoc());
        }
        
        // Return results
        return result;
    }
    
    
    /**
     * Process input stream to stdout/err string array
     * 
     * @param inputStream
     * @return String[]
     * <br><br>
     * @throws IOException 
     */
    public String[] readStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().toArray(String[]::new);
        }
    }
    
    
    /**
     * Build {@link ProcessLog} from Process
     * 
     * @param stdout
     * @param stderr
     * @return {@link ProcessLog}
     */
    private ProcessLog buildProcessLog(Process process) throws IOException {
        String[] stdout, stderr;
        stdout = this.readStream(process.getInputStream());
        stderr = this.readStream(process.getErrorStream());
        return BuilderUtility.buildProcessLog(stdout, stderr);
    }

    
    /**
     * Build {@link TaskLogging TaskLogging} from process
     * 
     * @param process
     * @param procLog
     * @param startTime
     * @param endTime
     * @return {@link TaskLogging}
     */
    private TaskLogging buildTaskLogging(Process process, ProcessLog procLog, long startTime, long endTime) {
        TaskLogging taskLog = BuilderUtility.buildTaskLogging(procLog, process);
        taskLog.setThreadName(Thread.currentThread().getName());
        taskLog.setStartTime(startTime);
        taskLog.setEndTime(endTime);
        taskLog.setExitCode(process.exitValue());
        return taskLog;
    }
}