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
package org.tasktide.api;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import org.eclipse.microprofile.config.Config;

import jakarta.nosql.Template;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.jnosql.mapping.document.DocumentTemplate;

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
        container.start();
        
        // Create database, and provide container if successful
        String uri = TestEnvironment.fetchCouchDbUri(container, dbName, secured);
	String authHeader = TestEnvironment.createDbAuthHeader(
		container.getEnvMap().get("COUCHDB_USER"),
		container.getEnvMap().get("COUCHDB_PASSWORD")
	);
        if ( TestEnvironment.createDbHttp(uri, authHeader) ) {
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
    public static Template fetchDocumentTemplate(SeContainer container) {
        return container.select(DocumentTemplate.class).get();
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
        int port = container.getFirstMappedPort();
        String user = container.getEnvMap().get("COUCHDB_USER");
        String password = container.getEnvMap().get("COUCHDB_PASSWORD");
        String uri = String.format(
		"http://%s:%s@%s:%d/%s",
		user, password, host, port, dbName
        );
        //LOGGER.info("Request URI:\n'{}'", uri);
        
        // Handle request context
        if ( secured ) {
            return uri.replace("http", "https");
        }
        return uri;
    }
    
    
    /**
     * Fetch auth header
     * 
     * @param username
     * @param password
     * @return String
     */
    public static String createDbAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        return "Basic " + encodedAuth;
    }
    

    /**
     * Fires create database request against uri
     * 
     * @param uri
     * @param authHeader
     * @return boolean
     */
    public static boolean createDbHttp(String uri, String authHeader) {
        
        // Initialize HTTP request
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Authorization", authHeader)
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
            return false;
        }
    }
}