/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.configurer;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentType;


/**
 * Class to template the {@link TaskTideServiceManager} module parameters
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
    
    @ConfigProperty(name = "tasktide.manager.method", defaultValue = "Import/Export")
    String method;
    
    @ConfigProperty(name = "tasktide.manager.targetStep", defaultValue = "myStep")
    String targetStep;
    
    @ConfigProperty(name = "tasktide.manager.target", defaultValue = "WorkItem")
    String target;
    
    
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
     * Applies the {@link TaskTideServiceManager} configurations to {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
        this.inputFile();
        this.delimiter();
        this.outputFile();
        this.nestedDelimiter();
        this.method();
        this.targetStep();
        this.target();
        
        if ( this.getPath().isEmpty() ) {
            argTree.getTree().getRoot().setData(this.getArgumentMap());
        }
        else {
            argTree.getTree().addChild(this.getPath(), this.getArgumentMap());
        }
    }
    
    
    public void target() {
        Argument<String> arg;
        this.target = this.getConfig().getValue("tasktide.manager.target", String.class);
        arg = this.getArgumentBuilder()
            .withName("Target")
            .withDescription("Defines the Import/Export target")
            .withShortFlag("-tgt")
            .withLongFlag("--target")
            .withValue(this.target, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the input file for the {@link TaskTideServiceManager} to use
     *  for any of its relevant operations
     * 
     */
    public void inputFile() {
        Argument<String> arg;
        this.inputFile = this.getConfig().getValue("tasktide.manager.inputFile", String.class);
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
     * Configure the delimiter that the {@link TaskTideServiceManager} should use
     *  for File I/O
     * 
     */
    public void delimiter() {
        Argument<String> arg;
        this.delimiter = this.getConfig().getValue("tasktide.manager.delimiter", String.class);
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
     * Configure the output file for the {@link TaskTideServiceManager} to use
     *  for any of its relevant operations
     * 
     */
    public void outputFile() {
        Argument<String> arg;
        this.outputFile = this.getConfig().getValue("tasktide.manager.outputFile", String.class);
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
     * Configure the delimiter that the {@link TaskTideServiceManager} should use
     *  for File I/O
     * 
     */
    public void nestedDelimiter() {
        Argument<String> arg;
        this.nestedDelimiter = this.getConfig().getValue("tasktide.manager.nestedDelimiter", String.class);
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
    
    
    /**
     * Configure method to manager method to run import/export
     * 
     */
    public void method() {
        Argument<String> arg;
        this.method = this.getConfig().getValue("tasktide.manager.method", String.class).toLowerCase();
        arg = this.getArgumentBuilder()
            .withName("Method")
            .withDescription("Whether to run import or export")
            .withShortFlag("-m")
            .withLongFlag("--method")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.method, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the target step to reference for import
     * 
     */
    public void targetStep() {
        Argument<String> arg;
        this.targetStep = this.getConfig().getValue("tasktide.manager.targetStep", String.class);
        arg = this.getArgumentBuilder()
            .withName("Target Step")
            .withDescription("Defines the target step for import")
            .withShortFlag("-ts")
            .withLongFlag("--target-step")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.targetStep, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
}
