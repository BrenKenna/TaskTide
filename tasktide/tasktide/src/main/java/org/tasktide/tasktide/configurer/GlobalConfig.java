/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.configurer;

import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentType;


/**
 * 
 * 
 * @author bkenna
 */
public class GlobalConfig extends AbstractConfigurer {
    
    
    /**
     * Collection Params
     * 
     */
    @Inject
    @ConfigProperty(name = "tasktide.core.collection.workflow.name", defaultValue = "Workflow")
    private String workflowName;
    
    @Inject
    @ConfigProperty(name = "tasktide.core.collection.step.name", defaultValue = "Step")
    private String stepName;
    
    @Inject
    @ConfigProperty(name = "tasktide.core.collection.work-item.name", defaultValue = "WorkItem")
    private String workItemName;
    
    
    /**
     * Repository Params
     * 
     */
    @Inject
    @ConfigProperty(name = "tasktide.core.repository.type", defaultValue = "RocksDB")// Or, JSON, NOSQL
    private String repositoryType;
    
    @Inject
    @ConfigProperty(name = "tasktide.core.repository.file-path", defaultValue = "myData")// RocksDB or Json
    private String filePath;
    
    
    /**
     * Jakarta NoSQL Config
     * 
     */
    @Inject
    @ConfigProperty(name = "tasktide.core.repository.jnosql.type", defaultValue = "")// Document, KeyValue, Graph, Column
    private String nosqlType;
    
    @Inject
    @ConfigProperty(name = "tasktide.core.repository.jnosql.provider", defaultValue = "")// CouchDb, MonogoDB etc
    private String nosqlProvider;
    
    @Inject
    @ConfigProperty(name = "tasktide.core.repository.jnosql.provider-class", defaultValue = "")
    private String nosqlProviderClass;
    
    @Inject
    @ConfigProperty(name = "tasktide.core.repository.jnosql.user", defaultValue = "")
    private String nosqlUser;
    
    @Inject
    @ConfigProperty(name = "tasktide.core.repository.jnosql.password", defaultValue = "")
    private String nosqlPassword;
    
    @Inject
    @ConfigProperty(name = "tasktide.core.repository.jnosql.host", defaultValue = "")
    private String nosqlHost;
    
    
    /**
     * Utility Params
     * 
     */
    @Inject
    @ConfigProperty(name = "tasktide.utils.date-format", defaultValue = "dd/MM/yy HH:mm:ss")
    private String dateFormat;
    
    @Inject
    @ConfigProperty(name = "tasktide.utils.token-expiration-days", defaultValue = "4")
    private int tokenExpirationDays;
    
    
    /**
     * Use default "tasktide" root config path
     * 
     */
    public GlobalConfig() {
        super("tasktide");
    }
    
    
    /**
     * Use the provided as root config path
     * 
     * @param path 
     */
    public GlobalConfig(String path) {
        super(path);
    }
    
    
    /**
     * Apply global configurations to the task tide {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
        
        // Collection name settings
        this.workflowName();
        this.stepName();
        this.workItemName();
        
        // Repository settings
        this.repositoryType();
        
        // Repository file path for RocksDB/Json
        this.filePath();
        
        // Jakarta NoSQL Parameters
        this.nosqlType();
        this.nosqlProvider();
        this.nosqlProviderClass();
        this.nosqlUser();
        this.nosqlPassword();
        this.nosqlHost();
        
        // Utility settings
        this.dateFormat();
        this.tokenExpirationDays();
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
            .withArgType(ArgumentType.ACTION)
            .withValue(this.workflowName, String.class)
        .build();
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
            .withArgType(ArgumentType.ACTION)
            .withValue(this.stepName, String.class)
        .build();
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
            .withArgType(ArgumentType.ACTION)
            .withValue(this.workItemName, String.class)
        .build();
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
            .withArgType(ArgumentType.ACTION)
            .withValue(this.dateFormat, String.class)
        .build();
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
            .withShortFlag("-df")
            .withLongFlag("--date-format")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.tokenExpirationDays, int.class)
        .build();
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
            .withArgType(ArgumentType.ACTION)
            .withValue(this.repositoryType, String.class)
        .build();
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
            .withArgType(ArgumentType.ACTION)
            .withValue(this.filePath, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository type
     * 
     */
    public void nosqlType() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("NoSQL Database Type")
            .withDescription("Specifies the backend NoSQL Database type: Document, Key-Value, Column, Graph")
            .withShortFlag("-nst")
            .withLongFlag("--nosql-type")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.nosqlType, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository provider
     * 
     */
    public void nosqlProvider() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("NoSQL Provider")
            .withDescription("Specifies the backend NoSQL-DB Provider: MongoDB, CouchDB, Neo4J etc")
            .withShortFlag("-nsp")
            .withLongFlag("--nosql-provider")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.nosqlProvider, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository type
     * 
     */
    public void nosqlProviderClass() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("NoSQL Provider Class")
            .withDescription("Specifies the backend NoSQL-DB Provider Class")
            .withShortFlag("-nspc")
            .withLongFlag("--nosql-provider-class")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.nosqlProviderClass, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository username
     * 
     */
    public void nosqlUser() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("NoSQL User")
            .withDescription("Specifies the backend NoSQL-DB Username to use")
            .withShortFlag("-nsu")
            .withLongFlag("--nosql-user")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.nosqlUser, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository password
     * 
     */
    public void nosqlPassword() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("NoSQL Password")
            .withDescription("Specifies the backend NoSQL-DB password to use")
            .withShortFlag("-nsp")
            .withLongFlag("--nosql-password")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.nosqlPassword, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository host:port
     * 
     */
    public void nosqlHost() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("NoSQL Host:Port")
            .withDescription("Specifies the backend NoSQL-DB host:port to use")
            .withShortFlag("-nsh")
            .withLongFlag("--nosql-host")
            .withArgType(ArgumentType.ACTION)
            .withValue(this.nosqlHost, String.class)
        .build();
        this.getArgumentMap().putArgument(arg);
    }
}
