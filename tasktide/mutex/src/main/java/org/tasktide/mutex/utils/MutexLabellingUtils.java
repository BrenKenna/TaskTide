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
package org.tasktide.mutex.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.UUID;

import org.tasktide.mutex.exceptions.MutexUncheckedException;


/**
 * Utility for {@link Mutex} labelling
 *
 * @author Brendan Kenna
 */
public class MutexLabellingUtils {
    
    // Attributes
    private static boolean isConfigured = false;
    private static volatile String
        NODE_ID, INSTANCE_ID,
        NODE_INSTANCE_ID;
    
    
    /**
     * Set instance Id from random UUID
     * 
     */
    private static synchronized void setInstanceId() {
        if ( INSTANCE_ID == null ) {
            INSTANCE_ID = UUID.randomUUID().toString();
        }
    }
    
    
    /**
     * Get instance Id
     * 
     * @return String
     */
    public static String getInstanceId() {
        if ( INSTANCE_ID == null ) {
            setInstanceId();
        }
        return INSTANCE_ID;
    }
    
    
    /**
     * Set node instance Id
     * 
     */
    private static synchronized void setNodeInstanceId() {
        if ( NODE_INSTANCE_ID == null ) {
            NODE_INSTANCE_ID = getNodeProcId();
        }
    }
    
    
    /**
     * Get configured node Id
     * 
     * @return String
     */
    public static synchronized String getNodeId() {
        if ( NODE_ID == null ) {
            InetAddress ipAddr = getHostIp();
            NODE_ID = formatIpAddr(ipAddr);
        }
        return NODE_ID;
    }
    
    
    /**
     * Configure utility
     * 
     */
    public static synchronized void configure() {
        setInstanceId();
        setNodeInstanceId();
        if ( NODE_INSTANCE_ID != null && INSTANCE_ID != null ) {
            isConfigured = true;
        }
    }
    
    
    /**
     * Get host IP address
     * 
     * @return {@link InetAddress}
     */
    public static InetAddress getHostIp() {
        try {
            return InetAddress.getLocalHost();
        }
        catch (UnknownHostException ex) {
            throw new MutexUncheckedException("Unable to retrieve hosting IP address");
        }
    }
    
    
    /**
     * Represent {@link InetAddress} with underscores
     *  instead of dots
     * 
     * @param addr
     * 
     * @return String 
     */
    public static String formatIpAddr(InetAddress addr) {
        return addr.getHostAddress().replace(".", "-");
    }
    
    
    /**
     * Concatenate Node and IP Address
     * 
     * @return String
     */
    public static String getNodeProcId() {
        InetAddress hostIp = getHostIp();
        return formatIpAddr(hostIp) + "_" + getInstanceId();
    }
    
    
    /**
     * Get epoch time
     * 
     * @return long
     */
    public static long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }
    
    
    /**
     * Get the file name for new lock file under node-instance,
     *  to be appended onto directory path
     * 
     * @return String
     */
    public static String getMutexFileName() {
        if ( isConfigured ) {
            return getCurrentTimeStamp() + "." + NODE_INSTANCE_ID + ".lock";
        }
        else {
            throw new IllegalStateException("Error, MutexUtility must be first configured");
        }
    }
    
    
    /**
     * Display whether utility is configured
     * 
     * @return boolean
     */
    public static boolean isConfigured() {
        return isConfigured;
    }
}