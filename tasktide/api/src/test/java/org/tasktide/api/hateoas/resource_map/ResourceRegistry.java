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


import java.util.Map;


/**
 *
 * @author Bren
 */
public class ResourceRegistry {

    private Map<String, ResourceMap> resources;

    public ResourceRegistry() {}    

    public Map<String, ResourceMap> getResources() {
        return resources;
    }

    public void setResources(Map<String, ResourceMap> resources) {
        this.resources = resources;
    }
    
    
    public ResourceMap getResource(String resource) {
        return this.resources.get(resource);
    }
    
    
    public ResourceMap getResource(Class<? extends ResourceMap> classRef) {
        return this.resources.get(classRef.getSimpleName());
    }
    
    
    public void addResource(String key, ResourceMap resource) {
        this.resources.put(key, resource);
    }
    
    
    public void addResource(ResourceMap resource) {
        this.resources.put(resource.getClass().getSimpleName(), resource);
    }
}