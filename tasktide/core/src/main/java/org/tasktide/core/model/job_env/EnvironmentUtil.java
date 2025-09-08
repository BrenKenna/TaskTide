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
package org.tasktide.core.model.job_env;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 *
 * @author Brendan Kenna
 */
class EnvironmentUtil {
    
    public static Map<String, String> getEnv() {
        return System.getenv();
    }
    
    
    public static Map<String, String> getProps() {
        Map<String, String> output = new HashMap<>();
        Properties props = System.getProperties();
        
        for ( String key : props.stringPropertyNames() ) {
            EnvironmentProperty envProp = EnvironmentProperty.get(key);
            if ( envProp != null ) {
                output.put(envProp.name(), props.getProperty(key));
            }
        }
        
        return output;
    }
    
    
    public static InetAddress getHostAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }
    
    
    public static String getHostname() throws UnknownHostException {
        return InetAddress.getLocalHost().getCanonicalHostName();
    }
}
