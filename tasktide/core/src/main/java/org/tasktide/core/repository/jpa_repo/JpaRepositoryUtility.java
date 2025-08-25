/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.jpa_repo;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.sql.DataSource;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.services.ServiceFactory;


/**
 * Supports creation of {@link JpaRepository} {@link TaskTideRepository},
 *  {@link TaskTideService}, and initialize the {@link TaskTideServiceManager} 
 * 
 * @author bkenna
 */
public class JpaRepositoryUtility {
    
    // There can be only one
    private static JpaRepositoryUtility INSTANCE;
    
    // Attributes
    private Config config;
    private String dbURL, dbUser, dbPassword, dbDriver;
    private String dialectDriver, ddlUpdate, showSql;
    
    
    /**
     * Construction
     */
    private JpaRepositoryUtility() {
        configure();
    }
    
    
    /**
     * Iniialize Utility
     * 
     * @return JpaRepositoryUtility
     */
    public static JpaRepositoryUtility get() {
        if ( INSTANCE == null ) {
            INSTANCE = new JpaRepositoryUtility();
            return INSTANCE;
        }
        return INSTANCE;
    } 
    
    
    /**
     * Fetch configurations
     */
    private void configure() {
        this.config = ConfigProvider.getConfig();
        try {
            this.dbURL = this.config.getValue("datasource.url", String.class);
            this.dbUser = this.config.getValue("datasource.user", String.class);
            this.dbPassword = this.config.getValue("datasource.password", String.class);
            this.dbDriver = this.config.getValue("datasource.driver", String.class);
        }
        catch (NoSuchElementException ex) {
            throw new IllegalArgumentException("URL, User, Password, and Driver must be defined");
        }
        try {
            this.dialectDriver = this.config.getValue("hibernate.dialect", String.class);
        }
        catch (NoSuchElementException ex) {
            this.dialectDriver = "";
        }
        try {
            this.ddlUpdate = this.config.getValue("hibernate.hbm2ddl.auto", String.class);
        }
        catch (NoSuchElementException ex) {
            this.ddlUpdate = "";
        }
        try {
            this.showSql = this.config.getValue("hibernate.show_sql", String.class);
        }
        catch (NoSuchElementException ex) {
            this.showSql = "false";
        }
    }
    
    
    /**
     * Initialize the {@link TaskTideServiceManager}
     * 
     */
    public void initServiceManager() {
        
        // Initialize vars
        EntityManager backend;
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Fetch entity manager
        backend = fetchEntityManager();
        
        // Make services
        workItemService = ServiceFactory.makeWorkItemService(RepositoryType.SQL, backend, "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(RepositoryType.SQL, backend, "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(RepositoryType.SQL, backend, "Workflow-Service");
        
        // Return manager
        TaskTideServiceManager.initialize(workItemService, stepService, workflowService);
    }
    
    
    /**
     * Wrapper method to construct HikariConfig
     * 
     * @return HikariConfig
     */
    public HikariConfig fetchHikariConfig() {
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl( this.dbURL );
        conf.setUsername( this.dbUser );
        conf.setPassword( this.dbPassword );
        conf.setDriverClassName( this.dbDriver );
        return conf;
    }
    
    
    /**
     * Configure HikariDataSource
     * 
     * @return DataSource
     */
    public DataSource fetchDataSource() {
        HikariConfig conf = fetchHikariConfig();
        return new HikariDataSource(conf);
    }
    
    
    /**
     * Fetch configuration map for entity manager
     * 
     * @param dataSource
     * @return Map-String, Object
     */
    public Map<String, Object> fetchEntityManagerConfig(DataSource dataSource) {
        Map<String, Object> conf = new HashMap<>();
        conf.put("jakarta.persistence.nonJtaDataSource", dataSource);
        if ( !this.ddlUpdate.isEmpty() ) {
            conf.put("hibernate.hbm2ddl.auto", this.ddlUpdate);
        }
        if ( !this.dialectDriver.isEmpty() ) {
            conf.put("hibernate.dialect", this.dialectDriver);
        }
        if ( !this.showSql.isEmpty() ) {
            conf.put("hibernate.show_sql", this.showSql);
        }
        return conf;
    }
    
    
    /**
     * Fetches entity manager factory using config
     * 
     * @param conf
     * @return EntityManagerFactory
     */
    public EntityManagerFactory fetchEntityManagerFactory(Map<String, Object> conf) {
        return Persistence.createEntityManagerFactory("TaskTide", conf);
    }
    
    
    /**
     * Fetch an EntityManager for the required {@link ManagerTarget}
     * 
     * @return EntityManager
     */
    public EntityManager fetchEntityManager() {
        
        // Configure dependanceis
        DataSource dataSource = fetchDataSource();
        Map<String, Object> conf = fetchEntityManagerConfig(dataSource);
        
        // Return entity manager for model type
        return fetchEntityManagerFactory(conf).createEntityManager();
    }
    
    
    /**
     * Wrapper method to fetch EntityManager {@link TaskTideRepository} map
     * 
     * @return Map-{@link ManagerTarget}, {@link TaskTideRepository}
     */
    public Map<ManagerTarget, TaskTideRepository> fetchEntityManagerRepoMap() {
        
        // Initialize output and fetch item store map
        Map<ManagerTarget, TaskTideRepository> output = new HashMap<>();
        EntityManager entityStore = fetchEntityManager();
        
        // Add work item repo
        TaskTideRepository repo = RepositoryType.SQL.createRepository(WorkItem.class, entityStore, "WorkItem");
        output.put(ManagerTarget.WORKITEM, repo);
        
        // Add step repo
        repo = RepositoryType.SQL.createRepository(Step.class, entityStore, "Step");
        output.put(ManagerTarget.STEP, repo);
        
        // Add workflow repo
        repo = RepositoryType.SQL.createRepository(Workflow.class, entityStore, "Workflow");
        output.put(ManagerTarget.WORKFLOW, repo);
        
        // Return results
        return output;
    }
}
