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
    
    @ConfigProperty(name = "tasktide.manager.targetFile", defaultValue = "")
    String targetFile;
    
    @ConfigProperty(name = "tasktide.manager.delimiter", defaultValue = "")
    String delimiter;
    
    @ConfigProperty(name = "tasktide.manager.nestedDelimiter", defaultValue = "")
    String nestedDelimiter;
    
    @ConfigProperty(name = "tasktide.manager.method", defaultValue = "Import/Export")
    String method;
    
    @ConfigProperty(name = "tasktide.manager.targetStep", defaultValue = "myStep")
    String targetStep;
    
    
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
        this.help();
        this.targetFile();
        this.delimiter();
        this.nestedDelimiter();
        this.method();
        
        if ( this.getPath().isEmpty() ) {
            argTree.getTree().getRoot().setData(this.getArgumentMap());
        }
        else {
            argTree.getTree().addChild(this.getPath(), this.getArgumentMap());
        }
    }
    
    
    /**
     * Configure help
     */
    public void help() {
        Argument<Boolean> arg;
        arg = this.getArgumentBuilder()
            .withName("Help")
            .withDescription("Displays command-line documentation")
            .withShortFlag("-h")
            .withLongFlag("--help")
            .withArgType(ArgumentType.ACTION)
            .withValue(false, Boolean.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the input file for the {@link TaskTideServiceManager} to use
     *  for any of its relevant operations
     * 
     */
    public void targetFile() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Target File")
            .withDescription("Defines the full file path for import")
            .withShortFlag("-f")
            .withLongFlag("--target-file")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        try {
            this.targetFile = this.getConfig().getValue("tasktide.manager.targetFile", String.class);
        }
        catch (Exception ex) {
            this.targetFile = "";
        }
        arg.setValue(targetFile);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the delimiter that the {@link TaskTideServiceManager} should use
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
        .build();
        arg.setRefClass(String.class);
        
        try {
            this.delimiter = this.getConfig().getValue("tasktide.manager.delimiter", String.class);
        }
        catch (Exception ex) {
            this.delimiter = "";
        }
        arg.setValue(delimiter);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the delimiter that the {@link TaskTideServiceManager} should use
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
        arg.setRefClass(String.class);
        
        try {
            this.nestedDelimiter = this.getConfig().getValue("tasktide.manager.nestedDelimiter", String.class);
        }
        catch (Exception ex) {
            this.nestedDelimiter = "";
        }
        arg.setValue(nestedDelimiter);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure method to manager method to run import/export
     * 
     */
    public void method() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Method")
            .withDescription("Whether to run import or export")
            .withShortFlag("-m")
            .withLongFlag("--method")
            .withArgType(ArgumentType.ACTION)
        .build();
        
        try {
            this.method = this.getConfig().getValue("tasktide.manager.method", String.class);
        }
        catch (Exception ex) {
            this.method = "";
        }
        arg.setValue(method);
        this.getArgumentMap().putArgument(arg);
    }
}