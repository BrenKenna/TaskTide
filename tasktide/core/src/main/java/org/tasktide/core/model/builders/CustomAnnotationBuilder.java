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
package org.tasktide.core.model.builders;

import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.model.CustomAnnotation;


/**
 * {@link CustomAnnotation} builder
 * 
 * @author Brendan Kenna
 */
public class CustomAnnotationBuilder extends ModelBuilder<CustomAnnotation> {
    
    // Attributes
    private String id;
    private Map<String, Object> anno;
    
    
    /**
     * Construct
     */
    public CustomAnnotationBuilder() {
        super();
    }
    
    
    /**
     * Adds Id field
     * 
     * @param id
     * @return {@link CustomAnnotationBuilder}
     */
    public CustomAnnotationBuilder withId(String id) {
        this.id = id;
        return this;
    }
    
    
    /**
     * Adds annotation field
     * 
     * @param anno
     * @return {@link CustomAnnotationBuilder}
     */
    public CustomAnnotationBuilder withAnno(Map<String, Object> anno) {
        this.anno = anno;
        return this;
    }
    
    
    /**
     * Build annotation
     * 
     * @return {@link CustomAnnotation}
     */
    @Override
    public CustomAnnotation build() {
        return new CustomAnnotation(id, anno);
    }
}