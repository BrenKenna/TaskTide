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
package org.tasktide.parser;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.parser.model.Argument;
import org.tasktide.parser.model.ArgumentBuilder;
import org.tasktide.parser.model.ArgumentMap;
import org.tasktide.parser.model.ArgumentType;


/**
 * Module for testing the parsing of command-line arguments into argument tree
 * 
 * @author bkenna
 */
@Tag("unit-parser")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ArgumentParsing {
    
    private static final Logger logger = LogManager.getLogger(ArgumentParsing.class);
    private static final Jsonb PRETTY_JSON = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    private static final Jsonb JSON = JsonbBuilder.create(new JsonbConfig());
    private static final ArgumentBuilder ARG_BUILDER = new ArgumentBuilder();
    
    
    public ArgumentParsing() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Argument Parsing Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Argument Parsing Tests ----------------\n";
        logger.info(msg);
    }
    
    
    @BeforeEach
    public void setUp() {
        logger.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("\n\n================ Terminating Test ================\n");
    }
    
    
    /**
     * Test emulating the docker command-line setup
     */
    @Test
    @Order(0)
    public void canEmulateDocker() {
    
        // Initialize test
        logger.info("\n\n================ Can EmulateDockerCommand Test ================\n");
        boolean assertionState;
        ArgumentTree argTree = new ArgumentTree(" ");
        
        // Set global arguments on root "docker"
        ArgumentMap globalArgs = new ArgumentMap();
        Argument<Boolean> debugArg = ARG_BUILDER
            .withName("debug")
            .withDescription("Enable debug mode")
            .withShortFlag("-d")
            .withLongFlag("--debug")
            .withArgType(ArgumentType.GLOBAL)
        .build();
        globalArgs.putArgument(debugArg);
        
        
        // Action argument-1 "docker container"
        ArgumentMap containerArgs = new ArgumentMap();
        Argument<String> containerHelp = ARG_BUILDER
            .withName("help")
            .withDescription("Show help for container actions")
            .withShortFlag("-h")
            .withLongFlag("--help")
            .withArgType(ArgumentType.ACTION)
        .build();
        containerArgs.putArgument(containerHelp);
        
        
        // Action argument-2 "docker container run"
        ArgumentMap containerRunArgs = new ArgumentMap();
        Argument<String> containerRunName = ARG_BUILDER
            .withName("name")
            .withDescription("Set container name")
            .withShortFlag("-n")
            .withLongFlag("--name")
            .withArgType(ArgumentType.ACTION)
        .build();
        containerRunArgs.putArgument(containerRunName);
        
        
        // Add the argument maps to the argument tree
        argTree.getTree().getRoot().setData(globalArgs);
        argTree.getTree().addChild("docker container run", containerRunArgs);
        argTree.getTree().addChild("docker container", containerArgs);
        
        
        // Check tree after definition
        assertionState = argTree.getTree().size() >= 1;
        if ( assertionState ) {
            logger.info(
                "\n\nDisplaying tree data:\n\n{}\n\n",
                PRETTY_JSON.toJson(argTree.getVerboseHelp())
            );
        }
        
        // Evaluate test
        assertTrue(assertionState, "No data added to the argument tree");
        logger.info("\n\n================ Can Setup Tree Test ================\n");
    }
}
