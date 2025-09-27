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
import org.tasktide.core.supporting.JsonUtils;
import org.tasktide.core.supporting.Utils;


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
        if ( anno == null ) {
            anno = new HashMap<>();
        }
        return new CustomAnnotation(id, anno);
    }
    
    
    /**
     * Staatic building from command-line input as JSON, where an
     *  Id would not already be present, as string is converted to Map
     * 
     * @param json
     * @return {@link CustomAnnotation}
     */
    public static CustomAnnotation fromJsonString(String json) {
        Map<String, Object> map = JsonUtils.mapFromJson(json);
        CustomAnnotation anno = new CustomAnnotationBuilder()
            .withAnno(map)
        .build();
        if ( anno.getId() == null ) {
            anno.setAnnoId("CustomAnnotation-" + Utils.generateSalt());
        }
        return anno;
    }
}