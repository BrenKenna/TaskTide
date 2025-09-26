/*
 * Copyright 2025 Brendan Kenna.
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
package org.tasktide.tasktide.configurer;

import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Paths;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.manager.command.ManagerAction;

import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentType;


/**
 * Class to template the {@link TaskTideServiceManager} module parameters
 * 
 * @author bkenna
 */
@ApplicationScoped
public class ManagerConfig extends AbstractConfig {
    
    @ConfigProperty(name = "tasktide.manager.targetFile", defaultValue = "")
    String targetFile;
    
    @ConfigProperty(name = "tasktide.manager.delimiter", defaultValue = "")
    String delimiter;
    
    @ConfigProperty(name = "tasktide.manager.nestedDelimiter", defaultValue = "")
    String nestedDelimiter;
    
    @ConfigProperty(name = "tasktide.manager.method", defaultValue = "")
    String method;
    
    @ConfigProperty(name = "tasktide.manager.importString", defaultValue = "")
    String importString;
    
    @ConfigProperty(name = "tasktide.manager.itemId", defaultValue = "")
    String itemId;
    
    @ConfigProperty(name = "tasktide.manager.target", defaultValue = "")
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
        this.help();
        this.targetFile();
        this.delimiter();
        this.nestedDelimiter();
        this.method();
        this.importString();
        this.itemId();
        this.target();
        
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
    @Override
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
            .withRefClass(String.class)
        .build();

        this.targetFile = this.getConfigValue("tasktide.manager.targetFile", String.class, "");
        //this.targetFile = Paths.get(targetFile).toString();
        arg.setValue(this.targetFile);
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
            .withRefClass(String.class)
        .build();
        
        this.delimiter = this.getConfigValue("tasktide.manager.delimiter", String.class, "");
        arg.setValue(this.delimiter);
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
            .withRefClass(String.class)
        .build();
        
        this.nestedDelimiter = this.getConfigValue("tasktide.manager.nestedDelimiter", String.class, "");
        arg.setValue(this.nestedDelimiter);
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
            .withRefClass(String.class)
        .build();
        
        this.method = this.getConfigValue("tasktide.manager.method", String.class, "");
        arg.setValue(method);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * JSON Import string
     */
    public void importString() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Import String")
            .withDescription("JSON formatted string:\t '{'Field': 'Value'}'")
            .withShortFlag("-is")
            .withLongFlag("--import-string")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(String.class)
        .build();
        
        this.importString = this.getConfigValue("tasktide.manager.importString", String.class, "");
        arg.setValue(this.importString);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures ItemId of ManagerTarget for ManagerAction
     * 
     */
    public void itemId() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("ItemId")
            .withDescription("ItemId over which the required ManagerAction is taken")
            .withShortFlag("-ii")
            .withLongFlag("--itemId")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(String.class)
        .build();
        
        this.itemId = this.getConfigValue("tasktide.manager.itemId", String.class, "");
        arg.setValue(this.itemId);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures target for {@link ManagerAction}
     * 
     */
    public void target() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Target")
            .withDescription("Target for manager action")
            .withShortFlag("-tgt")
            .withLongFlag("--target")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(String.class)
        .build();
        
        this.target = this.getConfigValue("tasktide.manager.target", String.class, "");
        arg.setValue(this.target);
        this.getArgumentMap().putArgument(arg);
    }
}