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
package org.tasktide.core.manager.generator;

import java.util.Random;


/**
 * Enum to hold values for various host names
 * 
 * @author bkenna
 */
public enum HostList {
    AMAZON("amazon.com"),
    GOOGLE("google.com"),
    FACEBOOK("facebook.com"),
    RTE("rte.ie"),
    NETFLIX("netflix.com"),
    YOUTUBE("youtube.com"),
    SPOTIFY("spotify.com"),
    BBC("bbc.com"),
    TWITTER("twitter.com"),
    INSTAGRAM("instagram.com")
    ;
    
    private final String host;
    
    HostList(String host) {
        this.host = host;
    }
    
    /**
     * Print host as string
     * 
     * @return String
     */
    public String getHost() {
        return host;
    }
    
    /**
     * Fetch random host
     * 
     * @return String 
     */
    public static String getRandomHost() {
        Random rand = new Random();
        return HostList.values()[ rand.nextInt(HostList.values().length) ].getHost();
    }
}
