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
package org.tasktide.core.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.lang.reflect.Type;
import java.util.HashMap;

import java.util.Map;
import org.tasktide.core.supporting.JsonUtils;


/**
 * {@link CustomAnnotation} JSON SERDE
 * 
 * @author Brendan Kenna
 */
@Converter(autoApply=false)
public class CustomAnnotationJpaConverter implements AttributeConverter<Map<String, Object>, String> {

    
    /**
     * Serialize {@link CustomAnnotation} data to JSON string
     * 
     * @param attribute
     * @return String
     */
    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        return JsonUtils.toJson(true, attribute);
    }

    
    /**
     * Deserialize {@link CustomAnnotation} data from JSON string
     * 
     * @param dbData
     * @return Map-String, Object
     */
    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new HashMap<>();
        }
        Type mapType = new HashMap<String, Object>() {}.getClass().getGenericSuperclass();
        return JsonUtils.fromJson(dbData, mapType);
    }
}