/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.configurer.dependent;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.tasktide.configurer.AbstractConfigurer;
import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentType;


/**
 * Applies JPA micro-profile config values to {@link ArgumentTree}
 * 
 * @author bkenna
 */
public class JpaConfigurer extends AbstractConfigurer {

    
    /**
     * JDBC Config from HikariCP
     */
    @ConfigProperty(name = "datasource.url", defaultValue = "")
    private String dbURL;
    
    @ConfigProperty(name = "datasource.user", defaultValue = "")
    private String dbUser;
    
    @ConfigProperty(name = "datasource.password", defaultValue = "")
    private String dbPassword;
    
    @ConfigProperty(name = "datasource.driver", defaultValue = "")
    private String dbDriver;
    
    
    /**
     * Config from Hibernate
     */
    @ConfigProperty(name = "hibernate.dialect", defaultValue = "")
    private String dialectDriver;
    
    @ConfigProperty(name = "hibernate.hbm2ddl.auto", defaultValue = "")
    private String ddlUpdate;
    
    @ConfigProperty(name = "hibernate.show_sql", defaultValue = "false")
    private boolean showSql;
    
    
    /**
     * Defaults {@link ArgumentTree} path to root
     */
    public JpaConfigurer() {
        super("tasktide");
    }
    
    
    /**
     * Sets {@link ArgumentTree} path to provided
     * 
     * @param path 
     */
    public JpaConfigurer(String path) {
        super(path);
    }
    
    
    /**
     * Apply micro-profile config to the {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
        
        // Apply configs
        this.dbURL();
        this.dbUser();
        this.dbPassword();
        this.dbDriver();
        this.dialectDriver();
        this.ddlUpdate();
        this.showSql();
        
        // Put argument map into tree
        if ( this.getPath().isEmpty() ) {
            argTree.getTree().getRoot().setData(this.getArgumentMap());
        }
        else {
            argTree.getTree().getRoot().getData().extend(this.getArgumentMap());
        }
    }
    
    
    /**
     * Sets dbURL
     */
    public void dbURL() {
        
        // Initialize vars
        Argument<String> arg;
        
        // Fetch value if present
        try {
            this.dbURL = this.getConfig().getValue("datasource.url", String.class);
        }
        catch (Exception ex) {
            this.dbURL = "";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Database URL")
            .withDescription("Configures database URL in the form jdbcLpostgresql://localhost:5432/mydb")
            .withLongFlag("--db-url")
            .withShortFlag("-dbu")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Handle setting value
        if (!this.dbURL.isEmpty()) arg.setValue(this.dbURL);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets dbUser
     */
    public void dbUser() {
        
        // Initialize vars
        Argument<String> arg;
        
        // Fetch value if present
        try {
            this.dbUser = this.getConfig().getValue("datasource.user", String.class);
        }
        catch (Exception ex) {
            this.dbUser = "";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Database Username")
            .withDescription("Configures database user name")
            .withLongFlag("--db-username")
            .withShortFlag("-dbusr")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Handle setting value
        if (!this.dbUser.isEmpty()) arg.setValue(this.dbUser);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets dbPassword
     */
    public void dbPassword() {
        
        // Initialize vars
        Argument<String> arg;
        
        // Fetch value if present
        try {
            this.dbPassword = this.getConfig().getValue("datasource.password", String.class);
        }
        catch (Exception ex) {
            this.dbPassword = "";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Database Password")
            .withDescription("Configures database password")
            .withLongFlag("--db-pass")
            .withShortFlag("-dbp")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Handle setting value
        if (!this.dbPassword.isEmpty()) arg.setValue(this.dbPassword);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets dbDriver
     */
    public void dbDriver() {
        
        // Initialize vars
        Argument<String> arg;
        
        // Fetch value if present
        try {
            this.dbDriver = this.getConfig().getValue("datasource.driver", String.class);
        }
        catch (Exception ex) {
            this.dbDriver = "";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Database Driver")
            .withDescription("Configures database user name")
            .withLongFlag("--db-driver")
            .withShortFlag("-dbd")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Handle setting value
        if (!this.dbDriver.isEmpty()) arg.setValue(this.dbDriver);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets dialectDriver
     */
    public void dialectDriver() {
        
        // Initialize vars
        Argument<String> arg;
        
        // Fetch value if present
        try {
            this.dialectDriver = this.getConfig().getValue("hibernate.dialect", String.class);
        }
        catch (Exception ex) {
            this.dialectDriver = "";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Database Dialect Driver")
            .withDescription("Configures database dialect driver")
            .withLongFlag("--db-dialect-driver")
            .withShortFlag("-dbdd")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Handle setting value
        if (!this.dialectDriver.isEmpty()) arg.setValue(this.dialectDriver);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets DDL update
     */
    public void ddlUpdate() {
        
        // Initialize vars
        Argument<String> arg;
        
        // Fetch value if present
        try {
            this.ddlUpdate = this.getConfig().getValue("hibernate.hbm2ddl.auto", String.class);
        }
        catch (Exception ex) {
            this.ddlUpdate = "";
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Database DDL Update")
            .withDescription("Configures DDL Update")
            .withLongFlag("--db-ddl-update")
            .withShortFlag("-dbdu")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        
        // Handle setting value
        if (!this.ddlUpdate.isEmpty()) arg.setValue(this.ddlUpdate);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets show sql
     */
    public void showSql() {
        
        // Initialize vars
        Argument<Boolean> arg;
        
        // Fetch value if present
        try {
            this.showSql = this.getConfig().getValue("hibernate.show_sql", boolean.class);
        }
        catch (Exception ex) {
            this.showSql = false;
        }
        
        // Build argument
        arg = this.getArgumentBuilder()
            .withName("Database Show SQL")
            .withDescription("Configures Show SQL")
            .withLongFlag("--db-show-sql")
            .withShortFlag("-dbsq")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(boolean.class);
        
        // Handle setting value
        arg.setValue(this.showSql);
        this.getArgumentMap().putArgument(arg);
    }
}