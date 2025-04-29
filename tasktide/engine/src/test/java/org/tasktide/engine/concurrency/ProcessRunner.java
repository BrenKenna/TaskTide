/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.concurrency;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.tasktide.core.model.task.ProcessLog;
import org.tasktide.core.model.task.TaskLogging;

import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.supporting.DateUtility;


/**
 *
 * @author bkenna
 */
public class ProcessRunner {
    
    // Attributes
    private final DateUtility dateUtils;
    
    
    /**
     * Construct with specific constants
     * 
     * @param dateFormat
     * @param expiration 
     */
    public ProcessRunner(
       @ConfigProperty(name = "task-tide.date-format", defaultValue = "dd/MM/yy HH:mm:ss") String dateFormat,
       @ConfigProperty(name = "task-tide.expiration", defaultValue = "2") int expiration
    ) {
        this.dateUtils = new DateUtility("dd/MM/yy HH:mm:ss", 2);
    }
    
    
    /**
     * Default constructor
     */
    public ProcessRunner() {
        this.dateUtils = new DateUtility("dd/MM/yy HH:mm:ss", 2);
    }

    
    /**
     * Run provided command returning {@link TaskLogging TaskLogging} 
     * 
     * @param command
     * @return {@link TaskLogging TaskLogging}
     * <br><br>
     * @throws IOException
     * @throws InterruptedException 
     */
    public TaskLogging execute(String command) throws IOException, InterruptedException {
        
        // Intialize vars
        long startTime, doneTime;
        Process process;
        ProcessLog procLog;
        TaskLogging result;
        
        // Run process
        startTime = dateUtils.getDateLong();
        process = Runtime.getRuntime().exec(command);
        doneTime = dateUtils.getDateLong();
        
        // Build process log from logs
        procLog = buildProcessLog(process);
        result = buildTaskLogging(process, procLog, startTime, doneTime);

        // Handle exit code: perhaps log
        if ( result.getExitCode() == 0 ) {
            
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
     * Build {@link ProcessLog ProcessLog} from Process
     * 
     * @param stdout
     * @param stderr
     * @return {@link ProcessLog Process Log}
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
     * @return {@link TaskLogging TaskLogging}
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
