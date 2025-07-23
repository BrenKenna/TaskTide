/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.builders;

import org.tasktide.core.model.task.ProcessLog;


/**
 * 
 * Allow ProcessLog objects to be built where all fields are optional
 *
 * @author bkenna
 */
public class ProcessLogBuilder extends ModelBuilder {
    
    // Attributes
    private String id = "";
    private String[] stdout, stderr = null;
    
    
    public ProcessLogBuilder() {
        super();
    }
    
    
    /**
     * Add id field
     * 
     * @param id
     * @return {@link ProcessLogBuilder}
     */
    public ProcessLogBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    
    /**
     * Add stdout field
     * 
     * @param stdout
     * @return {@link ProcessLogBuilder}
     */
    public ProcessLogBuilder stdout(String[] stdout) {
        this.stdout = stdout;
        return this;
    }
    
    
    /**
     * Add stderr field
     * 
     * @param stderr
     * @return {@link ProcessLogBuilder}
     */
    public ProcessLogBuilder stderr(String[] stderr) {
        this.stderr = stderr;
        return this;
    }
    
    
    /**
     * Construct ProcessLog from provided fields
     * 
     * @return {@link ProcessLogBuilder}
     */
    @Override
    public ProcessLog build() {
        return new ProcessLog(id, stdout, stderr);
    }
}
