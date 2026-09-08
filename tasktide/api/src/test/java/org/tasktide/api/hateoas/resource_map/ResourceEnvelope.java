/*
 * Copyright 2026 Bren.
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
package org.tasktide.api.hateoas.resource_map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;


/**
 * HATEOAS wrapper for REST resources
 * 
 * @param <T> Data type of Resource Envelope
 * @author Bren
 */
public class ResourceEnvelope<T> {

    // Attributes
    @JsonUnwrapped
    private final T data;
    
    @JsonIgnore
    private final Class<T> classRef;
    
    @JsonProperty("_links")
    private final Map<String, Object> links;
    
    
    /**
     * Construct with data
     * 
     * @param data
     * @param classRef 
     */
    public ResourceEnvelope(T data, Class<T> classRef) {
        this.data = data;
        this.classRef = classRef;
        this.links = new LinkedHashMap<>();
    }

    
    /**
     * Convenience factory
     * 
     * @param <T>
     * @param data
     * @return {@link ResourceEnvelope}
     */
    @SuppressWarnings("unchecked")
    public static <T> ResourceEnvelope<T> of(T data) {
        return new ResourceEnvelope<>(data, (Class<T>) data.getClass());
    }
    
    
    /**
     * Get data payload
     * 
     * @return T
     */
    public T getData() {
        return data;
    }

    
    /**
     * Get class type of data payload
     * 
     * @return Class-T
     */
    public Class<T> getClassRef() {
        return classRef;
    }

    
    /**
     * Get read-only links parameter
     * 
     * @return 
     */
    public Map<String, Object> getLinks() {
        return Collections.unmodifiableMap(this.links);
    }

    
    /**
     * Add provided link with relation key
     * 
     * @param rel
     * @param link 
     */
    public void addLink(String rel, Object link) {
        this.links.put(rel, link);
    }
    
    
    /**
     * Represent as String
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "ResourceEnvelope{" +
            "data=" + data +
            ", classRef=" + classRef +
            ", links=" + links +
        '}';
    }
}