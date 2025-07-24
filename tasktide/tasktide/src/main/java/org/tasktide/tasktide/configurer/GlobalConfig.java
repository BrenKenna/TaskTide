/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.configurer;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentType;


/**
 * 
 * 
 * @author bkenna
 */
@ApplicationScoped
public class GlobalConfig extends AbstractConfigurer {
    
    
    // Which client to use
    @ConfigProperty(name = "tasktide.client", defaultValue = "Manager")
    private String client;
    
    
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
     * Jakarta NoSQL Config
     * 
     */
    @ConfigProperty(name = "tasktide.core.repository.jnosql.type", defaultValue = "")// Document, KeyValue, Graph, Column
    private String nosqlType;
    
    @ConfigProperty(name = "tasktide.core.repository.jnosql.provider", defaultValue = "")// CouchDb, MonogoDB etc
    private String nosqlProvider;
    
    @ConfigProperty(name = "tasktide.core.repository.jnosql.provider-class", defaultValue = "")
    private String nosqlProviderClass;
    
    @ConfigProperty(name = "tasktide.core.repository.jnosql.user", defaultValue = "")
    private String nosqlUser;
    
    @ConfigProperty(name = "tasktide.core.repository.jnosql.password", defaultValue = "")
    private String nosqlPassword;
    
    @ConfigProperty(name = "tasktide.core.repository.jnosql.host", defaultValue = "")
    private String nosqlHost;
    
    
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
        this.client();
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
        
        // Put argument map into tree
        if ( this.getPath().isEmpty() ) {
            argTree.getTree().getRoot().setData(this.getArgumentMap());
        }
        else {
            argTree.getTree().addChild(this.getPath(), this.getArgumentMap());
        }
    }
    
    
    /**
     * Configures client to use
     */
    public void client() {
        Argument<String> arg;
        this.client = this.getConfig().getValue("tasktide.client", String.class);
        if ( this.client != null ) {
            arg = this.getArgumentBuilder()
                .withName("Client")
                .withDescription("Specifies which client is being configuerd")
                .withShortFlag("-c")
                .withLongFlag("--client")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.client, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("Client")
                .withDescription("Specifies which client is being configuerd")
                .withShortFlag("-c")
                .withLongFlag("--client")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the workflow name to use
     * 
     */
    public void workflowName() {
        Argument<String> arg;
        this.workflowName = this.getConfig().getValue("tasktide.core.collection.workflow.name", String.class);
        if ( this.workflowName != null ) {
            arg = this.getArgumentBuilder()
                .withName("Workflow Name")
                .withDescription("Specifies the workflow name to use")
                .withShortFlag("-wn")
                .withLongFlag("--workflow-name")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.workflowName, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("Workflow Name")
                .withDescription("Specifies the workflow name to use")
                .withShortFlag("-wn")
                .withLongFlag("--workflow-name")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the workflow name to use
     * 
     */
    public void stepName() {
        Argument<String> arg;
        this.stepName = this.getConfig().getValue("tasktide.core.collection.step.name", String.class);
        if ( this.stepName != null ) {
            arg = this.getArgumentBuilder()
                .withName("Step Name")
                .withDescription("Specifies the step name to use")
                .withShortFlag("-sn")
                .withLongFlag("--step-name")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.stepName, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("Step Name")
                .withDescription("Specifies the step name to use")
                .withShortFlag("-sn")
                .withLongFlag("--step-name")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the work item collection name to use
     * 
     */
    public void workItemName() {
        Argument<String> arg;
        this.workItemName = this.getConfig().getValue("tasktide.core.collection.workitem.name", String.class);
        if ( this.workItemName != null ) {
            arg = this.getArgumentBuilder()
                .withName("WorkItem Name")
                .withDescription("Specifies the WorkItem Collection Name to use")
                .withShortFlag("-win")
                .withLongFlag("--work-item-collection-name")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.workItemName, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("WorkItem Name")
                .withDescription("Specifies the WorkItem Collection Name to use")
                .withShortFlag("-win")
                .withLongFlag("--work-item-collection-name")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure date format
     * 
     */
    public void dateFormat() {
        Argument<String> arg;
        this.dateFormat = this.getConfig().getValue("tasktide.utils.date-format", String.class);
        if ( this.dateFormat != null) {
            arg = this.getArgumentBuilder()
                .withName("Date Format")
                .withDescription("Specifies date format to use. Default is 'dd/MM/yy HH:mm:ss'")
                .withShortFlag("-df")
                .withLongFlag("--date-format")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.dateFormat, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("Date Format")
                .withDescription("Specifies date format to use. Default is 'dd/MM/yy HH:mm:ss'")
                .withShortFlag("-df")
                .withLongFlag("--date-format")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure token expiration days
     * 
     */
    public void tokenExpirationDays() {
        Argument<Integer> arg;
        this.tokenExpirationDays = this.getConfig().getValue("tasktide.utils.token-expiration-days", int.class);
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
        this.repositoryType = this.getConfig().getValue("tasktide.core.repository.type", String.class);
        if ( this.repositoryType != null ) {
            arg = this.getArgumentBuilder()
                .withName("Repository Type")
                .withDescription("Specifies the backend repository type: RocksDB, NoSQL, Json")
                .withShortFlag("-rt")
                .withLongFlag("--repository-type")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.repositoryType, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("Repository Type")
                .withDescription("Specifies the backend repository type: RocksDB, NoSQL, Json")
                .withShortFlag("-rt")
                .withLongFlag("--repository-type")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the repository file path
     * 
     */
    public void filePath() {
        Argument<String> arg;
        this.filePath = this.getConfig().getValue("tasktide.core.repository.file-path", String.class);
        if ( this.filePath != null  ) {
            arg = this.getArgumentBuilder()
                .withName("File Path")
                .withDescription("Specifies the file path for RocksDB/Json repository")
                .withShortFlag("-fp")
                .withLongFlag("--file-path")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.filePath, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("File Path")
                .withDescription("Specifies the file path for RocksDB/Json repository")
                .withShortFlag("-fp")
                .withLongFlag("--file-path")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository type
     * 
     */
    public void nosqlType() {
        Argument<String> arg;
        this.nosqlType = this.getConfig().getValue("tasktide.core.repository.jnosql.type", String.class);
        if ( this.nosqlType != null ) {
            arg = this.getArgumentBuilder()
                .withName("NoSQL Database Type")
                .withDescription("Specifies the backend NoSQL Database type: Document, Key-Value, Column, Graph")
                .withShortFlag("-nst")
                .withLongFlag("--nosql-type")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.nosqlType, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("NoSQL Database Type")
                .withDescription("Specifies the backend NoSQL Database type: Document, Key-Value, Column, Graph")
                .withShortFlag("-nst")
                .withLongFlag("--nosql-type")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository provider
     * 
     */
    public void nosqlProvider() {
        Argument<String> arg;
        this.nosqlProvider = this.getConfig().getValue("tasktide.core.repository.jnosql.provider", String.class);
        if ( this.nosqlProvider != null ) {
            arg = this.getArgumentBuilder()
                .withName("NoSQL Provider")
                .withDescription("Specifies the backend NoSQL-DB Provider: MongoDB, CouchDB, Neo4J etc")
                .withShortFlag("-nsp")
                .withLongFlag("--nosql-provider")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.nosqlProvider, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("NoSQL Provider")
                .withDescription("Specifies the backend NoSQL-DB Provider: MongoDB, CouchDB, Neo4J etc")
                .withShortFlag("-nsp")
                .withLongFlag("--nosql-provider")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository type
     * 
     */
    public void nosqlProviderClass() {
        Argument<String> arg;
        this.nosqlProviderClass = this.getConfig().getValue("tasktide.core.repository.jnosql.provider-class", String.class);
        if ( this.nosqlProviderClass != null ) {
            arg = this.getArgumentBuilder()
                .withName("NoSQL Provider Class")
                .withDescription("Specifies the backend NoSQL-DB Provider Class")
                .withShortFlag("-nspc")
                .withLongFlag("--nosql-provider-class")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.nosqlProviderClass, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("NoSQL Provider Class")
                .withDescription("Specifies the backend NoSQL-DB Provider Class")
                .withShortFlag("-nspc")
                .withLongFlag("--nosql-provider-class")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository username
     * 
     */
    public void nosqlUser() {
        Argument<String> arg;
        this.nosqlUser = this.getConfig().getValue("tasktide.core.repository.jnosql.user", String.class);
        if ( this.nosqlUser != null ) {
            arg = this.getArgumentBuilder()
                .withName("NoSQL User")
                .withDescription("Specifies the backend NoSQL-DB Username to use")
                .withShortFlag("-nsu")
                .withLongFlag("--nosql-user")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.nosqlUser, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("NoSQL User")
                .withDescription("Specifies the backend NoSQL-DB Username to use")
                .withShortFlag("-nsu")
                .withLongFlag("--nosql-user")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository password
     * 
     */
    public void nosqlPassword() {
        Argument<String> arg;
        this.nosqlPassword = this.getConfig().getValue("tasktide.core.repository.jnosql.password", String.class);
        if ( this.nosqlPassword != null ) {
            arg = this.getArgumentBuilder()
                .withName("NoSQL Password")
                .withDescription("Specifies the backend NoSQL-DB password to use")
                .withShortFlag("-nsp")
                .withLongFlag("--nosql-password")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.nosqlPassword, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("NoSQL Password")
                .withDescription("Specifies the backend NoSQL-DB password to use")
                .withShortFlag("-nsp")
                .withLongFlag("--nosql-password")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the NoSQL repository host:port
     * 
     */
    public void nosqlHost() {
        Argument<String> arg;
        this.nosqlHost = this.getConfig().getValue("tasktide.core.repository.jnosql.host", String.class);
        if ( this.nosqlHost != null ) {
            arg = this.getArgumentBuilder()
                .withName("NoSQL Host:Port")
                .withDescription("Specifies the backend NoSQL-DB host:port to use")
                .withShortFlag("-nsh")
                .withLongFlag("--nosql-host")
                .withArgType(ArgumentType.ACTION)
                .withValue(this.nosqlHost, String.class)
            .build();
        }
        else {
            arg = this.getArgumentBuilder()
                .withName("NoSQL Host:Port")
                .withDescription("Specifies the backend NoSQL-DB host:port to use")
                .withShortFlag("-nsh")
                .withLongFlag("--nosql-host")
                .withArgType(ArgumentType.ACTION)
            .build();
        }
        //System.out.println(this.getArgumentMap().getArgMap());
        this.getArgumentMap().putArgument(arg);
    }
}