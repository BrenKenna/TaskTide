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
package org.tasktide.api.hateoas.links;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;


/**
 * Data model for HATEOAS
 *
 * @author Bren
 */
public class LinkTemplate {
    
    // Attributes
    @JsonbProperty("Path")
    private String path;
    
    @JsonbProperty("Description")
    private String description;
    
    @JsonbProperty("Templated")
    private boolean templated;
    
    
    public LinkTemplate() {}
    
    @JsonbCreator
    public LinkTemplate(
        @JsonbProperty("Path") String path,
        @JsonbProperty("Templated") Boolean templated
    ) {
        this.path = path;
        this.templated = templated != null
                ? templated
                : path != null && path.contains("{");
    }
    
    /**
     * Internal construction for HAL entry with path
     * 
     * @param path
     * @param description
     * @param templated 
     */
    @JsonbCreator
    public LinkTemplate(
        @JsonbProperty("Path") String path,
        @JsonbProperty("Description") String description,
        @JsonbProperty("Templated") Boolean templated
    ) {
        this.path = path;
        this.description = description;
        this.templated = templated != null
                ? templated
                : path != null && path.contains("{");
    }

    
    /**
     * Get link description
     * 
     * @return String
     */
    public String getDescription() {
        return description;
    }

    
    /**
     * Set link description
     * 
     * @param description 
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    
    /**
     * Build with path
     * 
     * @param path
     * 
     * @return {@link LinkTemplate}
     */
    public static LinkTemplate of(String path) {
        return new LinkTemplate(path, path.contains("{"));
    }
    
    
    /**
     * Generate templated link
     * 
     * @param path
     * 
     * @return {@link LinkTemplate}
     */
    public static LinkTemplate templated(String path) {
        return new LinkTemplate(path, true);
    }

    
    /**
     * Get path
     * 
     * @return String
     */
    public String getPath() {
        return path;
    }

    
    /**
     * Checked whether link is templated
     * 
     * @return boolean
     */
    public boolean isTemplated() {
        return templated;
    }
}