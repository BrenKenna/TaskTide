/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import org.eclipse.microprofile.config.Config;

import jakarta.nosql.Template;
import org.eclipse.jnosql.mapping.keyvalue.KeyValueTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.InternetProtocol;
import org.testcontainers.containers.wait.strategy.Wait;

import org.tasktide.core.supporting.ConfigUtil;
import org.testcontainers.containers.FixedHostPortGenericContainer;


/**
 * Utility class to support test modules in the spinning up of dependant docker containers
 *
 * @author bkenna
 */
public class TestEnvironment {
    
    // Logger
    private static final Logger LOGGER = LogManager.getLogger(TestEnvironment.class);
    
    
    /**
     * Starts redis container
     * 
     * @return Redis DockerContainer
     */
    public static FixedHostPortGenericContainer<?> redisContainer() {
        FixedHostPortGenericContainer<?> container = new FixedHostPortGenericContainer<>("redis:latest")
            .withExposedPorts(6379, 6379)
            .withFixedExposedPort(6379, 6379, InternetProtocol.TCP)
        .waitingFor(Wait.forListeningPort());
        container.start();
        return container;
    }
    
    
    /**
     * Create couchDB container with provided database. Stops container, and returns
     *  null if container not created/started
     * 
     * @param dbName
     * @param secured
     * @return CouchDB DockerContainer
     */
    public static FixedHostPortGenericContainer<?> couchDbContainer(String dbName, boolean secured) {
        
        // Start container
        FixedHostPortGenericContainer<?> container = new FixedHostPortGenericContainer<>("couchdb:latest")
            .withExposedPorts(5984)
            .withFixedExposedPort(5984, 5984, InternetProtocol.TCP)
            .withEnv("COUCHDB_USER", "admin")
            .withEnv("COUCHDB_PASSWORD", "password")
        .waitingFor(Wait.forHttp("/_up").forStatusCode(200));
        
        // Create database, and provide container if successful
        String uri = TestEnvironment.fetchCouchDbUri(container, dbName, secured);
        if ( TestEnvironment.createDbHttp(uri) ) {
            container.start();
            return container;
        }
        
        // Otherwise wait for its tear down
        return null;
    }
    
    
    /**
     * Starts mongoDB container with provided database
     * 
     * @param dbName
     * @return MongoDB DockerContainer
     */
    public static FixedHostPortGenericContainer<?> mongoDbContainer(String dbName) {
        FixedHostPortGenericContainer<?> container = new FixedHostPortGenericContainer<>("mongo:latest")
            .withExposedPorts(27017)
            .withFixedExposedPort(27017, 27017, InternetProtocol.TCP)
            .withEnv("MONGO_INITDB_DATABASE", dbName)
        .waitingFor(Wait.forListeningPort());
        container.start();
        return container;
    }
    
    
    /**
     * Starts mariaDB container with provided database
     * 
     * @param dbName
     * @return MariaDB DockerContainer
     */
    public static FixedHostPortGenericContainer<?> mariaDbContainer(String dbName) {
        FixedHostPortGenericContainer<?> container = new FixedHostPortGenericContainer<>("mariadb:latest")
            .withExposedPorts(3306)
            .withFixedExposedPort(3307, 3306, InternetProtocol.TCP)
            .withEnv("MARIADB_USER", "admin")
            .withEnv("MARIADB_PASSWORD", "password")
            .withEnv("MYSQL_ROOT_PASSWORD", "password")
            .withEnv("MYSQL_DATABASE", dbName)
        .waitingFor(Wait.forListeningPort());
        container.start();
        return container;
    }
    
    
    /**
     * Starts cassandra container
     * 
     * @param dbName
     * @return Cassandra DockerContainer
     */
    public static FixedHostPortGenericContainer<?> cassandraContainer(String dbName) {
        FixedHostPortGenericContainer<?> container = new FixedHostPortGenericContainer<>("cassandra:latest")
            .withExposedPorts(9842)
            .withFixedExposedPort(9842, 9842, InternetProtocol.TCP)
        .waitingFor(Wait.forListeningPort());
        container.start();
        return container;
    }
    
    
    /**
     * Spinup arangoDB container
     * 
     * @param dbName
     * @return ArangoDB DockerContainer
     */
    public static GenericContainer<?> arangoDBContainer(String dbName) {
        FixedHostPortGenericContainer<?> container = new FixedHostPortGenericContainer<>("arangodb:latest")
            .withExposedPorts(8529)
            .withFixedExposedPort(8529, 8529, InternetProtocol.TCP)
            .withEnv("ARANGO_ROOT_PASSWORD", "password")
        .waitingFor(Wait.forHttp("/").forStatusCode(200));
        container.start();
        return container;
    }
    
    
    /**
     * Start weld container with provided configuration
     * 
     * @param filePath
     * @param clazz
     * @return SeContainer
     */
    public static SeContainer startWeldContainer(String filePath, Class<?> clazz) {
        Config conf = ConfigUtil.loadFrom(filePath);
        ConfigUtil.register(conf, clazz);
        return SeContainerInitializer.newInstance()
            .initialize();
    }
    
    
    /**
     * Fetches template from configured container
     * 
     * @param container
     * @return Template
     */
    public static Template fetchKeyValueTemplate(SeContainer container) {
        return container.select(KeyValueTemplate.class).get();
    }
    
    
    /**
     * Fetch connection URI for couchDB container
     * 
     * @param container
     * @param dbName
     * @param secured
     * @return String
     */
    public static String fetchCouchDbUri(GenericContainer<?> container, String dbName, boolean secured) {
    
        // Parse authentication
        String host = container.getHost();
        int port = container.getMappedPort(5489);
        String user = container.getEnvMap().get("COUCHDB_USER");
        String password = container.getEnvMap().get("COUCHDB_PASSWORD");
        String uri = String.format(
		"http://%s:%s@%s:%d/%s",
		user, password, host, port, dbName
        );
        
        // Handle request context
        if ( secured ) {
            return uri.replace("http", "https");
        }
        return uri;
    }
    

    /**
     * Fires create database request against uri
     * 
     * @param uri
     * @return boolean
     */
    public static boolean createDbHttp(String uri) {
        
        // Initialize HTTP request
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .PUT(HttpRequest.BodyPublishers.noBody())
            .build();

            // Fire request
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("Status code:\t'{}'", resp.statusCode());
            LOGGER.info("Response body:\t'{}'", resp.body());
            
            // Check status code
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        }
        catch (Exception ex) {
            LOGGER.error("Error during create database request. Displaying stack trace\n\n", ex);
            ex.printStackTrace();
            return false;
        }
    }
}