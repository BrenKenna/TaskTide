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
package org.tasktide.core.supporting;

import io.smallrye.config.PropertiesConfigSource;
import org.eclipse.microprofile.config.Config;
import io.smallrye.config.SmallRyeConfigBuilder;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Properties;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.tasktide.mutex.utils.FileUtility;


/**
 *
 * @author bkenna
 */
public class ConfigUtil {
    
    public static Config loadFrom(String filePath) {
        try ( InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream(filePath) ) {
            String fileName = FileUtility.getBaseName(filePath);
            Properties props = new Properties();
            props.load(input);
            Map<String, String> propMap = new HashMap<>();
            for ( Entry<Object, Object> elm : props.entrySet() ) {
                propMap.put(String.valueOf(elm.getKey()), String.valueOf(elm.getValue()));
            }
            
            PropertiesConfigSource pcs = new PropertiesConfigSource(propMap, fileName);
            return new SmallRyeConfigBuilder()
                .withSources(pcs)
            .build();
        }
        
        catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalStateException("Could not create for provided filepath:\t" + filePath);
        } 
    }
    
    
    /**
     * Replace default config with provided
     * 
     * @param config
     * @param clazz 
     */
    public static void register(Config config, Class<?> clazz) {
        
        // Fetch current profile
        ConfigProviderResolver resolver = ConfigProviderResolver.instance();
        
        // Release this current class loader
        resolver.releaseConfig(resolver.getConfig());
        
        // Register config for the provided class' class loader
        resolver.registerConfig(config, clazz.getClassLoader());
    }
}