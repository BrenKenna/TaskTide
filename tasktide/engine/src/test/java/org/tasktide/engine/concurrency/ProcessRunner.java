/*
 * Copyright 2026 Bren.
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
package org.tasktide.engine.concurrency;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.task.ProcessLog;
import org.tasktide.core.model.task.TaskLogging;

import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.supporting.DateUtility;


/**
 *
 * @author bkenna
 */
@Deprecated
public class ProcessRunner {
    
    // Attributes
    private final Logger logger = LogManager.getLogger(ProcessRunner.class);
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
        logger.debug("Beginging execution of task:\t" + command);
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
        logger.debug("Displaying TaskLogging:\n" + procLog.toJsonDoc());

        // Handle exit code: perhaps log
        if ( result.getExitCode() == 0 ) {
            logger.info("Successful execution of task:\t" + command);
        }
        else {
            logger.error("Error executing of task:\t" + command);
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
