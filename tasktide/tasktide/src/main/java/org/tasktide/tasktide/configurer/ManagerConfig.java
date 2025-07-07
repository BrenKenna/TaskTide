/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.configurer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.tasktide.core.manager.TaskTideManager;

import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentType;


/**
 * Class to template the {@link TaskTideManager} module parameters
 * 
 * @author bkenna
 */
@ApplicationScoped
public class ManagerConfig extends AbstractConfigurer {
    
    
    @ConfigProperty(name = "tasktide.manager.inputFile", defaultValue = "")
    String inputFile;
    
    
    @ConfigProperty(name = "tasktide.manager.outputFile", defaultValue = "")
    String outputFile;
    
    
    @ConfigProperty(name = "tasktide.manager.delimiter", defaultValue = "")
    String delimiter;
    
    
    @ConfigProperty(name = "tasktide.manager.nestedDelimiter", defaultValue = "")
    String nestedDelimiter;
    
    
    /**
     * Defaults config path to 'tasktide engine'
     * 
     */
    public ManagerConfig() {
        super("manager");
    }
    
    
    /**
     * Uses supplied path for engine config
     * 
     * @param path 
     */
    public ManagerConfig(String path) {
        super(path);
    }
    
    
    /**
     * Applies the {@link TaskTideManager} configurations to {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
        this.inputFile();
        this.delimiter();
        this.outputFile();
        this.nestedDelimiter();
        
        if ( this.getPath().isEmpty() ) {
            argTree.getTree().getRoot().setData(this.getArgumentMap());
        }
        else {
            argTree.getTree().addChild(this.getPath(), this.getArgumentMap());
        }
    }
    
    
    /**
     * Configure the input file for the {@link TaskTideManager} to use
     *  for any of its relevant operations
     * 
     */
    public void inputFile() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Input File")
            .withDescription("Defines the full file path for import")
            .withShortFlag("-i")
            .withLongFlag("--input-file")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.inputFile, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the delimiter that the {@link TaskTideManager} should use
     *  for File I/O
     * 
     */
    public void delimiter() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Delimiter")
            .withDescription("Defines the delimiter to use for im/export")
            .withShortFlag("-d")
            .withLongFlag("--delimiter")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.delimiter, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the output file for the {@link TaskTideManager} to use
     *  for any of its relevant operations
     * 
     */
    public void outputFile() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Output File")
            .withDescription("Defines the full JSON formatted file path for export")
            .withShortFlag("-o")
            .withLongFlag("--output-file")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.outputFile, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the delimiter that the {@link TaskTideManager} should use
     *  for File I/O
     * 
     */
    public void nestedDelimiter() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Nested Delimiter")
            .withDescription("Defines the ItemTask delimiter if required")
            .withShortFlag("-nd")
            .withLongFlag("--nested-delimiter")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.nestedDelimiter, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
}
