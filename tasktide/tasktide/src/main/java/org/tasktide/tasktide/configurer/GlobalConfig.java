/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.configurer;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.tasktide.configurer.dependent.JNoSQLConfigurer;
import org.tasktide.tasktide.configurer.dependent.JpaConfigurer;

import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentType;


/**
 * Configure global settings for TaskTideClient used across
 *  each client type.
 * 
 * @author bkenna
 */
@ApplicationScoped
public class GlobalConfig extends AbstractConfigurer {
    
    // Delgate Jakarta and JPA
    private final Logger LOGGER = LogManager.getLogger(GlobalConfig.class);
    private final JpaConfigurer jpaConf;
    private final JNoSQLConfigurer jnosqlConf;
    
    
    /**
     * Collection Params
     * 
     */
    @ConfigProperty(name = "tasktide.core.collection.workflow.name", defaultValue = "Workflow")
    private String workflowName;
    
    @ConfigProperty(name = "tasktide.core.collection.step.name", defaultValue = "Step")
    private String stepName;
    
    @ConfigProperty(name = "tasktide.core.collection.work-item.name", defaultValue = "WorkItem")
    private String workItemName;
    
    
    /**
     * Repository Params
     * 
     */
    @ConfigProperty(name = "tasktide.core.repository.type", defaultValue = "RocksDB")// Or, JSON, NOSQL
    private String repositoryType;
    
    @ConfigProperty(name = "tasktide.core.repository.file-path", defaultValue = "myData")// RocksDB or Json
    private String filePath;

    
    /**
     * Utility Params
     * 
     */
    @ConfigProperty(name = "tasktide.utils.date-format", defaultValue = "dd/MM/yy HH:mm:ss")
    private String dateFormat;
    
    @ConfigProperty(name = "tasktide.utils.token-expiration-days", defaultValue = "4")
    private int tokenExpirationDays;
    
    
    /**
     * Use default "tasktide" root config path
     * 
     */
    public GlobalConfig() {
        super("");
        this.jnosqlConf = new JNoSQLConfigurer("");
        this.jpaConf = new JpaConfigurer("");
    }
    
    
    /**
     * Use the provided as root config path
     * 
     * @param path 
     */
    public GlobalConfig(String path) {
        super(path);
        this.jnosqlConf = new JNoSQLConfigurer(path);
        this.jpaConf = new JpaConfigurer(path);
    }
    
    
    /**
     * Apply global configurations to the task tide {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
        
        // Configure repo properties
        this.help();
        this.workflowName();
        this.stepName();
        this.workItemName();
        this.repositoryType();
        this.filePath();
        this.dateFormat();
        this.tokenExpirationDays();
        
        // Jakarta NoSQL Parameters
        this.jnosqlConf.initConfig(argTree);
        
        // JPA Config
        this.jpaConf.initConfig(argTree);

        // Put argument map into tree
        argTree.getTree().getRoot().getData().extend(this.getArgumentMap());
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
            .withArgType(ArgumentType.GLOBAL)
            .withValue(false, Boolean.class)
        .build();
        
        this.getArgumentMap().putArgument(arg);
    }

    
    /**
     * Configures the workflow name to use
     * 
     */
    public void workflowName() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Workflow Name")
            .withDescription("Specifies the workflow name to use")
            .withShortFlag("-wn")
            .withLongFlag("--workflow-name")
            .withArgType(ArgumentType.GLOBAL)
            .withRefClass(String.class)
        .build();
        
        this.workflowName = this.getConfigValue("tasktide.core.collection.workflow.name", String.class, "");
        arg.setValue(this.workflowName);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the workflow name to use
     * 
     */
    public void stepName() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Step Name")
            .withDescription("Specifies the step name to use")
            .withShortFlag("-sn")
            .withLongFlag("--step-name")
            .withArgType(ArgumentType.GLOBAL)
            .withRefClass(String.class)
        .build();
        
        this.stepName = this.getConfigValue("tasktide.core.collection.step.name", String.class, "");
        arg.setValue(this.stepName);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the work item collection name to use
     * 
     */
    public void workItemName() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("WorkItem Name")
            .withDescription("Specifies the WorkItem Collection Name to use")
            .withShortFlag("-win")
            .withLongFlag("--work-item-collection-name")
            .withArgType(ArgumentType.GLOBAL)
            .withRefClass(String.class)
        .build();
        
        this.workItemName = this.getConfigValue("tasktide.core.collection.workitem.name", String.class, "");
        arg.setValue(this.workItemName);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure date format
     * 
     */
    public void dateFormat() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Date Format")
            .withDescription("Specifies date format to use. Default is 'dd/MM/yy HH:mm:ss'")
            .withShortFlag("-df")
            .withLongFlag("--date-format")
            .withArgType(ArgumentType.GLOBAL)
            .withRefClass(String.class)
        .build();

        this.dateFormat = this.getConfigValue("tasktide.utils.date-format", String.class, "dd/MM/yy HH:mm:ss");
        arg.setValue(this.dateFormat);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure token expiration days
     * 
     */
    public void tokenExpirationDays() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("Token Expiration")
            .withDescription("Specifies the token expiration limit, default is 4 days")
            .withShortFlag("-te")
            .withLongFlag("--token-expiration")
            .withArgType(ArgumentType.GLOBAL)
            .withRefClass(Integer.class)
        .build();
        
        this.tokenExpirationDays = this.getConfigValue("tasktide.utils.token-expiration-days", Integer.class, 4);
        arg.setValue(this.tokenExpirationDays);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the repository type
     * 
     */
    public void repositoryType() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Repository Type")
            .withDescription("Specifies the backend repository type: RocksDB, NoSQL, Json")
            .withShortFlag("-rt")
            .withLongFlag("--repository-type")
            .withArgType(ArgumentType.GLOBAL)
            .withRefClass(String.class)
        .build();
        
        this.repositoryType = this.getConfigValue("tasktide.core.repository.type", String.class, "");
        arg.setValue(this.repositoryType);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the repository file path
     * 
     */
    public void filePath() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("File Path")
            .withDescription("Specifies the file path for RocksDB/Json repository")
            .withShortFlag("-fp")
            .withLongFlag("--file-path")
            .withArgType(ArgumentType.GLOBAL)
            .withRefClass(String.class)
        .build();
        
        this.filePath = this.getConfigValue("tasktide.core.repository.file-path", String.class, "");
        arg.setValue(this.filePath);
        LOGGER.debug("Argument is configured as:\n'{}'", arg);
        this.getArgumentMap().putArgument(arg);
    }
}