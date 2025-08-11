/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.executor.streamhandler;

import java.io.File;
import java.nio.file.Files;
import java.io.OutputStream;
import java.nio.file.Path;

import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 *
 * @author bkenna
 */
public class StreamHandler {
    
    // Attributes
    private final Path logDir;
    private final File stdout, stderr;
    private String[] stderrArr, stdoutArr;
    
    
    /**
     * Construct with log files
     * 
     * @param stdout
     * @param stderr 
     */
    public StreamHandler(File stdout, File stderr) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.logDir = stdout.toPath().getParent();
    }

    
    public void handleLogs() throws IOException {
    
        // Measure file sizes
        long size = measureSizes();
        
        // Place into array if <1MB
        if ( size < 1_000_000 ) {
            String[] log;
            
            // Read in stderr
            log = this.readStderr();
            this.setStderrArr(log);
            this.stderr.delete();
            
            // Read in stdout
            log = this.readStdout();
            this.setStdoutArr(log);
            this.stdout.delete();
        }
        
        // Otherwise zip and report on token
        else {
            Path zipFile = this.zipFiles();
            String msg = String.format("stderr saved to ", zipFile.toFile().getAbsolutePath());
            String[] logArr = { msg };
            this.setStderrArr(logArr);
            this.setStdoutArr(logArr);
        }
    }

    
    /**
     * Measure size of both stdout, and stderr
     * 
     * @return long
     * @throws IOException 
     */
    public long measureSizes() throws IOException {
        long output = Files.size(stdout.toPath());
        output += Files.size(stderr.toPath());
        return output;
    }
    
    
    /**
     * Zip the stdour/stderr logs
     * 
     * @return Zip Size
     * @throws IOException 
     */
    public Path zipFiles() throws IOException {
        Path zipFile = this.logDir.resolve("logs.zip");
        
        ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile));
        this.addToZip(zos, stdout.toPath(), "stdout.log");
        this.addToZip(zos, stderr.toPath(), "stderr.log");
        zos.close();
        
        return zipFile;
    }
    
    
    /**
     * Adds an entry to the zip
     * 
     * @param zos
     * @param file
     * @param entryName
     * @throws IOException 
     */
    private void addToZip(ZipOutputStream zos, Path file, String entryName) throws IOException {
        zos.putNextEntry( new ZipEntry(entryName) );
        Files.copy(file, zos);
        zos.closeEntry();
    }
    
    
    /**
     * Unpacks zip file
     * 
     * @param zipFile
     * @throws IOException 
     */
    public void unpackZip(Path zipFile) throws IOException {
        ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile));
        ZipEntry entry;
        while ( ( entry = zis.getNextEntry()) != null ) {
            OutputStream os = Files.newOutputStream(this.logDir);
            zis.transferTo(os);
            os.close();
        }
        zis.close();
    }

    
    /**
     * Read stderr file
     * 
     * @return String[]
     * @throws IOException 
     */
    public String[] readStderr() throws IOException {
        return Files.readAllLines(this.stderr.toPath()).toArray(new String[0]);
    }
    
    
    /**
     * Read stdout file
     * 
     * @return String[]
     * @throws IOException 
     */
    public String[] readStdout() throws IOException {
        return Files.readAllLines(this.stdout.toPath()).toArray(new String[0]);
    }
    
    
    /**
     * Gets stdout file
     * 
     * @return File
     */
    public File getStdout() {
        return stdout;
    }

    
    /**
     * Gets stderr file
     * 
     * @return File
     */
    public File getStderr() {
        return stderr;
    }
    
    
    /**
     * Gets stderr arr
     * 
     * @return 
     */
    public String[] getStderrArr() {
        return stderrArr;
    }

    
    /**
     * Sets stderr arr
     * 
     * @param stderrArr 
     */
    public void setStderrArr(String[] stderrArr) {
        this.stderrArr = stderrArr;
    }

    
    /**
     * Get stdout arr
     * 
     * @return 
     */
    public String[] getStdoutArr() {
        return stdoutArr;
    }

    
    /**
     * Sets stdout arr
     * 
     * @param stdoutArr 
     */
    public void setStdoutArr(String[] stdoutArr) {
        this.stdoutArr = stdoutArr;
    }
}
