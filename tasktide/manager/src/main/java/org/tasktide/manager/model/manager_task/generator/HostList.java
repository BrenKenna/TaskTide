/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.manager.model.manager_task.generator;

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
