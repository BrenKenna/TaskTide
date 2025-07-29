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
import javax.sql.DataSource;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.manager.ManagerTarget;
import static org.tasktide.core.manager.ManagerTarget.STEP;
import static org.tasktide.core.manager.ManagerTarget.WORKFLOW;
import static org.tasktide.core.manager.ManagerTarget.WORKITEM;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;


/**
 * Fetch configurations
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
    public static JpaRepositoryUtility getInstance() {
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
        this.dbURL = this.config.getValue("datasource.url", String.class);
        this.dbUser = this.config.getValue("datasource.user", String.class);
        this.dbPassword = this.config.getValue("datasource.password", String.class);
        this.dbDriver = this.config.getValue("datasource.driver", String.class);
        this.dialectDriver = this.config.getValue("hibernate.dialect", String.class);
        this.ddlUpdate = this.config.getValue("hibernate.hbm2ddl.auto", String.class);
        this.showSql = this.config.getValue("hibernate.show_sql", String.class);
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
        conf.put("hibernate.hbm2ddl.auto", this.ddlUpdate);
        conf.put("hibernate.dialect", this.dialectDriver);
        conf.put("hibernate.show_sql", this.showSql);
        return conf;
    }
    
    
    public EntityManagerFactory fetchEntityManagerFactory(Map<String, Object> conf, ManagerTarget modelType) {
        switch (modelType) {
            case WORKFLOW -> {
                return Persistence.createEntityManagerFactory("Workflow", conf);
            }
            case STEP -> {
                return Persistence.createEntityManagerFactory("Step", conf);
            }
            case WORKITEM -> {
                return Persistence.createEntityManagerFactory("WorkItem", conf);
            }
            default -> {
                throw new IllegalArgumentException("Task TideModel Type must be one of Workflow, Step, or WorkItem");
            }
        }
    }
    
    
    /**
     * Fetch an EntityManager for the required {@link ManagerTarget}
     * 
     * @param modelType
     * @return EntityManager
     */
    public EntityManager fetchEntityManager(ManagerTarget modelType) {
        
        // Configure dependanceis
        DataSource dataSource = fetchDataSource();
        Map<String, Object> conf = fetchEntityManagerConfig(dataSource);
        
        // Return entity manager for model type
        return fetchEntityManagerFactory(conf, modelType).createEntityManager();
    }
    
    
    /**
     * Fetch EnityManager map
     * 
     * @return Map-{@link ManagerTarget}, EntityManager
     */
    public Map<ManagerTarget, EntityManager> fetchEntityManagerMap() {
        Map<ManagerTarget, EntityManager> output = new HashMap<>();
        for ( ManagerTarget elm : ManagerTarget.values() ) {
           EntityManager manager = fetchEntityManager(elm);
           output.put(elm, manager);
        }
        return output;
    }
    
    
    /**
     * Wrapper method to fetch EntityManager {@link TaskTideRepository} map
     * 
     * @param repoType
     * @return Map-{@link ManagerTarget}, {@link TaskTideRepository}
     */
    public Map<ManagerTarget, TaskTideRepository> fetchEntityManagerRepoMap(RepositoryType repoType) {
        
        // Initialize output and fetch item store map
        Map<ManagerTarget, TaskTideRepository> output = new HashMap<>();
        Map<ManagerTarget, EntityManager> entityStoreMap = fetchEntityManagerMap();
        
        // Add work item repo
        EntityManager entity = entityStoreMap.get(ManagerTarget.WORKITEM);
        TaskTideRepository repo = repoType.createRepository(WorkItem.class, entity, "WorkItem");
        output.put(ManagerTarget.WORKITEM, repo);
        
        // Add step repo
        entity = entityStoreMap.get(ManagerTarget.STEP);
        repo = repoType.createRepository(Step.class, entity, "Step");
        output.put(ManagerTarget.STEP, repo);
        
        // Add workflow repo
        entity = entityStoreMap.get(ManagerTarget.WORKFLOW);
        repo = repoType.createRepository(Workflow.class, entity, "Workflow");
        output.put(ManagerTarget.WORKFLOW, repo);
        
        // Return results
        return output;
    }
}
